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

private const val OCEAN_WAVES_SHADER = """
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
    float t = time * 0.5;

    float wave = 0.0;
    float amp = 0.4;
    float freq = 2.0;
    for (int i = 0; i < 6; i++) {
        float fi = float(i);
        wave += amp * sin(uv.x * freq + t * (1.0 + fi * 0.2) + fi * 0.8);
        wave += amp * 0.5 * sin(uv.x * freq * 1.3 - t * 0.7 + fi * 1.2);
        freq *= 1.6;
        amp *= 0.55;
    }

    float surface = smoothstep(0.02, 0.0, abs(uv.y - wave * 0.3));
    float underWater = smoothstep(wave * 0.3, wave * 0.3 - 1.5, uv.y);

    float3 skyColor = mix(float3(0.1, 0.1, 0.2), float3(0.02, 0.02, 0.08), uv.y * 0.5 + 0.5);
    float3 deepColor = float3(0.0, 0.05, 0.15);
    float3 shallowColor = float3(0.0, 0.2, 0.4);
    float3 waterColor = mix(shallowColor, deepColor, clamp(-uv.y + wave * 0.3, 0.0, 1.0));

    float3 col = mix(skyColor, waterColor, underWater);
    col += float3(0.5, 0.8, 0.9) * surface;

    float foam = valueNoise(float2(uv.x * 8.0 + t * 2.0, wave * 10.0)) * surface * 2.0;
    col += float3(foam) * 0.5;

    float caustics = valueNoise(float2(uv.x * 6.0 + t, uv.y * 6.0 + t * 0.5));
    col += float3(0.0, 0.1, 0.15) * caustics * underWater * 0.4;

    return half4(half3(col), 1.0);
}
"""

@Composable
fun OceanWavesScreen(onBack: () -> Unit) {
    val (shader, provider) = rememberAppRuntimeShaderOrNull(OCEAN_WAVES_SHADER)
    val timeState = rememberShaderTime()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .drawBehind {
                if (shader != null && provider != null) {
                    provider.uniformFloat("resolution", size.width, size.height)
                    provider.uniformFloat("time", timeState.value)
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
            text = "Ocean Waves",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            color = Color.White,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
@Preview
private fun OceanWavesScreenPreview() {
    OceanWavesScreen(onBack = {})
}
