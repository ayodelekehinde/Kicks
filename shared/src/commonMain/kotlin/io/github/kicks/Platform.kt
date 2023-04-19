package io.github.kicks

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform