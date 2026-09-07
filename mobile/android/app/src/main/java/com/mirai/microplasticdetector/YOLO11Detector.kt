package com.mirai.microplasticdetector

import android.content.Context
import android.graphics.Bitmap
import ai.onnxruntime.*
import java.nio.FloatBuffer
import android.util.Log

data class Detection(
    val classId: Int,
    val className: String,
    val confidence: Float,
    val x1: Float,
    val y1: Float,
    val x2: Float,
    val y2: Float
)

class YOLO11Detector(
    private val context: Context,
    private val confidenceThreshold: Float = 0.55f,
    private val iouThreshold: Float = 0.45f
) {

    private val classNames = arrayOf(
        "ABS",
        "Nylon",
        "PE",
        "PET",
        "PS",
        "PVC"
    )

    private val ortEnvironment: OrtEnvironment = OrtEnvironment.getEnvironment()
    private val ortSession: OrtSession

    init {
        val modelBytes = context.assets.open("best.onnx").use { input ->
            input.readBytes()
        }
        val sessionOptions = OrtSession.SessionOptions()
        ortSession = ortEnvironment.createSession(modelBytes, sessionOptions)
    }

    fun detect(bitmap: Bitmap): List<Detection> {
        val meanLuma = calculateMeanBrightness(bitmap)
        Log.d("YOLO11_DEBUG", "Image Mean Brightness: $meanLuma / 255.0")

        // Reject room photos (too bright > 100) or total black captures (< 5)
        if (meanLuma > 100 || meanLuma < 5) {
            Log.d("YOLO11_DEBUG", "Rejected by Luma Gate (Not a dark-field slide sample)")
            return emptyList()
        }

        val resizedBitmap = Bitmap.createScaledBitmap(bitmap, 640, 640, true)
        val intValues = IntArray(640 * 640)
        resizedBitmap.getPixels(intValues, 0, 640, 0, 0, 640, 640)

        val inputData = FloatArray(1 * 3 * 640 * 640)
        val imageArea = 640 * 640

        for (i in 0 until imageArea) {
            val pixel = intValues[i]
            val r = ((pixel shr 16) and 0xFF) / 255.0f
            val g = ((pixel shr 8) and 0xFF) / 255.0f
            val b = (pixel and 0xFF) / 255.0f

            inputData[i] = r
            inputData[imageArea + i] = g
            inputData[2 * imageArea + i] = b
        }

        val inputBuffer = FloatBuffer.wrap(inputData)
        val inputTensor = OnnxTensor.createTensor(
            ortEnvironment,
            inputBuffer,
            longArrayOf(1, 3, 640, 640)
        )

        inputTensor.use { tensor ->
            val inputs = mapOf("images" to tensor)
            ortSession.run(inputs).use { results ->
                val output = results[0].value as Array<*>
                @Suppress("UNCHECKED_CAST")
                val outputData = output as Array<Array<FloatArray>>

                val detections = decodeOutput(outputData, resizedBitmap)
                Log.d("YOLO11_DEBUG", "PID=${android.os.Process.myPid()} | Final Detection Count = ${detections.size}")

                Log.d("YOLO11_DEBUG", "--- DETECTION BREAKDOWN ---")
                val summary = detections.groupBy { it.className }
                summary.forEach { (polymer, list) ->
                    val avgConf = list.map { it.confidence }.average() * 100
                    Log.d("YOLO11_DEBUG", "$polymer: ${list.size} particles (Avg Conf: ${String.format("%.1f", avgConf)}%)")
                }
                return detections
            }
        }
    }

    private fun calculateMeanBrightness(bitmap: Bitmap): Int {
        val sampleSize = 100
        val resized = Bitmap.createScaledBitmap(bitmap, sampleSize, sampleSize, false)
        val pixels = IntArray(sampleSize * sampleSize)
        resized.getPixels(pixels, 0, sampleSize, 0, 0, sampleSize, sampleSize)

        var totalLuma = 0L
        for (pixel in pixels) {
            val r = (pixel shr 16) and 0xFF
            val g = (pixel shr 8) and 0xFF
            val b = pixel and 0xFF
            totalLuma += (0.299 * r + 0.587 * g + 0.114 * b).toLong()
        }
        return (totalLuma / (sampleSize * sampleSize)).toInt()
    }

    private fun decodeOutput(output: Array<Array<FloatArray>>, resizedBitmap: Bitmap): List<Detection> {
        val predictions = output[0]
        val detections = mutableListOf<Detection>()
        val numberOfCandidates = predictions[0].size

        for (candidate in 0 until numberOfCandidates) {
            var bestClass = -1
            var bestConfidence = 0f

            for (classId in 0 until classNames.size) {
                val confidence = predictions[4 + classId][candidate]
                if (confidence > bestConfidence) {
                    bestConfidence = confidence
                    bestClass = classId
                }
            }

            if (bestClass < 0 || bestConfidence < confidenceThreshold) {
                continue
            }

            val centerX = predictions[0][candidate]
            val centerY = predictions[1][candidate]
            val width = predictions[2][candidate]
            val height = predictions[3][candidate]

            val boxArea = width * height
            if (boxArea > 80000f) {
                continue
            }

            val x1 = (centerX - width / 2f).coerceIn(0f, 640f)
            val y1 = (centerY - height / 2f).coerceIn(0f, 640f)
            val x2 = (centerX + width / 2f).coerceIn(0f, 640f)
            val y2 = (centerY + height / 2f).coerceIn(0f, 640f)

            // Spectral Verification: Rejects ISO noise & white glare false positives
            if (!verifyFluorescenceProfile(resizedBitmap, x1, y1, x2, y2)) {
                continue
            }

            detections.add(
                Detection(
                    classId = bestClass,
                    className = classNames[bestClass],
                    confidence = bestConfidence,
                    x1 = x1,
                    y1 = y1,
                    x2 = x2,
                    y2 = y2
                )
            )
        }

        return applyNms(detections)
    }

    private fun verifyFluorescenceProfile(
        bitmap: Bitmap,
        x1: Float,
        y1: Float,
        x2: Float,
        y2: Float
    ): Boolean {
        val startX = x1.toInt().coerceIn(0, bitmap.width - 1)
        val startY = y1.toInt().coerceIn(0, bitmap.height - 1)
        val w = (x2 - x1).toInt().coerceIn(1, bitmap.width - startX)
        val h = (y2 - y1).toInt().coerceIn(1, bitmap.height - startY)

        val pixels = IntArray(w * h)
        bitmap.getPixels(pixels, 0, w, startX, startY, w, h)

        var fluorescentPixels = 0
        for (pixel in pixels) {
            val r = (pixel shr 16) and 0xFF
            val g = (pixel shr 8) and 0xFF
            val b = pixel and 0xFF

            // Nile Red signature: Red dominant over Green & Blue
            if (r > 70 && r > (g * 1.15f) && r > (b * 1.3f)) {
                fluorescentPixels++
            }
        }

        // Must have at least 10% red-dominant pixels in the box
        return (fluorescentPixels.toFloat() / pixels.size) >= 0.10f
    }

    private fun applyNms(detections: List<Detection>): List<Detection> {
        val finalDetections = mutableListOf<Detection>()
        val sorted = detections.sortedByDescending { it.confidence }.toMutableList()

        while (sorted.isNotEmpty()) {
            val best = sorted.removeAt(0)
            finalDetections.add(best)

            sorted.removeAll { candidate ->
                calculateIoU(best, candidate) > iouThreshold
            }
        }

        return finalDetections
    }

    private fun calculateIoU(first: Detection, second: Detection): Float {
        val intersectionLeft = maxOf(first.x1, second.x1)
        val intersectionTop = maxOf(first.y1, second.y1)
        val intersectionRight = minOf(first.x2, second.x2)
        val intersectionBottom = minOf(first.y2, second.y2)

        val intersectionWidth = maxOf(0f, intersectionRight - intersectionLeft)
        val intersectionHeight = maxOf(0f, intersectionBottom - intersectionTop)
        val intersectionArea = intersectionWidth * intersectionHeight

        val firstArea = (first.x2 - first.x1) * (first.y2 - first.y1)
        val secondArea = (second.x2 - second.x1) * (second.y2 - second.y1)

        val unionArea = firstArea + secondArea - intersectionArea
        if (unionArea <= 0.0001f) return 0f

        return intersectionArea / unionArea
    }

    fun close() {
        ortSession.close()
    }
}