package com.oxiion.campuscart_user.navigation

sealed class Screens{
    sealed class Auth(val route:String,title:String){
        data object SignUp:Auth(route = "signup", title = "Sign Up")
        data object SignIn:Auth(route = "signin", title = "Sign In")
        data object ForgotPassword:Auth(route = "forgotpassword", title = "Forgot Password")
    }
    sealed class SplashScreen(val route: String){
        data object Splash:SplashScreen(route = "splash")
    }
    sealed class Home(val route:String,val title:String){
        data object HomeScreen: Home(route = "home", title = "Home")
    }
    sealed class Cart(route:String,title:String){

    }
    sealed class Orders(route:String,title:String){

    }
    sealed class Profile(route:String,title:String){

    }
}