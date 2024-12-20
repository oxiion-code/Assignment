package com.oxiion.campuscart.navigation

import android.util.Log
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.oxiion.campuscart.domain.models.AdminViewModel
import com.oxiion.campuscart.domain.screens.ProfileScreen
import com.oxiion.campuscart.domain.screens.adminScreens.AdminDashboard
import com.oxiion.campuscart.domain.screens.adminScreens.AdminLoginScreen
import com.oxiion.campuscart.domain.screens.adminScreens.AdminSignUpScreen
import com.oxiion.campuscart.domain.screens.adminScreens.productScreens.AddProductScreen
import com.oxiion.campuscart.domain.screens.adminScreens.productScreens.ManageProductsScreen

@Composable
fun StartNavigation(navController: NavController) {
    val collegeName = remember { mutableStateOf("Select College") }
    val profileRole = remember { mutableStateOf("User") }
    val adminViewModel: AdminViewModel = hiltViewModel()

    // A flag to prevent repeated navigation
    val hasNavigated = remember { mutableStateOf(false) }

    NavHost(
        navController = navController as NavHostController,
        startDestination = Screens.ProfileScreen.route,
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None }
    ) {
        composable(route = Screens.ProfileScreen.route) {
            ProfileScreen(profileRole = profileRole, collegeName = collegeName, onNextClick = {
                if (!hasNavigated.value) { // Only trigger navigation once
                    hasNavigated.value = true
                    Log.i("info profile", "${collegeName.value}: ${profileRole.value}")
                    when {
                        profileRole.value == "Admin" && collegeName.value != "Select College" -> {
                            navController.navigate(Screens.AdminScreens.LogIn.route)
                        }

                        profileRole.value == "User" && collegeName.value != "Select College" -> {
                            navController.navigate(Screens.UserScreens.LogIn.route)
                        }

                        profileRole.value == "CampusMan" && collegeName.value != "Select College" -> {
                            navController.navigate(Screens.CampusMenScreens.LogIn.route)
                        }

                        else -> {
                            // Reset navigation flag if no condition matches
                            hasNavigated.value = false
                        }
                    }
                }
            })
        }

        composable(Screens.AdminScreens.LogIn.route) {
            AdminLoginScreen(
                adminViewModel,
                onLoginSuccess = {
                    hasNavigated.value = false // Reset the flag after navigation
                    navController.navigate(Screens.AdminScreens.Dashboard.route) {
                        popUpTo(Screens.ProfileScreen.route) { inclusive = false }
                    }
                },
                onSignupClick = {
                    hasNavigated.value = false
                    navController.navigate(Screens.AdminScreens.SignUp.route)
                }
            )
        }
        composable(Screens.AdminScreens.SignUp.route) {
            AdminSignUpScreen(viewModel = adminViewModel) {
                hasNavigated.value = false
                navController.navigate(Screens.AdminScreens.Dashboard.route)
            }
        }
        composable(Screens.AdminScreens.Dashboard.route) {
            AdminDashboard(
                viewModel = adminViewModel,
                onLogoutClick = {
                    hasNavigated.value = false
                    navController.navigate(Screens.ProfileScreen.route) {
                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
                    }
                },
                onManageUsersClick = {
                    navController.navigate(Screens.AdminScreens.ManageUsers.route)
                },
                onManageCamusMenClick = {

                },
                onManageProductsClick = {
                    navController.navigate(Screens.AdminScreens.ManageProducts.route)
                })
        }
        composable(Screens.AdminScreens.ManageProducts.route) {
           ManageProductsScreen(viewModel =adminViewModel, onAddProductClick =  {
               navController.navigate(Screens.AdminScreens.AddProduct.route)
           }, onEditProductClick = {})
        }
        composable(Screens.AdminScreens.AddProduct.route){
            AddProductScreen(
                adminViewModel = adminViewModel
            )
        }
        composable(Screens.CampusMenScreens.LogIn.route) {
            // Placeholder for CampusMan Login Screen
        }
    }
}
