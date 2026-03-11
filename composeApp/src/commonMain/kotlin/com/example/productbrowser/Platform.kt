package com.example.productbrowser

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform