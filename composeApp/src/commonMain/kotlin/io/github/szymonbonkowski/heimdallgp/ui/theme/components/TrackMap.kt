package io.github.szymonbonkowski.heimdallgp.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import io.github.szymonbonkowski.heimdallgp.ui.theme.HeimdallColors

@Composable
fun TrackMap(
    modifier: Modifier = Modifier,
    carProgress: Float
) {
    val trackPath = remember {
        Path().apply {
            moveTo(100f, 400f)
            cubicTo(200f, 380f, 600f, 350f, 800f, 300f)
            cubicTo(950f, 250f, 900f, 450f, 750f, 500f)
            cubicTo(600f, 550f, 300f, 580f, 150f, 550f)
            cubicTo(50f, 520f, 50f, 420f, 100f, 400f)
            close()
        }
    }

    val pathMeasure = remember(trackPath) { PathMeasure() }
    pathMeasure.setPath(trackPath, false)
    val pathLength = pathMeasure.length

    Canvas(modifier = modifier.fillMaxSize()) {
        val scale = size.width / 1000f

        val offsetY = (size.height - (600f * scale)) / 2f

        with(drawContext.canvas) {
            save()
            translate(0f, offsetY)
            scale(scale, scale)

            drawPath(
                path = trackPath,
                color = HeimdallColors.NeonTrack.copy(alpha = 0.3f),
                style = Stroke(width = 20f, cap = StrokeCap.Round, join = StrokeJoin.Round)
            )
            drawPath(
                path = trackPath,
                color = HeimdallColors.NeonTrack,
                style = Stroke(width = 6f, cap = StrokeCap.Round, join = StrokeJoin.Round)
            )

            val sectors = listOf(0.1f, 0.35f, 0.6f, 0.85f)
            sectors.forEach { progress ->
                val pos = pathMeasure.getPosition(progress * pathLength)
                drawCircle(
                    color = Color.White,
                    radius = 5f,
                    center = pos
                )
            }

            val carPos = pathMeasure.getPosition(carProgress * pathLength)

            val ghostPos = pathMeasure.getPosition(((carProgress - 0.15f + 1f) % 1f) * pathLength)
            drawCircle(
                color = HeimdallColors.GhostCar,
                radius = 18f,
                center = ghostPos
            )

            drawCircle(
                color = Color.White,
                radius = 16f,
                center = carPos
            )
            drawCircle(
                color = HeimdallColors.DriverBadge,
                radius = 12f,
                center = carPos
            )

            restore()
        }
    }
}
