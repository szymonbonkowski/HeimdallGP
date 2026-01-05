package io.github.szymonbonkowski.heimdallgp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.szymonbonkowski.heimdallgp.data.F1DataManager
import io.github.szymonbonkowski.heimdallgp.data.F1SignalRClient
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
    var isDevMode by remember { mutableStateOf(false) }

    val simulator = remember { RaceSimulator() }
    val dataManager = remember { F1DataManager() }
    val signalRClient = remember { F1SignalRClient() }

    LaunchedEffect(Unit) {
        launch { simulator.startSimulation(this) }
        launch {
            println("Start SignalR...")
            signalRClient.startConnection()
        }
    }

    val incomingPacket by signalRClient.incomingData.collectAsState()
    LaunchedEffect(incomingPacket) {
        incomingPacket?.let { (topic, payload) ->
            dataManager.processData(topic, payload)
        }
    }

    val simDrivers by simulator.drivers.collectAsState()
    val simLap by simulator.currentLap.collectAsState()
    val liveDrivers by dataManager.drivers.collectAsState()
    val liveLap = 0

    val drivers = if (isDevMode) simDrivers else liveDrivers
    val currentLap = if (isDevMode) simLap else liveLap

    val showContent = isDevMode || drivers.isNotEmpty()

    var selectedDriverId by remember { mutableStateOf(1) }
    var currentTab by remember { mutableStateOf(DashboardTab.LEADERBOARD) }

    var trackPath by remember { mutableStateOf(Path()) }
    var trackName by remember { mutableStateOf("Loading...") }

    LaunchedEffect(Unit) {
        val tracks = TrackRepository.getTrackList()
        val defaultTrack = tracks.find { it.location.contains("Monaco") } ?: tracks.firstOrNull()

        if (defaultTrack != null) {
            trackName = defaultTrack.name
            trackPath = TrackRepository.loadTrackPath(defaultTrack.id)
        } else {
            trackName = "No Track"
        }
    }

    val selectedDriver = drivers.find { it.id == selectedDriverId }
        ?: drivers.firstOrNull().also {
            if (it != null) selectedDriverId = it.id
        }

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

            if (showContent) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        val statusColor = if (isDevMode) HeimdallColors.NeonTrack else Color.Green
                        val statusText = if (isDevMode) "DEV MODE" else "LIVE"

                        Box(Modifier.size(8.dp).background(statusColor, CircleShape))
                        Spacer(Modifier.width(8.dp))
                        Text(statusText, color = Color.White, fontWeight = FontWeight.Bold)
                    }

                    Text(
                        text = trackName,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )

                    Text("Lap $currentLap/78", color = Color.White, fontWeight = FontWeight.Medium)
                }
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                if (currentTab == DashboardTab.SETTINGS) {
                }
                else if (!showContent) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Waiting for live session data...", color = Color.Gray)
                    }
                } else {
                    InteractiveTrackMap(
                        drivers = drivers,
                        selectedDriverId = selectedDriverId,
                        trackPath = trackPath
                    )
                }
            }

            if (showContent || currentTab == DashboardTab.SETTINGS) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .background(HeimdallColors.Surface)
                ) {
                    if (showContent && selectedDriver != null && currentTab != DashboardTab.SETTINGS) {
                        DriverHeaderNew(driver = selectedDriver)
                        HorizontalDivider(color = Color(0xFF333333), thickness = 1.dp)
                    }

                    Box(modifier = Modifier.fillMaxSize()) {
                        when (currentTab) {
                            DashboardTab.LEADERBOARD -> if (showContent) LeaderboardTab(
                                drivers = drivers,
                                selectedDriverId = selectedDriverId,
                                onDriverSelect = { newId -> selectedDriverId = newId }
                            )
                            DashboardTab.TEAM_RADIO -> if (showContent && selectedDriver != null) TeamRadioTab(selectedDriver)
                            DashboardTab.RACE_DATA -> if (showContent && selectedDriver != null) RaceDataTab(selectedDriver)

                            DashboardTab.SETTINGS -> SettingsTab(
                                isDevMode = isDevMode,
                                onDevModeChange = { isDevMode = it }
                            )
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
