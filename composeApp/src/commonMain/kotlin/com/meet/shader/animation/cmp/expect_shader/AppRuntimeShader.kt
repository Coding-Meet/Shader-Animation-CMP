package com.meet.shader.animation.cmp.expect_shader

import androidx.compose.animation.core.withInfiniteAnimationFrameMillis
import androidx.compose.runtime.Composable
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.RenderEffect
import androidx.compose.ui.graphics.Shader
import kotlin.time.Clock

// check Shader Availability
// only Android 13+ support
expect fun isShaderAvailable(): Boolean


// Platform specific shader instance
// Android → RuntimeShader (AGSL)
// Desktop → Skia Shader
@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect class AppRuntimeShader


// Compile shader code (AGSL string → shader object)
// Heavy operation → always use with remember
expect fun createAppRuntimeShader(
    shaderCode: String
): AppRuntimeShader


// Convert shader → RenderEffect (used with graphicsLayer)
//
// This API is intended for applying shaders as a post-processing effect.
//
// shaderName is required when the shader processes existing UI content.
//
// If your shader includes:
//     uniform shader inputShader;
//
// Then you must pass the same name:
//
//     createAppRuntimeShaderRenderEffect(shader, "inputShader")
//
// Behavior:
//
// - With inputShader:
//     Existing UI content is passed into the shader and modified
//
// - Without inputShader:
//     Shader replaces the entire layer output
//
// Common mistakes:
//
// - Declaring inputShader but not passing shaderName → no output or incorrect result
// - Passing shaderName without declaring uniform shader → shader fails silently
//
expect fun createAppRuntimeShaderRenderEffect(
    appRuntimeShader: AppRuntimeShader,
    shaderName: String
): RenderEffect


// Provides uniform setters (time, resolution, touch, color, etc.)
//
// Example:
// provider.uniformFloat("time", time)
// provider.uniformFloat("resolution", width, height)
expect fun createShaderProvider(
    appRuntimeShader: AppRuntimeShader
): ShaderProvider


// Convert AppRuntimeShader → Shader (for Canvas / drawBehind)
//
// Best for:
// - Background shaders
// - Inner composables (safe usage)
expect fun createShader(
    appRuntimeShader: AppRuntimeShader,
): Shader


@Composable
fun rememberAppRuntimeShader(
    shaderCode: String,
): Pair<AppRuntimeShader, ShaderProvider> {
    val appRuntimeShader = remember {
        createAppRuntimeShader(shaderCode)
    }
    val shaderProvider = remember(appRuntimeShader) {
        createShaderProvider(appRuntimeShader)
    }

    return Pair(appRuntimeShader, shaderProvider)
}


@Composable
fun rememberAppRuntimeShaderOrNull(
    shaderCode: String,
): Pair<AppRuntimeShader?, ShaderProvider?> {

    val appRuntimeShader = remember {
        if (isShaderAvailable())
            createAppRuntimeShader(shaderCode)
        else null
    }

    val shaderProvider = remember(appRuntimeShader) {
        if (appRuntimeShader != null)
            createShaderProvider(appRuntimeShader)
        else null
    }
    return Pair(appRuntimeShader, shaderProvider)
}

@Composable
fun rememberShaderTime() = produceState(0f) {

    // Start time in milliseconds
    val start = Clock.System.now().toEpochMilliseconds()

    while (true) {
        withInfiniteAnimationFrameMillis {

            // Current time
            val now = Clock.System.now().toEpochMilliseconds()

            // Convert to seconds (float)
            value = (now - start) / 1000f
        }
    }

}