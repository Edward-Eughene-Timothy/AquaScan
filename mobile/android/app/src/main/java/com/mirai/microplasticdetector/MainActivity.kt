package com.mirai.microplasticdetector

import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.gson.Gson
import com.mirai.microplasticdetector.data.AnalysisReportEntity
import com.mirai.microplasticdetector.data.AppDatabase
import com.mirai.microplasticdetector.ml.YOLO11Detector
import com.mirai.microplasticdetector.models.AnalysisSession
import com.mirai.microplasticdetector.models.DetectionResult
import com.mirai.microplasticdetector.ui.AnalysisHistoryScreen
import com.mirai.microplasticdetector.ui.AnalysisReportDetailScreen
import com.mirai.microplasticdetector.ui.AnalysisResultsScreen
import com.mirai.microplasticdetector.ui.DetectionScreen
import com.mirai.microplasticdetector.ui.HomeScreen
import com.mirai.microplasticdetector.ui.ImageCaptureScreen
import com.mirai.microplasticdetector.ui.PreparationScreen
import com.mirai.microplasticdetector.ui.SamplingScreen
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
            val database = remember { AppDatabase.getDatabase(applicationContext) }
            val reportDao = database.analysisReportDao()

            val reportsList by reportDao.getAllReports().collectAsState(initial = emptyList())
            var selectedReport by remember { mutableStateOf<AnalysisReportEntity?>(null) }

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
                                navController.navigate("analysis_history")
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
                                                            "content" -> contentResolver.openInputStream(imageUri)?.use {
                                                                BitmapFactory.decodeStream(it)
                                                            }
                                                            "file" -> BitmapFactory.decodeFile(imageUri.path)
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
                                    imageUri = session.imageUri,
                                    onBack = { navController.popBackStack() },
                                    onSaveReport = {
                                        lifecycleScope.launch(Dispatchers.IO) {
                                            val polymers = result.polymerCounts
                                            val jsonDetections = Gson().toJson(result.detections)

                                            val entity = AnalysisReportEntity(
                                                reportCode = "RPT-${System.currentTimeMillis().toString().takeLast(4)}",
                                                sampleId = session.sampleId,
                                                source = session.source,
                                                volumeMl = session.volumeMl,
                                                notes = session.notes,
                                                objectCount = result.objectCount,
                                                averageConfidence = result.averageConfidence,
                                                countABS = polymers["ABS"] ?: 0,
                                                countNylon = polymers["Nylon"] ?: 0,
                                                countPE = polymers["PE"] ?: 0,
                                                countPET = polymers["PET"] ?: 0,
                                                countPS = polymers["PS"] ?: 0,
                                                countPVC = polymers["PVC"] ?: 0,
                                                imageUriString = session.imageUri?.toString(),
                                                detectionsJson = jsonDetections
                                            )

                                            reportDao.insertReport(entity)

                                            withContext(Dispatchers.Main) {
                                                navController.navigate("analysis_history")
                                            }
                                        }
                                    },
                                    onContinue = {
                                        currentSession = null
                                        navController.navigate("home") {
                                            popUpTo("home") { inclusive = true }
                                        }
                                    }
                                )
                            }
                        }
                    }

                    composable("analysis_history") {
                        AnalysisHistoryScreen(
                            reports = reportsList,
                            onBack = { navController.popBackStack() },
                            onBeginAnalysis = { navController.navigate("sampling") },
                            onReportClick = { report ->
                                selectedReport = report
                                navController.navigate("analysis_report_detail")
                            }
                        )
                    }

                    composable("analysis_report_detail") {
                        selectedReport?.let { report ->
                            AnalysisReportDetailScreen(
                                report = report,
                                onBack = { navController.popBackStack() },
                                onDeleteReport = { reportToDelete ->
                                    lifecycleScope.launch(Dispatchers.IO) {
                                        reportDao.deleteReport(reportToDelete)
                                        withContext(Dispatchers.Main) {
                                            selectedReport = null
                                            navController.popBackStack()
                                        }
                                    }
                                }
                            )
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