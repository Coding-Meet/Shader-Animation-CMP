package com.meet.shader.animation.cmp.expect_shader

import androidx.compose.ui.Modifier

expect fun Modifier.shader(
    shader: String,
    inputName: String? = null,
    uniforms: ShaderProvider.() -> Unit = {}
): Modifier