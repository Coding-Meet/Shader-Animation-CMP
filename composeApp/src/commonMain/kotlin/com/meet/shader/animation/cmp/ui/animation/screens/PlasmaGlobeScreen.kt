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

private const val PLASMA_GLOBE_SHADER = """
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
    float r = length(uv);
    float a = atan(uv.y, uv.x);
    float t = time;

    float sphereR = 0.95;
    float sphereEdge = smoothstep(sphereR, sphereR - 0.03, r);
    float rimGlow = exp(-abs(r - sphereR) * 30.0) * 0.3;

    float3 col = float3(0.01, 0.0, 0.02);

    for (int i = 0; i < 8; i++) {
        float fi = float(i);
        float baseAngle = fi * 0.785 + t * 0.3;

        for (int s = 0; s < 15; s++) {
            float fs = float(s) / 14.0;
            float segR = fs * sphereR * 0.9;

            float wobble = (valueNoise(float2(fi * 7.0 + fs * 3.0, t * 2.0)) - 0.5) * 1.2;
            wobble += (valueNoise(float2(fi * 13.0 + fs * 8.0, t * 3.5)) - 0.5) * 0.5;
            float segAngle = baseAngle + wobble * fs;

            float2 segPos = float2(cos(segAngle), sin(segAngle)) * segR;
            float d = length(uv - segPos);

            float core = 0.002 / (d * d + 0.002);
            float glow = 0.008 / (d + 0.008);

            float3 tendrilCol = mix(float3(0.8, 0.3, 1.0), float3(0.3, 0.5, 1.0), fs);
            float3 whiteCore = float3(0.9, 0.8, 1.0);

            float brightness = core * 0.06 + glow * 0.03;
            brightness *= (0.7 + 0.3 * sin(t * 5.0 + fi * 2.0 + fs * 4.0));
            col += mix(tendrilCol, whiteCore, core * 0.3) * brightness;
        }
    }

    float coreGlow = 0.04 / (r * r + 0.04);
    col += float3(0.6, 0.4, 0.9) * coreGlow * 0.5;
    float brightCore = 0.005 / (r * r + 0.005);
    col += float3(0.9, 0.85, 1.0) * brightCore * 0.3;

    col *= sphereEdge;
    col += float3(0.3, 0.2, 0.5) * rimGlow;
    col *= 0.9 + 0.1 * sin(t * 8.0);

    return half4(half3(col), 1.0);
}
"""

@Composable
fun PlasmaGlobeScreen(onBack: () -> Unit) {
    val time by rememberShaderTime()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .shader(PLASMA_GLOBE_SHADER) {
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
            text = "Plasma Globe",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            color = Color.White,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}
