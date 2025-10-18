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
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.template2025.navigation.Route
import com.example.template2025.screens.AdminHomeScreen
import com.example.template2025.screens.AdminReservationDetailScreen
import com.example.template2025.screens.AdminReservationScreen
import com.example.template2025.screens.SettingsScreen
import com.example.template2025.viewModel.AppViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScaffold(
    vm: AppViewModel,
    onLogoutClick: () -> Unit,
    onNavigateToAuth: () -> Unit
) {
    val nav = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val current = currentRoute(nav)
    val title = when (current) {
        Route.AdminHome.route -> "Panel de control"
        Route.AdminReservations.route -> "Reservaciones"
        Route.AdminTransport.route -> "Transporte"
        Route.AdminVolunteers.route -> "Voluntarios"
        else -> "Caritas MTY"
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = MaterialTheme.colorScheme.background,
                drawerContentColor = MaterialTheme.colorScheme.onBackground,
                drawerShape = RoundedCornerShape(0.dp)
            ) {
                Text("Navegación", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(16.dp))
                DrawerItem(nav, label = "Panel de control", dest = Route.AdminHome.route, drawerState, scope)
                DrawerItem(nav, label = "Reservaciones", dest = Route.AdminReservations.route, drawerState, scope)
                DrawerItem(nav, label = "Transporte", dest = Route.AdminTransport.route, drawerState, scope)
                DrawerItem(nav, label = "Voluntarios", dest = Route.AdminVolunteers.route, drawerState, scope)
                HorizontalDivider()
                NavigationDrawerItem(
                    label = { Text("Cerrar sesión", color = MaterialTheme.colorScheme.error) },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        onLogoutClick()
                        onNavigateToAuth()
                    }
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(title) },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Filled.Menu, contentDescription = "Menu")
                        }
                    }
                )
            }
        ) { innerPadding ->
            NavHost(navController = nav, startDestination = Route.AdminHome.route, modifier = Modifier.padding(innerPadding)) {
                composable(Route.AdminHome.route) { AdminHomeScreen(vm, nav) }
                composable(Route.AdminReservations.route) { AdminReservationScreen(nav, vm) }
                composable(Route.AdminTransport.route) { SettingsScreen() }
                composable(Route.AdminVolunteers.route) { SettingsScreen() }
                composable(
                    route = Route.AdminReservationsDetail.route,
                    arguments = listOf(navArgument("reservaId") { type = NavType.IntType })
                ) { backStackEntry ->
                    val reservaId = backStackEntry.arguments?.getInt("reservaId")
                    if (reservaId != null) {
                        AdminReservationDetailScreen(
                            navController = nav,
                            vm = vm,
                            reservaId = reservaId
                        )
                    }
                }
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