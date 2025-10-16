package com.example.template2025.screens

import androidx.compose.animation.core.copy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.template2025.navigation.Route
import com.example.template2025.viewModel.CheckReservationFormState
import com.example.template2025.viewModel.CheckReservationUiState
import com.example.template2025.viewModel.CheckReservationViewModel
import java.net.URLEncoder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckReservationScreen(
    navController: NavController,
    viewModel: CheckReservationViewModel = viewModel()
) {
    val formState = viewModel.formState
    val searchState = viewModel.searchState
    val onFormChange: (CheckReservationFormState) -> Unit = { newState -> viewModel.onFormStateChange(newState) }
    // Manejo de la lógica de navegación
    LaunchedEffect(searchState) {
        if (searchState is CheckReservationUiState.Success) {
            val response = searchState.response
            when (response.reservationStatus) {
                // Caso 1A: QR pendiente, mostrar QR
                "pendiente", "confirmada" -> {
                    val encodedUrl = URLEncoder.encode(response.qrCodeUrl, "UTF-8")
                    navController.navigate("qr/$encodedUrl") { popUpTo(Route.CheckReservation.route) { inclusive = true }
                    }
                }
                // Caso 1B: QR ya usado, ir a servicios
                "checkin" -> {
                    navController.navigate(Route.Services.route) {
                        popUpTo(Route.CheckReservation.route) { inclusive = true }
                    }
                }
            }
        }
        if (searchState is CheckReservationUiState.Error) {
            // Caso 2: No se encontró, ir a la pantalla de reserva
            navController.navigate(Route.Guest.route) {
                // --- CORRECCIÓN 5: Paréntesis fuera de lugar ---
                popUpTo(Route.CheckReservation.route) { inclusive = true }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Recuperar Reservación") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = 24.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                "Ingresa tus datos para encontrar tu reservación",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(Modifier.height(24.dp))

            OutlinedTextField(
                value = formState.fullName,
                onValueChange = { onFormChange(formState.copy(fullName = it)) },
                label = { Text("Nombre y Apellidos") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(16.dp))

            // --- CORRECCIÓN 6: Usar el PhoneField importado ---
            PhoneField(
                phone = formState.phone,
                onPhoneChange = { onFormChange(formState.copy(phone = it)) },
                selectedCountry = formState.country,
                onCountryChange = { onFormChange(formState.copy(country = it)) },
            )
            Spacer(Modifier.height(32.dp))

            Button(
                onClick = { viewModel.checkReservation() },
                enabled = formState.fullName.isNotBlank() && formState.phone.isNotBlank() && searchState !is CheckReservationUiState.Loading,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (searchState is CheckReservationUiState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Buscar Reservación")
                }
            }
        }
    }
}
@Preview(
    showBackground = true, // Muestra un fondo blanco para el componente
    device = "id:pixel_6"  // Simula el tamaño de un dispositivo específico (opcional pero útil)

)

@Composable
private fun CheckReservationScreenPreview() {
    val fakeNavController = NavController(androidx.compose.ui.platform.LocalContext.current)
    CheckReservationScreen(
        navController = fakeNavController,
        //viewModel = CheckReservationViewModel() // Creamos una instancia directa
    )
}