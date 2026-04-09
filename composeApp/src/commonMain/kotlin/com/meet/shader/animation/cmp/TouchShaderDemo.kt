package com.meet.shader.animation.cmp

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import com.meet.shader.animation.cmp.expect_shader.rememberShaderTime
import com.meet.shader.animation.cmp.expect_shader.shader

const val TOUCH_SHADER = """
    uniform float2 resolution;
    uniform float time;
    uniform float2 touch;

    half4 main(in float2 fragCoord) {
        float2 uv = fragCoord / resolution.xy;
        float2 touchUV = touch / resolution.xy;

        float dist = distance(uv, touchUV);

        // Ripple waves that emanate outward from the touch point
        float ripple = sin(dist * 30.0 - time * 6.0) * 0.5 + 0.5;
        float fade = 1.0 - smoothstep(0.0, 0.5, dist);

        // Color oscillates between blue and pink over time
        half3 col1 = half3(0.2, 0.5, 1.0);
        half3 col2 = half3(1.0, 0.3, 0.5);
        half3 col = mix(col1, col2, sin(time) * 0.5 + 0.5);

        return half4(col * ripple * fade, 1.0);
    }
"""


@Preview
@Composable
fun TouchShaderDemo() {
    val time by rememberShaderTime()

    var touch by remember { mutableStateOf(Offset.Zero) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(onPress = { touch = it })
            }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { touch = it },
                    onDrag = { change, _ -> touch = change.position }
                )
            }
            .shader(TOUCH_SHADER) {
                uniformFloat("time", time)
                uniformFloat("touch", touch.x, touch.y)
            }
    )
}