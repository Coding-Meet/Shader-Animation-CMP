package com.meet.shader.animation.cmp

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Shader Animation CMP",
    ) {
        App()
    }
}