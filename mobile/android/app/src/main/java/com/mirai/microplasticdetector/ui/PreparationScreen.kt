package com.mirai.microplasticdetector.ui

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
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mirai.microplasticdetector.models.AnalysisSession

@Composable
fun PreparationScreen(
    session: AnalysisSession,
    onBack: () -> Unit,
    onContinue: () -> Unit
) {

    // ------------------------------------------------
    // COLOURS
    // ------------------------------------------------

    val backgroundTop = Color(0xFF031827)
    val backgroundBottom = Color(0xFF051F31)

    val cardColor = Color(0xFF0A1B2B)

    val cyan = Color(0xFF00D9FF)
    val teal = Color(0xFF00EFA3)

    val white = Color.White
    val mutedText = Color(0xFF8EA6B8)

    // ------------------------------------------------
    // CHECKLIST STATE
    // ------------------------------------------------

    var membranePrepared by remember {
        mutableStateOf(false)
    }

    var containerCleaned by remember {
        mutableStateOf(false)
    }

    var sampleTransferred by remember {
        mutableStateOf(false)
    }

    var membranePositioned by remember {
        mutableStateOf(false)
    }

    val completedSteps =
        listOf(
            membranePrepared,
            containerCleaned,
            sampleTransferred,
            membranePositioned
        ).count { it }

    val preparationComplete =
        completedSteps == 4

    // ------------------------------------------------
    // BACKGROUND
    // ------------------------------------------------

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
            // TOP BAR
            // ------------------------------------------------

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
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(
                        horizontal = 4.dp
                    )
                ) {

                    Text(
                        text = "←  SAMPLING",
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
                            teal.copy(alpha = 0.4f),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {

                    Text(
                        text = "02",
                        color = teal,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // ------------------------------------------------
            // WORKFLOW INDICATOR
            // ------------------------------------------------

            WorkflowProgress(
                currentStage = 2
            )

            Spacer(modifier = Modifier.height(22.dp))

            // ------------------------------------------------
            // HEADER
            // ------------------------------------------------

            Text(
                text = "ANALYSIS WORKFLOW",
                color = cyan,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )

            Spacer(modifier = Modifier.height(7.dp))

            Text(
                text = "SAMPLE PREPARATION",
                color = white,
                fontSize = 27.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.8.sp
            )

            Spacer(modifier = Modifier.height(7.dp))

            Text(
                text = "Prepare the collected sample before\nmicroscopic image acquisition.",
                color = mutedText,
                fontSize = 13.sp,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(23.dp))

            // ------------------------------------------------
            // CURRENT SAMPLE CARD
            // ------------------------------------------------

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        cardColor,
                        RoundedCornerShape(20.dp)
                    )
                    .border(
                        1.dp,
                        cyan.copy(alpha = 0.18f),
                        RoundedCornerShape(20.dp)
                    )
                    .padding(18.dp)
            ) {

                Text(
                    text = "CURRENT SAMPLE",
                    color = mutedText,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(
                                teal,
                                CircleShape
                            )
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    Text(
                        text = session.sampleId,
                        color = cyan,
                        fontSize = 21.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(5.dp))

                Text(
                    text = "${session.source}  •  ${session.volumeMl} mL",
                    color = white.copy(alpha = 0.8f),
                    fontSize = 13.sp
                )
            }

            Spacer(modifier = Modifier.height(23.dp))

            // ------------------------------------------------
            // CHECKLIST HEADER
            // ------------------------------------------------

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                Text(
                    text = "PREPARATION CHECKLIST",
                    color = mutedText,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp
                )

                Text(
                    text = "$completedSteps / 4",
                    color = if (preparationComplete) {
                        teal
                    } else {
                        cyan
                    },
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // ------------------------------------------------
            // CHECKLIST
            // ------------------------------------------------

            PreparationCheckItem(
                number = "01",
                text = "Filter membrane prepared",
                checked = membranePrepared,
                onCheckedChange = {
                    membranePrepared = it
                }
            )

            PreparationCheckItem(
                number = "02",
                text = "Sample container cleaned",
                checked = containerCleaned,
                onCheckedChange = {
                    containerCleaned = it
                }
            )

            PreparationCheckItem(
                number = "03",
                text = "Sample transferred",
                checked = sampleTransferred,
                onCheckedChange = {
                    sampleTransferred = it
                }
            )

            PreparationCheckItem(
                number = "04",
                text = "Membrane positioned",
                checked = membranePositioned,
                onCheckedChange = {
                    membranePositioned = it
                }
            )

            Spacer(modifier = Modifier.height(22.dp))

            // ------------------------------------------------
            // STATUS
            // ------------------------------------------------

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        if (preparationComplete) {
                            Color(0xFF06291F)
                        } else {
                            Color.White.copy(alpha = 0.025f)
                        },
                        RoundedCornerShape(16.dp)
                    )
                    .border(
                        1.dp,
                        if (preparationComplete) {
                            teal.copy(alpha = 0.45f)
                        } else {
                            Color.White.copy(alpha = 0.08f)
                        },
                        RoundedCornerShape(16.dp)
                    )
                    .padding(15.dp)
            ) {

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(
                                if (preparationComplete) {
                                    teal
                                } else {
                                    cyan.copy(alpha = 0.5f)
                                },
                                CircleShape
                            )
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {

                        Text(
                            text = if (preparationComplete) {
                                "PREPARATION COMPLETE"
                            } else {
                                "PREPARATION IN PROGRESS"
                            },
                            color = if (preparationComplete) {
                                teal
                            } else {
                                white
                            },
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )

                        Spacer(modifier = Modifier.height(3.dp))

                        Text(
                            text = if (preparationComplete) {
                                "Sample ready for image capture."
                            } else {
                                "$completedSteps of 4 preparation steps completed."
                            },
                            color = mutedText,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // ------------------------------------------------
            // CONTINUE
            // ------------------------------------------------

            Button(
                onClick = onContinue,
                enabled = preparationComplete,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(59.dp)
                    .then(
                        if (preparationComplete) {
                            Modifier.shadow(
                                elevation = 14.dp,
                                shape = RoundedCornerShape(17.dp),
                                ambientColor = teal,
                                spotColor = teal
                            )
                        } else {
                            Modifier
                        }
                    ),
                shape = RoundedCornerShape(17.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = teal,
                    disabledContainerColor = Color(0xFF172730),
                    disabledContentColor = Color(0xFF53636D)
                )
            ) {

                Text(
                    text = if (preparationComplete) {
                        "CONTINUE TO IMAGE CAPTURE   →"
                    } else {
                        "COMPLETE ALL PREPARATION STEPS"
                    },
                    color = if (preparationComplete) {
                        Color.Black
                    } else {
                        Color(0xFF53636D)
                    },
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.7.sp,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // ------------------------------------------------
            // FOOTER
            // ------------------------------------------------

            Text(
                text = "AQUASCAN  •  SAMPLE PREPARATION",
                modifier = Modifier.fillMaxWidth(),
                color = mutedText.copy(alpha = 0.55f),
                fontSize = 8.sp,
                letterSpacing = 1.5.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(15.dp))
        }
    }
}


// ============================================================
// CHECKLIST ITEM
// ============================================================

@Composable
fun PreparationCheckItem(
    number: String,
    text: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {

    val cardColor = Color(0xFF0A1B2B)
    val teal = Color(0xFF00EFA3)
    val cyan = Color(0xFF00D9FF)
    val white = Color.White
    val mutedText = Color(0xFF8EA6B8)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .background(
                if (checked) {
                    Color(0xFF06291F)
                } else {
                    cardColor
                },
                RoundedCornerShape(15.dp)
            )
            .border(
                1.dp,
                if (checked) {
                    teal.copy(alpha = 0.35f)
                } else {
                    Color.White.copy(alpha = 0.06f)
                },
                RoundedCornerShape(15.dp)
            )
            .padding(
                horizontal = 10.dp,
                vertical = 7.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {

        // Step number

        Box(
            modifier = Modifier
                .size(34.dp)
                .background(
                    if (checked) {
                        teal.copy(alpha = 0.12f)
                    } else {
                        cyan.copy(alpha = 0.06f)
                    },
                    CircleShape
                )
                .border(
                    1.dp,
                    if (checked) {
                        teal.copy(alpha = 0.45f)
                    } else {
                        cyan.copy(alpha = 0.2f)
                    },
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {

            Text(
                text = if (checked) "✓" else number,
                color = if (checked) teal else cyan,
                fontSize = if (checked) 17.sp else 9.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = text,
            modifier = Modifier.weight(1f),
            color = if (checked) {
                teal
            } else {
                white
            },
            fontSize = 13.sp,
            fontWeight = if (checked) {
                FontWeight.SemiBold
            } else {
                FontWeight.Normal
            }
        )

        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}