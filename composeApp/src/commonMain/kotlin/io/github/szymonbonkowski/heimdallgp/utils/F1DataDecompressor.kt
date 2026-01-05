package io.github.szymonbonkowski.heimdallgp.utils

expect object F1DataDecompressor {
    fun decompress(base64Data: String): String
}
