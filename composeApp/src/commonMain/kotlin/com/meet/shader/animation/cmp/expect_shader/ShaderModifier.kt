package com.meet.shader.animation.cmp.expect_shader

import androidx.compose.animation.core.withInfiniteAnimationFrameMillis
import androidx.compose.runtime.Composable
import androidx.compose.runtime.produceState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import kotlin.time.Clock

expect fun Modifier.shader(
    shader: String,
    inputName: String? = null,
    uniforms: ShaderProvider.() -> Unit = {}
): Modifier

expect fun isShaderAvailable(): Boolean

fun ShaderProvider.updateResolution(width: Float, height: Float) {
    uniformFloat("resolution", width, height)
}

fun ShaderProvider.color(name: String, color: Color) {
    uniformColor(name, color.red, color.green, color.blue, color.alpha)
}

@Composable
fun rememberShaderTime() = produceState(0f) {
    val start = Clock.System.now().toEpochMilliseconds()
    while (true) {
        withInfiniteAnimationFrameMillis {
            val now = Clock.System.now().toEpochMilliseconds()
            value = (now - start) / 1000f
        }
    }
}