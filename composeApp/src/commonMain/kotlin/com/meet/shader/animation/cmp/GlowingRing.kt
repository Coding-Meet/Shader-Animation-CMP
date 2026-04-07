package com.meet.shader.animation.cmp

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.meet.shader.animation.cmp.expect_shader.runTapTimeBackgroundShader

@Composable
fun GlowingRing(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.runTapTimeBackgroundShader(ROTATING_HUE_GLOWING_RING_SHADER),
    ) {}
}

/**
 * A shader for [Modifier.runTapTimeBackgroundShader] shader runner.
 *
 * The shader shall use SkSL to be multiplatform:
 * SkSL will be translated into the platform specific shader languages.
 */
// language=GLSL
private const val ROTATING_HUE_GLOWING_RING_SHADER = """
    /*
    Put a glowing ring above the background using parameters from the shader runner (tap position and time for the rotation effect).
    
    We have 3 aspects:
    * Position (where to draw)
    * Hue (which color)
    * Intensity (of the color)
    
    The form is rotational symmetric so the geometry get simplified to a radial half-line from the circle center.
    
    For the hue we need a mathematical formula to map hue from angle.
    
    The intensity will be a mix of linear amplification and exponential decay on the radial line.
    
    In the calculations we often use normalized ranges to keep an overview.
    */
    
    uniform shader background; // The background, the variable name is indifferent, SkSL uses the first uniform shader type as the background.
                               // Not deliviered by the shader runner.
    uniform float2 uSize;      // The surface size, required by the shader runner
    uniform float uTimeS;      // The time in seconds since composition, required by the shader runner
    uniform float2 uTouchPos;  // The touch position, required by the shader runner.

    /*
    A function on [0..1] mapping into [0..1] (when stretchFactor is 1)
    This function in three instances is used to get the R, G, B values for the hue.
    Having the same function with equally shifted phases creates beautiful periodic hue.
    */
    half wave(half x, half stretchFactor) {
        // 6.28318 is 2 * PI
        return sin(x * 6.28318) * 0.5 * stretchFactor + 0.5;
    }
        
    half3 hueToRgb(float hue) {
        half h_half = half(hue);
        half stretchFactor = 1.0;
    
        half r = wave(h_half, stretchFactor);
        half g = wave(h_half - 0.333, stretchFactor);
        half b = wave(h_half + 0.333, stretchFactor);
        
        return clamp(half3(r, g, b), 0.0, 1.0);
    }
    
    // Map pixel(position) to color.
    half4 main(float2 pixel) {
        // Get direction vector from touch to current pixel.
        float2 direction = pixel - uTouchPos;
        float distance = length(direction);

        // Determine hue:
        // Calculate the angle (0 to 2*PI) and normalize it to [0..1] for the Hue.
        // atan2 returns angle; we add uTimeS to make the colors spin.
        float angle = atan(direction.y, direction.x) / 6.28318 + 0.5;
        float hue = fract(angle + uTimeS * 0.5);
        half4 color = half4(hueToRgb(hue), 1.0);
        
        // Determine intensity: the glow around the ring.
        // Think in the half-line from circle center over the pixel positon.
        // The center is the origo.
        // We want a smooth glowing:
        // From center to (radius - glowThickness) nothing,
        // then linearly amplified up to radius,
        // then exponential decay. 
        float radius = 300.0; 
        float glowThickness = 30.0;
        float mask = smoothstep(radius - glowThickness, radius, distance);
        float intensity = mask * exp(-(distance - radius) * 0.03);
        
        // Mix the background content with the glow.
        return background.eval(pixel) + (color * intensity);
    }
"""

// language=GLSL
private const val MEDIUM_ARTICLE_VERSION_OF_ROTATING_HUE_GLOWING_RING_SHADER = """
    uniform shader background; // The background, the variable name is indifferent, SkSL uses the first uniform shader type as the background.
                               // Not deliviered by the shader runner.
    uniform float2 uSize;      // The surface size, required by the shader runner
    uniform float uTimeS;      // The time in seconds since composition, required by the shader runner
    uniform float2 uTouchPos;  // The touch position, required by the shader runner.

    half wave(half normalized) {
        return sin(normalized * 6.28318) * 0.5 + 0.5;
    }
        
    half3 hueToRgb(float h) {
        half r = wave(h);
        half g = wave(h - 0.333);
        half b = wave(h + 0.333);
        return half3(r, g, b);
    }
    
    // Map pixel(position) to color.
    half4 main(float2 pixel) {
        // Get direction vector from touch to current pixel.
        float2 direction = pixel - uTouchPos;
        float distance = length(direction);

        // Determine hue:
        // First calculate the angle of [-PI..PI] and normalize it to [0..1].
        float angle = atan(direction.y, direction.x) / 6.28318 + 0.5;
        // Mix time to it to make the hue spin but keep normalization.
        float hue = fract(angle + uTimeS);
        // Map [0..1] to all the hue colors as half4 in [0..1].
        half4 color = half4(hueToRgb(hue), 1.0);
        
        // Determine intensity: the glow around the ring.
        // Think in the half-line from circle center over the pixel positon.
        // The center is the origo.
        // We want a smooth glowing:
        // From center to (radius - glowThickness) nothing,
        // then linearly amplified up to radius,
        // then exponential decay. 
        float radius = 300.0; 
        float glowThickness = 30.0;
        float mask = smoothstep(radius - glowThickness, radius, distance);
        float intensity = mask * exp(-(distance - radius) * 0.03);
        
        // Mix the background content with the glow.
        return background.eval(pixel) + (color * intensity);
    }
"""