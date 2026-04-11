package com.meet.shader.animation.cmp.expect_shader

import androidx.compose.ui.graphics.RenderEffect
import androidx.compose.ui.graphics.Shader
import androidx.compose.ui.graphics.asComposeRenderEffect
import org.jetbrains.skia.ImageFilter
import org.jetbrains.skia.RuntimeEffect
import org.jetbrains.skia.RuntimeShaderBuilder

actual fun isShaderAvailable(): Boolean = true


actual typealias AppRuntimeShader = RuntimeShaderBuilder

actual fun createAppRuntimeShader(shaderCode: String): AppRuntimeShader {
    val effect = RuntimeEffect.makeForShader(shaderCode)
    return RuntimeShaderBuilder(effect)
}

actual fun createAppRuntimeShaderRenderEffect(
    appRuntimeShader: AppRuntimeShader,
    shaderName: String
): RenderEffect {
    return ImageFilter.makeRuntimeShader(appRuntimeShader, shaderName, null).asComposeRenderEffect()
}

actual fun createShaderProvider(
    appRuntimeShader: AppRuntimeShader
): ShaderProvider = ShaderProviderImpl(
    appRuntimeShader
)

actual fun createShader(
    appRuntimeShader: AppRuntimeShader,
): Shader {
    return appRuntimeShader.makeShader()
}