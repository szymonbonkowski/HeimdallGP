package io.github.szymonbonkowski.heimdallgp.utils

import io.ktor.util.decodeBase64Bytes
import java.util.zip.Inflater

actual object F1DataDecompressor {
    actual fun decompress(base64Data: String): String {
        return try {
            val compressedBytes = base64Data.decodeBase64Bytes()

            val inflater = Inflater(true)
            inflater.setInput(compressedBytes)

            val buffer = ByteArray(1024 * 16)
            val outputStream = java.io.ByteArrayOutputStream()

            while (!inflater.finished()) {
                val count = inflater.inflate(buffer)
                if (count == 0) break
                outputStream.write(buffer, 0, count)
            }
            inflater.end()

            outputStream.toString("UTF-8")
        } catch (e: Exception) {
            println("Android Decompress Error: ${e.message}")
            "{}"
        }
    }
}
