package com.meet.shader.animation.cmp

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform