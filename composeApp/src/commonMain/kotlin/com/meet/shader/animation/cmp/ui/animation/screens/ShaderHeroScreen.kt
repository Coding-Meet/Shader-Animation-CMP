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

private const val SHADER_HERO_SHADER = """
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

float fbmHero(float2 p) {
    float t2 = 0.0;
    float a2 = 1.0;
    // Inlined rotation: col0=(1.0,-0.5), col1=(0.2,1.2)
    for (int i = 0; i < 5; i++) {
        t2 += a2 * valueNoise(p);
        float2 rp = float2(1.0 * p.x + 0.2 * p.y, -0.5 * p.x + 1.2 * p.y);
        p = rp * 2.0;
        a2 *= 0.5;
    }
    return t2;
}

float heroCloudsFn(float2 p) {
    float d = 1.0;
    float t2 = 0.0;
    for (float i = 0.0; i < 3.0; i += 1.0) {
        float a2 = d * fbmHero(float2(i * 10.0 + p.x * 0.2 + 0.2 * (1.0 + i) * p.y + d + i * i + p.x, p.y));
        t2 = mix(t2, d, a2);
        d = a2;
        p *= 2.0 / (i + 1.0);
    }
    return t2;
}

half4 main(float2 fragCoord) {
    float2 uv = (fragCoord - 0.5 * resolution) / min(resolution.x, resolution.y);
    float2 st = uv * float2(2.0, 1.0);

    float3 col = float3(0.0);
    float bg = heroCloudsFn(float2(st.x + time * 0.5, -st.y));

    uv *= 1.0 - 0.3 * (sin(time * 0.2) * 0.5 + 0.5);

    for (float i = 1.0; i < 12.0; i += 1.0) {
        uv += 0.1 * cos(i * float2(0.1 + 0.01 * i, 0.8) + i * i + time * 0.5 + 0.1 * uv.x);
        float2 p = uv;
        float d = length(p);
        col += 0.00125 / d * (cos(sin(i) * float3(1.0, 2.0, 3.0)) + 1.0);
        float b = valueNoise(float2(i + p.x + bg * 1.731, i + p.y + bg * 1.731));
        col += 0.002 * b / length(max(p, float2(b * p.x * 0.02, p.y)));
        col = mix(col, float3(bg * 0.25, bg * 0.137, bg * 0.05), d);
    }

    return half4(half3(col), 1.0);
}
"""

@Composable
fun ShaderHeroScreen(onBack: () -> Unit) {
    val time by rememberShaderTime()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .shader(SHADER_HERO_SHADER) {
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
            text = "Shader Hero",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            color = Color.White,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}
