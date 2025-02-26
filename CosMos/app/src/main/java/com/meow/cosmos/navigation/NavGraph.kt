package com.meow.cosmos.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.meow.cosmos.ui.screens.SplashScreen
import com.meow.cosmos.ui.screens.cards.CardDeckScreen
import com.meow.cosmos.ui.screens.home.ChatScreen
import com.meow.cosmos.viewModels.ChatViewModel

@Composable
fun StartNavigation(navController: NavController){
    val viewModel:ChatViewModel= hiltViewModel()
    NavHost(
        navController = navController as NavHostController,
        startDestination = Screens.SplashScreen.route
    ){
        composable(Screens.SplashScreen.route){
            SplashScreen(onNavigateToChatScreen = {
                navController.navigate(Screens.ChatScreen.route)
            })
        }
        composable(Screens.ChatScreen.route){
            ChatScreen(viewModel, navigateToCardsScreen = {
                navController.navigate(Screens.CardsScreen.route)
            })
        }
        composable(Screens.CardsScreen.route){
            CardDeckScreen(chatViewModel = viewModel,
                navigateToInputScreen = {
                    navController.navigate(Screens.ChatScreen.route){
                        popUpTo(Screens.ChatScreen.route) { inclusive = true }
                    }
                }
            )
        }
    }
}