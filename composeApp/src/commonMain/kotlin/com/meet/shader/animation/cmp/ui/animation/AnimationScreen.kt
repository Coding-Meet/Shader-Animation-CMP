package com.meet.shader.animation.cmp.ui.animation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.meet.shader.animation.cmp.navigation.AnimationEntry
import com.meet.shader.animation.cmp.navigation.AnimationRoute
import com.meet.shader.animation.cmp.ui.theme.ShaderAnimationTheme

@Composable
fun AnimationScreen(onNavigate: (AnimationRoute) -> Unit) {
    val entries = AnimationEntry.entries
    var showInfo by remember { mutableStateOf(false) }

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
                AnimationScreenHeader(
                    count = entries.size,
                    onInfoClick = { showInfo = true }
                )
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

    if (showInfo) {
        ProjectInfoDialog(onDismiss = { showInfo = false })
    }
}

@Composable
private fun ProjectInfoDialog(onDismiss: () -> Unit) {
    val uriHandler = LocalUriHandler.current

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column {
                // Gradient header
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primary,
                                    MaterialTheme.colorScheme.tertiary
                                )
                            )
                        )
                        .background(Color.Black.copy(alpha = 0.30f))
                        .padding(start = 24.dp, end = 8.dp, top = 20.dp, bottom = 24.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "Shader Animation",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Light),
                            color = Color.White.copy(alpha = 0.90f)
                        )
                        Text(
                            text = "CMP",
                            style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.ExtraBold),
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Shader effects on Android, iOS, Desktop\nand Web — one Kotlin codebase.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.85f)
                        )
                    }
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.align(Alignment.TopEnd)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color.White
                        )
                    }
                }

                // Links
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    InfoLinkRow(
                        badge = "M",
                        badgeColor = MaterialTheme.colorScheme.primaryContainer,
                        badgeTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        label = "Article",
                        sublabel = "Read on Medium"
                    ) {
                        uriHandler.openUri("https://medium.com/@meet26/how-to-implement-shaders-in-compose-multiplatform-android-ios-desktop-web-c86a36dd9666")
                    }
                    InfoLinkRow(
                        badge = "G",
                        badgeColor = MaterialTheme.colorScheme.secondaryContainer,
                        badgeTextColor = MaterialTheme.colorScheme.onSecondaryContainer,
                        label = "Source Code",
                        sublabel = "GitHub"
                    ) {
                        uriHandler.openUri("https://github.com/Coding-Meet/Shader-Animation-CMP")
                    }
                    InfoLinkRow(
                        badge = "W",
                        badgeColor = MaterialTheme.colorScheme.tertiaryContainer,
                        badgeTextColor = MaterialTheme.colorScheme.onTertiaryContainer,
                        label = "Web Demo",
                        sublabel = "coding-meet.github.io"
                    ) {
                        uriHandler.openUri("https://coding-meet.github.io/Shader-Animation-CMP/")
                    }
                    InfoLinkRow(
                        badge = "YT",
                        badgeColor = MaterialTheme.colorScheme.errorContainer,
                        badgeTextColor = MaterialTheme.colorScheme.onErrorContainer,
                        label = "YouTube",
                        sublabel = "@CodingMeet26"
                    ) {
                        uriHandler.openUri("https://youtube.com/@CodingMeet26")
                    }
                }

                // Footer
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "By Meet",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    TextButton(onClick = onDismiss) {
                        Text("Close")
                    }
                }
            }
        }
    }
}

@Composable
private fun InfoLinkRow(
    badge: String,
    badgeColor: Color,
    badgeTextColor: Color,
    label: String,
    sublabel: String,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(badgeColor, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = badge,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.ExtraBold),
                    color = badgeTextColor
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = sublabel,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = "→",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
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
private fun AnimationScreenHeader(count: Int, onInfoClick: () -> Unit) {
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
            .padding(start = 24.dp, end = 8.dp, top = 32.dp, bottom = 28.dp)
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

        IconButton(
            onClick = onInfoClick,
            modifier = Modifier.align(Alignment.TopEnd)
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = "Project info",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
@Preview
private fun AnimationScreenHeaderPreview() {
    ShaderAnimationTheme(true) {
        AnimationScreenHeader(count = 10, {})
    }
}
