package com.example.template2025.composables

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.template2025.navigation.Route
import com.example.template2025.screens.HomeScreen
import com.example.template2025.screens.ProfileScreen
import com.example.template2025.screens.SettingsScreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


data class BottomItem(val route: String, val label: String, val icon: ImageVector)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScaffold(onLogoutClick: () -> Unit, onNavigateToAuth: () -> Unit) {
    val nav = rememberNavController()
    val items = listOf(
        BottomItem(Route.Home.route, "Inicio", Icons.Filled.Home),
        BottomItem(Route.Profile.route, "Perfil", Icons.Filled.Person),
        BottomItem(Route.Settings.route, "Config", Icons.Filled.Settings),
    )
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Text("Navegación", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(16.dp))
                DrawerItem(nav, label = "Inicio", dest = Route.Home.route, drawerState, scope)
                DrawerItem(nav, label = "Perfil", dest = Route.Profile.route, drawerState, scope)
                DrawerItem(nav, label = "Configuración", dest = Route.Settings.route, drawerState, scope)
                Divider()
                NavigationDrawerItem(
                    label = { Text("Cerrar sesión") },
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
                    title = { Text("Template App") },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Filled.Menu, contentDescription = "Menu")
                        }
                    }
                )
            },
            bottomBar = {
                NavigationBar {
                    val current = currentRoute(nav)
                    items.forEach { item ->
                        NavigationBarItem(
                            selected = current == item.route,
                            onClick = { if (current != item.route) nav.navigate(item.route) },
                            icon = { Icon(item.icon, contentDescription = item.label) },
                            label = { Text(item.label) }
                        )
                    }
                }
            }
        ) { innerPadding ->
            NavHost(navController = nav, startDestination = Route.Home.route, modifier = Modifier.padding(innerPadding)) {
                composable(Route.Home.route)     { HomeScreen() }
                composable(Route.Profile.route)  { ProfileScreen() }
                composable(Route.Settings.route) { SettingsScreen() }
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
            nav.navigate(dest) { launchSingleTop = true }
            scope.launch { drawerState.close() }
        }
    )
}

@Composable
private fun currentRoute(nav: NavHostController): String? {
    val backStackEntry by nav.currentBackStackEntryAsState()
    return backStackEntry?.destination?.route
}