package com.meet.shader.animation.cmp.ui.animation.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.meet.shader.animation.cmp.expect_shader.rememberShaderTime
import com.meet.shader.animation.cmp.expect_shader.shader

private const val SHADER_HERO_SHADER = """
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

float fbmHero(float2 p) {
    float t2 = 0.0;
    float a2 = 1.0;
    // Inlined rotation: col0=(1.0,-0.5), col1=(0.2,1.2)
    for (int i = 0; i < 5; i++) {
        t2 += a2 * valueNoise(p);
        float2 rp = float2(1.0 * p.x + 0.2 * p.y, -0.5 * p.x + 1.2 * p.y);
        p = rp * 2.0;
        a2 *= 0.5;
    }
    return t2;
}

float heroCloudsFn(float2 p) {
    float d = 1.0;
    float t2 = 0.0;
    for (float i = 0.0; i < 3.0; i += 1.0) {
        float a2 = d * fbmHero(float2(i * 10.0 + p.x * 0.2 + 0.2 * (1.0 + i) * p.y + d + i * i + p.x, p.y));
        t2 = mix(t2, d, a2);
        d = a2;
        p *= 2.0 / (i + 1.0);
    }
    return t2;
}

half4 main(float2 fragCoord) {
    float2 uv = (fragCoord - 0.5 * resolution) / min(resolution.x, resolution.y);
    float2 st = uv * float2(2.0, 1.0);

    float3 col = float3(0.0);
    float bg = heroCloudsFn(float2(st.x + time * 0.5, -st.y));

    uv *= 1.0 - 0.3 * (sin(time * 0.2) * 0.5 + 0.5);

    for (float i = 1.0; i < 12.0; i += 1.0) {
        uv += 0.1 * cos(i * float2(0.1 + 0.01 * i, 0.8) + i * i + time * 0.5 + 0.1 * uv.x);
        float2 p = uv;
        float d = length(p);
        col += 0.00125 / d * (cos(sin(i) * float3(1.0, 2.0, 3.0)) + 1.0);
        float b = valueNoise(float2(i + p.x + bg * 1.731, i + p.y + bg * 1.731));
        col += 0.002 * b / length(max(p, float2(b * p.x * 0.02, p.y)));
        col = mix(col, float3(bg * 0.25, bg * 0.137, bg * 0.05), d);
    }

    return half4(half3(col), 1.0);
}
"""

@Composable
fun ShaderHeroScreen(onBack: () -> Unit) {
    val time by rememberShaderTime()
    var appeared by remember { mutableStateOf(false) }

    val alpha by animateFloatAsState(
        targetValue = if (appeared) 1f else 0f,
        animationSpec = tween(durationMillis = 1000)
    )

    val offsetY by animateFloatAsState(
        targetValue = if (appeared) 0f else 30f,
        animationSpec = tween(durationMillis = 1000)
    )

    LaunchedEffect(Unit) {
        appeared = true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .shader(SHADER_HERO_SHADER) {
                uniformFloat("time", time)
            }
    ) {
        IconButton(
            onClick = onBack,
            modifier = Modifier.align(Alignment.TopStart).systemBarsPadding().padding(8.dp)
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .systemBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(60.dp))

            // Trusted by capsule
            Row(
                modifier = Modifier
                    .graphicsLayer {
                        this.alpha = alpha
                        this.translationY = -offsetY // Offset from top
                    }
                    .clip(CircleShape)
                    .background(Color(0xFFFFA500).copy(alpha = 0.1f))
                    .border(1.dp, Color(0xFFFFA500).copy(alpha = 0.3f), CircleShape)
                    .padding(horizontal = 20.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text("✨")
                Text(
                    text = "Trusted by forward-thinking teams.",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color(0xFFFFA500).copy(alpha = 0.9f)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Headline
            Text(
                text = "Launch Your Workflow Into Orbit",
                style = MaterialTheme.typography.displaySmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 42.sp,
                    lineHeight = 48.sp
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .graphicsLayer {
                        this.alpha = alpha
                        this.translationY = offsetY
                    }
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color(0xFFFFA500),
                                Color(0xFFFFFF00),
                                Color(0xFFFFA500).copy(alpha = 0.8f)
                            )
                        )
                    )
                    .graphicsLayer(alpha = 0.99f)
            )

            Spacer(modifier = Modifier.weight(1f))

            // Subtitle
            Text(
                text = "Supercharge productivity with AI-powered automation and integrations built for the next generation of teams.",
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.Light,
                    fontSize = 14.sp
                ),
                color = Color(0xFFFFA500).copy(alpha = 0.85f),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .graphicsLayer {
                        this.alpha = alpha
                        this.translationY = offsetY
                    }
                    .padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // CTA Buttons
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .graphicsLayer {
                        this.alpha = alpha
                        this.translationY = offsetY
                    },
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Button(
                    onClick = {},
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(Color(0xFFFFA500), Color(0xFFFFFF00))
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "Get Started for Free",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color.Black
                        )
                    }
                }

                OutlinedButton(
                    onClick = {},
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = CircleShape,
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = Color(0xFFFFA500).copy(alpha = 0.1f),
                        contentColor = Color(0xFFFFA500).copy(alpha = 0.9f)
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFA500).copy(alpha = 0.3f))
                ) {
                    Text(
                        "Explore Features",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
@Preview
private fun ShaderHeroScreenPreview(){
    ShaderHeroScreen(onBack = {})
}