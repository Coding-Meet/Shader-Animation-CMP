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
import androidx.compose.ui.unit.dp
import com.meet.shader.animation.cmp.expect_shader.rememberShaderTime
import com.meet.shader.animation.cmp.expect_shader.shader

private const val ANOMALOUS_MATTER_SHADER = """
uniform float2 resolution;
uniform float time;

float hash3to1(float3 p) {
    return fract(sin(dot(p, float3(127.1, 311.7, 74.7))) * 43758.5453);
}

float snoise3D(float3 v) {
    float3 i = floor(v + (v.x + v.y + v.z) / 3.0);
    float3 x0 = v - i + (i.x + i.y + i.z) / 6.0;
    float3 g = step(x0.yzx, x0.xyz);
    float3 l = 1.0 - g;
    float3 i1 = min(g, l.zxy);
    float3 i2 = max(g, l.zxy);
    float3 x1 = x0 - i1 + 1.0 / 6.0;
    float3 x2 = x0 - i2 + 1.0 / 3.0;
    float3 x3 = x0 - 0.5;
    float4 m = max(0.6 - float4(dot(x0,x0), dot(x1,x1), dot(x2,x2), dot(x3,x3)), 0.0);
    m = m * m * m * m;
    float4 px = float4(hash3to1(i), hash3to1(i+i1), hash3to1(i+i2), hash3to1(i+1.0));
    return dot(m, px) * 4.0 - 1.0;
}

float sampleDisp(float3 dir, float t) {
    float d = snoise3D(dir * 2.5 + float3(t * 0.4, t * 0.25, t * 0.55)) * 0.25;
    d += snoise3D(dir * 5.0 + float3(-t * 0.3, t * 0.5, t * 0.2)) * 0.12;
    d += snoise3D(dir * 10.0 + float3(t * 0.2, -t * 0.35, t * 0.45)) * 0.06;
    return d;
}

half4 main(float2 fragCoord) {
    float2 uv = (fragCoord * 2.0 - resolution) / min(resolution.x, resolution.y);
    float t = time * 0.25;

    float3 ro = float3(0.0, 0.0, 3.2);
    float3 rd = normalize(float3(uv, -1.5));
    float sphereR = 1.3;

    float3 col = float3(0.0);
    float tRay = 0.0;
    bool hit = false;
    float3 hitPos = float3(0.0);
    float3 hitNormal = float3(0.0, 1.0, 0.0);

    for (int i = 0; i < 64; i++) {
        float3 p = ro + rd * tRay;
        float baseR = length(p);
        float3 dir = normalize(p);
        float surfaceR = sphereR + sampleDisp(dir, t);
        float d = baseR - surfaceR;

        if (d < 0.005) {
            hit = true;
            hitPos = p;
            float eps = 0.02;
            float3 n = float3(0.0);
            float3 ep;
            float3 em;
            ep = normalize(p + float3(eps, 0.0, 0.0));
            em = normalize(p - float3(eps, 0.0, 0.0));
            n.x = (length(p + float3(eps,0.0,0.0)) - (sphereR + sampleDisp(ep, t)))
                - (length(p - float3(eps,0.0,0.0)) - (sphereR + sampleDisp(em, t)));
            ep = normalize(p + float3(0.0, eps, 0.0));
            em = normalize(p - float3(0.0, eps, 0.0));
            n.y = (length(p + float3(0.0,eps,0.0)) - (sphereR + sampleDisp(ep, t)))
                - (length(p - float3(0.0,eps,0.0)) - (sphereR + sampleDisp(em, t)));
            ep = normalize(p + float3(0.0, 0.0, eps));
            em = normalize(p - float3(0.0, 0.0, eps));
            n.z = (length(p + float3(0.0,0.0,eps)) - (sphereR + sampleDisp(ep, t)))
                - (length(p - float3(0.0,0.0,eps)) - (sphereR + sampleDisp(em, t)));
            hitNormal = normalize(n);
            break;
        }

        tRay += max(d * 0.5, 0.01);
        if (tRay > 6.0) break;
    }

    if (hit) {
        float3 n = hitNormal;
        float3 dir = normalize(hitPos);

        float noiseWarp = snoise3D(dir * 3.0 + float3(t * 0.3)) * 0.4;
        float theta = atan(dir.z, dir.x) + noiseWarp;
        float phi = acos(clamp(dir.y, -1.0, 1.0)) + noiseWarp * 0.6;

        float gridFreq = 24.0;
        float lineTheta = abs(fract(theta * gridFreq / 6.2832) - 0.5) * 2.0;
        float linePhi   = abs(fract(phi * gridFreq / 3.14159) - 0.5) * 2.0;
        float lineDiag  = abs(fract((theta + phi) * gridFreq * 0.5 / 3.14159) - 0.5) * 2.0;

        float wire = min(min(lineTheta, linePhi), lineDiag);
        float wireEdge = 1.0 - smoothstep(0.03, 0.10, wire);

        float fresnel = 1.0 - max(dot(n, normalize(ro - hitPos)), 0.0);
        fresnel = pow(fresnel, 2.0);

        float3 lightDir = normalize(float3(0.5, 1.0, 2.0));
        float diffuse = max(dot(n, lightDir), 0.0) * 0.5 + 0.5;

        float3 wireColor = float3(0.75, 0.78, 0.82);
        float3 edgeGlow  = float3(0.5, 0.55, 0.6);

        float3 surfaceCol = wireColor * wireEdge * diffuse;
        surfaceCol += edgeGlow * fresnel * 0.6;
        float innerGlow = (1.0 - wireEdge) * fresnel * 0.08;
        surfaceCol += float3(0.3, 0.35, 0.4) * innerGlow;

        float depthFade = smoothstep(0.0, 0.5, dot(n, normalize(ro - hitPos)));
        surfaceCol *= 0.3 + depthFade * 0.7;

        col = surfaceCol;
    }

    float sphereDist = length(uv);
    float glow = exp(-sphereDist * 2.0) * 0.06;
    col += float3(0.4, 0.42, 0.45) * glow;

    return half4(half3(col), 1.0);
}
"""

@Composable
fun AnomalousMatterScreen(onBack: () -> Unit) {
    val time by rememberShaderTime()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .shader(ANOMALOUS_MATTER_SHADER) {
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
            text = "Anomalous Matter",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            color = Color.White,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}
