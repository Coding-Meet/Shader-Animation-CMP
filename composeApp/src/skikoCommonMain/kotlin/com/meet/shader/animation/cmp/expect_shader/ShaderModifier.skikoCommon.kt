package com.meet.shader.animation.cmp.expect_shader

import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.graphicsLayer
import org.jetbrains.skia.ImageFilter
import org.jetbrains.skia.RuntimeEffect
import org.jetbrains.skia.RuntimeShaderBuilder

actual fun isShaderAvailable(): Boolean = true

actual fun Modifier.shader(
    shader: String,
    inputName: String?,
    uniforms: ShaderProvider.() -> Unit
): Modifier = composed {

    val effect = remember(shader) { RuntimeEffect.makeForShader(shader) }
    val builder = remember(shader) { RuntimeShaderBuilder(effect) }
    val provider = remember(shader) { ShaderProviderImpl(builder) }

    if (inputName != null) {
        this.graphicsLayer {
            uniforms(provider)
            provider.updateResolution(size.width, size.height)
            renderEffect = ImageFilter.makeRuntimeShader(
                runtimeShaderBuilder = builder,
                shaderName = inputName,
                input = null,
            ).asComposeRenderEffect()
        }
    } else {
        this.drawBehind {
            uniforms(provider)
            provider.updateResolution(size.width, size.height)
            drawRect(ShaderBrush(builder.makeShader()))
        }
    }
}