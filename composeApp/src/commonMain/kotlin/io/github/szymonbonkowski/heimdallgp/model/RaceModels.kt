package io.github.szymonbonkowski.heimdallgp.model

import androidx.compose.ui.graphics.Color

enum class TireCompound(val color: Color, val label: String) {
    SOFT(Color(0xFFFF3333), "Soft"),
    MEDIUM(Color(0xFFFFFF33), "Medium"),
    HARD(Color(0xFFCCCCCC), "Hard"),
    INTERMEDIATE(Color(0xFF33CC33), "Intermediate"),
    WET(Color(0xFF3366FF), "Wet")
}

data class Driver(
    val id: Int,
    val name: String,
    val shortName: String,
    val team: String,
    val teamColor: Color,
    val number: Int,
    var position: Int,
    var tireCompound: TireCompound,
    var speedKmh: Int = 0,
    var rpm: Int = 0,
    var gear: Int = 0,
    var throttle: Float = 0f,
    var brake: Float = 0f,
    var lap: Int = 0,
    var lastLap: String = "",
    var bestLap: String = "",
    var gapToLeader: String = "",
    var progress: Float = 0f,

    var x: Double = 0.0,
    var y: Double = 0.0,
    var z: Double = 0.0
)
