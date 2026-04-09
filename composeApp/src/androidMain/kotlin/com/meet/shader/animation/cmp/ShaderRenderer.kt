package com.meet.shader.animation.cmp

import android.opengl.GLES20
import android.opengl.GLSurfaceView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.viewinterop.AndroidView
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.opengles.GL10

@Composable
fun ShaderViewOld() {
    val renderer = remember { ShaderRenderer() }

    AndroidView(
        factory = { context ->
            GLSurfaceView(context).apply {
                setEGLContextClientVersion(2)
                setRenderer(renderer)
                renderMode = GLSurfaceView.RENDERMODE_CONTINUOUSLY

                setOnTouchListener { _, event ->
                    renderer.setTouch(event.x, event.y)
                    true
                }
            }
        }
    )
}
const val FRAGMENT_SHADER = """
precision mediump float;

uniform vec2 resolution;
uniform float time;
uniform vec2 touch;

void main() {
    vec2 uv = gl_FragCoord.xy / resolution.xy;
    vec2 touchUV = touch / resolution.xy;

    float dist = distance(uv, touchUV);

    float ripple = sin(dist * 30.0 - time * 6.0) * 0.5 + 0.5;
    float fade = 1.0 - smoothstep(0.0, 0.5, dist);

    vec3 col1 = vec3(0.2, 0.5, 1.0);
    vec3 col2 = vec3(1.0, 0.3, 0.5);
    vec3 col = mix(col1, col2, sin(time) * 0.5 + 0.5);

    gl_FragColor = vec4(col * ripple * fade, 1.0);
}
"""
const val VERTEX_SHADER = """
attribute vec4 aPosition;

void main() {
    gl_Position = aPosition;
}
"""
class ShaderRenderer : GLSurfaceView.Renderer {

    private var program = 0
    private var time = 0f

    private var width = 0
    private var height = 0

    private var touchX = 0f
    private var touchY = 0f

    private lateinit var vertexBuffer: FloatBuffer

    private val vertices = floatArrayOf(
        -1f, -1f,
         1f, -1f,
        -1f,  1f,
         1f,  1f
    )

    fun setTouch(x: Float, y: Float) {
        touchX = x
        touchY = height - y // IMPORTANT: flip Y
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        this.width = width
        this.height = height
        GLES20.glViewport(0, 0, width, height)
    }
    fun createProgram(vertexSrc: String, fragmentSrc: String): Int {
        val vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexSrc)
        val fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentSrc)

        val program = GLES20.glCreateProgram()

        GLES20.glAttachShader(program, vertexShader)
        GLES20.glAttachShader(program, fragmentShader)

        GLES20.glLinkProgram(program)

        // Check link status
        val linkStatus = IntArray(1)
        GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, linkStatus, 0)

        if (linkStatus[0] == 0) {
            val error = GLES20.glGetProgramInfoLog(program)
            GLES20.glDeleteProgram(program)
            throw RuntimeException("Program link error: $error")
        }

        return program
    }
    fun loadShader(type: Int, shaderCode: String): Int {
        val shader = GLES20.glCreateShader(type)

        GLES20.glShaderSource(shader, shaderCode)
        GLES20.glCompileShader(shader)

        // Check compile status
        val compileStatus = IntArray(1)
        GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compileStatus, 0)

        if (compileStatus[0] == 0) {
            val error = GLES20.glGetShaderInfoLog(shader)
            GLES20.glDeleteShader(shader)
            throw RuntimeException("Shader compile error: $error")
        }

        return shader
    }
    override fun onSurfaceCreated(
        p0: GL10?,
        p1: javax.microedition.khronos.egl.EGLConfig?
    ) {
        program = createProgram(VERTEX_SHADER, FRAGMENT_SHADER)

        vertexBuffer = ByteBuffer
            .allocateDirect(vertices.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
            .apply {
                put(vertices)
                position(0)
            }
    }

    override fun onDrawFrame(gl: GL10?) {
        time += 0.016f

        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)
        GLES20.glUseProgram(program)

        // Position
        val posHandle = GLES20.glGetAttribLocation(program, "aPosition")
        GLES20.glEnableVertexAttribArray(posHandle)
        GLES20.glVertexAttribPointer(
            posHandle,
            2,
            GLES20.GL_FLOAT,
            false,
            0,
            vertexBuffer
        )

        // Uniforms
        GLES20.glUniform1f(
            GLES20.glGetUniformLocation(program, "time"),
            time
        )

        GLES20.glUniform2f(
            GLES20.glGetUniformLocation(program, "resolution"),
            width.toFloat(),
            height.toFloat()
        )

        GLES20.glUniform2f(
            GLES20.glGetUniformLocation(program, "touch"),
            touchX,
            touchY
        )

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4)

        GLES20.glDisableVertexAttribArray(posHandle)
    }
}