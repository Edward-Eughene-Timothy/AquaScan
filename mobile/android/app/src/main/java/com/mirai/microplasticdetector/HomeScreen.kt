package com.mirai.microplasticdetector

import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.layout.ContentScale
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
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
import com.mirai.microplasticdetector.ui.theme.Aqua
import com.mirai.microplasticdetector.ui.theme.DeepBlue
import com.mirai.microplasticdetector.ui.theme.LightAqua
import com.mirai.microplasticdetector.ui.theme.Navy
import com.mirai.microplasticdetector.ui.theme.SuccessGreen
import androidx.compose.ui.tooling.preview.Preview
import com.mirai.microplasticdetector.ui.theme.MicroplasticDetectorTheme

@Composable
fun HomeScreen(
    onBeginAnalysis: () -> Unit,
    onShowReport: () -> Unit
) {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF031827),
                        Navy,
                        Color(0xFF05283D)
                    )
                )
            )
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(32.dp))

            // -----------------------------------------
            // TOP STATUS
            // -----------------------------------------

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {

                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(
                            Color.White.copy(alpha = 0.04f),
                            CircleShape
                        )
                        .border(
                            1.dp,
                            Aqua.copy(alpha = 0.45f),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "⚙",
                        fontSize = 21.sp,
                        color = Aqua
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // -----------------------------------------
            // LOGO
            // -----------------------------------------

            Image(
                painter = painterResource(
                    id = R.drawable.microplastic_logo
                ),
                contentDescription = "Microplastic Detection logo",
                modifier = Modifier.size(120.dp),
                contentScale = ContentScale.Fit
            )

            // -----------------------------------------
            // TITLE
            // -----------------------------------------

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = "Aqua",
                    color = Color.White,
                    fontSize = 39.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Scan",
                    color = Aqua,
                    fontSize = 39.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Text(
                text = "MICROPLASTIC DETECTION",
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 4.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "AI-POWERED WATER ANALYSIS",
                color = Aqua,
                fontSize = 11.sp,
                letterSpacing = 2.sp
            )

            Spacer(modifier = Modifier.height(18.dp))

            Box(
                modifier = Modifier
                    .size(width = 55.dp, height = 3.dp)
                    .background(
                        Aqua,
                        RoundedCornerShape(50)
                    )
            )

            Spacer(modifier = Modifier.height(15.dp))

            Text(
                text = "Small particles.\nA bigger tomorrow.",
                color = LightAqua,
                fontSize = 18.sp,
                lineHeight = 27.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(28.dp))

            // -----------------------------------------
            // BEGIN ANALYSIS
            // -----------------------------------------

            Button(
                onClick = onBeginAnalysis,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(61.dp)
                    .shadow(
                        elevation = 15.dp,
                        shape = RoundedCornerShape(18.dp),
                        ambientColor = Aqua,
                        spotColor = Aqua
                    ),
                shape = RoundedCornerShape(18.dp),
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
                                    Color(0xFF0798FF),
                                    Aqua,
                                    Color(0xFF20D98A)
                                )
                            ),
                            RoundedCornerShape(18.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {

                    Text(
                        text = "▷   BEGIN ANALYSIS   →",
                        color = Color.White,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(13.dp))

            // -----------------------------------------
            // REPORTS
            // -----------------------------------------

            OutlinedButton(
                onClick = onShowReport,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp),
                shape = RoundedCornerShape(18.dp),
                colors = androidx.compose.material3.ButtonDefaults.outlinedButtonColors(
                    contentColor = Color.White
                ),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    Aqua.copy(alpha = 0.75f)
                )
            ) {

                Text(
                    text = "\uD83D\uDCCA   VIEW REPORTS   →",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }

            Spacer(modifier = Modifier.height(13.dp))

            // -----------------------------------------
            // SYSTEM STATUS
            // -----------------------------------------

            OutlinedButton(
                onClick = {
                    // System status will be added later
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp),
                shape = RoundedCornerShape(18.dp),
                colors = androidx.compose.material3.ButtonDefaults.outlinedButtonColors(
                    contentColor = Color.White
                ),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    Aqua.copy(alpha = 0.75f)
                )
            ) {

                Text(
                    text = "⚙   SYSTEM STATUS   →",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // -----------------------------------------
            // SYSTEM READY
            // -----------------------------------------

            Box(
                modifier = Modifier
                    .border(
                        1.dp,
                        SuccessGreen.copy(alpha = 0.55f),
                        RoundedCornerShape(50)
                    )
                    .background(
                        SuccessGreen.copy(alpha = 0.05f),
                        RoundedCornerShape(50)
                    )
                    .padding(
                        horizontal = 20.dp,
                        vertical = 9.dp
                    )
            ) {

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(
                                SuccessGreen,
                                CircleShape
                            )
                    )

                    Spacer(modifier = Modifier.size(9.dp))

                    Text(
                        text = "SYSTEM READY",
                        color = SuccessGreen,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "CLEANER WATERS   •   BRIGHTER FUTURES",
                color = LightAqua.copy(alpha = 0.7f),
                fontSize = 9.sp,
                letterSpacing = 1.5.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(18.dp))
        }
    }
}
@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    MicroplasticDetectorTheme {
        HomeScreen(
            onBeginAnalysis = {},
            onShowReport = {}
        )
    }
}