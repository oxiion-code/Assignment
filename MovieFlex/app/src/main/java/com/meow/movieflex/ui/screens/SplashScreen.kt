package com.meow.movieflex.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.meow.movieflex.R
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onAnimationEnd: () -> Unit) {
    var isVisible by remember { mutableStateOf(false) }

    // Scale animation (Grows from 0.5x to 1.0x)
    val scale by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0.5f,
        animationSpec = tween(durationMillis = 4000, easing = EaseOutExpo)
    )

    // Opacity animation (Fade-in effect)
    val alpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(durationMillis = 2500, easing = EaseInOutQuad)
    )

    // Slide-up animation (Moves text upwards slightly)
    val offsetY by animateDpAsState(
        targetValue = if (isVisible) 0.dp else 50.dp,
        animationSpec = tween(durationMillis = 3000, easing = EaseOutQuad)
    )

    // Start animation after a short delay
    LaunchedEffect(Unit) {
        delay(800)
        isVisible = true
        delay(3500) // Allow animation to complete before navigating
        onAnimationEnd()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Background Image
        Image(
            painter = painterResource(id = R.drawable.blurred_background),
            contentDescription = "Background",
            contentScale = ContentScale.FillHeight,
            modifier = Modifier.fillMaxSize()
        )

        // Animated MovieFlex Text with fade, scale, and slide-up effects
        AnimatedVisibility(
            visible = isVisible,
            modifier = Modifier.align(Alignment.Center)
        ) {
            Text(
                text = "MovieFlex",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White,
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .scale(scale)
                    .alpha(alpha)
                    .offset(y = offsetY)
            )
        }
    }
}



