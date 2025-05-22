package com.example.microproyecto

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform