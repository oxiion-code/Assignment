package com.oxiion.campuscart_user.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.oxiion.campuscart_user.ui.screens.SplashScreen
import com.oxiion.campuscart_user.ui.screens.auth.ForgotPasswordScreen
import com.oxiion.campuscart_user.ui.screens.auth.SignInScreen
import com.oxiion.campuscart_user.ui.screens.auth.SignUpInfoScreen
import com.oxiion.campuscart_user.ui.screens.auth.SignUpScreen
import com.oxiion.campuscart_user.ui.screens.cart.CartScreen
import com.oxiion.campuscart_user.ui.screens.home.HomeScreen
import com.oxiion.campuscart_user.ui.screens.orders.OrdersScreen
import com.oxiion.campuscart_user.ui.screens.profile.ProfileScreen
import com.oxiion.campuscart_user.viewmodels.AuthViewModel

@Composable
fun StartAppNavigation(navController: NavController, paddingValues: PaddingValues) {
    val authViewModel: AuthViewModel = hiltViewModel()
    NavHost(navController = navController as NavHostController, startDestination = Screens.SplashScreen.Splash.route) {
        composable(Screens.SplashScreen.Splash.route) {
            SplashScreen(
                authViewModel = authViewModel,
                onNavigationToLogin = {
                    navController.navigate(Screens.Auth.SignIn.route)
                },
                onNavigationToHome = {
                    navController.navigate(Screens.Home.HomeScreen.route)
                }
            )
        }
        composable(Screens.Auth.SignIn.route) {
            SignInScreen(
                paddingValues = paddingValues,
                onForgotPasswordClick = {
                    navController.navigate(Screens.Auth.ForgotPassword.route)
                },
                onCreateAccountClick = {
                    navController.navigate(Screens.Auth.SignUp.route)
                },
                onSignInSuccess = {
                    navController.navigate(Screens.Home.HomeScreen.route) {
                        popUpTo(Screens.Auth.SignIn.route) { inclusive = true }
                    }
                },
                authViewModel = authViewModel
            )
        }
        composable(Screens.Auth.SignUp.route) {
            SignUpScreen(
                paddingValues = paddingValues,
                authViewModel = authViewModel,
                onNextClick = {
                    navController.navigate(Screens.Auth.SignUpInfo.route)
                }
            )
        }
        composable(Screens.Auth.SignUpInfo.route) {
            SignUpInfoScreen(
                paddingValues = paddingValues,
                authViewModel = authViewModel,
                onSignUpSuccess = {
                    navController.navigate(Screens.Home.HomeScreen.route) {
                        popUpTo(Screens.Auth.SignUp.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screens.Auth.ForgotPassword.route) {
            ForgotPasswordScreen(
                innerPaddingValues = paddingValues,
                onSignInClick = {
                    navController.navigateUp()
                }
            )
        }
        composable(Screens.Home.HomeScreen.route) {
            HomeScreen(
                authViewModel = authViewModel,
                navigateToScreen = {
                    navController.navigate(it)
                },
                navigateBack = {

                }
            )
        }
        composable(Screens.Cart.CartScreen.route) {
            CartScreen(
              authViewModel =   authViewModel,
                navigateToScreen = {
                    navController.navigate(it)
                },
                navigateBack = {
                    navController.popBackStack(Screens.Home.HomeScreen.route, inclusive = true)
                    navController.navigate(Screens.Home.HomeScreen.route){
                        popUpTo(Screens.Home.HomeScreen.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screens.Orders.OrdersScreen.route) {
            OrdersScreen(paddingValues = paddingValues)
        }
        composable(Screens.Profile.ProfileScreen.route) {
            ProfileScreen(paddingValues = paddingValues)
        }
    }
}
