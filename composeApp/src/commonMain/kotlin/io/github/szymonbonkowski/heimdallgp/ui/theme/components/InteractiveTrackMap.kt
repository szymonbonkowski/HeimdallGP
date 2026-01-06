package io.github.szymonbonkowski.heimdallgp.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import io.github.szymonbonkowski.heimdallgp.model.Driver
import io.github.szymonbonkowski.heimdallgp.ui.theme.HeimdallColors
import kotlin.math.max
import kotlin.math.min
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

    val pathBounds = remember(trackPath) { trackPath.getBounds() }

    var f1MinX by remember { mutableStateOf(Float.MAX_VALUE) }
    var f1MaxX by remember { mutableStateOf(Float.MIN_VALUE) }
    var f1MinY by remember { mutableStateOf(Float.MAX_VALUE) }
    var f1MaxY by remember { mutableStateOf(Float.MIN_VALUE) }

    LaunchedEffect(drivers) {
        var changed = false
        drivers.forEach { d ->
            if (d.x != 0.0 || d.y != 0.0) {
                if (d.x < f1MinX) { f1MinX = d.x.toFloat(); changed = true }
                if (d.x > f1MaxX) { f1MaxX = d.x.toFloat(); changed = true }
                if (d.y < f1MinY) { f1MinY = d.y.toFloat(); changed = true }
                if (d.y > f1MaxY) { f1MaxY = d.y.toFloat(); changed = true }
            }
        }
    }

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
                    translate(left = cw / 2 - pathBounds.center.x, top = ch / 2 - pathBounds.center.y)
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

                    val f1Width = f1MaxX - f1MinX
                    val f1Height = f1MaxY - f1MinY

                    if (f1Width > 100 && f1Height > 100) {


                        val scaleX = pathBounds.width / f1Width
                        val scaleY = pathBounds.height / f1Height


                        drivers.forEach { driver ->
                            if (driver.x != 0.0 || driver.y != 0.0) {

                                val mapX = (driver.x.toFloat() - f1MinX) * scaleX + pathBounds.left

                                val mapY = (driver.y.toFloat() - f1MinY) * scaleY + pathBounds.top

                                val pos = Offset(mapX, mapY)
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
    }
}

private fun Offset.getDistance() = sqrt(x * x + y * y)
