package io.github.szymonbonkowski.heimdallgp.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class TrackMetadata(
    val id: String,
    val name: String,
    val location: String,
    val lat: Double,
    val lon: Double,
    val zoom: Int
)

@Serializable
data class GeoCollection(
    val type: String,
    val features: List<GeoFeature>
)

@Serializable
data class GeoFeature(
    val type: String,
    val properties: Map<String, JsonElement>,
    val geometry: GeoGeometry
)

@Serializable
data class GeoGeometry(
    val type: String,
    val coordinates: List<List<Double>>
)
