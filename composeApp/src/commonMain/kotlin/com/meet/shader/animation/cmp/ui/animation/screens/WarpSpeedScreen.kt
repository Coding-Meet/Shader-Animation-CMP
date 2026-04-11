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
import com.meet.shader.animation.cmp.expect_shader.rememberShaderInstanceOrNull
import com.meet.shader.animation.cmp.expect_shader.rememberShaderTime

private const val WARP_SPEED_SHADER = """
uniform float2 resolution;
uniform float time;

half4 main(float2 fragCoord) {
    float2 uv = (fragCoord * 2.0 - resolution) / min(resolution.x, resolution.y);
    float t = time * 0.07;
    float lw = 0.002;

    float3 col = float3(0.0);
    for (int j = 0; j < 3; j++) {
        for (int i = 0; i < 6; i++) {
            float dist = length(uv * float2(0.4, 1.0));
            col[j] += lw * float(i * i) / abs(fract(t - 0.008 * float(j) + float(i) * 0.015) * 4.0 - dist + mod(uv.x, 0.3));
        }
    }
    col *= float3(0.6, 0.9, 1.3);
    return half4(half3(col), 1.0);
}
"""

@Composable
fun WarpSpeedScreen(onBack: () -> Unit) {
    val (shader, provider) = rememberShaderInstanceOrNull(WARP_SPEED_SHADER)
    val time by rememberShaderTime()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .drawBehind {
                if (shader != null && provider != null) {
                    provider.update {
                        uniformFloat("resolution", size.width, size.height)
                        uniformFloat("time", time)
                    }
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
            text = "Warp Speed",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            color = Color.White,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
@Preview
private fun WarpSpeedScreenPreview() {
    WarpSpeedScreen(onBack = {})
}
