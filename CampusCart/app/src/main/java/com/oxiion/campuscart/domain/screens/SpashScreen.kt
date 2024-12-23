package com.oxiion.campuscart.domain.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.oxiion.campuscart.domain.models.AuthViewModel
import com.oxiion.campuscart.utils.SharedPreferencesManager

@Composable
fun SplashScreen(
    authViewModel: AuthViewModel,
    onNavigationToLogin: () -> Unit,
    onNavigationToDashboard: () -> Unit
) {
    val context = LocalContext.current
    val isLoggedOut= SharedPreferencesManager.isLoggedOut(context)
    LaunchedEffect(Unit) {
        if (!isLoggedOut) {
            if (FirebaseAuth.getInstance().currentUser != null) {
                authViewModel.fetchAdminData(FirebaseAuth.getInstance().currentUser?.uid)
                onNavigationToDashboard()
            } else {
                onNavigationToLogin()
            }
        } else {
            onNavigationToLogin()
        }
    }
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Admin App",
            fontSize = 40.sp,
        )
        Spacer(modifier = Modifier.height(16.dp))
        CircularProgressIndicator()
    }
}