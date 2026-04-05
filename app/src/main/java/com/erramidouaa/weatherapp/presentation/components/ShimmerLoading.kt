package com.erramidouaa.weatherapp.presentation.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun ShimmerLoading() {
    val shimmerColors = listOf(
        Color.White.copy(alpha = 0.1f),
        Color.White.copy(alpha = 0.2f),
        Color.White.copy(alpha = 0.1f),
    )

    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim = transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmerTranslate"
    )

    val brush = Brush.linearGradient(
        colors = shimmerColors,
        start = Offset.Zero,
        end = Offset(x = translateAnim.value, y = translateAnim.value)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(48.dp))
        
        // Search bar shimmer
        Box(modifier = Modifier.fillMaxWidth().height(56.dp).clip(RoundedCornerShape(24.dp)).background(brush))
        
        Spacer(modifier = Modifier.height(40.dp))
        
        // City name shimmer
        Box(modifier = Modifier.width(200.dp).height(40.dp).clip(RoundedCornerShape(8.dp)).background(brush))
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Icon shimmer
        Box(modifier = Modifier.size(160.dp).clip(CircleShape).background(brush))
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Temp shimmer
        Box(modifier = Modifier.width(120.dp).height(80.dp).clip(RoundedCornerShape(12.dp)).background(brush))
        
        Spacer(modifier = Modifier.height(48.dp))
        
        // Row shimmer
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            repeat(4) {
                Box(modifier = Modifier.size(80.dp).clip(RoundedCornerShape(20.dp)).background(brush))
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Grid shimmer
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Box(modifier = Modifier.weight(1f).height(100.dp).clip(RoundedCornerShape(20.dp)).background(brush))
                Box(modifier = Modifier.weight(1f).height(100.dp).clip(RoundedCornerShape(20.dp)).background(brush))
            }
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Box(modifier = Modifier.weight(1f).height(100.dp).clip(RoundedCornerShape(20.dp)).background(brush))
                Box(modifier = Modifier.weight(1f).height(100.dp).clip(RoundedCornerShape(20.dp)).background(brush))
            }
        }
    }
}
