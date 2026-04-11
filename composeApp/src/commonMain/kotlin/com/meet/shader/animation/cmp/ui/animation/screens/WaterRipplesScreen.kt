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

private const val WATER_RIPPLES_SHADER = """
uniform float2 resolution;
uniform float time;

half4 main(float2 fragCoord) {
    float2 uv = (fragCoord * 2.0 - resolution) / min(resolution.x, resolution.y);
    float t = time * 0.6;

    float2 e1 = float2(sin(t * 0.7) * 0.5, cos(t * 0.5) * 0.5);
    float2 e2 = float2(cos(t * 0.4) * 0.8, sin(t * 0.6) * 0.3);
    float2 e3 = float2(sin(t * 0.3) * 0.3, cos(t * 0.8) * 0.7);

    float wave = 0.0;
    float d1 = length(uv - e1);
    float d2 = length(uv - e2);
    float d3 = length(uv - e3);
    wave += sin(d1 * 20.0 - time * 4.0) / (1.0 + d1 * 5.0);
    wave += sin(d2 * 20.0 - time * 3.5) / (1.0 + d2 * 5.0);
    wave += sin(d3 * 20.0 - time * 4.5) / (1.0 + d3 * 5.0);

    float3 baseColor = float3(0.0, 0.1, 0.3);
    float3 col = baseColor + wave * float3(0.1, 0.3, 0.2);
    col += pow(max(wave, 0.0), 8.0) * float3(0.8, 0.9, 1.0);

    return half4(half3(col), 1.0);
}
"""

@Composable
fun WaterRipplesScreen(onBack: () -> Unit) {
    val (shader, provider) = rememberAppRuntimeShaderOrNull(WATER_RIPPLES_SHADER)
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
            text = "Water Ripples",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            color = Color.White,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
@Preview
private fun WaterRipplesScreenPreview() {
    WaterRipplesScreen(onBack = {})
}
