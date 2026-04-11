package com.meet.shader.animation.cmp.expect_shader

import androidx.compose.ui.graphics.Color

interface ShaderProvider {
    fun uniformInt(name: String, value: Int)
    fun uniformInt(name: String, value1: Int, value2: Int)
    fun uniformInt(name: String, value1: Int, value2: Int, value3: Int)
    fun uniformInt(name: String, value1: Int, value2: Int, value3: Int, value4: Int)

    fun uniformFloat(name: String, value: Float)
    fun uniformFloat(name: String, value1: Float, value2: Float)
    fun uniformFloat(name: String, value1: Float, value2: Float, value3: Float)
    fun uniformFloat(name: String, value1: Float, value2: Float, value3: Float, value4: Float)
    fun uniformFloat(name: String, values: List<Float>)

    fun uniformColor(name: String, r: Float, g: Float, b: Float, a: Float)
    fun uniformColor(name: String,color: Color)

    fun update(block: ShaderProvider.() -> Unit) {
        this.block()
    }
}
