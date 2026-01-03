package io.github.szymonbonkowski.heimdallgp.model

import androidx.compose.ui.graphics.Color

data class Driver(
    val id: Int,
    val name: String,
    val team: String,
    val teamColor: Color,
    val number: Int,
    val shortName: String,
    var position: Int,
    var progress: Float,
    var lap: Int,
    var speedKmh: Int,
    var rpm: Int,
    var gear: Int,
    var throttle: Float,
    var brake: Float,
    var tireCompound: TireCompound,
    var lastLap: String,
    var bestLap: String,
    var gapToLeader: String
)

enum class TireCompound(val label: String, val color: Color) {
    SOFT("Soft", Color(0xFFFF3B30)),
    MEDIUM("Medium", Color(0xFFFFD60A)),
    HARD("Hard", Color(0xFFFFFFFF)),
    INTER("Inter", Color(0xFF30D158)),
    WET("Wet", Color(0xFF0A84FF))
}

enum class DashboardTab {
    LEADERBOARD, TEAM_RADIO, RACE_DATA
}
