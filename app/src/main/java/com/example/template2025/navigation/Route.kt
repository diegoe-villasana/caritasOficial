package com.example.template2025.navigation

sealed class Route(val route: String) {
    data object Splash : Route("splash")
    data object Auth : Route("auth")
    data object GuestMain : Route("guestmain")
    data object AdminMain : Route("adminmain")

    // Auth internas
    data object User : Route("user")
    data object AdminLogin : Route("adminlogin")
    data object Login : Route("login")
    data object Register : Route("register")

    // Main internas
    data object Home : Route("home")
    data object AdminHome : Route("adminhome")
    data object AdminReservations : Route("adminreservations")
    data object AdminReservationsDetail : Route("adminreservationsdetail/{reservaId}") {
        fun createRoute(reservaId: Int) = "adminreservationsdetail/$reservaId"
    }
    data object AdminQR : Route("adminqr")
    data object AdminTransport : Route("admintransport")
    data object AdminVolunteers : Route("adminvolunteers")
    data object Profile : Route("profile")
    data object Settings : Route("settings")
}
