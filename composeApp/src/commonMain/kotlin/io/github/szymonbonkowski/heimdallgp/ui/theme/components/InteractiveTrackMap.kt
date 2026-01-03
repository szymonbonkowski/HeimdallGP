package io.github.szymonbonkowski.heimdallgp.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import io.github.szymonbonkowski.heimdallgp.model.Driver
import kotlin.math.sqrt

@Composable
fun InteractiveTrackMap(
    drivers: List<Driver>,
    selectedDriverId: Int,
    modifier: Modifier = Modifier
) {
    var scale by remember { mutableStateOf(1.2f) }
    var rotationZ by remember { mutableStateOf(0f) }
    var rotationX by remember { mutableStateOf(45f) }

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
    val pathLength = pathMeasure.length

    Box(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                awaitPointerEventScope {
                    while (true) {
                        val event = awaitPointerEvent()
                        val changes = event.changes
                        val pressed = changes.filter { it.pressed }
                        val pointerCount = pressed.size

                        if (pointerCount == 1) {
                            val change = pressed.first()
                            val dragAmount = change.positionChange()

                            rotationZ += dragAmount.x * 0.4f

                            rotationX = (rotationX + dragAmount.y * 0.4f).coerceIn(0f, 85f)

                            change.consume()

                        } else if (pointerCount == 2) {
                            val p1 = pressed[0]
                            val p2 = pressed[1]

                            val p1Pos = p1.position
                            val p2Pos = p2.position
                            val prevP1Pos = p1Pos - p1.positionChange()
                            val prevP2Pos = p2Pos - p2.positionChange()

                            val currentDist = (p1Pos - p2Pos).getDistance()
                            val prevDist = (prevP1Pos - prevP2Pos).getDistance()

                            val zoomFactor = if (prevDist > 0) currentDist / prevDist else 1f
                            scale = (scale * zoomFactor).coerceIn(0.5f, 6f)

                            changes.forEach { it.consume() }
                        }
                    }
                }
            }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                    this.rotationZ = rotationZ
                    this.rotationX = rotationX
                    cameraDistance = 16f * density

                    transformOrigin = TransformOrigin.Center
                }
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val canvasWidth = size.width
                val canvasHeight = size.height

                withTransform({
                    translate(left = canvasWidth / 2 - 500f, top = canvasHeight / 2 - 450f)
                }) {
                    drawPath(
                        path = trackPath,
                        color = Color(0xFFFF5722),
                        style = Stroke(width = 28f, cap = StrokeCap.Round, join = StrokeJoin.Round)
                    )
                    drawPath(
                        path = trackPath,
                        color = Color(0xFF000000),
                        style = Stroke(width = 16f, cap = StrokeCap.Round, join = StrokeJoin.Round)
                    )
                    drawPath(
                        path = trackPath,
                        color = Color.White.copy(alpha = 0.5f),
                        style = Stroke(width = 1f)
                    )

                    val sectors = listOf(0.15f, 0.4f, 0.65f, 0.9f)
                    sectors.forEach { prog ->
                        val pos = pathMeasure.getPosition(prog * pathLength)
                        drawCircle(Color(0xFF2196F3), radius = 8f, center = pos)
                        drawCircle(Color.Black, radius = 4f, center = pos)
                    }

                    drivers.forEach { driver ->
                        val pos = pathMeasure.getPosition(driver.progress * pathLength)
                        val isSelected = driver.id == selectedDriverId

                        if (isSelected) {
                            drawCircle(Color.White, radius = 14f, center = pos)
                        } else {
                            drawCircle(driver.teamColor, radius = 8f, center = pos)
                        }
                    }
                }
            }
        }
    }
}

private fun Offset.getDistance(): Float = sqrt(x * x + y * y)
