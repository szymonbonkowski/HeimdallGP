package io.github.szymonbonkowski.heimdallgp.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DataUsage
import androidx.compose.material.icons.filled.HeadsetMic
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

enum class DashboardTab(val label: String, val icon: ImageVector) {
    LEADERBOARD("Leaderboard", Icons.Default.List),
    RACE_DATA("Race Data", Icons.Default.DataUsage),
    TEAM_RADIO("Team Radio", Icons.Default.HeadsetMic),
    SETTINGS("Settings", Icons.Default.Settings)
}
