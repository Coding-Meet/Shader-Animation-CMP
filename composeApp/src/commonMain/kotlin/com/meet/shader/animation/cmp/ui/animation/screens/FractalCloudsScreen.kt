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

private const val FRACTAL_CLOUDS_SHADER = """
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
    float2 uv = fragCoord / resolution;
    uv *= 3.0;
    uv += float2(time * 0.08, time * 0.04);

    float f1 = fbm(uv);
    float f2 = fbm(uv + f1 * 2.0 + float2(time * 0.02, time * 0.03));

    float3 sky = float3(0.1, 0.15, 0.35);
    float3 cloud = float3(0.9, 0.9, 1.0);
    float3 col = mix(sky, cloud, clamp(f2, 0.0, 1.0));

    col += float3(0.1, 0.05, 0.0) * f1 * 0.5;

    return half4(half3(col), 1.0);
}
"""

@Composable
fun FractalCloudsScreen(onBack: () -> Unit) {
    val (shader, provider) = rememberAppRuntimeShaderOrNull(FRACTAL_CLOUDS_SHADER)
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
            text = "Fractal Clouds",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            color = Color.White,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
@Preview
private fun FractalCloudsScreenPreview() {
    FractalCloudsScreen(onBack = {})
}
