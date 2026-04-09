package com.meet.shader.animation.cmp.ui.animation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.meet.shader.animation.cmp.navigation.AnimationEntry
import com.meet.shader.animation.cmp.navigation.AnimationRoute
import com.meet.shader.animation.cmp.ui.theme.ShaderAnimationTheme

@Composable
fun AnimationScreen(onNavigate: (AnimationRoute) -> Unit) {
    val entries = AnimationEntry.entries

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        LazyVerticalStaggeredGrid(
            columns = StaggeredGridCells.Adaptive(minSize = 160.dp),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 32.dp),
            verticalItemSpacing = 12.dp,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header spans full width
            item(span = StaggeredGridItemSpan.FullLine) {
                AnimationScreenHeader(count = entries.size)
            }

            itemsIndexed(entries) { index, entry ->
                AnimationItem(
                    title = entry.title,
                    index = index,
                    // Alternate aspect ratios for staggered effect
                    aspectRatio = if (index % 3 == 0) 4f / 5f else 1f,
                    onClick = { onNavigate(entry.route) }
                )
            }
        }
    }
}

@Composable
@Preview
private fun AnimationScreenPreview() {
    ShaderAnimationTheme(true) {
        AnimationScreen(onNavigate = {})
    }
}

@Composable
private fun AnimationScreenHeader(count: Int) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primaryContainer,
                        MaterialTheme.colorScheme.background
                    )
                )
            )
            .statusBarsPadding()
            .padding(start = 24.dp, end = 24.dp, top = 32.dp, bottom = 28.dp)
    ) {
        Column {
            Text(
                text = "Shader",
                style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.Light),
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "Animations",
                style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.ExtraBold),
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "$count effects",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
            )
        }
    }
}

@Composable
@Preview
private fun AnimationScreenHeaderPreview() {
    ShaderAnimationTheme(true) {
        AnimationScreenHeader(count = 10)
    }
}
