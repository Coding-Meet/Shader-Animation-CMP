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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.meet.shader.animation.cmp.expect_shader.rememberShaderTime
import com.meet.shader.animation.cmp.expect_shader.shader

private const val LIQUID_CHROME_SHADER = """
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

half4 main(float2 fragCoord) {
    float2 uv = (fragCoord * 2.0 - resolution) / min(resolution.x, resolution.y);
    float t = time * 0.3;

    float2 p = uv * 2.0;
    float n1 = valueNoise(p + float2(t, t * 0.6));
    float n2 = valueNoise(p + n1 * 1.5 + float2(-t * 0.4, t * 0.3));
    float n3 = valueNoise(p * 1.5 + n2 * 1.2 + float2(t * 0.2, -t * 0.5));

    float chrome = n3 * 0.5 + 0.5;
    chrome = pow(chrome, 0.6);

    float3 silver    = float3(0.2, 0.2, 0.25);
    float3 highlight = float3(0.5, 0.5, 0.6);
    float3 shadow    = float3(0.02, 0.01, 0.05);
    float3 tint      = float3(0.15, 0.2, 0.4);

    float3 col = mix(shadow, silver, chrome);
    col = mix(col, highlight, smoothstep(0.8, 0.98, chrome));
    col += tint * smoothstep(0.3, 0.6, n1) * 0.15;

    float spec = pow(max(chrome, 0.0), 12.0);
    col += float3(0.6, 0.6, 0.8) * spec * 0.3;

    return half4(half3(col), 1.0);
}
"""

@Composable
fun LiquidChromeScreen(onBack: () -> Unit) {
    val time by rememberShaderTime()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .shader(LIQUID_CHROME_SHADER) {
                uniformFloat("time", time)
            }
    ) {
        IconButton(
            onClick = onBack,
            modifier = Modifier.align(Alignment.TopStart).systemBarsPadding().padding(8.dp)
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
        }
        Text(
            text = "Liquid Chrome",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            color = Color.White,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}
