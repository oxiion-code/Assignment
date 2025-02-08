package com.oxiion.campuscart_user.ui.screens.auth
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.oxiion.campuscart_user.ui.components.AppAlertBox
import com.oxiion.campuscart_user.ui.components.AppCustomWhiteButton
import com.oxiion.campuscart_user.ui.components.AppOutlinedTextBox
import com.oxiion.campuscart_user.ui.components.LoadingDialogFullScreen
import com.oxiion.campuscart_user.ui.components.LoadingDialogSmall
import com.oxiion.campuscart_user.utils.DataState
import com.oxiion.campuscart_user.utils.DataStateAuth
import com.oxiion.campuscart_user.viewmodels.AuthViewModel

@Composable
fun SignInScreen(
    paddingValues: PaddingValues,
    onForgotPasswordClick:()->Unit,
    onCreateAccountClick:()->Unit,
    onSignInSuccess:()->Unit,
    authViewModel: AuthViewModel
) {
    var showExitDialog by remember { mutableStateOf(false) }
    val isLoading= remember { mutableStateOf(false) }
    val isSignInSuccess= remember { mutableStateOf(false) }
    // BackHandler for handling back press
    BackHandler {
        showExitDialog = true
    }
    val context = LocalContext.current
    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val isPasswordVisible = remember { mutableStateOf(false) }
    val collegeListState by authViewModel.getCollegeListState.collectAsState()
    val signInState by authViewModel.signInState.collectAsState()
    if (showExitDialog) {
        AppAlertBox(
            onConfirm = {
                showExitDialog = false
                (context as? android.app.Activity)?.finish() // Exit the app
            },
            onDismiss = {
                showExitDialog = false
            }
        )
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .background(Color(0xFF50606F))
            .imePadding().navigationBarsPadding(), // Ensure the keyboard pushes the layout up
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier.weight(1f), // Push this column up when space is constrained
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Sign In",
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
                isPasswordVisible = isPasswordVisible
            )
            Spacer(modifier = Modifier.height(16.dp))
            AppOutlinedTextBox(
                givenValue = password,
                label = "Password",
                keyboardType = KeyboardType.Password,
                isPassword = true,
                isPasswordVisible = isPasswordVisible
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Forgot Password?",
                color = Color.White,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(end = 30.dp)
                    .clickable {
                        onForgotPasswordClick()
                    }
            )
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = "Click here to create an account",
                color = Color(0xFFFFFFFF),
                style = TextStyle(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    textDecoration = TextDecoration.Underline // Adds underline
                ),
                modifier = Modifier.clickable {
                    onCreateAccountClick()
                }
            )
        }
        AppCustomWhiteButton(
            onClick = {
                authViewModel.signIn(email = email.value, password = password.value)
            },
            text = "Sign In",
        )
        Spacer(modifier = Modifier.height(16.dp))
        if (isSignInSuccess.value){
            isSignInSuccess.value=false
            onSignInSuccess()
        }
        when(signInState){
            is DataStateAuth.Error -> {
                isLoading.value=false
               Toast.makeText(context, (signInState as DataStateAuth.Error).message,Toast.LENGTH_SHORT).show()
                authViewModel.resetSignInState()
            }
            DataStateAuth.Idle -> {

            }
            DataStateAuth.Loading -> {
                isLoading.value=true
                LoadingDialogFullScreen(isLoading)
            }
            is DataStateAuth.Success -> {
                isLoading.value=false
                isSignInSuccess.value=true
                authViewModel.resetSignInState()
            }
        }
    }
}
