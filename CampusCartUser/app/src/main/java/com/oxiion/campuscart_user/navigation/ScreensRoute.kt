package com.oxiion.campuscart_user.navigation

sealed class Screens {

    // Authentication Screens
    sealed class Auth(val route: String, val title: String) {
        data object SignUp : Auth(route = "signup", title = "Sign Up")
        data object SignUpInfo : Auth(route = "signupInfo", title = "Sign Up Info")
        data object SignIn : Auth(route = "signin", title = "Sign In")
        data object ForgotPassword : Auth(route = "forgotpassword", title = "Forgot Password")
    }

    // Splash Screens
    sealed class SplashScreen(val route: String) {
        data object Splash : SplashScreen(route = "splash")
    }

    // Home Screens
    sealed class Home(val route: String, val title: String) {
        data object HomeScreen : Home( route = "home", title = "Home")
    }

    // Cart Screens
    sealed class Cart(val route: String, val title: String) {
        data object CartScreen : Cart(route = "cart", title = "Cart")
    }

    // Orders Screens
    sealed class Orders(val route: String, val title: String) {
        data object OrdersScreen : Orders(route = "orders", title = "Orders")
    }
    // Profile Screens
    sealed class Profile(val route: String, val title: String) {
        data object ProfileScreen : Profile(route = "profile", title = "Profile")
    }
}
