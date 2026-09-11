package com.mirai.microplasticdetector.ui

import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mirai.microplasticdetector.models.AnalysisSession
import com.mirai.microplasticdetector.models.DetectionResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun AnalysisResultsScreen(
    session: AnalysisSession,
    detectionResult: DetectionResult,
    imageUri: Uri?,
    onBack: () -> Unit,
    onSaveReport: () -> Unit,
    onContinue: () -> Unit
) {

    val context = LocalContext.current

    val backgroundTop = Color(0xFF031827)
    val backgroundBottom = Color(0xFF051F31)

    val cardColor = Color(0xFF0A1B2B)

    val cyan = Color(0xFF00D9FF)
    val teal = Color(0xFF00EFA3)

    val white = Color.White
    val mutedText = Color(0xFF8EA6B8)

    val materialCounts = detectionResult.detections
        .groupingBy { it.className }
        .eachCount()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        backgroundTop,
                        backgroundBottom
                    )
                )
            )
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(
                    horizontal = 22.dp,
                    vertical = 18.dp
                )
        ) {

            // ------------------------------------------------
            // TOP BAR + STAGE NUMBER
            // ------------------------------------------------

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
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(
                        horizontal = 4.dp
                    )
                ) {

                    Text(
                        text = "←  DETECTION",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }

                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .background(
                            Color.White.copy(alpha = 0.035f),
                            CircleShape
                        )
                        .border(
                            1.dp,
                            cyan.copy(alpha = 0.35f),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {

                    Text(
                        text = "05",
                        color = cyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // ------------------------------------------------
            // WORKFLOW PROGRESS
            // ------------------------------------------------

            WorkflowProgress(
                currentStage = 5
            )

            Spacer(modifier = Modifier.height(22.dp))

            Text(
                text = "ANALYSIS RESULTS",
                color = white,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.8.sp
            )

            Spacer(modifier = Modifier.height(5.dp))

            Text(
                text = "${session.sampleId}  •  ${session.source}",
                color = mutedText,
                fontSize = 13.sp
            )

            Spacer(modifier = Modifier.height(18.dp))

            // ------------------------------------------------
            // RESULT STATUS
            // ------------------------------------------------

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Color(0xFF06291F),
                        RoundedCornerShape(16.dp)
                    )
                    .border(
                        1.dp,
                        teal.copy(alpha = 0.3f),
                        RoundedCornerShape(16.dp)
                    )
                    .padding(
                        horizontal = 16.dp,
                        vertical = 13.dp
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .background(
                            teal,
                            CircleShape
                        )
                )

                Spacer(modifier = Modifier.width(10.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text(
                        text = "DETECTION COMPLETE",
                        color = teal,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )

                    Text(
                        text = "Analysis successfully completed",
                        color = mutedText,
                        fontSize = 10.sp
                    )
                }

                Text(
                    text = "AI DETECTION",
                    color = cyan,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(15.dp))

            // ------------------------------------------------
            // IMAGE RESULT AREA
            // ------------------------------------------------

            var imageBitmap by remember {
                mutableStateOf<android.graphics.Bitmap?>(null)
            }

            LaunchedEffect(imageUri) {

                if (imageUri != null) {

                    imageBitmap = withContext(Dispatchers.IO) {

                        context.contentResolver
                            .openInputStream(imageUri)
                            ?.use { inputStream ->
                                BitmapFactory.decodeStream(inputStream)
                            }
                    }
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(245.dp)
                    .background(
                        Color(0xFF020A12),
                        RoundedCornerShape(20.dp)
                    )
                    .border(
                        1.dp,
                        cyan.copy(alpha = 0.2f),
                        RoundedCornerShape(20.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {

                imageBitmap?.let { bitmap ->

                    // ---------------------------------------------
                    // ACTUAL CAPTURED IMAGE
                    // ---------------------------------------------

                    androidx.compose.foundation.Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = "Analyzed microscopic image",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit
                    )

                    // ---------------------------------------------
                    // DETECTION BOXES
                    // ---------------------------------------------

                    Canvas(
                        modifier = Modifier.fillMaxSize()
                    ) {

                        val originalWidth =
                            bitmap.width.toFloat()

                        val originalHeight =
                            bitmap.height.toFloat()

                        // YOLO input was 640 x 640
                        val originalXScale =
                            originalWidth / 640f

                        val originalYScale =
                            originalHeight / 640f

                        // Match ContentScale.Fit
                        val displayScale =
                            minOf(
                                size.width / originalWidth,
                                size.height / originalHeight
                            )

                        val displayedWidth =
                            originalWidth * displayScale

                        val displayedHeight =
                            originalHeight * displayScale

                        val offsetX =
                            (size.width - displayedWidth) / 2f

                        val offsetY =
                            (size.height - displayedHeight) / 2f

                        detectionResult.detections.forEach { detection ->

                            val originalX1 =
                                detection.x1 * originalXScale

                            val originalY1 =
                                detection.y1 * originalYScale

                            val originalX2 =
                                detection.x2 * originalXScale

                            val originalY2 =
                                detection.y2 * originalYScale

                            val left =
                                offsetX +
                                        originalX1 * displayScale

                            val top =
                                offsetY +
                                        originalY1 * displayScale

                            val right =
                                offsetX +
                                        originalX2 * displayScale

                            val bottom =
                                offsetY +
                                        originalY2 * displayScale

                            drawRect(
                                color = teal,
                                topLeft = Offset(
                                    left,
                                    top
                                ),
                                size = Size(
                                    right - left,
                                    bottom - top
                                ),
                                style = androidx.compose.ui.graphics.drawscope.Stroke(
                                    width = 2.dp.toPx()
                                )
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(15.dp))

            // ------------------------------------------------
            // MAIN METRICS
            // ------------------------------------------------

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {

                // OBJECT COUNT

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .background(
                            cardColor,
                            RoundedCornerShape(17.dp)
                        )
                        .border(
                            1.dp,
                            cyan.copy(alpha = 0.18f),
                            RoundedCornerShape(17.dp)
                        )
                        .padding(16.dp)
                ) {

                    Text(
                        text = "DETECTED",
                        color = mutedText,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.2.sp
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = detectionResult.objectCount.toString(),
                        color = cyan,
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "OBJECTS",
                        color = mutedText,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // CONFIDENCE

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .background(
                            Color(0xFF06291F),
                            RoundedCornerShape(17.dp)
                        )
                        .border(
                            1.dp,
                            teal.copy(alpha = 0.25f),
                            RoundedCornerShape(17.dp)
                        )
                        .padding(16.dp),
                    horizontalAlignment = Alignment.End
                ) {

                    Text(
                        text = "CONFIDENCE",
                        color = mutedText,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.2.sp
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = String.format(
                            "%.1f%%",
                            detectionResult.averageConfidence
                        ),
                        color = teal,
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "AVERAGE",
                        color = mutedText,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(15.dp))

            // ------------------------------------------------
            // MATERIAL BREAKDOWN
            // ------------------------------------------------

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        cardColor,
                        RoundedCornerShape(17.dp)
                    )
                    .border(
                        1.dp,
                        teal.copy(alpha = 0.18f),
                        RoundedCornerShape(17.dp)
                    )
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

                val materials = listOf(
                    "ABS",
                    "Nylon",
                    "PE",
                    "PET",
                    "PS",
                    "PVC"
                )

                materials.forEach { material ->

                    val count = materialCounts[material] ?: 0

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

                        Spacer(
                            modifier = Modifier.width(10.dp)
                        )

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
                            fontWeight = if (count > 0) {
                                FontWeight.Bold
                            } else {
                                FontWeight.Normal
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(15.dp))

            // ------------------------------------------------
            // SUMMARY CARD
            // ------------------------------------------------

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        cardColor,
                        RoundedCornerShape(17.dp)
                    )
                    .padding(16.dp)
            ) {

                Text(
                    text = "ANALYSIS SUMMARY",
                    color = mutedText,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                SummaryRow(
                    label = "Sample",
                    value = session.sampleId
                )

                SummaryRow(
                    label = "Source",
                    value = session.source
                )

                SummaryRow(
                    label = "Volume",
                    value = "${session.volumeMl} mL"
                )

                SummaryRow(
                    label = "Detection",
                    value = if (detectionResult.objectCount > 0) {
                        "Candidates detected"
                    } else {
                        "No candidates detected"
                    }
                )
            }

            Spacer(modifier = Modifier.height(15.dp))

            // ------------------------------------------------
            // SAVE REPORT BUTTON
            // ------------------------------------------------

            Button(
                onClick = onSaveReport,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(57.dp)
                    .border(
                        width = 1.dp,
                        color = cyan.copy(alpha = 0.45f),
                        shape = RoundedCornerShape(16.dp)
                    ),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    contentColor = cyan
                )
            ) {

                Text(
                    text = "SAVE ANALYSIS REPORT",
                    color = cyan,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // ------------------------------------------------
            // CONTINUE BUTTON
            // ------------------------------------------------

            Button(
                onClick = onContinue,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(57.dp)
                    .shadow(
                        elevation = 12.dp,
                        shape = RoundedCornerShape(16.dp),
                        ambientColor = teal,
                        spotColor = teal
                    ),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = teal
                )
            ) {

                Text(
                    text = "CONTINUE ANALYSIS   →",
                    color = Color.Black,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Results can be reviewed before continuing.",
                color = mutedText.copy(alpha = 0.7f),
                fontSize = 9.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

// ============================================================
// RESULT MARKER
// ============================================================

@Composable
fun ResultMarker() {

    val teal = Color(0xFF00EFA3)

    Box(
        modifier = Modifier
            .size(27.dp)
            .border(
                width = 1.dp,
                color = teal.copy(alpha = 0.75f),
                shape = RoundedCornerShape(5.dp)
            ),
        contentAlignment = Alignment.Center
    ) {

        Box(
            modifier = Modifier
                .size(5.dp)
                .background(
                    teal,
                    CircleShape
                )
        )
    }
}

// ============================================================
// SUMMARY ROW
// ============================================================

@Composable
fun SummaryRow(
    label: String,
    value: String
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        Text(
            text = label,
            color = Color(0xFF8EA6B8),
            fontSize = 10.sp
        )

        Text(
            text = value,
            color = Color.White,
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun WorkflowProgress(
    currentStage: Int
) {

    val cyan = Color(0xFF00D9FF)
    val teal = Color(0xFF00EFA3)
    val muted = Color(0xFF284354)

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {

        for (stage in 1..8) {

            // Stage dot
            Box(
                modifier = Modifier
                    .size(
                        if (stage == currentStage) {
                            12.dp
                        } else {
                            8.dp
                        }
                    )
                    .then(
                        if (stage == currentStage) {
                            Modifier.shadow(
                                elevation = 8.dp,
                                shape = CircleShape,
                                ambientColor = cyan,
                                spotColor = cyan
                            )
                        } else {
                            Modifier
                        }
                    )
                    .background(
                        color = when {
                            stage < currentStage -> teal
                            stage == currentStage -> cyan
                            else -> muted
                        },
                        shape = CircleShape
                    )
            )

            // Connecting line
            if (stage < 8) {

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(3.dp)
                        .background(
                            color = if (stage < currentStage) {
                                teal
                            } else {
                                muted
                            },
                            shape = RoundedCornerShape(50)
                        )
                )
            }
        }
    }
}