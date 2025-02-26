package com.meow.cosmos

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
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.WindowCompat
import androidx.navigation.compose.rememberNavController
import com.meow.cosmos.navigation.StartNavigation
import com.meow.cosmos.ui.screens.SplashScreen
import com.meow.cosmos.ui.screens.home.ChatScreen
import com.meow.cosmos.ui.theme.AppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        enableEdgeToEdge()
        setContent {
            val navController= rememberNavController()
            AppTheme{
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                   StartNavigation(navController = navController)
                }
            }
        }
    }
}
