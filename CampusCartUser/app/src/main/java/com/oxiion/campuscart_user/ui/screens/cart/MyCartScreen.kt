package com.oxiion.campuscart_user.ui.screens.cart

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.oxiion.campuscart_user.navigation.Screens
import com.oxiion.campuscart_user.ui.components.AppBottomBar
import com.oxiion.campuscart_user.ui.components.AppTopBar
import com.oxiion.campuscart_user.viewmodels.AuthViewModel

@Composable
fun CartScreen(
    authViewModel:AuthViewModel,
    navigateToScreen: (String) -> Unit,
    navigateBack:()->Unit
){
    val userData by authViewModel.userData.collectAsState()
    BackHandler {
        navigateBack()
    }
    Scaffold(
        modifier = Modifier.fillMaxSize().navigationBarsPadding(),
        topBar = {
            userData.address?.let {
                AppTopBar(
                    title = "Cart",
                    isHomeScreen = false,
                    hostelName = it.hostelNumber,
                    onBackClick = navigateBack
                )
            }
        },
        bottomBar = {
           AppBottomBar(
               currentScreen = Screens.Cart.CartScreen.route,
               onNavigate = {route->
                   navigateToScreen(route)
               }
           )
        }
    ) { padding->
        Column (modifier = Modifier.fillMaxSize().padding(padding)){
           Text("Cart Screen")
        }
    }
}