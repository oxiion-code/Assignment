package com.meow.movieflex.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.meow.movieflex.ui.screens.SplashScreen
import com.meow.movieflex.ui.screens.description.DetailsScreen
import com.meow.movieflex.ui.screens.home.HomeScreen
import com.meow.movieflex.viewmodels.AppViewModel

@Composable
fun StartNavigation(navController: NavHostController,paddingValues: PaddingValues) {
    val viewModel: AppViewModel = hiltViewModel()

    NavHost(
        navController = navController,
        startDestination = Screens.SplashScreen.route
    ) {
        composable(Screens.SplashScreen.route) {
            SplashScreen(onAnimationEnd = {
                navController.navigate(Screens.Home.route) {
                    popUpTo(Screens.SplashScreen.route) { inclusive = true }
                }
            })
        }
        composable(Screens.Home.route) {
            HomeScreen(navigateToMovieDetails = { movieId ->
                navController.navigate(Screens.Description.passMovieId(movieId.toInt()))
            }, viewModel = viewModel, paddingValues = paddingValues)
        }
        composable(Screens.Description.route) { backStackEntry ->
            val movieId = backStackEntry.arguments?.getString("movieId")?.toIntOrNull()
            if (movieId != null) {
                DetailsScreen(movieId = movieId, viewModel = viewModel, onBackPress = {
                    navController.navigateUp()
                }, innerPaddingValues =paddingValues )
            }
        }
    }
}
