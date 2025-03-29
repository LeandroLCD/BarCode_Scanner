package com.blipblipcode.scanner.ui.component

import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.TwoWayConverter
import androidx.compose.animation.core.animateValue
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer

@Composable
fun ScannerFacade(modifier: Modifier = Modifier.fillMaxSize()){

    val animation = rememberInfiniteTransition(label = "animation")
    val showLine by animation.animateValue(
        initialValue = false,
        targetValue = true,
        typeConverter = TwoWayConverter(
            convertToVector = { if (it) AnimationVector1D(1f) else AnimationVector1D(0f) },
            convertFromVector = { it.value > 0.5f }
        ), // Convierte Boolean a Vector
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )
    val colorBorder = MaterialTheme.colorScheme.primary
    Canvas(modifier = modifier
        .graphicsLayer {
            compositingStrategy = CompositingStrategy.Offscreen
        }) {
        drawRect(color = Color.Black.copy(0.7f))
        val width = size.height * 0.3f
        val height = size.width * 0.3f
        val cord = Offset((size.width - width) / 2f, (size.height - height) / 2f)
        drawRect(
            color = Color.Transparent,
            topLeft = cord,
            size = Size(width, height),
            blendMode = BlendMode.Clear,
        )
        val path = Path().apply {
            moveTo(cord.x, cord.y)
            lineTo(cord.x, cord.y + height)
            lineTo(cord.x + width, cord.y + height)
            lineTo(cord.x + width, cord.y)
            close()
        }
        drawPath(path, colorBorder, style = Stroke(width = 5f))

        if(showLine) {
            drawLine(
                color = Color.Green,
                strokeWidth = 3f,
                start = Offset((size.width - width) / 2f, (size.height) / 2f),
                end = Offset((size.width + width) / 2f, (size.height) / 2f)
            )
        }
    }
}