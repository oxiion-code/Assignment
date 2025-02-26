package com.meow.cosmos.navigation

sealed class Screens(val route:String) {
    data object SplashScreen:Screens("SplashScreen")
    data object ChatScreen:Screens("ChatScreen")
    data object CardsScreen:Screens("CardsScreen")
}