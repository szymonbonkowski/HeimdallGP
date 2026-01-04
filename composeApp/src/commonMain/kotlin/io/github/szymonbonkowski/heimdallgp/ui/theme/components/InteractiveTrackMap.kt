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
import io.github.szymonbonkowski.heimdallgp.ui.theme.HeimdallColors
import kotlin.math.sqrt

@Composable
fun InteractiveTrackMap(
    drivers: List<Driver>,
    selectedDriverId: Int,
    trackPath: Path,
    modifier: Modifier = Modifier
) {
    var scale by remember { mutableStateOf(1.1f) }
    var rotationZ by remember { mutableStateOf(0f) }
    var rotationX by remember { mutableStateOf(40f) }

    val pathMeasure = remember(trackPath) { PathMeasure() }
    pathMeasure.setPath(trackPath, false)
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

                        if (pressed.size == 1) {
                            val change = pressed.first()
                            val drag = change.positionChange()
                            rotationZ += drag.x * 0.4f
                            rotationX = (rotationX + drag.y * 0.4f).coerceIn(0f, 70f)
                            change.consume()
                        } else if (pressed.size == 2) {
                            val p1 = pressed[0]
                            val p2 = pressed[1]
                            val currentDist = (p1.position - p2.position).getDistance()
                            val prevDist = ((p1.position - p1.positionChange()) - (p2.position - p2.positionChange())).getDistance()
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
                val cw = size.width
                val ch = size.height

                withTransform({
                    translate(left = cw / 2 - 500f, top = ch / 2 - 500f)
                }) {
                    drawPath(
                        path = trackPath,
                        color = HeimdallColors.NeonTrack,
                        style = Stroke(width = 36f, cap = StrokeCap.Round, join = StrokeJoin.Round)
                    )
                    drawPath(
                        path = trackPath,
                        color = Color(0xFF151515),
                        style = Stroke(width = 24f, cap = StrokeCap.Round, join = StrokeJoin.Round)
                    )

                    drivers.forEach { driver ->
                        val pos = pathMeasure.getPosition(driver.progress * pathLength)
                        val isSelected = driver.id == selectedDriverId

                        if (isSelected) {
                            drawCircle(Color.White, radius = 20f, center = pos)
                            drawCircle(driver.teamColor, radius = 8f, center = pos)
                        } else {
                            drawCircle(Color.Black, radius = 14f, center = pos)
                            drawCircle(driver.teamColor, radius = 10f, center = pos)
                        }
                    }
                }
            }
        }
    }
}

private fun Offset.getDistance() = sqrt(x * x + y * y)
