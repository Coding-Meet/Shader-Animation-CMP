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

private const val TUNNEL_SHADER = """
uniform float2 resolution;
uniform float time;

half4 main(float2 fragCoord) {
    float2 uv = (fragCoord * 2.0 - resolution) / min(resolution.x, resolution.y);
    float r = length(uv);
    float a = atan(uv.y, uv.x);

    float tunnelU = 1.0 / (r + 0.001);
    float tunnelV = a / 3.14159265;

    tunnelU += time * 2.0;

    float pattern = mod(floor(tunnelU * 4.0) + floor(tunnelV * 8.0), 2.0);
    float3 col = mix(float3(0.05, 0.0, 0.1), float3(0.2, 0.1, 0.4), pattern);

    float glow = 1.0 / (r * 8.0 + 1.0);
    col += float3(0.2, 0.5, 1.0) * glow;

    float fade = smoothstep(0.0, 0.3, r);
    col *= fade;

    return half4(half3(col), 1.0);
}
"""

@Composable
fun TunnelScreen(onBack: () -> Unit) {
    val (shader, provider) = rememberAppRuntimeShaderOrNull(TUNNEL_SHADER)
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
            text = "Tunnel",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            color = Color.White,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
@Preview
private fun TunnelScreenPreview() {
    TunnelScreen(onBack = {})
}
