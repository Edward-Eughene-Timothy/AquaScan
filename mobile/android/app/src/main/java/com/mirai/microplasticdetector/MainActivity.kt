package com.mirai.microplasticdetector

import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mirai.microplasticdetector.ui.theme.MicroplasticDetectorTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {
    private lateinit var yoloDetector: YOLO11Detector

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        yoloDetector = YOLO11Detector(this)

        enableEdgeToEdge()

        setContent {
            MicroplasticDetectorTheme {
                val navController = rememberNavController()

                var currentSession by remember {
                    mutableStateOf<AnalysisSession?>(null)
                }

                NavHost(
                    navController = navController,
                    startDestination = "home"
                ) {

                    composable("home") {
                        HomeScreen(
                            onBeginAnalysis = {
                                navController.navigate("sampling")
                            },
                            onShowReport = {
                                // Report screen connection
                            }
                        )
                    }

                    composable("sampling") {
                        SamplingScreen(
                            onBack = {
                                navController.popBackStack()
                            },
                            onContinue = { session ->
                                currentSession = session
                                navController.navigate("preparation")
                            }
                        )
                    }

                    composable("preparation") {
                        currentSession?.let { session ->
                            PreparationScreen(
                                session = session,
                                onBack = {
                                    navController.popBackStack()
                                },
                                onContinue = {
                                    navController.navigate("capture")
                                }
                            )
                        }
                    }

                    composable("capture") {
                        currentSession?.let { session ->
                            ImageCaptureScreen(
                                session = session,
                                onBack = {
                                    navController.popBackStack()
                                },
                                onContinue = { imageUri ->
                                    // Reset detection result when a new image is captured
                                    currentSession = currentSession?.copy(
                                        imageUri = imageUri,
                                        detectionResult = null
                                    )
                                    navController.navigate("detection")
                                }
                            )
                        }
                    }

                    composable("detection") {
                        currentSession?.let { session ->
                            var analysisStarted by remember {
                                mutableStateOf(false)
                            }

                            DetectionScreen(
                                session = session,
                                detectionResult = session.detectionResult,
                                isAnalyzing = analysisStarted,
                                onBack = {
                                    // Clear stale detection state on back navigation
                                    currentSession = currentSession?.copy(
                                        detectionResult = null
                                    )
                                    navController.popBackStack()
                                },
                                onRunAnalysis = {
                                    if (!analysisStarted) {
                                        analysisStarted = true

                                        val imageUri = session.imageUri

                                        if (imageUri != null) {
                                            lifecycleScope.launch {
                                                try {
                                                    val bitmap = withContext(Dispatchers.IO) {
                                                        when (imageUri.scheme) {
                                                            "content" ->
                                                                contentResolver
                                                                    .openInputStream(imageUri)
                                                                    ?.use {
                                                                        BitmapFactory.decodeStream(it)
                                                                    }

                                                            "file" ->
                                                                BitmapFactory.decodeFile(
                                                                    imageUri.path
                                                                )

                                                            else -> null
                                                        }
                                                    }

                                                    if (bitmap != null) {
                                                        val detections = withContext(Dispatchers.Default) {
                                                            yoloDetector.detect(bitmap)
                                                        }

                                                        val averageConfidence = if (detections.isNotEmpty()) {
                                                            detections.map { it.confidence }.average() * 100.0
                                                        } else {
                                                            0.0
                                                        }

// Group detections by polymer name (e.g., {"Nylon": 2, "PE": 1})
                                                        val polymerCounts = detections.groupBy { it.className }
                                                            .mapValues { it.value.size }

                                                        val result = DetectionResult(
                                                            detections = detections,
                                                            objectCount = detections.size,
                                                            averageConfidence = averageConfidence,
                                                            polymerCounts = polymerCounts
                                                        )

                                                        currentSession = currentSession?.copy(
                                                            detectionResult = result
                                                        )
                                                    }

                                                } catch (e: Exception) {
                                                    e.printStackTrace()
                                                } finally {
                                                    analysisStarted = false
                                                }
                                            }

                                        } else {
                                            analysisStarted = false
                                        }
                                    }
                                },
                                onContinue = {
                                    navController.navigate("analysis_results")
                                }
                            )
                        }
                    }

                    composable("analysis_results") {
                        currentSession?.let { session ->
                            session.detectionResult?.let { result ->
                                AnalysisResultsScreen(
                                    session = session,
                                    detectionResult = result,
                                    imageUri = session.imageUri, // Pass the image URI here!
                                    onBack = {
                                        navController.popBackStack()
                                    },
                                    onContinue = {
                                        // Next stage connection
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        if (::yoloDetector.isInitialized) {
            yoloDetector.close()
        }
        super.onDestroy()
    }
}