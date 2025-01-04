package com.oxiion.campuscart_user.ui.screens.auth

import android.widget.Toast
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.oxiion.campuscart_user.ui.components.AppCustomWhiteButton
import com.oxiion.campuscart_user.ui.components.AppOutlinedTextBox
import com.oxiion.campuscart_user.ui.components.LoadingDialogSmall
import com.oxiion.campuscart_user.utils.DataState
import com.oxiion.campuscart_user.viewmodels.AuthViewModel

@Composable
fun SignUpScreen(
    paddingValues: PaddingValues,
    authViewModel: AuthViewModel,
    onNextClick: () -> Unit
) {
    val context = LocalContext.current
    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val reEnterPassword = remember { mutableStateOf("") }
    val isPasswordVisible = remember { mutableStateOf(false) }
    val isRenterPasswordVisible = remember { mutableStateOf(false) }
    val isLoading= remember { mutableStateOf(false) }
    val collegeListState by authViewModel.getCollegeListState.collectAsState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .background(Color(0xFF50606F))
            .imePadding()
            .navigationBarsPadding(), // Ensure the keyboard pushes the layout up
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier.weight(1f), // Push this column up when space is constrained
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Sign Up",
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
                isPasswordVisible = isRenterPasswordVisible
            )
            Spacer(modifier = Modifier.height(16.dp))
            AppOutlinedTextBox(
                givenValue = reEnterPassword,
                label = "Re-enter password",
                keyboardType = KeyboardType.Password,
                isPassword = true,
                isPasswordVisible = isPasswordVisible
            )
        }
        AppCustomWhiteButton(
            onClick = {
                authViewModel.getCollegeList()
                onNextClick()
            },
            text = "Next",
        )
        Spacer(modifier = Modifier.height(16.dp))
    }
    when(collegeListState){
        is DataState.Error -> {
            isLoading.value=false
            Toast.makeText(context,"Unable to fetch data", Toast.LENGTH_SHORT).show()
        }
        DataState.Idle -> {

        }
        DataState.Loading ->{
            isLoading.value=true
            LoadingDialogSmall(isLoading)
        }
        DataState.Success -> {
            isLoading.value=false
        }
    }
}
