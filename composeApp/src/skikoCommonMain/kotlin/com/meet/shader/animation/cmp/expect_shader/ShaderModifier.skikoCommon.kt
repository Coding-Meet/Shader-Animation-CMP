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

actual fun shaderAvailable(): Boolean = true

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

private class ShaderProviderImpl(
    private val runtimeShaderBuilder: RuntimeShaderBuilder,
) : ShaderProvider {

    override fun uniformInt(name: String, value: Int) {
        runtimeShaderBuilder.uniform(name, value)
    }

    override fun uniformInt(name: String, value1: Int, value2: Int) {
        runtimeShaderBuilder.uniform(name, value1, value2)
    }

    override fun uniformInt(name: String, value1: Int, value2: Int, value3: Int) {
        runtimeShaderBuilder.uniform(name, value1, value2, value3)
    }

    override fun uniformInt(name: String, value1: Int, value2: Int, value3: Int, value4: Int) {
        runtimeShaderBuilder.uniform(name, value1, value2, value3, value4)
    }

    override fun uniformFloat(name: String, value: Float) {
        runtimeShaderBuilder.uniform(name, value)
    }

    override fun uniformFloat(name: String, value1: Float, value2: Float) {
        runtimeShaderBuilder.uniform(name, value1, value2)
    }

    override fun uniformFloat(name: String, value1: Float, value2: Float, value3: Float) {
        runtimeShaderBuilder.uniform(name, value1, value2, value3)
    }

    override fun uniformFloat(
        name: String,
        value1: Float,
        value2: Float,
        value3: Float,
        value4: Float
    ) {
        runtimeShaderBuilder.uniform(name, value1, value2, value3, value4)
    }

    override fun uniformFloat(name: String, values: List<Float>) {
        runtimeShaderBuilder.uniform(name, values.toFloatArray())
    }

    override fun uniformColor(name: String, r: Float, g: Float, b: Float, a: Float) {
        runtimeShaderBuilder.uniform(name, r, g, b, a)
    }
}