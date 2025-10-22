package com.example.template2025.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.template2025.R
import com.example.template2025.composables.PhoneField
import com.example.template2025.navigation.Route
import com.example.template2025.viewModel.CheckReservationFormState
import com.example.template2025.viewModel.CheckReservationUiState
import com.example.template2025.viewModel.CheckReservationViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckReservationScreen(
    navController: NavController,
    viewModel: CheckReservationViewModel = viewModel(),
    onBack: () -> Unit = {}
) {
    val formState = viewModel.formState
    val searchState = viewModel.searchState
    val onFormChange: (CheckReservationFormState) -> Unit =
        { newState -> viewModel.onFormStateChange(newState) }
    // Manejo de la lógica de navegación
    LaunchedEffect(searchState) {
        when (searchState) {
            is CheckReservationUiState.Success -> {
                val response = searchState.response
                when (response.reservationStatus) {
                    "pendiente", "confirmada" -> {
                        response.qrCodeUrl?.let { url ->
                            val route = Route.QrCode.createRoute(
                                qrCodeUrl = url,
                                posada = response.posada,
                                personas = response.personas.toString(),
                                fecha = response.entrada,
                                telefono = response.telefono?.replaceFirst(formState.country.dialCode, "${formState.country.dialCode}|")
                            )
                            navController.navigate(route) {
                                popUpTo(Route.GuestLogin.route) { inclusive = true }
                            }
                        }
                    }
                    "checkin" -> {
                        navController.navigate(Route.Servicios.route)
                    }
                }
            }
            is CheckReservationUiState.Error -> {
                navController.navigate(Route.Guest.route) {
                    popUpTo(Route.GuestLogin.route) { inclusive = true }
                }
            }
            is CheckReservationUiState.Loading -> { /* Do nothing while loading */ }
            is CheckReservationUiState.Idle -> { /* Do nothing in initial state */ }
        }
    }

    Scaffold(
        containerColor = Color.White
    ) { padding ->

        // Usamos LazyColumn en lugar de Column para toda la pantalla
        LazyColumn(
            modifier = Modifier
                .background(Color.White)
                .padding(padding)
                .fillMaxSize(),
            // Centramos horizontalmente los items por defecto, pero podemos sobreescribirlo
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // --- ITEM 1: ENCABEZADO ---
            item {
                // Spacer para dar espacio superior
                Spacer(Modifier.height(48.dp))
                // Título y subtítulo ya están centrados por el horizontalAlignment de LazyColumn
                Text(
                    text = "Iniciar sesión de huésped",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "Si cerraste la app sin guardar tu QR, ingrese los datos correspondientes",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 24.dp) // Padding para que el texto no toque los bordes si es largo
                )
                Spacer(Modifier.height(24.dp))
            }

            // --- ITEM 2: FORMULARIO ---
            item {
                // Este Column agrupa los campos del formulario para alinearlos a la izquierda
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp) // Padding horizontal para los campos
                ) {
                    // Campo teléfono
                    PhoneField(
                        phone = formState.phone,
                        onPhoneChange = { onFormChange(formState.copy(phone = it)) },
                        selectedCountry = formState.country,
                        onCountryChange = { onFormChange(formState.copy(country = it)) },
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                    Spacer(Modifier.height(4.dp))

                    // Campo nombre
                    OutlinedTextField(
                        value = formState.fullName,
                        onValueChange = { onFormChange(formState.copy(fullName = it)) },
                        label = { Text("Nombre y apellidos") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            }

            // --- ITEM 3: BOTÓN PRINCIPAL Y PIE DE PÁGINA ---
            item {
                Spacer(Modifier.height(24.dp))
                // Botón principal
                Button(
                    onClick = { viewModel.checkReservation() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp) // Padding
                        .height(48.dp),
                    shape = MaterialTheme.shapes.medium,
                    //enabled = formState.fullName.isNotBlank() && formState.phone.isNotBlank() && searchState !is CheckReservationUiState.Loading,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0,156, 166), // Color de fondo personalizado (un verde azulado oscuro)
                        contentColor = Color.White)
                ) {
                    if (searchState is CheckReservationUiState.Loading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Entrar", fontWeight = FontWeight.Bold)
                    }
                }

                // Separador con círculo
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp, horizontal = 24.dp)
                ) {
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .clip(CircleShape)
                    )
                }

                // Botón secundario
                OutlinedButton(
                    onClick = { navController.navigate(Route.Guest.route) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp) // Padding
                        .height(48.dp),
                    shape = MaterialTheme.shapes.medium,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("¿Primera vez? Haz una reservación", fontWeight = FontWeight.Bold,color = Color(0,156,166))
                }
                Spacer(Modifier.height(24.dp))
            }
        }
        Box(modifier = Modifier
            .background(Color.White),
            contentAlignment = Alignment.Center
        ){
            IconButton(
                onClick = { onBack() },
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(start = 16.dp, top = 32.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.arrow_back),
                    contentDescription = "Back"
                )
            }
        }
    }
}




@Preview(showBackground = true, device = "id:pixel_6")
@Composable
private fun CheckReservationScreenPreview() {
    MaterialTheme { // Envuelve en un tema para que los colores funcionen
        CheckReservationScreen(
            navController = NavController(androidx.compose.ui.platform.LocalContext.current),
            //viewModel = CheckReservationViewModel() // Crea una instancia directa para el preview
        )
    }
}