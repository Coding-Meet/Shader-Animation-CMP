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

private const val NEBULA_SHADER = """
uniform float2 resolution;
uniform float time;

float hashVal(float2 p) {
    return fract(sin(dot(p, float2(12.9898, 78.233))) * 43758.5453);
}

float valueNoise(float2 p) {
    float2 i = floor(p);
    float2 f = fract(p);
    f = f * f * (3.0 - 2.0 * f);
    float a = hashVal(i);
    float b = hashVal(i + float2(1.0, 0.0));
    float c = hashVal(i + float2(0.0, 1.0));
    float d = hashVal(i + float2(1.0, 1.0));
    return mix(mix(a, b, f.x), mix(c, d, f.x), f.y);
}

float fbm(float2 p) {
    float v = 0.0;
    float a = 0.5;
    for (int i = 0; i < 6; i++) {
        v += a * valueNoise(p);
        p *= 2.0;
        a *= 0.5;
    }
    return v;
}

half4 main(float2 fragCoord) {
    float2 uv = (fragCoord * 2.0 - resolution) / min(resolution.x, resolution.y);
    float t = time * 0.15;

    float2 p = uv * 1.5;
    float n1 = fbm(p + float2(t, t * 0.7));
    float n2 = fbm(p + n1 * 1.5 + float2(t * 0.3, -t * 0.2));
    float n3 = fbm(p + n2 * 1.5 + float2(-t * 0.2, t * 0.4));

    float3 purple = float3(0.3, 0.1, 0.5) * smoothstep(0.2, 0.8, n1);
    float3 blue   = float3(0.1, 0.2, 0.6) * smoothstep(0.3, 0.9, n2);
    float3 pink   = float3(0.6, 0.15, 0.4) * smoothstep(0.3, 0.85, n3);
    float3 orange = float3(0.5, 0.2, 0.05) * smoothstep(0.5, 0.95, n1 * n2);

    float3 col = float3(0.02, 0.01, 0.04);
    col += purple + blue + pink + orange;

    float core = exp(-length(uv + float2(sin(t), cos(t * 0.7)) * 0.3) * 2.5);
    col += float3(0.4, 0.2, 0.5) * core;

    float stars = hashVal(floor(uv * 200.0 + 0.5));
    float starBright = step(0.97, stars);
    float twinkle = sin(time * 2.0 + stars * 100.0) * 0.3 + 0.7;
    col += float3(0.9, 0.9, 1.0) * starBright * twinkle;

    float dimStars = hashVal(floor(uv * 80.0 + 10.0));
    col += float3(0.5, 0.5, 0.7) * step(0.96, dimStars) * 0.3;

    return half4(half3(col), 1.0);
}
"""

@Composable
fun NebulaScreen(onBack: () -> Unit) {
    val (shader, provider) = rememberAppRuntimeShaderOrNull(NEBULA_SHADER)
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
            text = "Nebula",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            color = Color.White,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
@Preview
private fun NebulaScreenPreview() {
    NebulaScreen(onBack = {})
}
