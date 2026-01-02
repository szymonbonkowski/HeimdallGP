package io.github.szymonbonkowski.heimdallgp.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.szymonbonkowski.heimdallgp.ui.components.*
import io.github.szymonbonkowski.heimdallgp.ui.theme.HeimdallColors

@Composable
fun DashboardScreen() {
    val infiniteTransition = rememberInfiniteTransition()
    val carProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(12000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    val rpm by infiniteTransition.animateFloat(
        initialValue = 10000f,
        targetValue = 12500f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    val speed = (rpm * 0.025).toInt()

    Scaffold(
        containerColor = HeimdallColors.Background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                TrackMap(
                    carProgress = carProgress
                )

                TeamRadioPopup(
                    modifier = Modifier.align(androidx.compose.ui.Alignment.BottomStart),
                    transcript = "Box box, box box. Stay out."
                )
            }

            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                DriverHeader()

                TelemetryPanel(
                    speed = speed,
                    gear = 8,
                    rpm = rpm.toInt(),
                    throttle = 0.85f,
                    brake = 0.0f
                )
            }
        }
    }
}
