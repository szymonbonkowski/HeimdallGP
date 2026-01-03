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
import kotlin.math.sin
import kotlin.random.Random

class RaceSimulator {
    private val _drivers = MutableStateFlow<List<Driver>>(emptyList())
    val drivers = _drivers.asStateFlow()

    private val _currentLap = MutableStateFlow(52)
    val currentLap = _currentLap.asStateFlow()

    init {
        val initialDrivers = listOf(
            createDriver(1, "Max Verstappen", "Red Bull Racing", Color(0xFF161853), 1, "VER", TireCompound.MEDIUM, 0.45f),
            createDriver(2, "Sergio Perez", "Red Bull Racing", Color(0xFF161853), 11, "PER", TireCompound.HARD, 0.30f),
            createDriver(3, "Charles Leclerc", "Ferrari", Color(0xFFDC0000), 16, "LEC", TireCompound.HARD, 0.42f),
            createDriver(4, "Carlos Sainz", "Ferrari", Color(0xFFDC0000), 55, "SAI", TireCompound.MEDIUM, 0.41f),
            createDriver(5, "Lando Norris", "McLaren", Color(0xFFFF8000), 4, "NOR", TireCompound.SOFT, 0.40f),
            createDriver(6, "Oscar Piastri", "McLaren", Color(0xFFFF8000), 81, "PIA", TireCompound.MEDIUM, 0.39f),
            createDriver(7, "Lewis Hamilton", "Mercedes", Color(0xFF00D2BE), 44, "HAM", TireCompound.MEDIUM, 0.38f),
            createDriver(8, "George Russell", "Mercedes", Color(0xFF00D2BE), 63, "RUS", TireCompound.HARD, 0.37f),
            createDriver(9, "Fernando Alonso", "Aston Martin", Color(0xFF006F62), 14, "ALO", TireCompound.HARD, 0.35f),
            createDriver(10, "Lance Stroll", "Aston Martin", Color(0xFF006F62), 18, "STR", TireCompound.MEDIUM, 0.28f),
            createDriver(11, "Yuki Tsunoda", "RB", Color(0xFF6692FF), 22, "TSU", TireCompound.SOFT, 0.25f),
            createDriver(12, "Daniel Ricciardo", "RB", Color(0xFF6692FF), 3, "RIC", TireCompound.MEDIUM, 0.24f),
            createDriver(13, "Nico Hulkenberg", "Haas", Color(0xFFB6BABD), 27, "HUL", TireCompound.HARD, 0.22f),
            createDriver(14, "Kevin Magnussen", "Haas", Color(0xFFB6BABD), 20, "MAG", TireCompound.MEDIUM, 0.21f),
            createDriver(15, "Alex Albon", "Williams", Color(0xFF005AFF), 23, "ALB", TireCompound.SOFT, 0.20f),
            createDriver(16, "Logan Sargeant", "Williams", Color(0xFF005AFF), 2, "SAR", TireCompound.HARD, 0.18f),
            createDriver(17, "Esteban Ocon", "Alpine", Color(0xFF0093CC), 31, "OCO", TireCompound.MEDIUM, 0.15f),
            createDriver(18, "Pierre Gasly", "Alpine", Color(0xFF0093CC), 10, "GAS", TireCompound.HARD, 0.14f),
            createDriver(19, "Valtteri Bottas", "Kick Sauber", Color(0xFF52E252), 77, "BOT", TireCompound.MEDIUM, 0.12f),
            createDriver(20, "Guanyu Zhou", "Kick Sauber", Color(0xFF52E252), 24, "ZHO", TireCompound.SOFT, 0.10f)
        )
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
        val updatedList = _drivers.value.map { driver ->
            val baseSpeed = 0.001f
            val performanceFactor = (21 - driver.id) * 0.00001f
            val randomFactor = Random.nextFloat() * 0.00005f

            var newProgress = driver.progress + baseSpeed + performanceFactor + randomFactor

            var newLap = driver.lap
            if (newProgress >= 1.0f) {
                newProgress = 0f
                newLap++
                if (driver.id == 1) _currentLap.value = newLap
            }

            val time = getTimeMillis() / 1000.0
            val simulatedRpm = 10000 + (2000 * sin(time + driver.id)).toInt()
            val simulatedSpeed = (simulatedRpm * 0.026).toInt()
            val simulatedThrottle = (0.5 + 0.5 * sin(time + driver.id)).toFloat().coerceIn(0f, 1f)
            val simulatedBrake = if (simulatedThrottle < 0.1f) Random.nextFloat() * 0.8f else 0f

            val gap = if(driver.id == 1) "Leader" else "+${(driver.id * 1.2 + Random.nextDouble(0.1)).toString().take(4)}s"

            driver.copy(
                progress = newProgress,
                lap = newLap,
                rpm = simulatedRpm,
                speedKmh = simulatedSpeed,
                gear = (simulatedSpeed / 40).coerceIn(1, 8),
                throttle = simulatedThrottle,
                brake = simulatedBrake,
                gapToLeader = gap
            )
        }

        val sortedList = updatedList.sortedByDescending { it.lap + it.progress }

        _drivers.value = sortedList.mapIndexed { index, driver ->
            driver.copy(position = index + 1)
        }
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
