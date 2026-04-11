package com.meet.shader.animation.cmp.expect_shader


import android.graphics.RuntimeShader
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.ui.graphics.RenderEffect
import androidx.compose.ui.graphics.Shader
import androidx.compose.ui.graphics.asComposeRenderEffect

actual fun isShaderAvailable(): Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU


actual typealias AppRuntimeShader = RuntimeShader

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
actual fun createAppRuntimeShader(shaderCode: String): AppRuntimeShader = RuntimeShader(shaderCode)

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
actual fun createAppRuntimeShaderRenderEffect(
    appRuntimeShader: AppRuntimeShader,
    shaderName: String
): RenderEffect {
    return android.graphics.RenderEffect.createRuntimeShaderEffect(appRuntimeShader, shaderName)
        .asComposeRenderEffect()
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
actual fun createShaderProvider(
    appRuntimeShader: AppRuntimeShader
): ShaderProvider = ShaderProviderImpl(
    appRuntimeShader
)

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
actual fun createShader(appRuntimeShader: AppRuntimeShader): Shader {
    return appRuntimeShader
}