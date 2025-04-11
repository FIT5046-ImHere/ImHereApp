package com.example.imhere.navigation

sealed class MainNavigation(val title: String, val route: String) {
    object Home : MainNavigation("Home", "home")
    object Dashboard : MainNavigation("Dashboard", "dashboard")
    object Profile : MainNavigation("Profile", "profile")
}