package com.meet.shader.animation.cmp.expect_shader

import android.graphics.RenderEffect
import android.graphics.RuntimeShader
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged

/**
 * Android implementation using the Android specific RuntimeShader.
 * this is a cuckoo's egg, because Android does not use Skiko.
 * While Compose Multiplatform on Desktop, iOS, and Web is powered by Skiko (a Kotlin wrapper for the Skia graphics engine),
 * Compose on Android is built directly on top of the native Android Canvas and Graphics pipeline.
 */
@Composable
actual fun Modifier.runTapTimeBackgroundShader(
    shaderCode: String, color1: Color?
): Modifier {
    val shader = remember(shaderCode) { RuntimeShader(shaderCode) }
    val brush = remember(shader) { ShaderBrush(shader) }
    val timeS by rememberTimeMaxFPS_S()
    remember(shader, color1) {
        if (color1 != null) shader.setColorUniform("uColor1", color1.toAndroidColor)
        true
    }
    var touchPos by remember { mutableStateOf(Offset.Zero) }
    remember(shader, touchPos) {
        shader.setFloatUniform("uTouchPos", touchPos.x, touchPos.y)
        true
    }

    return pointerInput(Unit) {
        detectTapGestures {
            touchPos = it
            // l.d { "touchPos: $touchPos" }
        }
    }
        .onSizeChanged { size ->
            // l.d { "size: $size" }
            shader.setFloatUniform("uSize", size.width.toFloat(), size.height.toFloat())
        }
        .drawWithCache {
            // l.d { "timeS: $timeS" }
            shader.setFloatUniform("uTimeS", timeS)

            onDrawBehind {
                drawRect(brush)
            }
        }
}

/**
 * This shader runner treats the entire UI component (with children) as an input image.
 * It samples the actual buttons, text, image, etc.
 *
 * It needs:
 *
 * 1. a triggering graphics layer in the Modifier chain
 * defined after this modifier. Like a
 *        .border(2.dp, Color.Red, RoundedCornerShape(24.dp))
 *
 * 2. Explicit takeover of the background with the same name used in the shader.
 */
@Composable
fun Modifier.runTimeManipulatingShader(shaderCode: String, time: Float?, color1: Color?): Modifier {
    val shader = remember(shaderCode) { RuntimeShader(shaderCode) }
    if (time != null) shader.setFloatUniform("uTime", time)

    return graphicsLayer {
        shader.setFloatUniform("uSize", size.width, size.height)

        renderEffect = RenderEffect
            // The background explicitly named as in the shader code.
            .createRuntimeShaderEffect(shader, "content")
            .asComposeRenderEffect()
    }
}



private val Color.toAndroidColor: android.graphics.Color
    get() = android.graphics.Color.valueOf(toArgb())