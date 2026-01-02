package io.github.szymonbonkowski.heimdallgp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.szymonbonkowski.heimdallgp.ui.theme.HeimdallColors

@Composable
fun TelemetryPanel(
    modifier: Modifier = Modifier,
    speed: Int,
    gear: Int,
    rpm: Int,
    throttle: Float,
    brake: Float
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(HeimdallColors.Surface)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            TelemetryValue(label = "Speed", value = "$speed km/h")
            TelemetryValue(label = "Gear", value = "$gear", isBig = true)
            TelemetryValue(label = "RPM", value = "$rpm")
        }

        Spacer(modifier = Modifier.height(12.dp))

        TelemetryBar(label = "Throttle", value = throttle, color = HeimdallColors.Throttle)
        Spacer(modifier = Modifier.height(8.dp))
        TelemetryBar(label = "Brake", value = brake, color = HeimdallColors.Brake)
    }
}

@Composable
private fun TelemetryValue(label: String, value: String, isBig: Boolean = false) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            color = HeimdallColors.TextSecondary,
            fontSize = 12.sp
        )
        Text(
            text = value,
            color = HeimdallColors.TextPrimary,
            fontSize = if (isBig) 32.sp else 20.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace
        )
    }
}

@Composable
private fun TelemetryBar(label: String, value: String, color: androidx.compose.ui.graphics.Color) {
}

@Composable
private fun TelemetryBar(label: String, value: Float, color: androidx.compose.ui.graphics.Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "$label: ${(value * 100).toInt()}%",
            color = if (value > 0) color else HeimdallColors.TextSecondary,
            fontSize = 12.sp,
            modifier = Modifier.width(80.dp)
        )

        Box(
            modifier = Modifier
                .weight(1f)
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(HeimdallColors.TextSecondary.copy(alpha = 0.2f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(value)
                    .fillMaxHeight()
                    .background(color)
            )
        }
    }
}
