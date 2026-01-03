package io.github.szymonbonkowski.heimdallgp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        simulator.startSimulation(this)
    }

    val drivers by simulator.drivers.collectAsState()
    val currentLap by simulator.currentLap.collectAsState()

    var selectedDriverId by remember { mutableStateOf(1) }

    var currentTab by remember { mutableStateOf(DashboardTab.LEADERBOARD) }

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
                Text("Monaco GP", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text("$currentLap/78", color = Color.White)
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                InteractiveTrackMap(
                    drivers = drivers,
                    selectedDriverId = selectedDriverId
                )
            }

            if (selectedDriver != null) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .background(HeimdallColors.Surface)
                ) {
                    DriverHeaderCompact(driver = selectedDriver)

                    HorizontalDivider(color = Color.DarkGray, thickness = 0.5.dp)

                    Box(modifier = Modifier.fillMaxSize()) {
                        when (currentTab) {
                            DashboardTab.LEADERBOARD -> LeaderboardTab(drivers)
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
fun DriverHeaderCompact(driver: Driver) {
    Row(
        modifier = Modifier.padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(48.dp)
                .background(driver.teamColor, CircleShape)
        ) {
            Text(driver.number.toString(), color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.width(16.dp))
        Column {
            Text(driver.name, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text(driver.team, color = HeimdallColors.TextSecondary, fontSize = 14.sp)
        }
    }
}
