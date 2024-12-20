package com.oxiion.campuscart.domain.screens.userScreens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.oxiion.campuscart.ui_app.components.AppTextBox
import com.oxiion.campuscart.ui_app.components.CustomButton

@Composable
fun UserSignUpScreen(snackbarHostState: SnackbarHostState, onCreateAccountClick: () -> Unit) {
    val userName = remember { mutableStateOf("") }
    val userEmail = remember { mutableStateOf("") }
    val userPhone = remember { mutableStateOf("") }
    val userPassword = remember { mutableStateOf("") }
    val userConfirmPassword = remember { mutableStateOf("") }

    // SnackbarHost should be inside the layout, preferably at the bottom of the screen
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.scrim).statusBarsPadding()// Apply background properly
    ) {
        // Title and other UI elements
        Text(
            text = "CampusCart",
            color = Color(0xFFD8C4A0),
            style = MaterialTheme.typography.headlineLarge,
            fontSize = 40.sp
        )
        Spacer(modifier = Modifier.height(32.dp))
        Text(
            text = "Sign Up",
            color = Color(0xFFD8C4A0),
            style = MaterialTheme.typography.bodyMedium,
            fontSize = 35.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))

        // User input fields
        AppTextBox(userName, "Enter your name")
        AppTextBox(userEmail, "Enter your email")
        AppTextBox(userPassword, "Enter your password")
        AppTextBox(userConfirmPassword, "Confirm your password")
        Spacer(modifier = Modifier.height(16.dp))

        // Create Account Button
        CustomButton(text = "Create Account") {
            onCreateAccountClick()
        }
    }

    // Show Snackbar at the bottom
    SnackbarHost(
        hostState = snackbarHostState,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp) // Snackbar will be aligned to the bottom center
    )
}
