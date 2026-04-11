package com.meet.shader.animation.cmp.expect_shader

import android.graphics.RuntimeShader
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.ui.graphics.Color

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
class ShaderProviderImpl(
    private val runtimeShader: RuntimeShader,
) : ShaderProvider {

    override fun uniformInt(name: String, value: Int) {
        runtimeShader.setIntUniform(name, value)
    }

    override fun uniformInt(name: String, value1: Int, value2: Int) {
        runtimeShader.setIntUniform(name, value1, value2)
    }

    override fun uniformInt(
        name: String,
        value1: Int,
        value2: Int,
        value3: Int
    ) {
        runtimeShader.setIntUniform(name, value1, value2, value3)
    }

    override fun uniformInt(
        name: String,
        value1: Int,
        value2: Int,
        value3: Int,
        value4: Int
    ) {
        runtimeShader.setIntUniform(name, value1, value2, value3, value4)
    }


    override fun uniformFloat(name: String, value: Float) {
        runtimeShader.setFloatUniform(name, value)
    }

    override fun uniformFloat(name: String, value1: Float, value2: Float) {
        runtimeShader.setFloatUniform(name, value1, value2)
    }

    override fun uniformFloat(name: String, value1: Float, value2: Float, value3: Float) {
        runtimeShader.setFloatUniform(name, value1, value2, value3)
    }

    override fun uniformFloat(
        name: String,
        value1: Float,
        value2: Float,
        value3: Float,
        value4: Float
    ) {
        runtimeShader.setFloatUniform(name, value1, value2, value3, value4)
    }

    override fun uniformFloat(name: String, values: List<Float>) {
        runtimeShader.setFloatUniform(name, values.toFloatArray())
    }

    override fun uniformColor(name: String, r: Float, g: Float, b: Float, a: Float) {
        runtimeShader.setFloatUniform(name, r, g, b, a)
    }

    override fun uniformColor(name: String, color: Color) {
        uniformColor(name, color.red, color.green, color.blue, color.alpha)
    }

}