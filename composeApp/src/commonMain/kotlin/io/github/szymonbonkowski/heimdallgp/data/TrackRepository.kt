package io.github.szymonbonkowski.heimdallgp.data

import androidx.compose.ui.graphics.Path
import io.github.szymonbonkowski.heimdallgp.model.GeoCollection
import io.github.szymonbonkowski.heimdallgp.model.TrackMetadata
import kotlinx.serialization.json.Json
import org.jetbrains.compose.resources.ExperimentalResourceApi
import heimdallgp.composeapp.generated.resources.Res

object TrackRepository {

    private val json = Json { ignoreUnknownKeys = true }

    @OptIn(ExperimentalResourceApi::class)
    suspend fun getTrackList(): List<TrackMetadata> {
        return try {
            val bytes = Res.readBytes("files/tracks/tracks.json")
            val jsonString = bytes.decodeToString()
            json.decodeFromString<List<TrackMetadata>>(jsonString)
        } catch (e: Exception) {
            println("Error loading track list: ${e.message}")
            emptyList()
        }
    }

    @OptIn(ExperimentalResourceApi::class)
    suspend fun loadTrackPath(trackId: String): Path {
        return try {
            val fileName = "files/tracks/$trackId.geojson"
            val bytes = Res.readBytes(fileName)
            val jsonString = bytes.decodeToString()

            val geoData = json.decodeFromString<GeoCollection>(jsonString)
            val coordinates = geoData.features.firstOrNull()?.geometry?.coordinates

            createPathFromCoordinates(coordinates ?: emptyList())
        } catch (e: Exception) {
            println("Error loading track $trackId: ${e.message}")
            Path()
        }
    }

    private fun createPathFromCoordinates(coordinates: List<List<Double>>): Path {
        if (coordinates.isEmpty()) return Path()

        val lons = coordinates.map { it[0] }
        val lats = coordinates.map { it[1] }

        val minLon = lons.min()
        val maxLon = lons.max()
        val minLat = lats.min()
        val maxLat = lats.max()

        val lonRange = maxLon - minLon
        val latRange = maxLat - minLat

        val scale = if (lonRange > latRange) 1000f / lonRange else 1000f / latRange

        val path = Path()

        coordinates.forEachIndexed { index, coord ->
            val lon = coord[0]
            val lat = coord[1]

            val x = ((lon - minLon) * scale).toFloat()
            val y = ((maxLat - lat) * scale).toFloat()

            if (index == 0) {
                path.moveTo(x, y)
            } else {
                path.lineTo(x, y)
            }
        }
        path.close()
        return path
    }
}
