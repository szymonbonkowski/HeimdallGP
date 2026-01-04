package io.github.szymonbonkowski.heimdallgp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Path
import io.github.szymonbonkowski.heimdallgp.data.TrackRepository
import io.github.szymonbonkowski.heimdallgp.logic.RaceSimulator
import io.github.szymonbonkowski.heimdallgp.model.DashboardTab
import io.github.szymonbonkowski.heimdallgp.model.Driver
import io.github.szymonbonkowski.heimdallgp.ui.components.*
import io.github.szymonbonkowski.heimdallgp.ui.tabs.*
import io.github.szymonbonkowski.heimdallgp.ui.theme.HeimdallColors
import kotlinx.coroutines.launch

@Composable
fun DashboardScreen() {
    val simulator = remember { RaceSimulator() }

    LaunchedEffect(Unit) {
        simulator.startSimulation(this)
    }

    val drivers by simulator.drivers.collectAsState()
    val currentLap by simulator.currentLap.collectAsState()

    var selectedDriverId by remember { mutableStateOf(1) }
    var currentTab by remember { mutableStateOf(DashboardTab.LEADERBOARD) }

    var trackPath by remember { mutableStateOf(Path()) }
    var trackName by remember { mutableStateOf("Loading...") }

    LaunchedEffect(Unit) {
        val tracks = TrackRepository.getTrackList()

        val selectedTrack = tracks.find { it.location.contains("Monaco") } ?: tracks.firstOrNull()

        if (selectedTrack != null) {
            trackName = selectedTrack.name
            trackPath = TrackRepository.loadTrackPath(selectedTrack.id)
        } else {
            trackName = "No Track Found"
        }
    }

    val selectedDriver = drivers.find { it.id == selectedDriverId } ?: drivers.firstOrNull()

    Scaffold(
        containerColor = HeimdallColors.Background,
        bottomBar = {
            BottomNavBar(currentTab = currentTab, onTabSelected = { currentTab = it })
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(8.dp).background(Color.Red, CircleShape))
                    Spacer(Modifier.width(8.dp))
                    Text("LIVE", color = Color.White, fontWeight = FontWeight.Bold)
                }
                Text(trackName, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text("$currentLap/78", color = Color.White)
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                InteractiveTrackMap(
                    drivers = drivers,
                    selectedDriverId = selectedDriverId,
                    trackPath = trackPath
                )
            }

            if (selectedDriver != null) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .background(HeimdallColors.Surface)
                ) {
                    DriverHeaderNew(driver = selectedDriver)

                    HorizontalDivider(color = Color(0xFF333333), thickness = 1.dp)

                    Box(modifier = Modifier.fillMaxSize()) {
                        when (currentTab) {
                            DashboardTab.LEADERBOARD -> LeaderboardTab(
                                drivers = drivers,
                                selectedDriverId = selectedDriverId,
                                onDriverSelect = { newId -> selectedDriverId = newId }
                            )
                            DashboardTab.TEAM_RADIO -> TeamRadioTab(selectedDriver)
                            DashboardTab.RACE_DATA -> RaceDataTab(selectedDriver)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DriverHeaderNew(driver: Driver) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(56.dp)
                    .background(driver.teamColor, RoundedCornerShape(16.dp))
            ) {
                Text(
                    text = driver.number.toString(),
                    color = Color.White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            }
            Spacer(Modifier.width(16.dp))
            Column {
                Text(
                    text = driver.name,
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = driver.team,
                    color = HeimdallColors.TextSecondary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = "P${driver.position}",
                color = HeimdallColors.TireSoft,
                fontSize = 32.sp,
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.Monospace
            )
            Spacer(Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "${driver.speedKmh}",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Text(" km/h", color = HeimdallColors.TextSecondary, fontSize = 12.sp)
                Spacer(Modifier.width(8.dp))
                TireIcon(driver.tireCompound)
            }
        }
    }
}
