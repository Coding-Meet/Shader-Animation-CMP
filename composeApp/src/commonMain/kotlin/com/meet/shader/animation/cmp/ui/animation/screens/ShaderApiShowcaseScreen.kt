package com.meet.shader.animation.cmp.ui.animation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.meet.shader.animation.cmp.expect_shader.createAppRuntimeShaderRenderEffect
import com.meet.shader.animation.cmp.expect_shader.createShader
import com.meet.shader.animation.cmp.expect_shader.isShaderAvailable
import com.meet.shader.animation.cmp.expect_shader.rememberAppRuntimeShaderOrNull
import com.meet.shader.animation.cmp.expect_shader.rememberShaderTime

private const val BG_SHADER = """
uniform float2 resolution;
uniform float time;

half4 main(float2 fragCoord) {
    float2 uv = fragCoord / resolution;
    float t = time * 0.4;
    float r = 0.5 + 0.5 * sin(uv.x * 6.0 + t);
    float g = 0.5 + 0.5 * sin(uv.y * 6.0 + t + 2.0);
    float b = 0.5 + 0.5 * sin((uv.x + uv.y) * 4.0 + t + 4.0);
    return half4(r * 0.4, g * 0.3, b * 0.8, 1.0);
}
"""

private const val FIRE_CARD_SHADER = """
uniform float2 resolution;
uniform float time;

float hash(float2 p) { return fract(sin(dot(p, float2(12.9898,78.233))) * 43758.5); }
float noise(float2 p) {
    float2 i = floor(p); float2 f = fract(p);
    f = f*f*(3.0-2.0*f);
    return mix(mix(hash(i),hash(i+float2(1,0)),f.x),mix(hash(i+float2(0,1)),hash(i+float2(1,1)),f.x),f.y);
}
half4 main(float2 fragCoord) {
    float2 uv = fragCoord / resolution;
    uv.y = 1.0 - uv.y;
    float2 p = uv * 3.0; p.y -= time * 2.0;
    float n = noise(p)*0.5 + noise(p*2.0)*0.25 + noise(p*4.0)*0.125;
    float fire = clamp(n * pow(1.0-uv.y,1.5)*2.2, 0.0, 1.0);
    float3 col = mix(float3(0.8,0.1,0.0), float3(1.0,0.9,0.2), fire*fire);
    return half4(half3(col*fire), 1.0);
}
"""

private const val PLASMA_CARD_SHADER = """
uniform float2 resolution;
uniform float time;

half4 main(float2 fragCoord) {
    float2 uv = fragCoord / resolution * 2.0 - 1.0;
    float v = sin(uv.x*4.0+time) + sin(uv.y*4.0+time) + sin((uv.x+uv.y)*3.0+time*1.3) + sin(length(uv)*5.0-time);
    float r = 0.5+0.5*sin(v*3.14);
    float g = 0.5+0.5*sin(v*3.14+2.09);
    float b = 0.5+0.5*sin(v*3.14+4.19);
    return half4(r,g,b,1.0);
}
"""

private const val NEON_CARD_SHADER = """
uniform float2 resolution;
uniform float time;

half4 main(float2 fragCoord) {
    float2 uv = fragCoord / resolution;
    float lines = abs(sin(uv.x*20.0 + time*2.0)) * abs(sin(uv.y*20.0 - time*1.5));
    float glow = pow(lines, 4.0);
    float3 col = float3(glow*0.2, glow*0.9, glow*1.0);
    return half4(half3(col), 1.0);
}
"""

private const val STARFIELD_CARD_SHADER = """
uniform float2 resolution;
uniform float time;

float hash(float2 p) { return fract(sin(dot(p,float2(127.1,311.7)))*43758.5); }
half4 main(float2 fragCoord) {
    float2 uv = fragCoord / resolution;
    float2 grid = floor(uv * 40.0);
    float star = hash(grid);
    float size = hash(grid + 0.5);
    float brightness = step(0.97, star) * (0.5 + 0.5*sin(time*2.0*size + star*6.28));
    return half4(brightness, brightness, brightness*0.8 + brightness*0.2*sin(time), 1.0);
}
"""

private const val FILTER_SHADER = """
uniform shader inputShader;
uniform float2 resolution;
uniform float time;

half4 main(float2 fragCoord) {
    float2 uv = fragCoord / resolution;
    float strength = 0.012;
    float2 offset = float2(
        sin(uv.y * 15.0 + time * 2.0) * strength,
        cos(uv.x * 15.0 + time * 2.0) * strength
    );
    float2 warped = fragCoord + offset * resolution;
    return inputShader.eval(warped);
}
"""

private const val UNIFORMS_SHADER = """
uniform float2 resolution;
uniform float time;
uniform float brightness;
uniform float2 offset;
uniform float3 tint;
uniform float4 rgba;
uniform float palette[5];
uniform int mode;
uniform int2 grid;
uniform int3 flags;
uniform int4 channels;
uniform float4 colorA;
uniform float4 colorB;

half4 main(float2 fragCoord) {
    float2 uv = fragCoord / resolution + offset;
    float base;
    if (mode == 0) {
        base = sin(uv.x * float(grid.x) + time) * cos(uv.y * float(grid.y) + time) * 0.5 + 0.5;
    } else if (mode == 1) {
        base = fract(uv.x * float(grid.x) + uv.y * float(grid.y) + time * 0.3);
    } else {
        float2 c = uv * 2.0 - 1.0;
        base = 1.0 - clamp(length(c) * float(grid.x) * 0.1, 0.0, 1.0);
    }
    int pidx = int(clamp(base * 5.0, 0.0, 4.0));
    float paletteVal;
    if (pidx == 0) paletteVal = palette[0];
    else if (pidx == 1) paletteVal = palette[1];
    else if (pidx == 2) paletteVal = palette[2];
    else if (pidx == 3) paletteVal = palette[3];
    else paletteVal = palette[4];
    float3 colA = float3(colorA.r * float(channels.r), colorA.g * float(channels.g), colorA.b * float(channels.b));
    float3 colB = float3(colorB.r * float(flags.r), colorB.g * float(flags.g), colorB.b * float(flags.b));
    float3 col = mix(colA, colB, base);
    col = mix(col, tint, 0.3) * brightness;
    col = mix(col, rgba.rgb, rgba.a * 0.4);
    col *= paletteVal;
    return half4(half3(col), 1.0);
}
"""

@Composable
fun ShaderApiShowcaseScreen(onBack: () -> Unit) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Background", "Inner Card", "Filter", "All Uniforms")

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        if (isShaderAvailable()) {
            Column(modifier = Modifier.fillMaxSize()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .systemBarsPadding()
                        .padding(top = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                    Text(
                        text = "Shader API Showcase",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                }

                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Color(0xFF1A1A2E),
                    contentColor = Color.White
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = {
                                Text(
                                    text = title,
                                    fontSize = 11.sp,
                                    maxLines = 1
                                )
                            }
                        )
                    }
                }

                when (selectedTab) {
                    0 -> Tab1Background()
                    1 -> Tab2InnerCard()
                    2 -> Tab3Filter()
                    3 -> Tab4AllUniforms()
                }
            }
        } else {
            Text(
                text = "Shaders not supported on this platform",
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
private fun Tab1Background() {
    val timeState = rememberShaderTime()
    val (shader, provider) = rememberAppRuntimeShaderOrNull(BG_SHADER)

    Box(modifier = Modifier.fillMaxSize()) {
        if (isShaderAvailable() && shader != null && provider != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .drawBehind {
                        provider.uniformFloat("resolution", size.width, size.height)
                        provider.uniformFloat("time", timeState.value)
                        drawRect(ShaderBrush(createShader(shader)))
                    }
            )
        } else {
            Box(
                modifier = Modifier.fillMaxSize().background(Color(0xFF1A1A2E)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "Shaders not supported on this platform",
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(Color.Black.copy(alpha = 0.5f))
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            ApiLabel("rememberAppRuntimeShaderOrNull(shaderCode)")
            ApiLabel("Modifier.drawBehind { drawRect(ShaderBrush(createShader(shader))) }")
            ApiLabel("provider.uniformFloat(\"resolution\", width, height)")
            ApiLabel("provider.uniformFloat(\"time\", timeState.value)")
            ApiLabel("isShaderAvailable() → fallback when false")
        }
    }
}

@Composable
private fun Tab2InnerCard() {
    val timeState = rememberShaderTime()
    val (fireShader, fireProvider) = rememberAppRuntimeShaderOrNull(FIRE_CARD_SHADER)
    val (plasmaShader, plasmaProvider) = rememberAppRuntimeShaderOrNull(PLASMA_CARD_SHADER)
    val (neonShader, neonProvider) = rememberAppRuntimeShaderOrNull(NEON_CARD_SHADER)
    val (starShader, starProvider) = rememberAppRuntimeShaderOrNull(STARFIELD_CARD_SHADER)

    val cards = listOf(
        Triple(fireShader, fireProvider, "Fire"),
        Triple(plasmaShader, plasmaProvider, "Plasma"),
        Triple(neonShader, neonProvider, "Neon"),
        Triple(starShader, starProvider, "Starfield")
    )

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.fillMaxSize().padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(cards.size) { i ->
            val (shader, provider, label) = cards[i]
            Column {
                Box(
                    modifier = Modifier
                        .aspectRatio(1f)
                        .clipToBounds()
                        .drawBehind {
                            if (isShaderAvailable() && shader != null && provider != null) {
                                provider.uniformFloat("resolution", size.width, size.height)
                                provider.uniformFloat("time", timeState.value)
                                drawRect(ShaderBrush(createShader(shader)))
                            }
                        }
                )
                Text(
                    text = label,
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

@Composable
private fun Tab3Filter() {
    val timeState = rememberShaderTime()
    val (shader, provider) = rememberAppRuntimeShaderOrNull(FILTER_SHADER)
    var filterEnabled by remember { mutableStateOf(true) }

    val coloredRows = listOf(
        Color(0xFFE040FB) to "uniform shader inputShader;",
        Color(0xFF40C4FF) to "createAppRuntimeShaderRenderEffect(shader, \"inputShader\")",
        Color(0xFF69F0AE) to "graphicsLayer { renderEffect = ... }",
        Color(0xFFFFD740) to "graphicsLayer applied to the content itself",
        Color(0xFFFF6D00) to "inputShader.eval(warpedUV)",
        Color(0xFFE040FB) to "inputShader = this composable's rendered output",
        Color(0xFF40C4FF) to "Use only for post-processing effects",
        Color(0xFF69F0AE) to "Not for background shaders → use drawBehind",
        Color(0xFFFFD740) to "uniformFloat(\"resolution\", width, height)",
        Color(0xFFFF6D00) to "uniformFloat(\"time\", timeState.value)",
    )

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .graphicsLayer {
                    if (isShaderAvailable() && filterEnabled && shader != null && provider != null) {
                        provider.uniformFloat("resolution", size.width, size.height)
                        provider.uniformFloat("time", timeState.value)
                        renderEffect = createAppRuntimeShaderRenderEffect(shader, "inputShader")
                    }
                },
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            items(coloredRows) { (color, text) ->
                Text(
                    text = text,
                    color = color,
                    fontSize = 13.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(Color.Black.copy(alpha = 0.7f))
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                "Ripple filter ON/OFF",
                color = Color.White,
                fontSize = 12.sp,
                fontFamily = FontFamily.Monospace
            )
            Switch(checked = filterEnabled, onCheckedChange = { filterEnabled = it })
        }
    }
}

@Composable
private fun Tab4AllUniforms() {
    val timeState = rememberShaderTime()
    val (shader, provider) = rememberAppRuntimeShaderOrNull(UNIFORMS_SHADER)

    var brightness by remember { mutableFloatStateOf(1f) }
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }
    var tintR by remember { mutableFloatStateOf(0.5f) }
    var tintG by remember { mutableFloatStateOf(0.5f) }
    var tintB by remember { mutableFloatStateOf(1f) }
    var rgbaR by remember { mutableFloatStateOf(1f) }
    var rgbaG by remember { mutableFloatStateOf(0.3f) }
    var rgbaB by remember { mutableFloatStateOf(0.5f) }
    var rgbaA by remember { mutableFloatStateOf(0.3f) }
    var mode by remember { mutableIntStateOf(0) }
    var gridX by remember { mutableIntStateOf(8) }
    var gridY by remember { mutableIntStateOf(8) }
    var flagR by remember { mutableIntStateOf(1) }
    var flagG by remember { mutableIntStateOf(1) }
    var flagB by remember { mutableIntStateOf(1) }
    var chanR by remember { mutableIntStateOf(1) }
    var chanG by remember { mutableIntStateOf(1) }
    var chanB by remember { mutableIntStateOf(1) }
    var chanA by remember { mutableIntStateOf(1) }
    var colorAR by remember { mutableFloatStateOf(0.2f) }
    var colorAG by remember { mutableFloatStateOf(0.5f) }
    var colorAB by remember { mutableFloatStateOf(1f) }
    var colorBR by remember { mutableFloatStateOf(1f) }
    var colorBG by remember { mutableFloatStateOf(0.4f) }
    var colorBB by remember { mutableFloatStateOf(0.1f) }

    val shaderBoxModifier: Modifier.() -> Modifier = {
        drawBehind {
            if (isShaderAvailable() && shader != null && provider != null) {
                provider.uniformFloat("resolution", size.width, size.height)
                provider.uniformFloat("time", timeState.value)
                provider.uniformFloat("brightness", brightness)
                provider.uniformFloat("offset", offsetX * 0.1f, offsetY * 0.1f)
                provider.uniformFloat("tint", tintR, tintG, tintB)
                provider.uniformFloat("rgba", rgbaR, rgbaG, rgbaB, rgbaA)
                provider.uniformFloat("palette", listOf(0.2f, 0.4f, 0.6f, 0.8f, 1.0f))
                provider.uniformInt("mode", mode)
                provider.uniformInt("grid", gridX, gridY)
                provider.uniformInt("flags", flagR, flagG, flagB)
                provider.uniformInt("channels", chanR, chanG, chanB, chanA)
                provider.uniformColor("colorA", Color(colorAR, colorAG, colorAB))
                provider.uniformColor("colorB", colorBR, colorBG, colorBB, 1f)
                drawRect(ShaderBrush(createShader(shader)))
            }
        }
    }

    val sliderItems: LazyListScope.() -> Unit = {
        item { UniformSection("uniformFloat(\"brightness\", Float)") }
        item {
            UniformSlider("brightness = ${brightness.fmt()}", brightness, 0f, 2f) {
                brightness = it
            }
        }

        item { UniformSection("uniformFloat(\"offset\", Float, Float) → float2") }
        item { UniformSlider("offset.x = ${offsetX.fmt()}", offsetX, -1f, 1f) { offsetX = it } }
        item { UniformSlider("offset.y = ${offsetY.fmt()}", offsetY, -1f, 1f) { offsetY = it } }

        item { UniformSection("uniformFloat(\"tint\", Float, Float, Float) → float3") }
        item { UniformSlider("tint.r = ${tintR.fmt()}", tintR, 0f, 1f) { tintR = it } }
        item { UniformSlider("tint.g = ${tintG.fmt()}", tintG, 0f, 1f) { tintG = it } }
        item { UniformSlider("tint.b = ${tintB.fmt()}", tintB, 0f, 1f) { tintB = it } }

        item { UniformSection("uniformFloat(\"rgba\", Float, Float, Float, Float) → float4") }
        item { UniformSlider("rgba.r = ${rgbaR.fmt()}", rgbaR, 0f, 1f) { rgbaR = it } }
        item { UniformSlider("rgba.g = ${rgbaG.fmt()}", rgbaG, 0f, 1f) { rgbaG = it } }
        item { UniformSlider("rgba.b = ${rgbaB.fmt()}", rgbaB, 0f, 1f) { rgbaB = it } }
        item { UniformSlider("rgba.a = ${rgbaA.fmt()}", rgbaA, 0f, 1f) { rgbaA = it } }

        item { UniformSection("uniformFloat(\"palette\", List<Float>) → float[5]") }
        item { ApiLabel("  listOf(0.2f, 0.4f, 0.6f, 0.8f, 1.0f)  (fixed)") }

        item { UniformSection("uniformInt(\"mode\", Int) → 0=sine, 1=fract, 2=radial") }
        item { UniformSlider("mode = $mode", mode.toFloat(), 0f, 2f) { mode = it.toInt() } }

        item { UniformSection("uniformInt(\"grid\", Int, Int) → int2") }
        item { UniformSlider("grid.x = $gridX", gridX.toFloat(), 1f, 20f) { gridX = it.toInt() } }
        item { UniformSlider("grid.y = $gridY", gridY.toFloat(), 1f, 20f) { gridY = it.toInt() } }

        item { UniformSection("uniformInt(\"flags\", Int, Int, Int) → int3") }
        item { UniformSlider("flags.r = $flagR", flagR.toFloat(), 0f, 1f) { flagR = it.toInt() } }
        item { UniformSlider("flags.g = $flagG", flagG.toFloat(), 0f, 1f) { flagG = it.toInt() } }
        item { UniformSlider("flags.b = $flagB", flagB.toFloat(), 0f, 1f) { flagB = it.toInt() } }

        item { UniformSection("uniformInt(\"channels\", Int, Int, Int, Int) → int4") }
        item { UniformSlider("ch.r = $chanR", chanR.toFloat(), 0f, 1f) { chanR = it.toInt() } }
        item { UniformSlider("ch.g = $chanG", chanG.toFloat(), 0f, 1f) { chanG = it.toInt() } }
        item { UniformSlider("ch.b = $chanB", chanB.toFloat(), 0f, 1f) { chanB = it.toInt() } }
        item { UniformSlider("ch.a = $chanA", chanA.toFloat(), 0f, 1f) { chanA = it.toInt() } }

        item { UniformSection("uniformColor(\"colorA\", Color) → Compose Color") }
        item { UniformSlider("colorA.r = ${colorAR.fmt()}", colorAR, 0f, 1f) { colorAR = it } }
        item { UniformSlider("colorA.g = ${colorAG.fmt()}", colorAG, 0f, 1f) { colorAG = it } }
        item { UniformSlider("colorA.b = ${colorAB.fmt()}", colorAB, 0f, 1f) { colorAB = it } }

        item { UniformSection("uniformColor(\"colorB\", r, g, b, a) → RGBA components") }
        item { UniformSlider("colorB.r = ${colorBR.fmt()}", colorBR, 0f, 1f) { colorBR = it } }
        item { UniformSlider("colorB.g = ${colorBG.fmt()}", colorBG, 0f, 1f) { colorBG = it } }
        item { UniformSlider("colorB.b = ${colorBB.fmt()}", colorBB, 0f, 1f) { colorBB = it } }

        item { Spacer(modifier = Modifier.height(32.dp)) }
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        if (maxWidth >= 600.dp) {
            Row(modifier = Modifier.fillMaxSize()) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .shaderBoxModifier()
                )
                Spacer(
                    modifier = Modifier.width(1.dp).fillMaxHeight()
                        .background(Color.White.copy(alpha = 0.1f))
                )
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    contentPadding = PaddingValues(bottom = 32.dp)
                ) {
                    sliderItems()
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 32.dp)
            ) {
                stickyHeader {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .shaderBoxModifier()
                    )
                }
                sliderItems()
            }
        }
    }
}

@Composable
private fun UniformSection(label: String) {
    Text(
        text = label,
        color = Color(0xFF69F0AE),
        fontSize = 10.sp,
        fontFamily = FontFamily.Monospace,
        modifier = Modifier.padding(start = 12.dp, top = 12.dp, bottom = 2.dp)
    )
}

@Composable
private fun UniformSlider(
    label: String,
    value: Float,
    min: Float,
    max: Float,
    onValueChange: (Float) -> Unit
) {
    Column(modifier = Modifier.padding(horizontal = 12.dp)) {
        Text(
            text = label,
            color = Color.White.copy(alpha = 0.8f),
            fontSize = 10.sp,
            fontFamily = FontFamily.Monospace
        )
        Slider(value = value, onValueChange = onValueChange, valueRange = min..max)
    }
}

@Composable
private fun ApiLabel(text: String) {
    Text(
        text = text,
        color = Color(0xFF40C4FF),
        fontSize = 10.sp,
        fontFamily = FontFamily.Monospace
    )
}

private fun Float.fmt(): String {
    val intPart = this.toInt()
    val fracPart = ((this - intPart) * 100).toInt().let { if (it < 0) -it else it }
    return "$intPart.${fracPart.toString().padStart(2, '0')}"
}
