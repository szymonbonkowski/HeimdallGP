package io.github.szymonbonkowski.heimdallgp.data

import io.github.szymonbonkowski.heimdallgp.model.SignalRMessage
import io.github.szymonbonkowski.heimdallgp.utils.F1DataDecompressor
import io.ktor.client.*
import io.ktor.client.plugins.websocket.*
import io.ktor.http.*
import io.ktor.websocket.*
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonPrimitive
import kotlin.time.Duration.Companion.seconds

class F1SignalRClient {

    private val client = HttpClient {
        install(WebSockets) {
            pingInterval = 15.seconds
        }
    }

    private val json = Json { ignoreUnknownKeys = true }

    private val _incomingData = MutableStateFlow<Pair<String, String>?>(null)
    val incomingData = _incomingData.asStateFlow()

    suspend fun startConnection() {
        val host = "livetiming.formula1.com"
        val path = "/signalr/connect?clientProtocol=1.5&transport=webSockets&connectionData=%5B%7B%22name%22%3A%22streaming%22%7D%5D"

        while (currentCoroutineContext().isActive) {
            try {
                println("Lączenie z F1 SignalR...")
                client.wss(host = host, path = path) {
                    println("Połączono!")

                    val subscribeMessage = """
                        {
                            "H": "Streaming",
                            "M": "Subscribe",
                            "A": [[
                                "Heartbeat", "CarData.z", "Position.z",
                                "ExtrapolatedClock", "TopThree", "TimingStats",
                                "TimingAppData", "WeatherData", "TrackStatus",
                                "DriverList", "RaceControlMessages", "SessionInfo"
                            ]],
                            "I": 1
                        }
                    """.trimIndent()

                    send(Frame.Text(subscribeMessage))

                    while (isActive) {
                        val frame = incoming.receive() as? Frame.Text ?: continue
                        val text = frame.readText()

                        if (text == "{}") continue

                        try {
                            val message = json.decodeFromString<SignalRMessage>(text)

                            message.m?.forEach { method ->
                                if (method.method == "feed") {
                                    val args = method.args
                                    if (args.size >= 2) {
                                        val topic = args[0].jsonPrimitive.content
                                        val dataContent = args[1].jsonPrimitive.content

                                        val finalPayload = if (topic.endsWith(".z")) {
                                            F1DataDecompressor.decompress(dataContent)
                                        } else {
                                            dataContent
                                        }

                                        _incomingData.emit(topic to finalPayload)
                                    }
                                }
                            }
                        } catch (e: Exception) {
                            println("Błąd parsowania ramki: ${e.message}")
                        }
                    }
                }
            } catch (e: Exception) {
                println("Błąd połączenia/rozłączenie: ${e.message}")
            }

            println("Ponawianie próby za 3 sekundy...")
            delay(3000L)
        }
    }
}
