package com.oxiion.campuscart.navigation
sealed class Screens(val route: String, val title: String) {
    data object ProfileScreen : Screens(route = "Profile", title = "App Profile")
    sealed class AdminScreens(val route: String, val title: String){
        data object LogIn : AdminScreens(route = "AdminLogin", title = "Admin LogIn")
        data object SignUp : AdminScreens(route = "AdminSignUp", title = "Admin SignUp")
        data object Dashboard : AdminScreens(route = "AdminDashboard", title = "Admin Dashboard")
        data object ManageUsers:AdminScreens(route = "AdminManageUsers", title = "Admin Management")
        data object ManageProducts:AdminScreens(route = "AdminManageProducts", title = "Admin Product")
        data object AddProduct: AdminScreens(route = "AdminAddProduct", title = "Admin Add Product")
        data object ManageCampusMen:AdminScreens(route = "AdminManageCampusMen", title = "Admin CampusMen")
    }
    sealed class UserScreens(val route: String, val title: String) {
        data object LogIn : UserScreens(route = "UserLogin", title = "User LogIn")
    }
    sealed class CampusMenScreens(val route: String, val title: String){
        data object LogIn : CampusMenScreens(route = "CampusManLogin", title = "CampusMan LogIn")
    }
}
