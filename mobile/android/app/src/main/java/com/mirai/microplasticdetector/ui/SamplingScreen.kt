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
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mirai.microplasticdetector.models.AnalysisSession

@Composable
fun SamplingScreen(
    onBack: () -> Unit,
    onContinue: (AnalysisSession) -> Unit
) {

    // ------------------------------------------------
    // COLOURS
    // ------------------------------------------------

    val backgroundTop = Color(0xFF031827)
    val backgroundBottom = Color(0xFF051F31)

    val cardColor = Color(0xFF0A1B2B)
    val fieldColor = Color(0xFF0D2438)

    val cyan = Color(0xFF00D9FF)
    val brightCyan = Color(0xFF18CFFF)
    val teal = Color(0xFF00EFA3)
    val white = Color.White
    val mutedText = Color(0xFF8EA6B8)

    // ------------------------------------------------
    // STATE
    // ------------------------------------------------

    var source by remember {
        mutableStateOf("")
    }

    var volume by remember {
        mutableStateOf("500")
    }

    var notes by remember {
        mutableStateOf("")
    }

    var sampleCollected by remember {
        mutableStateOf(false)
    }

    var sourceMenuExpanded by remember {
        mutableStateOf(false)
    }

    val sources = listOf(
        "Pond",
        "Lake",
        "River",
        "Tap Water",
        "Other"
    )

    // ------------------------------------------------
    // MAIN BACKGROUND
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
                .padding(horizontal = 22.dp, vertical = 18.dp)
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
                        text = "←  BACK",
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
                        text = "01",
                        color = cyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // ------------------------------------------------
            // WORKFLOW PROGRESS
            // ------------------------------------------------

            WorkflowProgress(
                currentStage = 1
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
                text = "SAMPLE COLLECTION",
                color = white,
                fontSize = 27.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.8.sp
            )

            Spacer(modifier = Modifier.height(7.dp))

            Text(
                text = "Collect and record the water sample\nbefore beginning laboratory analysis.",
                color = mutedText,
                fontSize = 13.sp,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(25.dp))

            // ------------------------------------------------
            // SAMPLE INFORMATION CARD
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
                    .padding(20.dp)
            ) {

                // SAMPLE ID

                Text(
                    text = "SAMPLE ID",
                    color = mutedText,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp
                )

                Spacer(modifier = Modifier.height(7.dp))

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
                        text = "MP-001",
                        color = cyan,
                        fontSize = 21.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // SOURCE

                Text(
                    text = "SAMPLE SOURCE",
                    color = mutedText,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp
                )

                Spacer(modifier = Modifier.height(7.dp))

                Box(
                    modifier = Modifier.fillMaxWidth()
                ) {

                    Button(
                        onClick = {
                            sourceMenuExpanded = true
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = fieldColor,
                            contentColor = white
                        ),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(
                            horizontal = 16.dp
                        )
                    ) {

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            Text(
                                text = if (source.isEmpty()) {
                                    "Select sample source"
                                } else {
                                    source
                                },
                                color = if (source.isEmpty()) {
                                    mutedText
                                } else {
                                    white
                                },
                                fontSize = 14.sp
                            )

                            Text(
                                text = "⌄",
                                color = cyan,
                                fontSize = 20.sp
                            )
                        }
                    }

                    DropdownMenu(
                        expanded = sourceMenuExpanded,
                        onDismissRequest = {
                            sourceMenuExpanded = false
                        }
                    ) {

                        sources.forEach { item ->

                            DropdownMenuItem(
                                text = {
                                    Text(item)
                                },
                                onClick = {
                                    source = item
                                    sourceMenuExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // VOLUME

                Text(
                    text = "SAMPLE VOLUME",
                    color = mutedText,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp
                )

                Spacer(modifier = Modifier.height(7.dp))

                OutlinedTextField(
                    value = volume,
                    onValueChange = {
                        if (it.all { character ->
                                character.isDigit()
                            }) {
                            volume = it
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    placeholder = {
                        Text(
                            text = "Enter volume",
                            color = mutedText
                        )
                    },
                    suffix = {
                        Text(
                            text = "mL",
                            color = cyan,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(20.dp))

                // NOTES

                Text(
                    text = "COLLECTION NOTES",
                    color = mutedText,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp
                )

                Spacer(modifier = Modifier.height(7.dp))

                OutlinedTextField(
                    value = notes,
                    onValueChange = {
                        notes = it
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(105.dp),
                    placeholder = {
                        Text(
                            text = "Optional observations...",
                            color = mutedText
                        )
                    },
                    maxLines = 4,
                    shape = RoundedCornerShape(12.dp)
                )
            }

            Spacer(modifier = Modifier.height(22.dp))

            // ------------------------------------------------
            // COLLECTION STATUS
            // ------------------------------------------------

            if (!sampleCollected) {

                Button(
                    onClick = {
                        sampleCollected = true
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(59.dp)
                        .shadow(
                            elevation = 14.dp,
                            shape = RoundedCornerShape(17.dp),
                            ambientColor = cyan,
                            spotColor = cyan
                        ),
                    shape = RoundedCornerShape(17.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent
                    ),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues()
                ) {

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(
                                        Color(0xFF079EFF),
                                        cyan,
                                        teal
                                    )
                                ),
                                RoundedCornerShape(17.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {

                        Text(
                            text = "✓   MARK SAMPLE COLLECTED",
                            color = Color.Black,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.8.sp
                        )
                    }
                }

            } else {

                // ------------------------------------------------
                // COMPLETED STATE
                // ------------------------------------------------

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Color(0xFF06291F),
                            RoundedCornerShape(17.dp)
                        )
                        .border(
                            1.dp,
                            teal.copy(alpha = 0.45f),
                            RoundedCornerShape(17.dp)
                        )
                        .padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .background(
                                teal.copy(alpha = 0.12f),
                                CircleShape
                            )
                            .border(
                                1.dp,
                                teal.copy(alpha = 0.5f),
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {

                        Text(
                            text = "✓",
                            color = teal,
                            fontSize = 23.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "SAMPLING COMPLETE",
                        color = teal,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.2.sp
                    )

                    Spacer(modifier = Modifier.height(7.dp))

                    Text(
                        text = "$volume mL  •  ${
                            if (source.isEmpty()) {
                                "Source not specified"
                            } else {
                                source
                            }
                        }",
                        color = mutedText,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(13.dp))

                // ------------------------------------------------
                // CONTINUE
                // ------------------------------------------------

                Button(
                    onClick = {

                        val session = AnalysisSession(
                            sampleId = "MP-001",
                            source = if (source.isEmpty()) {
                                "Not specified"
                            } else {
                                source
                            },
                            volumeMl = volume.toIntOrNull() ?: 0,
                            notes = notes
                        )

                        onContinue(session)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(59.dp)
                        .shadow(
                            elevation = 14.dp,
                            shape = RoundedCornerShape(17.dp),
                            ambientColor = teal,
                            spotColor = teal
                        ),
                    shape = RoundedCornerShape(17.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = teal
                    )
                ) {

                    Text(
                        text = "CONTINUE TO PREPARATION   →",
                        color = Color.Black,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // ------------------------------------------------
                // REPEAT
                // ------------------------------------------------

                Button(
                    onClick = {
                        sampleCollected = false
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        contentColor = mutedText
                    )
                ) {

                    Text(
                        text = "↻   REPEAT SAMPLE",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 1.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // ------------------------------------------------
            // FOOTER
            // ------------------------------------------------

            Text(
                text = "AQUASCAN  •  SAMPLE ANALYSIS SYSTEM",
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

@Preview(showBackground = true)
@Composable
fun SamplingScreenPreview() {

    SamplingScreen(
        onBack = {},
        onContinue = {}
    )
}