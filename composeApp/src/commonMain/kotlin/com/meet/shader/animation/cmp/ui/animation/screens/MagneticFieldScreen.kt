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

private const val MAGNETIC_FIELD_SHADER = """
uniform float2 resolution;
uniform float time;

float hashVal(float2 p) {
    return fract(sin(dot(p, float2(12.9898, 78.233))) * 43758.5453);
}

half4 main(float2 fragCoord) {
    float2 uv = (fragCoord * 2.0 - resolution) / min(resolution.x, resolution.y);
    float t = time * 0.3;

    float2 pole1 = float2(-0.5, 0.0);
    float2 pole2 = float2(0.5, 0.0);

    float2 d1 = uv - pole1;
    float2 d2 = uv - pole2;
    float r1 = length(d1) + 0.001;
    float r2 = length(d2) + 0.001;
    float2 field = d1 / (r1 * r1) - d2 / (r2 * r2);

    float angle = atan(field.y, field.x);
    float lines = sin(angle * 8.0 + t * 2.0);
    lines = pow(abs(lines), 0.3);

    float strength = length(field);
    strength = clamp(strength * 0.3, 0.0, 1.0);

    float blend = clamp((uv.x + 0.5) * 1.0, 0.0, 1.0);
    float3 cyan    = float3(0.0, 0.8, 1.0);
    float3 magenta = float3(1.0, 0.2, 0.8);
    float3 col = mix(cyan, magenta, blend) * lines * strength;

    col += cyan    * 0.3 / (r1 * 5.0 + 0.3);
    col += magenta * 0.3 / (r2 * 5.0 + 0.3);

    float flowPhase = fract(angle * 1.27 + t + hashVal(floor(float2(angle * 8.0, 0.0))) * 5.0);
    float particle = smoothstep(0.02, 0.0, abs(flowPhase - 0.5)) * strength;
    col += float3(1.0) * particle * 0.4;

    return half4(half3(col), 1.0);
}
"""

@Composable
fun MagneticFieldScreen(onBack: () -> Unit) {
    val (shader, provider) = rememberShaderInstanceOrNull(MAGNETIC_FIELD_SHADER)
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
            text = "Magnetic Field",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            color = Color.White,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
@Preview
private fun MagneticFieldScreenPreview() {
    MagneticFieldScreen(onBack = {})
}
