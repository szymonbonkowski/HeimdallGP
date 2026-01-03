package io.github.szymonbonkowski.heimdallgp.logic

import androidx.compose.ui.graphics.Color
import io.github.szymonbonkowski.heimdallgp.model.Driver
import io.github.szymonbonkowski.heimdallgp.model.TireCompound
import io.ktor.util.date.getTimeMillis
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.random.Random

class RaceSimulator {
    private val _drivers = MutableStateFlow<List<Driver>>(emptyList())
    val drivers = _drivers.asStateFlow()

    private val _currentLap = MutableStateFlow(52)
    val currentLap = _currentLap.asStateFlow()

    private val driverSpeeds = mutableMapOf<Int, Int>()

    init {
        val initialDrivers = listOf(
            createDriver(1, "Max Verstappen", "Red Bull Racing", Color(0xFF161853), 1, "VER", TireCompound.MEDIUM, 0.60f),
            createDriver(2, "Sergio Perez", "Red Bull Racing", Color(0xFF161853), 11, "PER", TireCompound.HARD, 0.58f),
            createDriver(3, "Charles Leclerc", "Ferrari", Color(0xFFDC0000), 16, "LEC", TireCompound.HARD, 0.575f),
            createDriver(4, "Carlos Sainz", "Ferrari", Color(0xFFDC0000), 55, "SAI", TireCompound.MEDIUM, 0.56f),
            createDriver(5, "Lando Norris", "McLaren", Color(0xFFFF8000), 4, "NOR", TireCompound.SOFT, 0.55f),
            createDriver(6, "Oscar Piastri", "McLaren", Color(0xFFFF8000), 81, "PIA", TireCompound.MEDIUM, 0.54f),
            createDriver(7, "Lewis Hamilton", "Mercedes", Color(0xFF00D2BE), 44, "HAM", TireCompound.MEDIUM, 0.52f),
            createDriver(8, "George Russell", "Mercedes", Color(0xFF00D2BE), 63, "RUS", TireCompound.HARD, 0.51f),
            createDriver(9, "Fernando Alonso", "Aston Martin", Color(0xFF006F62), 14, "ALO", TireCompound.HARD, 0.49f),
            createDriver(10, "Lance Stroll", "Aston Martin", Color(0xFF006F62), 18, "STR", TireCompound.MEDIUM, 0.48f),
            createDriver(11, "Yuki Tsunoda", "RB", Color(0xFF6692FF), 22, "TSU", TireCompound.SOFT, 0.45f),
            createDriver(12, "Daniel Ricciardo", "RB", Color(0xFF6692FF), 3, "RIC", TireCompound.MEDIUM, 0.44f),
            createDriver(13, "Nico Hulkenberg", "Haas", Color(0xFFB6BABD), 27, "HUL", TireCompound.HARD, 0.42f),
            createDriver(14, "Kevin Magnussen", "Haas", Color(0xFFB6BABD), 20, "MAG", TireCompound.MEDIUM, 0.41f),
            createDriver(15, "Alex Albon", "Williams", Color(0xFF005AFF), 23, "ALB", TireCompound.SOFT, 0.40f),
            createDriver(16, "Logan Sargeant", "Williams", Color(0xFF005AFF), 2, "SAR", TireCompound.HARD, 0.38f),
            createDriver(17, "Esteban Ocon", "Alpine", Color(0xFF0093CC), 31, "OCO", TireCompound.MEDIUM, 0.35f),
            createDriver(18, "Pierre Gasly", "Alpine", Color(0xFF0093CC), 10, "GAS", TireCompound.HARD, 0.34f),
            createDriver(19, "Valtteri Bottas", "Kick Sauber", Color(0xFF52E252), 77, "BOT", TireCompound.MEDIUM, 0.32f),
            createDriver(20, "Guanyu Zhou", "Kick Sauber", Color(0xFF52E252), 24, "ZHO", TireCompound.SOFT, 0.30f)
        )

        initialDrivers.forEach { driver ->
            driverSpeeds[driver.id] = Random.nextInt(200, 301)
        }

        _drivers.value = initialDrivers
    }

    fun startSimulation(scope: CoroutineScope) {
        scope.launch {
            while (isActive) {
                updateDrivers()
                delay(50)
            }
        }
    }

    private fun updateDrivers() {
        val currentList = _drivers.value

        val movingDrivers = currentList.map { driver ->
            val baseFixedSpeed = driverSpeeds[driver.id] ?: 220

            val currentSpeed = baseFixedSpeed + Random.nextInt(-1, 2)

            val moveFactor = 0.000004f
            var newProgress = driver.progress + (currentSpeed * moveFactor)

            var newLap = driver.lap
            if (newProgress >= 1.0f) {
                newProgress -= 1.0f
                newLap++
                if (driver.id == 1) _currentLap.value = newLap
            }

            val simulatedRpm = (currentSpeed * 42) + Random.nextInt(-50, 50)

            driver.copy(
                progress = newProgress,
                lap = newLap,
                rpm = simulatedRpm,
                speedKmh = currentSpeed,
                gear = (currentSpeed / 38).coerceIn(1, 8),
                throttle = (0.4f + (currentSpeed / 500f)).coerceIn(0f, 1f),
                brake = 0f
            )
        }

        val sortedDrivers = movingDrivers.sortedByDescending { it.lap + it.progress }

        val leaderDistance = sortedDrivers.first().lap + sortedDrivers.first().progress
        val averageLapTimeSeconds = 75.0f

        val finalDrivers = sortedDrivers.mapIndexed { index, driver ->
            val driverDistance = driver.lap + driver.progress
            val deltaDistance = leaderDistance - driverDistance
            val gapSeconds = deltaDistance * averageLapTimeSeconds

            val gapString = if (index == 0) {
                "Leader"
            } else {
                val roundedGap = (gapSeconds * 100).roundToInt() / 100.0
                "+${roundedGap}s"
            }

            driver.copy(
                position = index + 1,
                gapToLeader = gapString
            )
        }

        _drivers.value = finalDrivers
    }

    private fun createDriver(id: Int, name: String, team: String, color: Color, num: Int, short: String, tire: TireCompound, startProg: Float): Driver {
        return Driver(
            id = id, name = name, team = team, teamColor = color, number = num, shortName = short,
            position = id, progress = startProg, lap = 52,
            speedKmh = 0, rpm = 0, gear = 1, throttle = 0f, brake = 0f,
            tireCompound = tire, lastLap = "1:14.200", bestLap = "1:13.500", gapToLeader = "+0.000"
        )
    }
}
