package com.meet.shader.animation.cmp.ui.animation.screens

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.meet.shader.animation.cmp.expect_shader.createShader
import com.meet.shader.animation.cmp.expect_shader.isShaderAvailable
import com.meet.shader.animation.cmp.expect_shader.rememberAppRuntimeShader
import com.meet.shader.animation.cmp.expect_shader.rememberShaderTime

const val UNIVERSAL_RIPPLE_SHADER = """
uniform float2 resolution;
uniform float time;
uniform float2 touch;
uniform float4 color;

half4 main(in float2 fragCoord) {
    float2 uv = fragCoord / resolution.xy;
    float2 touchUV = touch / resolution.xy;

    float dist = distance(uv, touchUV);

    float ripple = sin(dist * 40.0 - time * 4.0);
    ripple *= exp(-dist * 4.0);

    float ring = smoothstep(0.03, 0.0, abs(dist - 0.2));

    float3 base = float3(0.05, 0.08, 0.12);
    float3 glow = color.rgb * ripple;

    float3 finalColor = base + glow + ring;

    return half4(finalColor, 1.0);
}
"""

@Composable
fun TouchShaderAnimationScreen(onBack: () -> Unit) {

    Box(
        modifier = Modifier
            .fillMaxSize()

    ) {
        if (isShaderAvailable()) {
            val (shader, provider) = rememberAppRuntimeShader(UNIVERSAL_RIPPLE_SHADER)
            val time by rememberShaderTime()
            var touch by remember { mutableStateOf(Offset.Zero) }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectTapGestures(onPress = { touch = it })
                    }
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDragStart = { touch = it },
                            onDrag = { change, _ -> touch = change.position }
                        )
                    }
                    .drawBehind {
                        provider.uniformFloat("resolution", size.width, size.height)
                        provider.uniformFloat("time", time)
                        provider.uniformFloat("touch", touch.x, touch.y)
                        provider.uniformColor("color", Color(0xFF4FC3F7))

                        drawRect(ShaderBrush(createShader(appRuntimeShader = shader)))
                    }
            )
        } else {
            Text(
                text = "Requires Android 13+",
                color = Color.White,
                modifier = Modifier.align(Alignment.Center)
            )
        }
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
            text = "Touch Shader Animation",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            color = Color.White,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
@Preview
private fun TouchShaderAnimationScreenPreview() {
    TouchShaderAnimationScreen(onBack = {})
}
