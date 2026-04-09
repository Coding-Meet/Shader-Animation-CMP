package com.meet.shader.animation.cmp

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.meet.shader.animation.cmp.navigation.AnimationNavGraph
import com.meet.shader.animation.cmp.ui.theme.ShaderAnimationTheme

@Composable
@Preview
fun App() {
    ShaderAnimationTheme(true) {
        AnimationNavGraph()
    }
}