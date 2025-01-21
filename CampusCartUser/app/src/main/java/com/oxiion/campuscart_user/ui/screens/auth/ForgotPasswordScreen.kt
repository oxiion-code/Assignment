package com.oxiion.campuscart_user.ui.screens.auth

import android.provider.ContactsContract.Data
import android.widget.Toast
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.oxiion.campuscart_user.ui.components.AppCustomWhiteButton
import com.oxiion.campuscart_user.ui.components.AppOutlinedTextBox
import com.oxiion.campuscart_user.ui.components.LoadingDialogTransparent
import com.oxiion.campuscart_user.utils.DataState
import com.oxiion.campuscart_user.viewmodels.AuthViewModel

@Composable
fun ForgotPasswordScreen(
    authViewModel: AuthViewModel,
    innerPaddingValues: PaddingValues,
    onSignInClick: () -> Unit
) {
    val context = LocalContext.current
    val email = remember { mutableStateOf("") }
    val forgotPasswordState by authViewModel.forgotPasswordState.collectAsState()
    val isLoading = remember { mutableStateOf(false) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPaddingValues)
            .background(Color(0xFF50606F))
            .imePadding() // Ensure the keyboard pushes the layout up
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
                isPasswordVisible = null
            )
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = "Go back",
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
                // Validate email format
                if (email.value.isValidEmail()) {
                    authViewModel.forgotPassword(email = email.value)
                } else {
                    // Show error toast
                    Toast.makeText(context, "Please enter a valid email address.", Toast.LENGTH_SHORT).show()
                }
            },
            text = "Reset"
        )
        if (isLoading.value){
            LoadingDialogTransparent(isLoading)
        }
        Spacer(modifier = Modifier.height(16.dp))
        when(forgotPasswordState){
            is DataState.Error -> {
                isLoading.value=false
               Toast.makeText(context, (forgotPasswordState as DataState.Error).message,Toast.LENGTH_SHORT).show()
                authViewModel.resetForgotPassword()
            }
            DataState.Idle -> {

            }
            DataState.Loading -> {
                isLoading.value=true
            }
            DataState.Success ->{
                isLoading.value=false
                Toast.makeText(context, "Password reset link sent to your email.", Toast.LENGTH_SHORT).show()
                authViewModel.resetForgotPassword()
            }
        }
    }
}

// Extension function for email validation
fun String.isValidEmail(): Boolean {
    return android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()
}

