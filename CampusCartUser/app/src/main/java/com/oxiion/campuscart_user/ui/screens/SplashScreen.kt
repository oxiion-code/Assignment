package com.oxiion.campuscart_user.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.oxiion.campuscart_user.R
import com.oxiion.campuscart_user.viewmodels.AuthViewModel
import kotlinx.coroutines.delay


@Composable
fun SplashScreen(
    authViewModel: AuthViewModel,
    onNavigationToLogin:()->Unit,
    onNavigationToHome:()->Unit) {
    LaunchedEffect(Unit) {
        delay(2000L) // Delay for 2 seconds
        onNavigationToLogin() // Navigate to the sign-in screen
    }
    Column(
        modifier = Modifier.fillMaxSize().background(Color.White),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = rememberAsyncImagePainter(R.mipmap.ic_launcher),
            contentDescription = "app image",
            modifier = Modifier.size(180.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "CampusCart", color = Color.Black,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(32.dp))
        Text(text = "Your needs Our priority !", color = Color.Black,
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(16.dp))
        CircularProgressIndicator(
            color = Color.Blue
        )
    }
}