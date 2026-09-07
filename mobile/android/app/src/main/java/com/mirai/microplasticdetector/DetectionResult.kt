package com.mirai.microplasticdetector

data class DetectionResult(
    val detections: List<Detection>,
    val objectCount: Int,
    val averageConfidence: Double,
    val polymerCounts: Map<String, Int> = detections.groupBy { it.className }.mapValues { it.value.size }
)