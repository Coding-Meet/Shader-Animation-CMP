package com.meet.shader.animation.cmp.expect_shader

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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import org.jetbrains.skia.RuntimeEffect
import org.jetbrains.skia.RuntimeShaderBuilder

/**
 * The Skiko implementation for all non-Android platforms.
 */
@Composable
actual fun Modifier.runTapTimeBackgroundShader(
    shaderCode: String,
    color1: Color?
): Modifier {
    val builder = remember(shaderCode) {
        RuntimeShaderBuilder(RuntimeEffect.makeForShader(shaderCode))
    }

    val timeS by rememberTimeMaxFPS_S()

    remember(builder, color1) {
        if (color1 != null) {
            builder.uniform("uColor1", color1.red, color1.green, color1.blue, color1.alpha)
        }
        true
    }

    var touchPos by remember { mutableStateOf(Offset.Zero) }
    remember(builder, touchPos) {
        builder.uniform("uTouchPos", touchPos.x, touchPos.y)
        true
    }

    return pointerInput(Unit) {
        detectTapGestures {
            touchPos = it
        }
    }.onSizeChanged { size ->
        builder.uniform("uSize", size.width.toFloat(), size.height.toFloat())
    }.drawWithCache {
        // update when State or Size changes.

        // l.d {"timeS: $timeS"}
        builder.uniform("uTimeS", timeS)

        onDrawBehind {
            drawRect(ShaderBrush(builder.makeShader()))
        }
    }
}