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

private const val NEON_PULSE_SHADER = """
uniform float2 resolution;
uniform float time;

float3 hsv2rgb(float3 c) {
    float3 p = abs(fract(float3(c.x) + float3(0.0, 2.0/3.0, 1.0/3.0)) * 6.0 - 3.0);
    return c.z * mix(float3(1.0), clamp(p - 1.0, 0.0, 1.0), c.y);
}

half4 main(float2 fragCoord) {
    float2 uv = (fragCoord * 2.0 - resolution) / min(resolution.x, resolution.y);
    float t = time * 0.6;
    float r = length(uv);
    float a = atan(uv.y, uv.x);

    float3 col = float3(0.02, 0.01, 0.04);

    for (int i = 0; i < 6; i++) {
        float fi = float(i);
        float ringTime = fract(t * 0.25 + fi / 6.0);
        float ringR = ringTime * 2.0;
        float fade = (1.0 - ringTime);
        fade *= fade;

        float dist = abs(r - ringR);
        float core = smoothstep(0.015, 0.002, dist) * fade;
        float glow = 0.006 / (dist + 0.006) * fade;

        float hueShift = fi / 6.0 + t * 0.1;
        float3 neon = hsv2rgb(float3(fract(hueShift), 0.9, 1.0));
        float3 white = float3(1.0);

        col += mix(neon, white, core * 0.6) * (core + glow * 0.4);
    }

    float orb = exp(-r * 5.0);
    float orbPulse = 0.7 + 0.3 * sin(t * 3.0);
    float3 orbColor = hsv2rgb(float3(fract(t * 0.08), 0.7, 1.0));
    col += orbColor * orb * orbPulse * 0.6;

    for (int j = 0; j < 4; j++) {
        float fj = float(j);
        float rayAngle = a + t * (0.5 + fj * 0.15) + fj * 1.57;
        float ray = pow(max(cos(rayAngle * 3.0), 0.0), 20.0);
        ray *= exp(-r * 2.5);
        float3 rayCol = hsv2rgb(float3(fract(fj * 0.25 + t * 0.05), 0.8, 1.0));
        col += rayCol * ray * 0.25;
    }

    for (int k = 0; k < 8; k++) {
        float fk = float(k);
        float pAngle = fk * 0.785 + t * (0.3 + fk * 0.05);
        float pR = 0.4 + sin(t * 0.7 + fk * 1.2) * 0.3;
        float2 pPos = float2(cos(pAngle), sin(pAngle)) * pR;
        float pDist = length(uv - pPos);
        float particle = 0.003 / (pDist * pDist + 0.003);
        float3 pCol = hsv2rgb(float3(fract(fk / 8.0 + t * 0.12), 0.9, 1.0));
        col += pCol * particle * 0.15;
    }

    return half4(half3(col), 1.0);
}
"""

@Composable
fun NeonPulseScreen(onBack: () -> Unit) {
    val (shader, provider) = rememberAppRuntimeShaderOrNull(NEON_PULSE_SHADER)
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
            text = "Neon Pulse",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            color = Color.White,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
@Preview
private fun NeonPulseScreenPreview() {
    NeonPulseScreen(onBack = {})
}
