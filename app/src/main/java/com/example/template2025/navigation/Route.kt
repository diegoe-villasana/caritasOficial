    package com.example.template2025.navigation

    import java.net.URLEncoder

    sealed class Route(val route: String) {
        data object Splash : Route("splash")
        data object Auth : Route("auth")
        data object GuestMain : Route("guestmain")
        data object AdminMain : Route("adminmain")

        // Auth internas
        data object User : Route("user")
        data object AdminLogin : Route("adminlogin")
        data object GuestLogin : Route("guestlogin")
        data object Login : Route("login")
        data object Register : Route("register")

    // Main internas
    data object  Servicios: Route("servicios")
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

        data object Guest : Route("guest_screen")

        data object Services : Route("services")
        data object QRScanner: Route("qrScanner")

        data object QrCode : Route("qr/{qr_code_url}?posada={posada}&personas={personas}&fecha={fecha}&telefono={telefono}") {
            // 2. UPDATE THE HELPER FUNCTION
            fun createRoute(
                qrCodeUrl: String?,
                posada: String?,
                personas: String?,
                fecha: String?,
                telefono: String?
            ): String {
                // Encode each parameter to handle special characters like spaces or slashes
                val encodedPosada = URLEncoder.encode(posada, "UTF-8")
                val encodedFecha = URLEncoder.encode(fecha, "UTF-8")
                val encodedTelefono = URLEncoder.encode(telefono, "UTF-8")

                // Build the final route string
                return "qr/$qrCodeUrl?posada=$encodedPosada&personas=$personas&fecha=$encodedFecha&telefono=$encodedTelefono"
            }
        }


    }
