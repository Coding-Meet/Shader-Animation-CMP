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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.meet.shader.animation.cmp.expect_shader.rememberShaderTime
import com.meet.shader.animation.cmp.expect_shader.shader

private const val NEON_ORBIT_SHADER = """
uniform float2 resolution;
uniform float time;

half4 main(float2 fragCoord) {
    float2 uv = (fragCoord * 2.0 - resolution) / min(resolution.x, resolution.y);
    float t = time * 0.06;
    float lw = 0.002;
    float a = atan(uv.y, uv.x);

    float3 col = float3(0.0);
    for (int j = 0; j < 3; j++) {
        for (int i = 0; i < 5; i++) {
            float angularShift = sin(a * 3.0 + time * 0.5 + float(j)) * 0.08;
            col[j] += lw * float(i * i) / abs(fract(t - 0.01 * float(j) + float(i) * 0.01) * 5.0 - length(uv) + angularShift + mod(uv.x + uv.y, 0.2));
        }
    }
    col *= float3(1.1, 0.8, 1.2);
    return half4(half3(col), 1.0);
}
"""

@Composable
fun NeonOrbitScreen(onBack: () -> Unit) {
    val time by rememberShaderTime()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .shader(NEON_ORBIT_SHADER) {
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
            text = "Neon Orbit",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            color = Color.White,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
@Preview
private fun NeonOrbitScreenPreview(){
    NeonOrbitScreen(onBack = {})
}