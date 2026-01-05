package io.github.szymonbonkowski.heimdallgp.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class SignalRMessage(
    @SerialName("C") val c: String? = null,
    @SerialName("M") val m: List<SignalRMethod>? = null,
    @SerialName("G") val g: String? = null
)

@Serializable
data class SignalRMethod(
    @SerialName("H") val hub: String,
    @SerialName("M") val method: String,
    @SerialName("A") val args: List<JsonElement>
)

@Serializable
data class F1LiveData(
    val positionData: List<String>? = null,
    val timingData: List<String>? = null
)
