package com.mirai.microplasticdetector.models

import android.net.Uri

data class AnalysisSession(
    val sampleId: String,
    val source: String,
    val volumeMl: Int,
    val notes: String,
    val imageUri: Uri? = null,
    val detectionResult: DetectionResult? = null
)