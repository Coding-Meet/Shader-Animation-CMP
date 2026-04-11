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

private const val STARFIELD_SHADER = """
uniform float2 resolution;
uniform float time;

float hashVal(float2 p) {
    return fract(sin(dot(p, float2(12.9898, 78.233))) * 43758.5453);
}

float2 hash2(float2 p) {
    return fract(sin(float2(dot(p, float2(127.1, 311.7)),
                            dot(p, float2(269.5, 183.3)))) * 43758.5453);
}

half4 main(float2 fragCoord) {
    float2 uv = fragCoord / resolution;
    float3 col = float3(0.0);

    for (int layer = 0; layer < 3; layer++) {
        float scale = 50.0 + float(layer) * 80.0;
        float speed = 0.03 + float(layer) * 0.02;
        float brightness = 1.0 - float(layer) * 0.25;

        float2 st = uv * scale;
        st.y += time * speed * scale;
        float2 cell = floor(st);
        float2 f = fract(st);

        float h = hashVal(cell);
        if (h > 0.95) {
            float2 center = hash2(cell);
            float d = length(f - center);
            float twinkle = sin(time * 3.0 + h * 100.0) * 0.3 + 0.7;
            float star = smoothstep(0.1, 0.0, d) * twinkle * brightness;
            col += float3(star);
        }
    }

    col += float3(0.01, 0.01, 0.03);
    return half4(half3(col), 1.0);
}
"""

@Composable
fun StarfieldScreen(onBack: () -> Unit) {
    val (shader, provider) = rememberAppRuntimeShaderOrNull(STARFIELD_SHADER)
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
            text = "Starfield",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            color = Color.White,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
@Preview
private fun StarfieldScreenPreview() {
    StarfieldScreen(onBack = {})
}
