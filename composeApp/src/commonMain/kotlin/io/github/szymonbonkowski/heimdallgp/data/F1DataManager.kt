package io.github.szymonbonkowski.heimdallgp.data

import androidx.compose.ui.graphics.Color
import io.github.szymonbonkowski.heimdallgp.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.decodeFromJsonElement

class F1DataManager {

    private val _drivers = MutableStateFlow<List<Driver>>(emptyList())
    val drivers = _drivers.asStateFlow()

    private val driverMap = mutableMapOf<String, Driver>()
    private val json = Json { ignoreUnknownKeys = true }

    fun processData(topic: String, jsonPayload: String) {
        if (jsonPayload == "{}") return

        try {
            when (topic) {
                "DriverList" -> parseDriverList(jsonPayload)
                "TimingData", "TimingAppData" -> parseTimingData(jsonPayload)
                "Position.z" -> parsePositionData(jsonPayload)
                "CarData.z" -> parseTelemetry(jsonPayload)
            }

            _drivers.value = driverMap.values.toList().sortedBy { it.position }

        } catch (e: Exception) {
            println("Błąd parsowania ($topic): ${e.message}")
        }
    }

    private fun parseDriverList(payload: String) {
        try {
            val rawMap = json.parseToJsonElement(payload).jsonObject

            rawMap.forEach { (driverNumber, element) ->
                val dto = json.decodeFromJsonElement<F1DriverListDto>(element)

                val existing = driverMap[driverNumber]
                val colorHex = dto.teamColour?.let { "FF$it" }?.toLongOrNull(16) ?: 0xFF808080

                val updatedDriver = existing?.copy(
                    name = "${dto.firstName} ${dto.lastName}",
                    shortName = dto.tla ?: dto.lastName?.take(3)?.uppercase() ?: "UNK",
                    team = dto.teamName ?: "Unknown Team",
                    teamColor = Color(colorHex),
                    number = dto.racingNumber.toIntOrNull() ?: 0
                ) ?: Driver(
                    id = dto.racingNumber.toIntOrNull() ?: 0,
                    name = "${dto.firstName} ${dto.lastName}",
                    shortName = dto.tla ?: "UNK",
                    team = dto.teamName ?: "",
                    teamColor = Color(colorHex),
                    number = dto.racingNumber.toIntOrNull() ?: 0,
                    position = 0,
                    tireCompound = TireCompound.HARD,

                    progress = 0f,
                    lap = 0,
                    speedKmh = 0,
                    rpm = 0,
                    gear = 0,
                    throttle = 0f,
                    brake = 0f,
                    lastLap = "",
                    bestLap = "",
                    gapToLeader = ""
                )

                driverMap[driverNumber] = updatedDriver
            }
        } catch (e: Exception) {
            println("Błąd DriverList: $e")
        }
    }

    private fun parseTimingData(payload: String) {
        val dto = json.decodeFromString<F1TimingDataDto>(payload)

        dto.lines?.forEach { (driverNumber, timing) ->
            val existing = driverMap[driverNumber] ?: return@forEach

            val newPos = timing.position?.toIntOrNull() ?: existing.position
            val newGap = timing.gapToLeader ?: existing.gapToLeader
            val newLastLap = timing.lastLapTime?.value ?: existing.lastLap
            val newBestLap = timing.bestLapTime?.value ?: existing.bestLap

            driverMap[driverNumber] = existing.copy(
                position = newPos,
                gapToLeader = newGap,
                lastLap = newLastLap,
                bestLap = newBestLap
            )
        }
    }

    private fun parsePositionData(payload: String) {
    }

    private fun parseTelemetry(payload: String) {
        try {
            val dto = json.decodeFromString<F1CarDataDto>(payload)
            val latestEntry = dto.entries?.lastOrNull() ?: return

            latestEntry.cars?.forEach { (driverNumber, data) ->
                val existing = driverMap[driverNumber] ?: return@forEach
                val channels = data.channels ?: return@forEach


                if (channels.size > 5) {
                    val rpm = channels[0].toInt()
                    val speed = channels[2].toInt()
                    val gear = channels[3].toInt()
                    val throttle = (channels[4] / 100.0).toFloat()
                    val brake = (channels[5] / 100.0).toFloat()

                    driverMap[driverNumber] = existing.copy(
                        rpm = rpm,
                        speedKmh = speed,
                        gear = gear,
                        throttle = throttle.coerceIn(0f, 1f),
                        brake = brake.coerceIn(0f, 1f)
                    )
                }
            }
        } catch (e: Exception) {
            println("Telemetry Error: ${e.message}")
        }
    }
}
