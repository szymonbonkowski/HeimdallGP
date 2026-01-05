package io.github.szymonbonkowski.heimdallgp.utils

import io.ktor.util.decodeBase64Bytes
import kotlinx.cinterop.*
import platform.zlib.*

@OptIn(ExperimentalForeignApi::class)
actual object F1DataDecompressor {
    actual fun decompress(base64Data: String): String {
        return try {
            val compressedData = base64Data.decodeBase64Bytes()

            if (compressedData.isEmpty()) return "{}"

            memScoped {
                val strm = alloc<z_stream>()

                strm.next_in = compressedData.refTo(0).getPointer(this).reinterpret()
                strm.avail_in = compressedData.size.toUInt()

                if (inflateInit2(strm.ptr, -15) != Z_OK) {
                    return "{}"
                }

                val bufferSize = 16 * 1024
                val buffer = allocArray<ByteVar>(bufferSize)
                val output = StringBuilder()

                try {
                    while (true) {
                        strm.next_out = buffer.reinterpret()
                        strm.avail_out = bufferSize.toUInt()

                        val res = inflate(strm.ptr, Z_NO_FLUSH)

                        if (res != Z_OK && res != Z_STREAM_END) {
                            break
                        }

                        val bytesDecompressed = bufferSize - strm.avail_out.toInt()
                        if (bytesDecompressed > 0) {
                            val chunk = buffer.readBytes(bytesDecompressed).toKString()
                            output.append(chunk)
                        }

                        if (res == Z_STREAM_END) break
                    }
                } finally {
                    inflateEnd(strm.ptr)
                }

                output.toString()
            }
        } catch (e: Exception) {
            println("iOS Decompress Error: ${e.message}")
            "{}"
        }
    }
}
