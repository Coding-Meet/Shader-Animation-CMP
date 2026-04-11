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

private const val FIRE_SHADER = """
uniform shader inputShader; // 👈 REQUIRED
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
    // 👇 original UI content
    half4 base = inputShader.eval(fragCoord);

    float2 uv = fragCoord / resolution;
    float2 nUV = uv;
    nUV.y = 1.0 - nUV.y;

    float2 p = nUV * float2(4.0, 4.0);
    p.y -= time * 3.0;

    float n = 0.0;
    float amp = 0.5;
    float2 q = p;

    for (int i = 0; i < 5; i++) {
        n += amp * valueNoise(q);
        q *= 2.0;
        amp *= 0.5;
    }

    float mask = pow(1.0 - nUV.y, 1.5);
    float fire = n * mask * 2.0;
    fire = clamp(fire, 0.0, 1.0);

    float3 col;
    if (fire < 0.33) {
        col = mix(float3(0.0), float3(0.8, 0.1, 0.0), fire / 0.33);
    } else if (fire < 0.66) {
        col = mix(float3(0.8, 0.1, 0.0), float3(1.0, 0.5, 0.0), (fire - 0.33) / 0.33);
    } else {
        col = mix(float3(1.0, 0.5, 0.0), float3(1.0, 1.0, 0.3), (fire - 0.66) / 0.34);
    }

    // 👇 fire glow
    float3 fireColor = col * fire;

    // 👇 blend with UI (IMPORTANT)
    float3 finalColor = base.rgb + fireColor;

    return half4(finalColor, 1.0);
}
"""

@Composable
fun FireScreen(onBack: () -> Unit) {
    val (shader, provider) = rememberShaderInstanceOrNull(FIRE_SHADER)
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
            text = "Fire",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            color = Color.White,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
@Preview
private fun FireScreenPreview() {
    FireScreen(onBack = {})
}
