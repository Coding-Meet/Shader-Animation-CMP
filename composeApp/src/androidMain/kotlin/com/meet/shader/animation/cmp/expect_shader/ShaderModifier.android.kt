package com.meet.shader.animation.cmp.expect_shader

import android.graphics.RenderEffect
import android.graphics.RuntimeShader
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.nativeCanvas

actual fun shaderAvailable(): Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU

actual fun Modifier.shader(
    shader: String,
    inputName: String?,
    uniforms: ShaderProvider.() -> Unit
): Modifier {
    if (!shaderAvailable()) return this
    return shaderImpl(shader, inputName, uniforms)
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
private fun Modifier.shaderImpl(
    shader: String,
    inputName: String?,
    uniforms: ShaderProvider.() -> Unit
): Modifier = composed {

    val runtimeShader = remember(shader) { RuntimeShader(shader) }
    val provider = remember(shader) { ShaderProviderImpl(runtimeShader) }
    val paint = remember(runtimeShader) { android.graphics.Paint().apply { this.shader = runtimeShader } }

    if (inputName != null) {
        this.graphicsLayer {
            uniforms(provider)
            provider.updateResolution(size.width, size.height)
            renderEffect = RenderEffect.createRuntimeShaderEffect(runtimeShader, inputName)
                .asComposeRenderEffect()
        }
    } else {
        this.drawBehind {
            uniforms(provider)
            provider.updateResolution(size.width, size.height)
            drawIntoCanvas { canvas ->
                canvas.nativeCanvas.drawRect(0f, 0f, size.width, size.height, paint)
            }
        }
    }
}
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
private class ShaderProviderImpl(
    private val runtimeShader: RuntimeShader,
) : ShaderProvider {

    override fun uniformInt(name: String, value: Int) {
        runtimeShader.setIntUniform(name, value)
    }

    override fun uniformInt(name: String, value1: Int, value2: Int) {
        runtimeShader.setIntUniform(name, value1, value2)
    }

    override fun uniformInt(
        name: String,
        value1: Int,
        value2: Int,
        value3: Int
    ) {
        runtimeShader.setIntUniform(name, value1, value2, value3)
    }

    override fun uniformInt(
        name: String,
        value1: Int,
        value2: Int,
        value3: Int,
        value4: Int
    ) {
        runtimeShader.setIntUniform(name, value1, value2, value3, value4)
    }


    override fun uniformFloat(name: String, value: Float) {
        runtimeShader.setFloatUniform(name, value)
    }

    override fun uniformFloat(name: String, value1: Float, value2: Float) {
        runtimeShader.setFloatUniform(name, value1, value2)
    }

    override fun uniformFloat(name: String, value1: Float, value2: Float, value3: Float) {
        runtimeShader.setFloatUniform(name, value1, value2, value3)
    }
    override fun uniformFloat(
        name: String,
        value1: Float,
        value2: Float,
        value3: Float,
        value4: Float
    ) {
        runtimeShader.setFloatUniform(name, value1, value2, value3, value4)
    }

    override fun uniformFloat(name: String, values: List<Float>) {
        runtimeShader.setFloatUniform(name, values.toFloatArray())
    }

    override fun uniformColor(name: String, r: Float, g: Float, b: Float, a: Float) {
        runtimeShader.setFloatUniform(name, r, g, b, a)
    }
}