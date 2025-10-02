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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.template2025.composables.MainScaffold
import com.example.template2025.navigation.Route
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
    val nav = rememberNavController()

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
                onLoggedIn = {
                    vm.login()
                    nav.navigate(Route.Main.route) {
                        popUpTo(Route.Splash.route) { inclusive = true }
                        launchSingleTop = true
                    }
                },
            )
        }

        // MAIN FLOW (con Scaffold + Drawer + BottomBar)
        composable(Route.Main.route) {
            MainScaffold(
                onLogoutClick = { vm.logout() },
                onNavigateToAuth = {
                    nav.navigate(Route.Auth.route) { popUpTo(0) } // limpia back stack
                }
            )
        }
    }

}

@Composable
fun AuthNavHost(onLoggedIn: () -> Unit) {
    val nav = rememberNavController()
    NavHost(navController = nav, startDestination = Route.User.route) {
        composable(Route.User.route) {
            UserScreen()
        }
    }
}

@Composable
fun SplashScreen(vm: AppViewModel, nav: NavHostController) {
    val state by vm.auth.collectAsState()

    LaunchedEffect(state.isLoading, state.isLoggedIn) {
        if (!state.isLoading) {
            if (state.isLoggedIn) {
                nav.navigate(Route.Main.route) {
                    popUpTo(Route.Splash.route) { inclusive = true }
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