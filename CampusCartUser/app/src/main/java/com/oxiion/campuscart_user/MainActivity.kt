package com.oxiion.campuscart_user

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.oxiion.campuscart_user.navigation.StartAppNavigation
import com.oxiion.campuscart_user.ui.screens.auth.SignInScreen
import com.oxiion.campuscart_user.ui.theme.AppTheme
import com.phonepe.intent.sdk.api.PhonePe
import com.phonepe.intent.sdk.api.models.PhonePeEnvironment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        PhonePe.init(this, PhonePeEnvironment.SANDBOX, Constants.MERCHANT_ID, "")
        setContent {
            AppTheme{
                val navController= rememberNavController()
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    StartAppNavigation(navController,innerPadding)
                }
            }
        }
    }
}
