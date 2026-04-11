package com.meet.shader.animation.cmp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.meet.shader.animation.cmp.ui.animation.AnimationScreen
import com.meet.shader.animation.cmp.ui.animation.screens.AnomalousMatterScreen
import com.meet.shader.animation.cmp.ui.animation.screens.DiamondRingsScreen
import com.meet.shader.animation.cmp.ui.animation.screens.FireScreen
import com.meet.shader.animation.cmp.ui.animation.screens.FractalCloudsScreen
import com.meet.shader.animation.cmp.ui.animation.screens.GlitchArtScreen
import com.meet.shader.animation.cmp.ui.animation.screens.HologramScreen
import com.meet.shader.animation.cmp.ui.animation.screens.InkSmokeScreen
import com.meet.shader.animation.cmp.ui.animation.screens.LiquidChromeScreen
import com.meet.shader.animation.cmp.ui.animation.screens.MagneticFieldScreen
import com.meet.shader.animation.cmp.ui.animation.screens.MatrixRainScreen
import com.meet.shader.animation.cmp.ui.animation.screens.NebulaScreen
import com.meet.shader.animation.cmp.ui.animation.screens.NeonOrbitScreen
import com.meet.shader.animation.cmp.ui.animation.screens.NeonPulseScreen
import com.meet.shader.animation.cmp.ui.animation.screens.OceanWavesScreen
import com.meet.shader.animation.cmp.ui.animation.screens.PlasmaGlobeScreen
import com.meet.shader.animation.cmp.ui.animation.screens.PlasmaWavesScreen
import com.meet.shader.animation.cmp.ui.animation.screens.ShaderApiShowcaseScreen
import com.meet.shader.animation.cmp.ui.animation.screens.ShaderHeroScreen
import com.meet.shader.animation.cmp.ui.animation.screens.StarfieldScreen
import com.meet.shader.animation.cmp.ui.animation.screens.SupernovaScreen
import com.meet.shader.animation.cmp.ui.animation.screens.TouchShaderAnimationScreen
import com.meet.shader.animation.cmp.ui.animation.screens.TunnelScreen
import com.meet.shader.animation.cmp.ui.animation.screens.WarpSpeedScreen
import com.meet.shader.animation.cmp.ui.animation.screens.WaterRipplesScreen

@Composable
fun AnimationNavGraph() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = AnimationList) {
        composable<AnimationList> {
            AnimationScreen(onNavigate = { navController.navigate(it) })
        }
        composable<ShaderApiShowcase> {
            ShaderApiShowcaseScreen(onBack = { navController.navigateUp() })
        }
        composable<TouchShaderAnimation> {
            TouchShaderAnimationScreen(onBack = { navController.navigateUp() })
        }
        composable<PlasmaWaves> {
            PlasmaWavesScreen(onBack = { navController.navigateUp() })
        }
        composable<Starfield> {
            StarfieldScreen(onBack = { navController.navigateUp() })
        }
        composable<WaterRipples> {
            WaterRipplesScreen(onBack = { navController.navigateUp() })
        }
        composable<Fire> {
            FireScreen(onBack = { navController.navigateUp() })
        }
        composable<OceanWaves> {
            OceanWavesScreen(onBack = { navController.navigateUp() })
        }
        composable<MatrixRain> {
            MatrixRainScreen(onBack = { navController.navigateUp() })
        }
        composable<NeonPulse> {
            NeonPulseScreen(onBack = { navController.navigateUp() })
        }
        composable<Tunnel> {
            TunnelScreen(onBack = { navController.navigateUp() })
        }
        composable<FractalClouds> {
            FractalCloudsScreen(onBack = { navController.navigateUp() })
        }
        composable<Nebula> {
            NebulaScreen(onBack = { navController.navigateUp() })
        }
        composable<ShaderHero> {
            ShaderHeroScreen(onBack = { navController.navigateUp() })
        }
        composable<AnomalousMatter> {
            AnomalousMatterScreen(onBack = { navController.navigateUp() })
        }
        composable<DiamondRings> {
            DiamondRingsScreen(onBack = { navController.navigateUp() })
        }
        composable<LiquidChrome> {
            LiquidChromeScreen(onBack = { navController.navigateUp() })
        }
        composable<Hologram> {
            HologramScreen(onBack = { navController.navigateUp() })
        }
        composable<Supernova> {
            SupernovaScreen(onBack = { navController.navigateUp() })
        }
        composable<WarpSpeed> {
            WarpSpeedScreen(onBack = { navController.navigateUp() })
        }
        composable<MagneticField> {
            MagneticFieldScreen(onBack = { navController.navigateUp() })
        }
        composable<GlitchArt> {
            GlitchArtScreen(onBack = { navController.navigateUp() })
        }
        composable<InkSmoke> {
            InkSmokeScreen(onBack = { navController.navigateUp() })
        }
        composable<NeonOrbit> {
            NeonOrbitScreen(onBack = { navController.navigateUp() })
        }
        composable<PlasmaGlobe> {
            PlasmaGlobeScreen(onBack = { navController.navigateUp() })
        }
    }
}
