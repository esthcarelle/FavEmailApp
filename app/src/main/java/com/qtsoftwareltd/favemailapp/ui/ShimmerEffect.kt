package com.qtsoftwareltd.favemailapp.ui

import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * Shimmer effect for loading states
 * Creates an animated gradient that gives a shimmer effect
 */
@Composable
fun ShimmerEffect(
    modifier: Modifier = Modifier,
    shimmerColor: Color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
    highlightColor: Color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
) {
    val infiniteTransition = rememberInfiniteTransition(label = "shimmer")
    
    // Create a smoother, more continuous shimmer animation
    val shimmerOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2000f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1800,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_offset"
    )
    
    // Create a smoother gradient with more color stops for better shimmer effect
    val brush = Brush.linearGradient(
        colors = listOf(
            shimmerColor,
            shimmerColor.copy(alpha = 0.8f),
            highlightColor.copy(alpha = 0.7f),
            highlightColor,
            highlightColor.copy(alpha = 0.7f),
            shimmerColor.copy(alpha = 0.8f),
            shimmerColor
        ),
        start = Offset(shimmerOffset - 600f, shimmerOffset - 600f),
        end = Offset(shimmerOffset + 200f, shimmerOffset + 200f)
    )
    
    Box(
        modifier = modifier
            .background(brush = brush)
    )
}

/**
 * Shimmer placeholder for text
 */
@Composable
fun ShimmerText(
    modifier: Modifier = Modifier,
    height: androidx.compose.ui.unit.Dp = 16.dp
) {
    ShimmerEffect(
        modifier = modifier
            .height(height)
            .clip(RoundedCornerShape(4.dp))
    )
}

/**
 * Shimmer placeholder for circular icon
 */
@Composable
fun ShimmerCircle(
    modifier: Modifier = Modifier,
    size: androidx.compose.ui.unit.Dp = 40.dp
) {
    ShimmerEffect(
        modifier = modifier
            .size(size)
            .clip(androidx.compose.foundation.shape.CircleShape)
    )
}

/**
 * Shimmer placeholder for card content
 * Mimics the email content layout
 */
@Composable
fun EmailShimmerContent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Sender card shimmer
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ShimmerCircle(size = 40.dp)
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ShimmerText(
                        modifier = Modifier.width(40.dp),
                        height = 12.dp
                    )
                    ShimmerText(
                        modifier = Modifier.fillMaxWidth(0.7f),
                        height = 20.dp
                    )
                    ShimmerText(
                        modifier = Modifier.fillMaxWidth(0.9f),
                        height = 16.dp
                    )
                }
            }
        }
        
        // Subject card shimmer
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ShimmerCircle(size = 24.dp)
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ShimmerText(
                        modifier = Modifier.width(50.dp),
                        height = 12.dp
                    )
                    ShimmerText(
                        modifier = Modifier.fillMaxWidth(0.8f),
                        height = 20.dp
                    )
                }
            }
        }
        
        // Body card shimmer
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ShimmerCircle(size = 20.dp)
                        ShimmerText(
                            modifier = Modifier.width(40.dp),
                            height = 16.dp
                        )
                    }
                    ShimmerText(
                        modifier = Modifier.width(80.dp),
                        height = 16.dp
                    )
                }
                repeat(4) {
                    ShimmerText(
                        modifier = Modifier.fillMaxWidth(),
                        height = 16.dp
                    )
                }
                ShimmerText(
                    modifier = Modifier.fillMaxWidth(0.7f),
                    height = 16.dp
                )
            }
        }
        
        // Image card shimmer
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ShimmerCircle(size = 20.dp)
                        ShimmerText(
                            modifier = Modifier.width(100.dp),
                            height = 16.dp
                        )
                    }
                    ShimmerText(
                        modifier = Modifier.width(80.dp),
                        height = 16.dp
                    )
                }
                ShimmerEffect(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .clip(RoundedCornerShape(8.dp))
                )
            }
        }
        
        // Verification status card shimmer
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
            ) {
                ShimmerCircle(size = 32.dp)
                Spacer(modifier = Modifier.width(12.dp))
                Column(
                    horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    ShimmerText(
                        modifier = Modifier.width(120.dp),
                        height = 20.dp
                    )
                    ShimmerText(
                        modifier = Modifier.width(200.dp),
                        height = 14.dp
                    )
                }
            }
        }
    }
}

