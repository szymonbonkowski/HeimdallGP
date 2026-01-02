package io.github.szymonbonkowski.heimdallgp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.szymonbonkowski.heimdallgp.ui.theme.HeimdallColors

@Composable
fun DriverHeader(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(HeimdallColors.Surface)
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 24.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(48.dp)
                    .background(HeimdallColors.DriverBadge, CircleShape)
            ) {
                Text(
                    text = "1",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = "Max Verstappen",
                    color = HeimdallColors.TextPrimary,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Red Bull Racing",
                    color = HeimdallColors.TextSecondary,
                    fontSize = 14.sp
                )
            }
        }

        HorizontalDivider(color = Color.DarkGray, thickness = 0.5.dp)
        Spacer(modifier = Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.weight(1f)) {
                StatItem(label = "Position", value = "P1")
                Spacer(modifier = Modifier.height(16.dp))
                StatItem(
                    label = "Tire",
                    value = "Medium",
                    dotColor = HeimdallColors.TireMedium
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                StatItem(label = "Last Lap", value = "1:12.462")
                Spacer(modifier = Modifier.height(16.dp))
                StatItem(label = "Best Lap", value = "1:12.909")
            }
        }
    }
}

@Composable
private fun StatItem(label: String, value: String, dotColor: Color? = null) {
    Column {
        Text(
            text = label,
            color = HeimdallColors.TextSecondary,
            fontSize = 12.sp
        )
        Spacer(modifier = Modifier.height(4.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (dotColor != null) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(dotColor, CircleShape)
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = value,
                color = HeimdallColors.TextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
