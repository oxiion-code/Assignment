package com.meow.cosmos.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.meow.cosmos.R
import kotlinx.coroutines.launch

import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch

@Composable
fun SplashScreen(onNavigateToChatScreen: () -> Unit) { // Accepts navigation function
    val scale = remember { Animatable(0.5f) } // Start small
    val rotation = remember { Animatable(0f) } // Start with no rotation

    LaunchedEffect(Unit) {
        val scaleJob = launch {
            scale.animateTo(
                targetValue = 1.4f, // Grow bigger
                animationSpec = tween(durationMillis = 2000, easing = FastOutSlowInEasing)
            )
        }

        val rotationJob = launch {
            rotation.animateTo(
                targetValue = 360f, // Rotate fully
                animationSpec = tween(durationMillis = 2000, easing = FastOutSlowInEasing)
            )
        }

        // Wait for both animations to finish before navigating
        joinAll(scaleJob, rotationJob)

        // Wait for 500ms after animation completes, then navigate
        kotlinx.coroutines.delay(200)
        onNavigateToChatScreen()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black), // Background color
        contentAlignment = Alignment.Center
    ) {
        // Background Image with Transparency
        Image(
            painter = painterResource(id = R.drawable.spashbackground),
            contentDescription = "Background",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .alpha(0.7f) // 70% transparent
        )

        // Zoom-In Rotating Image
        Image(
            painter = painterResource(id = R.drawable.splashlogo),
            contentDescription = "Rotating Logo",
            modifier = Modifier
                .size(200.dp)
                .graphicsLayer(
                    scaleX = scale.value,
                    scaleY = scale.value,
                    rotationZ = rotation.value // Rotates while scaling
                )
        )
    }
}





