 package com.example.template2025

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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.template2025.composables.AdminScaffold
import com.example.template2025.composables.MainScaffold
import com.example.template2025.model.LoginCredentials
import com.example.template2025.navigation.Route
import com.example.template2025.screens.AdminLoginScreen
import com.example.template2025.screens.UserScreen
import com.example.template2025.ui.theme.CaritasTheme
import com.example.template2025.viewModel.AppViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CaritasTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AppRoot(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}


@Composable
fun AppRoot(modifier: Modifier = Modifier) {
    val vm: AppViewModel = viewModel()
    val authState by vm.auth.collectAsState()
    val nav = rememberNavController()
    val context = LocalContext.current

    NavHost(navController = nav, startDestination = Route.Splash.route) {
        composable(Route.Splash.route) {
            SplashScreen(
                vm = vm,
                nav = nav
            )
        }

        // AUTH FLOW (sin Drawer/BottomBar)
        composable(Route.Auth.route) {
            AuthNavHost(
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

        // MAIN FLOW (con Scaffold + Drawer + BottomBar)
        composable(Route.GuestMain.route) {
            MainScaffold(
                userType = "guest",
                onLogoutClick = { vm.logout() },
                onNavigateToAuth = {
                    nav.navigate(Route.Auth.route) { popUpTo(0) } // limpia back stack
                }
            )
        }

        composable(Route.AdminMain.route) {
            AdminScaffold(
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
    isLoading: Boolean,
    error: String?,
    onErrorDismiss: () -> Unit,
    onLoggedIn: (credentials: LoginCredentials) -> Unit
) {
    val nav = rememberNavController()
    NavHost(navController = nav, startDestination = Route.User.route) {
        composable(Route.User.route) {
            UserScreen(
                onAdminClick = {nav.navigate(Route.AdminLogin.route)}
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