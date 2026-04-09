package com.meet.shader.animation.cmp.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed interface AnimationRoute

@Serializable
data object AnimationList : AnimationRoute

@Serializable
data object TouchShaderAnimation : AnimationRoute

@Serializable
data object PlasmaWaves : AnimationRoute

@Serializable
data object Starfield : AnimationRoute

@Serializable
data object WaterRipples : AnimationRoute

@Serializable
data object Fire : AnimationRoute

@Serializable
data object OceanWaves : AnimationRoute

@Serializable
data object MatrixRain : AnimationRoute

@Serializable
data object NeonPulse : AnimationRoute

@Serializable
data object Tunnel : AnimationRoute

@Serializable
data object FractalClouds : AnimationRoute

@Serializable
data object Nebula : AnimationRoute

@Serializable
data object ShaderHero : AnimationRoute

@Serializable
data object AnomalousMatter : AnimationRoute

@Serializable
data object DiamondRings : AnimationRoute

@Serializable
data object LiquidChrome : AnimationRoute

@Serializable
data object Hologram : AnimationRoute

@Serializable
data object Supernova : AnimationRoute

@Serializable
data object WarpSpeed : AnimationRoute

@Serializable
data object MagneticField : AnimationRoute

@Serializable
data object GlitchArt : AnimationRoute

@Serializable
data object InkSmoke : AnimationRoute

@Serializable
data object NeonOrbit : AnimationRoute

@Serializable
data object PlasmaGlobe : AnimationRoute

enum class AnimationEntry(val title: String, val route: AnimationRoute) {
    SHADER_ANIMATION("Touch Shader Animation", TouchShaderAnimation),
    PLASMA_WAVES("Plasma Waves", PlasmaWaves),
    STARFIELD("Starfield", Starfield),
    WATER_RIPPLES("Water Ripples", WaterRipples),
    FIRE("Fire", Fire),
    OCEAN_WAVES("Ocean Waves", OceanWaves),
    MATRIX_RAIN("Matrix Rain", MatrixRain),
    NEON_PULSE("Neon Pulse", NeonPulse),
    TUNNEL("Tunnel", Tunnel),
    FRACTAL_CLOUDS("Fractal Clouds", FractalClouds),
    NEBULA("Nebula", Nebula),
    SHADER_HERO("Shader Hero", ShaderHero),
    ANOMALOUS_MATTER("Anomalous Matter", AnomalousMatter),
    DIAMOND_RINGS("Diamond Rings", DiamondRings),
    LIQUID_CHROME("Liquid Chrome", LiquidChrome),
    HOLOGRAM("Hologram", Hologram),
    SUPERNOVA("Supernova", Supernova),
    WARP_SPEED("Warp Speed", WarpSpeed),
    MAGNETIC_FIELD("Magnetic Field", MagneticField),
    GLITCH_ART("Glitch Art", GlitchArt),
    INK_SMOKE("Ink Smoke", InkSmoke),
    NEON_ORBIT("Neon Orbit", NeonOrbit),
    PLASMA_GLOBE("Plasma Globe", PlasmaGlobe),
}
