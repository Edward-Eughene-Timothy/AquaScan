package com.mirai.microplasticdetector.ui

import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.exifinterface.media.ExifInterface
import com.mirai.microplasticdetector.data.AnalysisReportEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Locale

@Composable
fun AnalysisReportDetailScreen(
    report: AnalysisReportEntity,
    onBack: () -> Unit,
    onDeleteReport: (AnalysisReportEntity) -> Unit
) {
    val context = LocalContext.current

    val backgroundTop = Color(0xFF031827)
    val backgroundBottom = Color(0xFF051F31)
    val cardColor = Color(0xFF0A1B2B)

    val cyan = Color(0xFF42F3A7)
    val teal = Color(0xFF00EFA3)
    val white = Color.White
    val mutedText = Color(0xFF8EA6B8)
    val deleteRed = Color(0xFFFA4C4C)

    val polymerCounts = mapOf(
        "ABS" to report.countABS,
        "Nylon" to report.countNylon,
        "PE" to report.countPE,
        "PET" to report.countPET,
        "PS" to report.countPS,
        "PVC" to report.countPVC
    )

    var imageBitmap by remember { mutableStateOf<android.graphics.Bitmap?>(null) }

    // Load actual image from saved Uri String and apply EXIF orientation correction
    LaunchedEffect(report.imageUriString) {
        if (!report.imageUriString.isNullOrEmpty()) {
            imageBitmap = withContext(Dispatchers.IO) {
                try {
                    val uri = Uri.parse(report.imageUriString)

                    val inputStream = context.contentResolver.openInputStream(uri)
                    val rawBitmap = inputStream?.use { BitmapFactory.decodeStream(it) }

                    if (rawBitmap != null) {
                        val exifStream = context.contentResolver.openInputStream(uri)
                        val orientation = exifStream?.use { stream ->
                            val exif = ExifInterface(stream)
                            exif.getAttributeInt(
                                ExifInterface.TAG_ORIENTATION,
                                ExifInterface.ORIENTATION_NORMAL
                            )
                        } ?: ExifInterface.ORIENTATION_NORMAL

                        val matrix = Matrix()
                        when (orientation) {
                            ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
                            ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
                            ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
                        }

                        if (orientation != ExifInterface.ORIENTATION_NORMAL && orientation != ExifInterface.ORIENTATION_UNDEFINED) {
                            android.graphics.Bitmap.createBitmap(
                                rawBitmap, 0, 0, rawBitmap.width, rawBitmap.height, matrix, true
                            )
                        } else {
                            rawBitmap
                        }
                    } else null
                } catch (e: Exception) {
                    e.printStackTrace()
                    null
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(backgroundTop, backgroundBottom)
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 22.dp, vertical = 18.dp)
        ) {
            // TOP BAR
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(42.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = onBack,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        contentColor = mutedText
                    ),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 4.dp)
                ) {
                    Text(
                        text = "←  REPORTS",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }

                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .background(Color.White.copy(alpha = 0.035f), CircleShape)
                        .border(1.dp, cyan.copy(alpha = 0.35f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "07",
                        color = cyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(22.dp))

            Text(
                text = "ANALYSIS ARCHIVE",
                color = cyan,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "FULL REPORT",
                color = white,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.8.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = report.reportCode,
                color = teal,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            // STATUS BANNER
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF06291F), RoundedCornerShape(16.dp))
                    .border(1.dp, teal.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                    .padding(horizontal = 16.dp, vertical = 13.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.size(10.dp).background(teal, CircleShape))
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "ANALYSIS COMPLETE",
                        color = teal,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Saved SQLite report record",
                        color = mutedText,
                        fontSize = 10.sp
                    )
                }
                Text(
                    text = "AI ARCHIVE",
                    color = cyan,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(15.dp))

            // SAMPLE INFORMATION CARD
            ReportDetailCard(title = "SAMPLE INFORMATION") {
                DetailRow(label = "Report Code", value = report.reportCode)
                DetailRow(label = "Sample ID", value = report.sampleId)
                DetailRow(label = "Source", value = report.source)
                DetailRow(label = "Volume", value = "${report.volumeMl} mL")
            }

            Spacer(modifier = Modifier.height(15.dp))

            // SEPARATE DEDICATED COLLECTION NOTES CARD
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(cardColor, RoundedCornerShape(17.dp))
                    .border(1.dp, cyan.copy(alpha = 0.16f), RoundedCornerShape(17.dp))
                    .padding(16.dp)
            ) {
                Text(
                    text = "COLLECTION NOTES",
                    color = cyan,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = if (report.notes.isNotBlank()) report.notes else "No observations recorded for this sample.",
                    color = if (report.notes.isNotBlank()) white else mutedText,
                    fontSize = 12.sp,
                    lineHeight = 18.sp
                )
            }

            Spacer(modifier = Modifier.height(15.dp))

            // CORE METRICS
            ReportDetailCard(title = "DETECTION RESULTS") {
                DetailRow(label = "Objects Detected", value = report.objectCount.toString())
                DetailRow(
                    label = "Average Confidence",
                    value = String.format(Locale.getDefault(), "%.1f%%", report.averageConfidence)
                )
                DetailRow(
                    label = "Detection Status",
                    value = if (report.objectCount > 0) "Candidates detected" else "No candidates detected"
                )
            }

            Spacer(modifier = Modifier.height(15.dp))

            // IMAGE VISUALIZATION (ROTATION FIXED)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(cardColor, RoundedCornerShape(17.dp))
                    .border(1.dp, cyan.copy(alpha = 0.16f), RoundedCornerShape(17.dp))
                    .padding(16.dp)
            ) {
                Text(
                    text = "DETECTION VISUALIZATION",
                    color = mutedText,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                        .background(Color(0xFF020A12), RoundedCornerShape(14.dp))
                        .border(1.dp, cyan.copy(alpha = 0.15f), RoundedCornerShape(14.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    if (imageBitmap != null) {
                        Image(
                            bitmap = imageBitmap!!.asImageBitmap(),
                            contentDescription = "Archived Microscopic Image",
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(14.dp),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "IMAGE ARCHIVE",
                                color = cyan,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.3.sp
                            )
                            Spacer(modifier = Modifier.height(5.dp))
                            Text(
                                text = "No image available or file unreadable",
                                color = mutedText,
                                fontSize = 10.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(15.dp))

            // MATERIAL BREAKDOWN CARD
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(cardColor, RoundedCornerShape(17.dp))
                    .border(1.dp, teal.copy(alpha = 0.18f), RoundedCornerShape(17.dp))
                    .padding(16.dp)
            ) {
                Text(
                    text = "MATERIAL BREAKDOWN",
                    color = mutedText,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                polymerCounts.forEach { (material, count) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .background(
                                    if (count > 0) teal else mutedText.copy(alpha = 0.35f),
                                    CircleShape
                                )
                        )

                        Spacer(modifier = Modifier.width(10.dp))

                        Text(
                            text = material,
                            color = white,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.weight(1f)
                        )

                        Text(
                            text = "$count detected",
                            color = if (count > 0) teal else mutedText,
                            fontSize = 10.sp,
                            fontWeight = if (count > 0) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // ACTION BUTTONS
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = { onDeleteReport(report) },
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = deleteRed)
                ) {
                    Text(
                        text = "DELETE REPORT",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp
                    )
                }

                Button(
                    onClick = onBack,
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = cyan)
                ) {
                    Text(
                        text = "BACK TO REPORTS",
                        color = Color.Black,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "MICROPLASTIC ANALYSIS ARCHIVE",
                color = mutedText.copy(alpha = 0.65f),
                fontSize = 8.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun ReportDetailCard(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF0A1B2B), RoundedCornerShape(17.dp))
            .border(1.dp, Color(0xFF00D9FF).copy(alpha = 0.16f), RoundedCornerShape(17.dp))
            .padding(16.dp)
    ) {
        Text(
            text = title,
            color = Color(0xFF8EA6B8),
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.2.sp
        )
        Spacer(modifier = Modifier.height(10.dp))
        content()
    }
}

@Composable
fun DetailRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = Color(0xFF8EA6B8),
            fontSize = 10.sp
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = value,
            color = Color.White,
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.End
        )
    }
}