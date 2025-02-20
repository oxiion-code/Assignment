package com.meow.movieflex.navigation

sealed class Screens(val route: String) {
    data object SplashScreen : Screens("SplashScreen")
    data object Home : Screens("HomeScreen")
    data object Description : Screens("DescriptionScreen/{movieId}") {
        fun passMovieId(movieId: Int) = "DescriptionScreen/$movieId"
    }
}
