package com.example.template2025.composables

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.template2025.navigation.Route
import com.example.template2025.screens.AdminHomeScreen
import com.example.template2025.screens.CameraPreviewScreen
import com.example.template2025.screens.ProfileScreen
import com.example.template2025.screens.QRScreen
import com.example.template2025.screens.SettingsScreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScaffold(onLogoutClick: () -> Unit, onNavigateToAuth: () -> Unit) {
    val nav = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Text("Navegación", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(16.dp))
                DrawerItem(nav, label = "Panel de control", dest = Route.AdminHome.route, drawerState, scope)
                DrawerItem(nav, label = "Reservaciones", dest = Route.AdminReservations.route, drawerState, scope)
                DrawerItem(nav, label = "Transporte", dest = Route.AdminTransport.route, drawerState, scope)
                DrawerItem(nav, label = "Voluntarios", dest = Route.AdminVolunteers.route, drawerState, scope)
                DrawerItem(nav, label = "Escaner QR", dest = Route.QRScanner.route, drawerState, scope)
                HorizontalDivider()
                NavigationDrawerItem(
                    label = { Text("Cerrar sesión", color = MaterialTheme.colorScheme.error) },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        onLogoutClick()       // pone bandera en false
                        onNavigateToAuth()    // navega inmediato al flujo Auth
                    }
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Caritas MTY") },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Filled.Menu, contentDescription = "Menu")
                        }
                    }
                )
            }
        ) { innerPadding ->
            NavHost(navController = nav, startDestination = Route.AdminHome.route, modifier = Modifier.padding(innerPadding)) {
                composable(Route.AdminHome.route) { AdminHomeScreen() }
                composable(Route.AdminReservations.route) { ProfileScreen() }
                composable(Route.AdminTransport.route) { SettingsScreen() }
                composable(Route.AdminVolunteers.route) { SettingsScreen() }
                composable(Route.QRScanner.route) { CameraPreviewScreen() }
            }
        }
    }
}

@Composable
private fun DrawerItem(
    nav: NavHostController,
    label: String,
    dest: String,
    drawerState: DrawerState,
    scope: CoroutineScope
) {
    NavigationDrawerItem(
        label = { Text(label) },
        selected = currentRoute(nav) == dest,
        onClick = {
            nav.navigate(dest) {
                launchSingleTop = true
                popUpTo(nav.graph.startDestinationId) {saveState = true}
                restoreState = true
            }
            scope.launch { drawerState.close() }
        },
        shape = RoundedCornerShape(0.dp)
    )
}

@Composable
private fun currentRoute(nav: NavHostController): String? {
    val backStackEntry by nav.currentBackStackEntryAsState()
    return backStackEntry?.destination?.route
}