package com.example.template2025.navigation

sealed class Route(val route: String) {
    data object Splash : Route("splash")
    data object Auth : Route("auth")
    data object Main : Route("main")

    // Auth internas
    data object Login : Route("login")
    data object Register : Route("register")

    // Main internas
    data object Home : Route("home")
    data object Profile : Route("profile")
    data object Settings : Route("settings")
}
