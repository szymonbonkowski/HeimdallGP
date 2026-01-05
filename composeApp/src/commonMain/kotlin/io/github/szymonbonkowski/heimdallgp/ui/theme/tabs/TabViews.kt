package io.github.szymonbonkowski.heimdallgp.ui.tabs

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.szymonbonkowski.heimdallgp.model.Driver
import io.github.szymonbonkowski.heimdallgp.model.TireCompound
import io.github.szymonbonkowski.heimdallgp.ui.components.TelemetryPanel
import io.github.szymonbonkowski.heimdallgp.ui.theme.HeimdallColors


@Composable
fun RaceDataTab(driver: Driver) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        TelemetryPanel(
            speed = driver.speedKmh,
            gear = driver.gear,
            rpm = driver.rpm,
            throttle = driver.throttle,
            brake = driver.brake
        )

        Spacer(modifier = Modifier.weight(1f))

        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            InfoBox("Tire Age", "12 Laps")
            InfoBox("Est. Pit", "Lap 60")
        }
    }
}

@Composable
fun InfoBox(label: String, value: String) {
    Column {
        Text(label, color = HeimdallColors.TextSecondary, fontSize = 12.sp)
        Text(value, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun LeaderboardTab(
    drivers: List<Driver>,
    selectedDriverId: Int,
    onDriverSelect: (Int) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(top = 16.dp, start = 16.dp, end = 16.dp, bottom = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Row(modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp)) {
                Text("POS", color = HeimdallColors.TextSecondary, fontSize = 10.sp, modifier = Modifier.width(40.dp).padding(start = 12.dp))
                Text("DRIVER", color = HeimdallColors.TextSecondary, fontSize = 10.sp, modifier = Modifier.weight(1f))
                Text("GAP", color = HeimdallColors.TextSecondary, fontSize = 10.sp, modifier = Modifier.width(60.dp), textAlign = TextAlign.End)
                Text("TIRE", color = HeimdallColors.TextSecondary, fontSize = 10.sp, modifier = Modifier.width(40.dp), textAlign = TextAlign.End)
            }
        }

        items(drivers.sortedBy { it.position }, key = { it.id }) { driver ->
            Box(modifier = Modifier.animateItem()) {
                LeaderboardItem(
                    driver = driver,
                    isSelected = driver.id == selectedDriverId,
                    onClick = { onDriverSelect(driver.id) }
                )
            }
        }
    }
}

@Composable
fun LeaderboardItem(driver: Driver, isSelected: Boolean, onClick: () -> Unit) {
    val bgColor = if (isSelected) Color(0xFF252525) else HeimdallColors.Surface

    val animatedBarWidth by animateDpAsState(
        targetValue = if (isSelected) 8.dp else 4.dp,
        animationSpec = tween(durationMillis = 300)
    )

    Card(
        colors = CardDefaults.cardColors(containerColor = bgColor),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(8.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(animatedBarWidth)
                        .background(driver.teamColor)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = driver.position.toString(),
                color = if (isSelected) driver.teamColor else Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.width(30.dp)
            )

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = driver.shortName,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = driver.name,
                        color = HeimdallColors.TextSecondary,
                        fontSize = 12.sp,
                        maxLines = 1
                    )
                }
                Text(
                    text = driver.team.uppercase(),
                    color = HeimdallColors.TextSecondary.copy(alpha = 0.7f),
                    fontSize = 10.sp
                )
            }

            Column(horizontalAlignment = Alignment.End, modifier = Modifier.width(80.dp)) {
                if (driver.position == 1) {
                    Text("Interval", color = Color(0xFF2196F3), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Text("LEADER", color = Color(0xFFC0FF02), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                } else {
                    Text(driver.gapToLeader, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text("interval", color = HeimdallColors.TextSecondary, fontSize = 10.sp)
                }
            }

            Spacer(modifier = Modifier.width(8.dp))
            TireIcon(driver.tireCompound)
            Spacer(modifier = Modifier.width(12.dp))
        }
    }
}

@Composable
fun TireIcon(compound: TireCompound) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(28.dp)
            .border(BorderStroke(2.dp, compound.color), CircleShape)
    ) {
        Text(
            text = compound.label.first().toString(),
            color = compound.color,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun TeamRadioTab(driver: Driver) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text("Latest Transcript:", color = HeimdallColors.TextSecondary, fontSize = 12.sp)
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Box box. Stay out.",
            color = HeimdallColors.TireMedium,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { println("Audio click") },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4C9EEB)),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth().height(40.dp)
        ) {
            Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.White)
            Spacer(Modifier.width(8.dp))
            Text("Replay Audio", color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun SettingsTab(
    isDevMode: Boolean,
    onDevModeChange: (Boolean) -> Unit
) {
    var textInput by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "App Mode Configuration",
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(8.dp))

        Text(
            text = "Current Mode: ${if (isDevMode) "DEVELOPER (Dummy Data)" else "USER (Live SignalR)"}",
            color = if (isDevMode) HeimdallColors.NeonTrack else HeimdallColors.TireSoft,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(32.dp))

        OutlinedTextField(
            value = textInput,
            onValueChange = { textInput = it },
            label = { Text("Enter command") },
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedBorderColor = HeimdallColors.RedBullBlue,
                unfocusedBorderColor = HeimdallColors.TextSecondary,
                focusedLabelColor = HeimdallColors.RedBullBlue,
                unfocusedLabelColor = HeimdallColors.TextSecondary
            ),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = {
                when (textInput.lowercase().trim()) {
                    "dev" -> {
                        onDevModeChange(true)
                        message = "Switched to Developer Mode"
                        textInput = ""
                    }
                    "user" -> {
                        onDevModeChange(false)
                        message = "Switched to User Mode"
                        textInput = ""
                    }
                    else -> {
                        message = "Unknown command. Try 'dev' or 'user'."
                    }
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = HeimdallColors.SurfaceHighlight),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Confirm", color = Color.White)
        }

        if (message.isNotEmpty()) {
            Spacer(Modifier.height(16.dp))
            Text(message, color = HeimdallColors.TextSecondary, fontSize = 14.sp)
        }
    }
}
