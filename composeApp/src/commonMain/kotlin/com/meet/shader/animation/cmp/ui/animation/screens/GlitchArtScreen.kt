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

private const val GLITCH_ART_SHADER = """
uniform float2 resolution;
uniform float time;

float hashVal(float2 p) {
    return fract(sin(dot(p, float2(12.9898, 78.233))) * 43758.5453);
}

half4 main(float2 fragCoord) {
    float2 uv = fragCoord / resolution;
    float t = time;

    float blockY = floor(uv.y * 20.0);
    float glitchSeed = floor(t * 3.0);
    float blockRand = hashVal(float2(blockY, glitchSeed));
    float isGlitched = step(0.85, blockRand);
    float offset = (hashVal(float2(blockY + 1.0, glitchSeed)) - 0.5) * 0.15 * isGlitched;

    float2 uvR = uv + float2(offset + sin(t * 10.0) * 0.005, 0.0);
    float2 uvG = uv;
    float2 uvB = uv - float2(offset + sin(t * 10.0) * 0.005, 0.0);

    float2 grid  = fract(uv  * 8.0 + float2(t * 0.2, t * 0.1));
    float2 gridR = fract(uvR * 8.0 + float2(t * 0.2, t * 0.1));
    float2 gridB = fract(uvB * 8.0 + float2(t * 0.2, t * 0.1));

    float basePattern = step(0.4, grid.x) * step(0.4, grid.y)
                      + sin(uv.x * 30.0 + t * 5.0) * sin(uv.y * 20.0 - t * 3.0) * 0.3;
    float patR = step(0.4, gridR.x) * step(0.4, gridR.y) + sin(uvR.x * 30.0 + t * 5.0) * 0.3;
    float patB = step(0.4, gridB.x) * step(0.4, gridB.y) + sin(uvB.x * 30.0 + t * 5.0) * 0.3;

    float3 col = float3(patR * 0.9, basePattern * 0.9, patB * 0.9);

    float scan = sin(uv.y * 300.0) * 0.5 + 0.5;
    col *= 0.85 + scan * 0.15;

    float noiseLine = step(0.97, hashVal(float2(floor(uv.y * 200.0), floor(t * 20.0))));
    col += float3(0.5, 1.0, 0.8) * noiseLine * 0.4;

    float2 vc = uv - 0.5;
    col *= 1.0 - dot(vc, vc) * 0.8;

    return half4(half3(col), 1.0);
}
"""

@Composable
fun GlitchArtScreen(onBack: () -> Unit) {
    val time by rememberShaderTime()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .shader(GLITCH_ART_SHADER) {
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
            text = "Glitch Art",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            color = Color.White,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}
@Composable
@Preview
private fun GlitchArtScreenPreview(){
    GlitchArtScreen(onBack = {})
}