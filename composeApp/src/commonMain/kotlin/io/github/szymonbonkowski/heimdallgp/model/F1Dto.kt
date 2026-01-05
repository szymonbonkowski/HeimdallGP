package io.github.szymonbonkowski.heimdallgp.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement


@Serializable
data class F1TimingDataDto(
    @SerialName("Lines") val lines: Map<String, F1DriverTimingDto>? = null,
    @SerialName("SessionPart") val sessionPart: Int? = null
)

@Serializable
data class F1DriverTimingDto(
    @SerialName("ShowPosition") val showPosition: Boolean? = null,
    @SerialName("Position") val position: String? = null,
    @SerialName("GapToLeader") val gapToLeader: String? = null,
    @SerialName("IntervalToPositionAhead") val interval: String? = null,
    @SerialName("BestLapTime") val bestLapTime: F1LapTimeDto? = null,
    @SerialName("LastLapTime") val lastLapTime: F1LapTimeDto? = null,
    @SerialName("InPit") val inPit: Boolean? = null,
    @SerialName("PitOut") val pitOut: Boolean? = null,
    @SerialName("Stopped") val stopped: Boolean? = null,
    @SerialName("Retired") val retired: Boolean? = null,
    @SerialName("Sectors") val sectors: List<F1SectorDto>? = null,
    @SerialName("Speeds") val speeds: Map<String, F1SpeedDto>? = null
)

@Serializable
data class F1LapTimeDto(
    @SerialName("Value") val value: String? = null
)

@Serializable
data class F1SectorDto(
    @SerialName("Value") val value: String? = null
)

@Serializable
data class F1SpeedDto(
    @SerialName("Value") val value: String? = null
)

@Serializable
data class F1PositionPacketDto(
    @SerialName("Position") val position: List<F1CarPositionDto>? = null,
    @SerialName("Timestamp") val timestamp: String? = null
)

@Serializable
data class F1CarPositionDto(
    @SerialName("N") val number: Int,
    @SerialName("X") val x: Double,
    @SerialName("Y") val y: Double,
    @SerialName("Z") val z: Double
)

@Serializable
data class F1DriverListDto(
    @SerialName("RacingNumber") val racingNumber: String,
    @SerialName("BroadcastName") val broadcastName: String? = null,
    @SerialName("FirstName") val firstName: String? = null,
    @SerialName("LastName") val lastName: String? = null,
    @SerialName("TeamName") val teamName: String? = null,
    @SerialName("TeamColour") val teamColour: String? = null,
    @SerialName("Tla") val tla: String? = null
)

@Serializable
data class F1CarDataDto(
    @SerialName("Entries") val entries: List<F1CarDataEntryDto>? = null
)

@Serializable
data class F1CarDataEntryDto(
    @SerialName("Utc") val utc: String? = null,
    @SerialName("Cars") val cars: Map<String, F1CarDataChannelsDto>? = null
)

@Serializable
data class F1CarDataChannelsDto(
    @SerialName("Channels") val channels: List<Double>? = null
)
