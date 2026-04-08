package com.meet.shader.animation.cmp.expect_shader

import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

expect fun Modifier.shader(
    shader: String,
    inputName: String? = null,
    uniforms: ShaderProvider.() -> Unit = {}
): Modifier

expect fun shaderAvailable(): Boolean

fun ShaderProvider.updateResolution(width: Float, height: Float) {
    uniformFloat("resolution", width, height)
}
fun ShaderProvider.color(name: String, color: Color) {
    uniformColor(name, color.red, color.green, color.blue, color.alpha)
}