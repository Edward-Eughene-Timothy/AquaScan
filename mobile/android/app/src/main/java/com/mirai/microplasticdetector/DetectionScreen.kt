package com.mirai.microplasticdetector

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun DetectionScreen(
    session: AnalysisSession,
    detectionResult: DetectionResult?,
    isAnalyzing: Boolean,
    onBack: () -> Unit,
    onRunAnalysis: () -> Unit,
    onContinue: () -> Unit
) {

    // ---------------------------------------------------------
    // COLORS
    // ---------------------------------------------------------

    val backgroundTop = Color(0xFF031827)
    val backgroundBottom = Color(0xFF051F31)
    val cardColor = Color(0xFF0A1B2B)

    val cyan = Color(0xFF00D9FF)
    val teal = Color(0xFF00EFA3)

    val white = Color.White
    val mutedText = Color(0xFF8EA6B8)


    // ---------------------------------------------------------
    // MAIN SCREEN
    // ---------------------------------------------------------

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
                .padding(
                    horizontal = 22.dp,
                    vertical = 18.dp
                )
        ) {

            // -------------------------------------------------
            // TOP BAR
            // -------------------------------------------------

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                Button(
                    onClick = onBack,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        contentColor = mutedText
                    ),
                    contentPadding =
                        androidx.compose.foundation.layout.PaddingValues(
                            horizontal = 4.dp
                        )
                ) {

                    Text(
                        text = "←  IMAGE CAPTURE",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }


                // Stage number

                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .background(
                            Color.White.copy(alpha = 0.035f),
                            CircleShape
                        )
                        .border(
                            1.dp,
                            teal.copy(alpha = 0.45f),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {

                    Text(
                        text = "04",
                        color = teal,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }


            Spacer(
                modifier = Modifier.height(10.dp)
            )


            // -------------------------------------------------
            // WORKFLOW PROGRESS
            // -------------------------------------------------

            WorkflowProgress(
                currentStage = 4
            )


            Spacer(
                modifier = Modifier.height(22.dp)
            )


            // -------------------------------------------------
            // TITLE
            // -------------------------------------------------

            Text(
                text = "ANALYSIS ENGINE",
                color = cyan,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )


            Spacer(
                modifier = Modifier.height(6.dp)
            )


            Text(
                text = "MICROPLASTIC DETECTION",
                color = white,
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.7.sp
            )


            Spacer(
                modifier = Modifier.height(6.dp)
            )


            Text(
                text = "${session.sampleId}  •  ${session.source}",
                color = mutedText,
                fontSize = 13.sp
            )


            Spacer(
                modifier = Modifier.height(18.dp)
            )


            // -------------------------------------------------
            // YOLO ENGINE STATUS CARD
            // -------------------------------------------------

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        cardColor,
                        RoundedCornerShape(15.dp)
                    )
                    .border(
                        1.dp,
                        cyan.copy(alpha = 0.16f),
                        RoundedCornerShape(15.dp)
                    )
                    .padding(
                        horizontal = 15.dp,
                        vertical = 12.dp
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {

                // Status indicator

                Box(
                    modifier = Modifier
                        .size(9.dp)
                        .background(
                            if (detectionResult != null) {
                                teal
                            } else if (isAnalyzing) {
                                cyan
                            } else {
                                cyan
                            },
                            CircleShape
                        )
                )


                Spacer(
                    modifier = Modifier.width(10.dp)
                )


                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text(
                        text = "YOLOv11",
                        color = white,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )


                    Text(
                        text = when {
                            isAnalyzing ->
                                "Detection engine active"

                            detectionResult != null ->
                                "Detection engine completed"

                            else ->
                                "Detection engine ready"
                        },
                        color = mutedText,
                        fontSize = 10.sp
                    )
                }


                // Status text

                Text(
                    text = if (detectionResult != null) {
                        "COMPLETE"
                    } else if (isAnalyzing) {
                        "RUNNING"
                    } else {
                        "READY"
                    },
                    color = if (detectionResult != null) {
                        teal
                    } else {
                        cyan
                    },
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }


            Spacer(
                modifier = Modifier.height(15.dp)
            )


            // -------------------------------------------------
            // ANALYSIS DISPLAY AREA
            // -------------------------------------------------

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
                        if (detectionResult != null) {
                            teal.copy(alpha = 0.5f)
                        } else {
                            cyan.copy(alpha = 0.22f)
                        },
                        RoundedCornerShape(20.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {


                // =================================================
                // BEFORE ANALYSIS
                // =================================================

                if (detectionResult == null) {

                    Column(
                        horizontalAlignment =
                            Alignment.CenterHorizontally
                    ) {

                        // AI circle

                        Box(
                            modifier = Modifier
                                .size(65.dp)
                                .background(
                                    cyan.copy(alpha = 0.06f),
                                    CircleShape
                                )
                                .border(
                                    1.dp,
                                    cyan.copy(alpha = 0.3f),
                                    CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {

                            Text(
                                text = if (isAnalyzing) {
                                    "..."
                                } else {
                                    "AI"
                                },
                                color = cyan,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }


                        Spacer(
                            modifier = Modifier.height(14.dp)
                        )


                        // Main status

                        Text(
                            text = if (isAnalyzing) {
                                "ANALYZING SAMPLE"
                            } else {
                                "IMAGE READY"
                            },
                            color = if (isAnalyzing) {
                                cyan
                            } else {
                                white
                            },
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.8.sp
                        )


                        Spacer(
                            modifier = Modifier.height(7.dp)
                        )


                        // Description

                        Text(
                            text = if (isAnalyzing) {
                                "YOLOv11 is examining the captured image"
                            } else {
                                "Captured microscopic image ready"
                            },
                            color = mutedText,
                            fontSize = 11.sp,
                            textAlign = TextAlign.Center
                        )


                        // Scanning indicator

                        if (isAnalyzing) {

                            Spacer(
                                modifier = Modifier.height(14.dp)
                            )

                            Text(
                                text = "SCANNING  •••",
                                color = teal,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.5.sp
                            )
                        }
                    }
                }


                // =================================================
                // AFTER ANALYSIS
                // =================================================

                else {

                    // Temporary visual representation.
                    // Actual bounding boxes can be added next.

                    Column(
                        horizontalAlignment =
                            Alignment.CenterHorizontally
                    ) {

                        Row(
                            horizontalArrangement =
                                Arrangement.spacedBy(10.dp),
                            verticalAlignment =
                                Alignment.CenterVertically
                        ) {

                            DetectionMarker()
                            DetectionMarker()
                            DetectionMarker()
                        }


                        Spacer(
                            modifier = Modifier.height(14.dp)
                        )


                        Text(
                            text = "✓",
                            color = teal,
                            fontSize = 34.sp,
                            fontWeight = FontWeight.Bold
                        )


                        Text(
                            text = "ANALYSIS COMPLETE",
                            color = teal,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.5.sp
                        )


                        Spacer(
                            modifier = Modifier.height(5.dp)
                        )


                        Text(
                            text = if (
                                detectionResult.objectCount > 0
                            ) {
                                "Microplastic candidates identified"
                            } else {
                                "No microplastic candidates identified"
                            },
                            color = mutedText,
                            fontSize = 11.sp
                        )
                    }
                }
            }


            Spacer(
                modifier = Modifier.height(15.dp)
            )


            // -------------------------------------------------
            // DETECTION RESULTS
            // -------------------------------------------------

            if (detectionResult != null) {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement =
                        Arrangement.spacedBy(10.dp)
                ) {


                    // =============================================
                    // OBJECT COUNT
                    // =============================================

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .background(
                                cardColor,
                                RoundedCornerShape(17.dp)
                            )
                            .border(
                                1.dp,
                                cyan.copy(alpha = 0.16f),
                                RoundedCornerShape(17.dp)
                            )
                            .padding(15.dp)
                    ) {

                        Text(
                            text = "DETECTED",
                            color = mutedText,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.2.sp
                        )


                        Spacer(
                            modifier = Modifier.height(5.dp)
                        )


                        Text(
                            text =
                                detectionResult.objectCount.toString(),
                            color = cyan,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold
                        )


                        Text(
                            text = "OBJECTS",
                            color = mutedText,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }


                    // =============================================
                    // CONFIDENCE
                    // =============================================

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
                            .padding(15.dp),
                        horizontalAlignment =
                            Alignment.End
                    ) {

                        Text(
                            text = "CONFIDENCE",
                            color = mutedText,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.2.sp
                        )


                        Spacer(
                            modifier = Modifier.height(5.dp)
                        )


                        Text(
                            text =
                                String.format(
                                    "%.1f%%",
                                    detectionResult.averageConfidence
                                ),
                            color = teal,
                            fontSize = 28.sp,
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


                Spacer(
                    modifier = Modifier.height(14.dp)
                )


                // -------------------------------------------------
                // CONTINUE BUTTON
                // -------------------------------------------------

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
                        text =
                            "VIEW ANALYSIS RESULTS   →",
                        color = Color.Black,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp
                    )
                }

            }


            // -------------------------------------------------
            // RUN ANALYSIS BUTTON
            // -------------------------------------------------

            else {

                Button(
                    onClick = onRunAnalysis,
                    enabled = !isAnalyzing,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(58.dp)
                        .then(
                            if (!isAnalyzing) {

                                Modifier.shadow(
                                    elevation = 12.dp,
                                    shape =
                                        RoundedCornerShape(17.dp),
                                    ambientColor = cyan,
                                    spotColor = cyan
                                )

                            } else {

                                Modifier
                            }
                        ),
                    shape = RoundedCornerShape(17.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = cyan,
                        disabledContainerColor =
                            Color(0xFF16313B),
                        disabledContentColor =
                            mutedText
                    )
                ) {

                    Text(
                        text = if (isAnalyzing) {
                            "ANALYZING..."
                        } else {
                            "RUN YOLOv11 ANALYSIS"
                        },
                        color = if (isAnalyzing) {
                            mutedText
                        } else {
                            Color.Black
                        },
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }
            }


            Spacer(
                modifier = Modifier.height(14.dp)
            )


            // -------------------------------------------------
            // FOOTER
            // -------------------------------------------------

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement =
                    Arrangement.Center,
                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .background(
                            teal,
                            CircleShape
                        )
                )


                Spacer(
                    modifier = Modifier.width(7.dp)
                )


                Text(
                    text =
                        "YOLOv11 MICROPLASTIC DETECTION ENGINE",
                    color = mutedText.copy(alpha = 0.65f),
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }


            Spacer(
                modifier = Modifier.height(12.dp)
            )
        }
    }
}


// =============================================================
// DETECTION MARKER
// =============================================================

@Composable
fun DetectionMarker() {

    val teal = Color(0xFF00EFA3)

    Box(
        modifier = Modifier
            .size(25.dp)
            .border(
                width = 1.dp,
                color = teal.copy(alpha = 0.7f),
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