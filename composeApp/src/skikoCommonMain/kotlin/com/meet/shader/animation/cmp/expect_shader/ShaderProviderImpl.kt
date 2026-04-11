package com.meet.shader.animation.cmp.expect_shader

import androidx.compose.ui.graphics.Color
import org.jetbrains.skia.RuntimeShaderBuilder

class ShaderProviderImpl(
    private val runtimeShaderBuilder: RuntimeShaderBuilder,
) : ShaderProvider {

    override fun uniformInt(name: String, value: Int) {
        runtimeShaderBuilder.uniform(name, value)
    }

    override fun uniformInt(name: String, value1: Int, value2: Int) {
        runtimeShaderBuilder.uniform(name, value1, value2)
    }

    override fun uniformInt(name: String, value1: Int, value2: Int, value3: Int) {
        runtimeShaderBuilder.uniform(name, value1, value2, value3)
    }

    override fun uniformInt(name: String, value1: Int, value2: Int, value3: Int, value4: Int) {
        runtimeShaderBuilder.uniform(name, value1, value2, value3, value4)
    }

    override fun uniformFloat(name: String, value: Float) {
        runtimeShaderBuilder.uniform(name, value)
    }

    override fun uniformFloat(name: String, value1: Float, value2: Float) {
        runtimeShaderBuilder.uniform(name, value1, value2)
    }

    override fun uniformFloat(name: String, value1: Float, value2: Float, value3: Float) {
        runtimeShaderBuilder.uniform(name, value1, value2, value3)
    }

    override fun uniformFloat(
        name: String,
        value1: Float,
        value2: Float,
        value3: Float,
        value4: Float
    ) {
        runtimeShaderBuilder.uniform(name, value1, value2, value3, value4)
    }

    override fun uniformFloat(name: String, values: List<Float>) {
        runtimeShaderBuilder.uniform(name, values.toFloatArray())
    }

    override fun uniformColor(name: String, r: Float, g: Float, b: Float, a: Float) {
        runtimeShaderBuilder.uniform(name, r, g, b, a)
    }
    override fun uniformColor(name: String, color: Color) {
        uniformColor(name, color.red, color.green, color.blue, color.alpha)
    }
}