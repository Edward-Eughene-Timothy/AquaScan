package com.mirai.microplasticdetector.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AnalysisReportDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReport(report: AnalysisReportEntity): Long

    @Query("SELECT * FROM analysis_reports ORDER BY timestamp DESC")
    fun getAllReports(): Flow<List<AnalysisReportEntity>>

    @Query("SELECT * FROM analysis_reports WHERE id = :id")
    suspend fun getReportById(id: Long): AnalysisReportEntity?

    @Delete
    suspend fun deleteReport(report: AnalysisReportEntity)

    @Query("DELETE FROM analysis_reports WHERE id = :id")
    suspend fun deleteReportById(id: Long)
}