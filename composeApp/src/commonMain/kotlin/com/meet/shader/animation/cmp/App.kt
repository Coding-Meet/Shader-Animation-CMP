package com.meet.shader.animation.cmp

import androidx.compose.animation.core.withInfiniteAnimationFrameMillis
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.meet.shader.animation.cmp.expect_shader.ShaderProvider
import com.meet.shader.animation.cmp.expect_shader.shader

@Composable
@Preview
fun App() {
    MaterialTheme {
        ShaderDemo()
    }
}

val WAVE_SHADER = """
        uniform float2 resolution;
        uniform float time;
    uniform half4 color;
  uniform half4 color2;

    half4 main(in float2 fragCoord) {
        float2 uv = fragCoord/resolution.xy;

        float mixValue = distance(uv, vec2(0, 1));
        return mix(color, color2, mixValue);
    }
   
"""

@Composable
fun ShaderDemo() {

    val time by produceState(0f) {
        while (true) {
            withInfiniteAnimationFrameMillis {
                value = it / 1000f
            }
        }
    }
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {

        Box(
            modifier = Modifier
                .size(100.dp)
                .shader(WAVE_SHADER) {

                    uniformFloat("time", time)
                    color("color", ShaderColor(1f, 0f, 0f, 1f)) // red
                    color("color2", ShaderColor(0f, 0f, 1f, 1f)) // blue
                }
        )
    }
}

data class ShaderColor(
    val r: Float,
    val g: Float,
    val b: Float,
    val a: Float
)

fun ShaderProvider.color(name: String, color: ShaderColor) {
    uniformColor(name, color.r, color.g, color.b, color.a)
}