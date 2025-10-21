 package com.example.template2025

import android.graphics.Bitmap
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.template2025.composables.AdminScaffold
import com.example.template2025.composables.MainScaffold
import com.example.template2025.model.LoginCredentials
import com.example.template2025.navigation.Route
import com.example.template2025.screens.AdminLoginScreen
import com.example.template2025.screens.ChatScreen
import com.example.template2025.screens.CheckReservationScreen
import com.example.template2025.screens.GuestScreen
import com.example.template2025.screens.LoginScreen
import com.example.template2025.screens.QRScreen
import com.example.template2025.screens.RegisterScreen
import com.example.template2025.screens.UserScreen
import com.example.template2025.ui.theme.CaritasTheme
import com.example.template2025.viewModel.AppViewModel
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import androidx.core.graphics.set
import androidx.core.graphics.createBitmap
import com.example.template2025.screens.MovableChatBubble

 class ChatActivity : ComponentActivity() {
     @OptIn(ExperimentalMaterial3Api::class)
     override fun onCreate(savedInstanceState: Bundle?) {
         super.onCreate(savedInstanceState)
         setContent {
             CaritasTheme {
                 Scaffold(
                     topBar = {
                         TopAppBar(
                             title = { Text("Chat") },
                             navigationIcon = {
                                 IconButton(onClick = { finish() }) {
                                     Icon(
                                         imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                         contentDescription = "Atrás"
                                     )
                                 }
                             }
                         )
                     }
                 ) { padding ->
                     ChatScreen(
                         modifier = Modifier
                             .fillMaxSize()
                             .padding(padding)
                     )
                 }
             }
         }
     }
 }



 class MainActivity : ComponentActivity() {
     override fun onCreate(savedInstanceState: Bundle?) {
         super.onCreate(savedInstanceState)
         enableEdgeToEdge()
         setContent {
             CaritasTheme {
                 Scaffold(
                     modifier = Modifier.fillMaxSize(),
                     // Puedes eliminar o dejar el FloatingActionButton tradicional si quieres solo el flotante
                 ) { innerPadding ->
                     Box(Modifier.fillMaxSize().padding(innerPadding)) {
                         // Tu app principal
                         AppRoot(modifier = Modifier.matchParentSize())
                         // Aquí se agrega el chat flotante
                         MovableChatBubble()
                     }
                 }
             }
         }
     }
 }




@Composable
fun AppRoot(modifier: Modifier = Modifier) {
    val vm: AppViewModel = viewModel()
    val authState by vm.auth.collectAsState()
    val shouldNavigateToAuth by vm.navigateToAuth.collectAsState()
    val nav = rememberNavController()

    LaunchedEffect(shouldNavigateToAuth) {
        if (shouldNavigateToAuth) {
            nav.navigate(Route.Auth.route) {
                popUpTo(0) { inclusive = true }
            }
            vm.onAuthNavigationComplete()
        }
    }

    NavHost(navController = nav, startDestination = Route.Splash.route) {
        composable(Route.Splash.route) {
            SplashScreen(
                vm = vm,
                nav = nav
            )
        }

        // AUTH FLOW
        composable(Route.Auth.route) {
            AuthNavHost(
                vm = vm,
                isLoading = authState.isLoading,
                error = authState.error,
                onErrorDismiss = { vm.clearError() },
                onLoggedIn = { credentials ->
                    vm.login(credentials) {success, _ ->
                        if (success) {
                            val destination = when(credentials) {
                                is LoginCredentials.Admin -> Route.AdminMain.route
                                is LoginCredentials.Guest -> Route.GuestMain.route
                            }

                            nav.navigate(destination) {
                                popUpTo(Route.Splash.route) { inclusive = true }
                                launchSingleTop = true
                            }
                        }
                    }
                }
            )
        }

        // MAIN FLOW
        composable(Route.GuestMain.route) {
            MainScaffold(
                vm = vm,
                userType = "guest",
                onLogoutClick = { vm.logout() },
                onNavigateToAuth = {
                    nav.navigate(Route.Auth.route) { popUpTo(0) } // limpia back stack
                }
            )
        }

        composable(Route.AdminMain.route) {
            AdminScaffold(
                vm = vm,
                onLogoutClick = { vm.logout() },
                onNavigateToAuth = {
                    nav.navigate(Route.Auth.route) { popUpTo(0) } // limpia back stack
                }
            )
        }
    }

}

@Composable
fun AuthNavHost(
    vm: AppViewModel,
    isLoading: Boolean,
    error: String?,
    onErrorDismiss: () -> Unit,
    onLoggedIn: (credentials: LoginCredentials) -> Unit
) {
    val nav = rememberNavController()
    NavHost(navController = nav, startDestination = Route.User.route) {
        composable(Route.User.route) {
            UserScreen(
                error = error,
                onGuestClick = {
                    onErrorDismiss()
                    nav.navigate(Route.GuestLogin.route)
                },
                onVolunteerClick = {
                    onErrorDismiss()
                },
                onAdminClick = {
                    onErrorDismiss()
                    nav.navigate(Route.AdminLogin.route)
                }
            )
        }

        composable(Route.AdminLogin.route) {
            AdminLoginScreen(
                isLoading = isLoading,
                error = error,
                onErrorDismiss = onErrorDismiss,
                onBack = { nav.popBackStack() },
                onLogin = { user, password ->
                    val credentials = LoginCredentials.Admin(user, password)
                    onLoggedIn(credentials)
                }
            )
        }

        composable(Route.GuestLogin.route) {
            CheckReservationScreen(
                navController = nav,
                onBack = { nav.navigateUp() }
            )
        }

        composable(Route.Guest.route) {
            GuestScreen(navController = nav, vm = vm)
        }

        composable(
            route = Route.QrCode.route,
            arguments = listOf(
                navArgument("qr_code_url") { type = NavType.StringType },
                navArgument("posada") { type = NavType.StringType; nullable = true },
                navArgument("personas") { type = NavType.StringType; nullable = true },
                navArgument("fecha") { type = NavType.StringType; nullable = true },
                navArgument("telefono") { type = NavType.StringType; nullable = true }
            )
        ) { backStackEntry ->
            val qrCodeUrl = backStackEntry.arguments?.getString("qr_code_url")
            val posada = backStackEntry.arguments?.getString("posada")
            val personas = backStackEntry.arguments?.getString("personas")
            val fecha = backStackEntry.arguments?.getString("fecha")
            val telefono = backStackEntry.arguments?.getString("telefono")
            QRScreen(
                navController = nav,
                qrCodeUrl = qrCodeUrl,
                posadaName = posada,
                personCount = personas,
                entryDate = fecha,
                phone = telefono
            )
        }
    }
}

@Composable
fun SplashScreen(vm: AppViewModel, nav: NavHostController) {
    val state by vm.auth.collectAsState()

    LaunchedEffect(state.isLoading, state.isLoggedIn, state.userType) {
        if (state.userType != null) {
            if (state.isLoggedIn) {
                when (state.userType) {
                    "admin" -> {
                        nav.navigate(Route.AdminMain.route) {
                            popUpTo(Route.Splash.route) { inclusive = true }
                        }
                    }
                    "guest" -> {
                        nav.navigate(Route.GuestMain.route) {
                            popUpTo(Route.Splash.route) { inclusive = true }
                        }
                    }
                    else -> {
                        // Fallback: something went wrong
                        nav.navigate(Route.Auth.route) {
                            popUpTo(Route.Splash.route) { inclusive = true }
                        }
                    }
                }
            } else {
                nav.navigate(Route.Auth.route) {
                    popUpTo(Route.Splash.route) { inclusive = true }
                }
            }
        }
    }

    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator()
            Spacer(Modifier.height(12.dp))
            Text("Cargando...")
        }
    }
}