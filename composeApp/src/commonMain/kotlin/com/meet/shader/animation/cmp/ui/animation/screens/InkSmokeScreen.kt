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

private const val INK_SMOKE_SHADER = """
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
    float t = time * 0.2;

    float2 p = uv * 1.8;

    float2 q = float2(fbm(p + float2(t * 0.4, t * 0.3)),
                      fbm(p + float2(t * 0.2, -t * 0.4)));
    float2 r2 = float2(fbm(p + q * 4.0 + float2(1.7, 9.2) + t * 0.15),
                       fbm(p + q * 4.0 + float2(8.3, 2.8) - t * 0.1));
    float f = fbm(p + r2 * 2.0);

    float3 ink1 = float3(0.05, 0.0, 0.1);
    float3 ink2 = float3(0.1, 0.2, 0.5);
    float3 ink3 = float3(0.4, 0.1, 0.3);
    float3 ink4 = float3(0.0, 0.3, 0.4);

    float3 col = mix(ink1, ink2, clamp(f * 2.0, 0.0, 1.0));
    col = mix(col, ink3, clamp(q.x * 1.5, 0.0, 1.0));
    col = mix(col, ink4, clamp(r2.y * 0.8, 0.0, 1.0));

    float wisp = pow(clamp(f * 1.5, 0.0, 1.0), 3.0);
    col += float3(0.3, 0.2, 0.4) * wisp;

    return half4(half3(col), 1.0);
}
"""

@Composable
fun InkSmokeScreen(onBack: () -> Unit) {
    val time by rememberShaderTime()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .shader(INK_SMOKE_SHADER) {
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
            text = "Ink Smoke",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            color = Color.White,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}
