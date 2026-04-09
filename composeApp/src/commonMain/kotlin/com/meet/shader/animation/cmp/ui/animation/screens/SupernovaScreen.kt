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

private const val SUPERNOVA_SHADER = """
uniform float2 resolution;
uniform float time;

float hashVal(float2 p) {
    return fract(sin(dot(p, float2(12.9898, 78.233))) * 43758.5453);
}

float3 hsv2rgb(float3 c) {
    float3 p = abs(fract(float3(c.x) + float3(0.0, 2.0/3.0, 1.0/3.0)) * 6.0 - 3.0);
    return c.z * mix(float3(1.0), clamp(p - 1.0, 0.0, 1.0), c.y);
}

half4 main(float2 fragCoord) {
    float2 uv = (fragCoord * 2.0 - resolution) / min(resolution.x, resolution.y);
    float r = length(uv);
    float a = atan(uv.y, uv.x);
    float t = time * 0.4;

    float coreSize = 0.08 + sin(t * 3.0) * 0.02;
    float core = coreSize / (r + coreSize);
    core = pow(core, 2.0);

    float3 col = float3(0.0);
    for (int i = 0; i < 4; i++) {
        float fi = float(i);
        float waveR = fract(t * 0.3 + fi * 0.25) * 2.5;
        float waveFade = exp(-fract(t * 0.3 + fi * 0.25) * 3.0);
        float wave = exp(-abs(r - waveR) * 20.0) * waveFade;
        float3 waveCol = hsv2rgb(float3(fract(fi * 0.15 + 0.05), 0.8, 1.0));
        col += waveCol * wave;
    }

    float rays = 0.0;
    for (int j = 0; j < 12; j++) {
        float fj = float(j);
        float rayAngle = fj * 0.5236 + t * 0.2 + sin(t + fj) * 0.3;
        float ray = pow(max(cos((a - rayAngle) * 6.0), 0.0), 40.0);
        ray *= exp(-r * 1.5) * (0.5 + 0.5 * sin(t * 2.0 + fj));
        rays += ray;
    }
    col += float3(1.0, 0.6, 0.2) * rays * 0.4;

    float3 coreCol = mix(float3(1.0, 0.4, 0.1), float3(1.0, 1.0, 0.9), smoothstep(0.3, 0.0, r));
    col += coreCol * core;

    for (int k = 0; k < 20; k++) {
        float fk = float(k);
        float pAngle = hashVal(float2(fk, 0.0)) * 6.283;
        float pSpeed = 0.3 + hashVal(float2(fk, 1.0)) * 0.7;
        float pR = fract(t * pSpeed * 0.2 + hashVal(float2(fk, 2.0))) * 1.8;
        float2 pPos = float2(cos(pAngle + t * 0.1), sin(pAngle + t * 0.1)) * pR;
        float pDist = length(uv - pPos);
        float p = 0.002 / (pDist * pDist + 0.002);
        float pFade = exp(-pR * 2.0);
        col += float3(1.0, 0.8, 0.4) * p * pFade * 0.08;
    }

    return half4(half3(col), 1.0);
}
"""

@Composable
fun SupernovaScreen(onBack: () -> Unit) {
    val time by rememberShaderTime()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .shader(SUPERNOVA_SHADER) {
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
            text = "Supernova",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            color = Color.White,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
@Preview
private fun SupernovaScreenPreview(){
    SupernovaScreen(onBack = {})
}