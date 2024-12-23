package com.oxiion.campuscart.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.oxiion.campuscart.domain.models.ProductViewModel
import com.oxiion.campuscart.domain.models.AuthViewModel
import com.oxiion.campuscart.domain.screens.SplashScreen
import com.oxiion.campuscart.domain.screens.adminScreens.AdminDashboard
import com.oxiion.campuscart.domain.screens.adminScreens.AdminLoginScreen
import com.oxiion.campuscart.domain.screens.adminScreens.AdminSignUpScreen
import com.oxiion.campuscart.domain.screens.adminScreens.productScreens.AddProductScreen
import com.oxiion.campuscart.domain.screens.adminScreens.productScreens.EditProductScreen
import com.oxiion.campuscart.domain.screens.adminScreens.productScreens.ManageProductsScreen

@Composable
fun StartNavigation(navController: NavController) {
    val authViewModel: AuthViewModel = hiltViewModel()
    val productViewModel:ProductViewModel= hiltViewModel()
    NavHost(
        navController = navController as NavHostController,
        startDestination = Screens.AdminScreens.SplashScreen.route
    ) {
        composable(Screens.AdminScreens.SplashScreen.route){
            SplashScreen(
                authViewModel,
                onNavigationToDashboard = {
                    navController.navigate(Screens.AdminScreens.Dashboard.route)
                },
                onNavigationToLogin = {
                    navController.navigate(Screens.AdminScreens.LogIn.route)
                }
            )
        }
        composable(Screens.AdminScreens.LogIn.route) {
            AdminLoginScreen(
                authViewModel,
                onLoginSuccess = {
                    navController.navigate(Screens.AdminScreens.Dashboard.route) {
                        popUpTo(Screens.AdminScreens.LogIn.route) { inclusive = true }
                    }
                },
                onSignupClick = {
                    navController.navigate(Screens.AdminScreens.SignUp.route)
                }
            )
        }
        composable(Screens.AdminScreens.SignUp.route) {
            AdminSignUpScreen(viewModel = authViewModel) {
                navController.navigate(Screens.AdminScreens.Dashboard.route) {
                    popUpTo(Screens.AdminScreens.LogIn.route) { inclusive = true }
                }
            }
        }
        composable(Screens.AdminScreens.Dashboard.route) {
            AdminDashboard(
                viewModel = authViewModel,
                onLogoutClick = {
                    navController.navigate(Screens.AdminScreens.LogIn.route) {
                        popUpTo(Screens.AdminScreens.SplashScreen.route) { inclusive = true }
                    }
                }
                ,
                onManageUsersClick = { /* Manage users */ },
                onManageCamusMenClick = { /* Manage CampusMen */ },
                onManageProductsClick = {
                    navController.navigate(Screens.AdminScreens.ManageProducts.route)
                }
            )
        }
        composable(Screens.AdminScreens.ManageProducts.route) {
            ManageProductsScreen(
                viewModel = authViewModel,
                onAddProductClick = {
                    navController.navigate(Screens.AdminScreens.AddProduct.route)
                },
                onEditProductClick = { product->
                    navController.navigate("${Screens.AdminScreens.EditProduct.route}/${product.id}")
                },
                onBackClick = {
                    navController.navigateUp()
                }
            )
        }
        composable(Screens.AdminScreens.AddProduct.route) {
            AddProductScreen(
                authViewModel = authViewModel,
                productViewModel = productViewModel,
                onBackClick = {
                    navController.navigateUp()
                })
        }
        composable("${Screens.AdminScreens.EditProduct.route}/{productId}"){ backStackEntry ->
            val productId=backStackEntry.arguments?.getString("productId")
            val product=authViewModel.getProductById(productId)
            product?.let {
                EditProductScreen(
                    authViewModel = authViewModel,
                    product = it,
                    onBackClick = {
                        navController.navigateUp()
                    },
                   productViewModel = productViewModel,
                    onConfirmDeletion = {
                        navController.navigateUp()
                    })
            }
        }
    }
}
