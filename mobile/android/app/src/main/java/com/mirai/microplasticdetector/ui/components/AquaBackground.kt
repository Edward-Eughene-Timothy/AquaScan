package com.mirai.microplasticdetector.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import com.mirai.microplasticdetector.ui.theme.DeepBlue
import com.mirai.microplasticdetector.ui.theme.Navy

@Composable
fun AquaBackground(
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Navy,
                        DeepBlue
                    )
                )
            )
    ) {
        content()
    }
}
