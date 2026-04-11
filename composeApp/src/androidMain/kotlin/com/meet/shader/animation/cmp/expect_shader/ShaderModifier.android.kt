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

actual fun isShaderAvailable(): Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU

actual fun Modifier.shader(
    shader: String,
    inputName: String?,
    uniforms: ShaderProvider.() -> Unit
): Modifier {
    if (!isShaderAvailable()) return this
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

