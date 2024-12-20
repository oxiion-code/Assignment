package com.oxiion.campuscart.domain.screens.deliveryBoyScreens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
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
fun CampusManLogInScreen(){
    val phoneNumber= remember { mutableStateOf("")}
    val code= remember { mutableStateOf("") }
    Column (
        modifier = Modifier.fillMaxSize().
        background(Color(0xFF000000)).
    statusBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally){
        Text(
            text = "CampusMan",
            style = MaterialTheme.typography.headlineLarge,
            color = Color(0xFFEAC16C),
            fontSize = 32.sp
        )
        Spacer(Modifier.height(10.dp))
        Text(
            text = "Your Needs! Our Priority",
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFFEAC16C),
            fontSize =23.sp
        )
        Spacer(Modifier.height(32.dp))
        Text(
            text = "Login",
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFFD8C4A0),
            fontSize =32.sp,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(Modifier.height(16.dp))
        AppTextBox(
            text=phoneNumber,
            placeholderName = "Enter your phone number"
        )
        AppTextBox(
            text=code,
            placeholderName = "Enter your primary code"
        )
        CustomButton(text = "Login") {
            // Handle login logic
        }
    }
}