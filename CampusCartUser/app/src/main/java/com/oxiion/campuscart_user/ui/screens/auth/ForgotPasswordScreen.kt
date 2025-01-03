package com.oxiion.campuscart_user.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.oxiion.campuscart_user.ui.components.AppCustomWhiteButton
import com.oxiion.campuscart_user.ui.components.AppOutlinedTextBox

@Composable
fun  ForgotPasswordScreen(
    innerPaddingValues: PaddingValues,
    onSignInClick:()->Unit) {
    val email = remember { mutableStateOf("") }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier =Modifier.fillMaxSize()
            .padding(innerPaddingValues)
            .background(Color(0xFF50606F))
            .imePadding(), // Ensure the keyboard pushes the layout up
    ) {
        Column(
            modifier = Modifier.weight(1f), // Push this column up when space is constrained
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Forgot Password !",
                style = MaterialTheme.typography.headlineLarge,
                color = Color.White,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(16.dp))
            AppOutlinedTextBox(
                givenValue = email,
                label = "Email",
                keyboardType = KeyboardType.Email,
                isPassword = false,
                isPasswordVisible =null
            )
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text="Go back",
                modifier = Modifier.clickable {
                    onSignInClick()
                },
                color = Color.White,
                style = TextStyle(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    textDecoration = TextDecoration.Underline // Adds underline
                )
            )
        }
        AppCustomWhiteButton(
            onClick = {

            },
            text = "Reset"
        )
        Spacer(modifier = Modifier.height(16.dp))
    }
}
