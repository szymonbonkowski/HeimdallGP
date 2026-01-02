package io.github.szymonbonkowski.heimdallgp

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform