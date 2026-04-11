package com.meet.shader.animation.cmp.ui.animation.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.meet.shader.animation.cmp.expect_shader.createShader
import com.meet.shader.animation.cmp.expect_shader.rememberAppRuntimeShaderOrNull
import com.meet.shader.animation.cmp.expect_shader.rememberShaderTime

private const val HOLOGRAM_SHADER = """
uniform float2 resolution;
uniform float time;

float hashVal(float2 p) {
    return fract(sin(dot(p, float2(12.9898, 78.233))) * 43758.5453);
}

float3 hsv2rgb(float3 c) {
    float3 p = abs(fract(float3(c.x) + float3(0.0, 2.0/3.0, 1.0/3.0)) * 6.0 - 3.0);
    return c.z * mix(float3(1.0), clamp(p - 1.0, 0.0, 1.0), c.y);
}

half4 main(float2 fragCoord) {
    float2 uv = (fragCoord * 2.0 - resolution) / min(resolution.x, resolution.y);
    float t = time * 0.5;

    float scan = sin(uv.y * 80.0 + t * 5.0) * 0.5 + 0.5;
    scan = pow(scan, 0.3) * 0.15 + 0.85;

    float pattern = 0.0;
    pattern += sin(uv.x * 30.0 + t * 2.0) * 0.5;
    pattern += sin(uv.y * 25.0 - t * 1.5) * 0.5;
    pattern += sin((uv.x + uv.y) * 20.0 + t) * 0.3;
    pattern += sin(length(uv) * 15.0 - t * 3.0) * 0.4;

    float hue = fract(pattern * 0.15 + uv.x * 0.3 + uv.y * 0.2 + t * 0.1);
    float3 col = hsv2rgb(float3(hue, 0.6, 0.9));

    float shimmer = sin(uv.x * 50.0 + uv.y * 30.0 + t * 4.0) * 0.5 + 0.5;
    col = mix(col, float3(1.0), shimmer * 0.15);

    col *= scan;

    float glitch = step(0.98, hashVal(float2(floor(uv.y * 40.0), floor(t * 10.0))));
    col += float3(0.3, 0.6, 1.0) * glitch * 0.5;

    return half4(half3(col), 1.0);
}
"""

@Composable
fun HologramScreen(onBack: () -> Unit) {
    val (shader, provider) = rememberAppRuntimeShaderOrNull(HOLOGRAM_SHADER)
    val time by rememberShaderTime()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .drawBehind {
                if (shader != null && provider != null) {
                    provider.uniformFloat("resolution", size.width, size.height)
                    provider.uniformFloat("time", time)
                    drawRect(ShaderBrush(createShader(appRuntimeShader = shader)))
                }
            }
    ) {
        IconButton(
            onClick = onBack,
            modifier = Modifier.align(Alignment.TopStart).systemBarsPadding().padding(8.dp)
        ) {
            Icon(
                Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = Color.White
            )
        }
        Text(
            text = "Hologram",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            color = Color.White,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
@Preview
private fun HologramScreenPreview() {
    HologramScreen(onBack = {})
}
