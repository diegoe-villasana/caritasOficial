package com.example.template2025.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.template2025.navigation.Route

@Composable
fun HomeScreen(nav: NavHostController) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "Pantalla de Reserva")
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { nav.navigate(Route.QRScreen.route) }) {
                Text("Generar QR de Reserva")
            }
        }
    }
}