package com.mirai.microplasticdetector.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "analysis_reports")
data class AnalysisReportEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val reportCode: String,
    val sampleId: String,
    val source: String,
    val volumeMl: Int,
    val notes: String = "",
    val objectCount: Int,
    val averageConfidence: Double,
    val countABS: Int = 0,
    val countNylon: Int = 0,
    val countPE: Int = 0,
    val countPET: Int = 0,
    val countPS: Int = 0,
    val countPVC: Int = 0,
    val imageUriString: String?,
    val timestamp: Long = System.currentTimeMillis(),
    val detectionsJson: String? = null // Add this parameter here
)