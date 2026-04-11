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

private const val MATRIX_RAIN_SHADER = """
uniform float2 resolution;
uniform float time;

float hashVal(float2 p) {
    return fract(sin(dot(p, float2(12.9898, 78.233))) * 43758.5453);
}

half4 main(float2 fragCoord) {
    float2 uv = fragCoord / resolution;
    float columns = 40.0;
    float2 grid = float2(columns, columns * resolution.y / resolution.x);

    float2 cell = floor(uv * grid);
    float2 f = fract(uv * grid);

    float columnSpeed = 2.0 + hashVal(float2(cell.x, 0.0)) * 4.0;
    float columnOffset = hashVal(float2(cell.x, 1.0)) * 100.0;

    float rain = fract(-time * columnSpeed * 0.1 + cell.y * 0.05 + columnOffset);
    float trail = pow(rain, 3.0);

    float charFlicker = step(0.3, hashVal(cell + floor(time * 8.0)));
    float brightness = trail * charFlicker;

    float head = step(0.95, rain);
    float3 col = float3(0.0, brightness * 0.8, brightness * 0.2);
    col += float3(head * 0.8, head, head * 0.8);

    float charShape = smoothstep(0.1, 0.2, f.x) * smoothstep(0.9, 0.8, f.x)
                    * smoothstep(0.05, 0.15, f.y) * smoothstep(0.95, 0.85, f.y);
    col *= charShape;

    return half4(half3(col), 1.0);
}
"""

@Composable
fun MatrixRainScreen(onBack: () -> Unit) {
    val (shader, provider) = rememberShaderInstanceOrNull(MATRIX_RAIN_SHADER)
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
            text = "Matrix Rain",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            color = Color.White,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
@Preview
private fun MatrixRainScreenPreview() {
    MatrixRainScreen(onBack = {})
}
