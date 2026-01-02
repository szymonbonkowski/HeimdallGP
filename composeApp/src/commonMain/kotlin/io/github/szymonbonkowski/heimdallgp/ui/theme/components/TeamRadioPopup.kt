package io.github.szymonbonkowski.heimdallgp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.szymonbonkowski.heimdallgp.ui.theme.HeimdallColors

@Composable
fun TeamRadioPopup(
    modifier: Modifier = Modifier,
    transcript: String
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(Color.Transparent)
    ) {
        Text(
            text = "Latest Transcript:",
            color = HeimdallColors.TextSecondary,
            fontSize = 12.sp
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = transcript,
            color = HeimdallColors.TireMedium,
            fontSize = 16.sp,
            fontStyle = FontStyle.Italic,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier
                .border(1.dp, HeimdallColors.RedBullBlue, RoundedCornerShape(4.dp))
                .padding(horizontal = 12.dp, vertical = 6.dp)
                .clickable { /* TODO: Play audio */ },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = "Play",
                tint = HeimdallColors.RedBullBlue,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "Replay audio",
                color = HeimdallColors.RedBullBlue,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
