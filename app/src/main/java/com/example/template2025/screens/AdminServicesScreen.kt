package com.example.template2025.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalOverscrollFactory
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.template2025.model.Posada
import com.example.template2025.model.Servicio // Import the real model
import com.example.template2025.viewModel.AppViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun AdminServicesScreen(
    navController: NavController,
    vm: AppViewModel
) {
    val posadaState by vm.posadaState.collectAsState()
    val reservaState by vm.reservaState.collectAsState()
    val servicioState by vm.servicioState.collectAsState() // Use real state

    var selectedPosada by remember { mutableStateOf<Posada?>(null) }
    var selectedStatus by remember { mutableStateOf("Todos") }
    var isPosadaDropdownExpanded by remember { mutableStateOf(false) }
    var isStatusDropdownExpanded by remember { mutableStateOf(false) }

    val statusOptions = listOf("Todos", "Pendiente", "Sin Pagar")

    val filteredServices = remember(servicioState.servicios, reservaState.reservas, selectedStatus) {
        val reservationIdsForPosada = reservaState.reservas
            .filter { it.posadaId == selectedPosada?.id }
            .map { it.id }
            .toSet()

        val servicesForPosada = servicioState.servicios.filter { service ->
            service.reservaId in reservationIdsForPosada
        }

        when (selectedStatus.lowercase()) {
            "todos" -> servicesForPosada
            "pendiente" -> servicesForPosada.filter { it.estado.equals("pendiente", ignoreCase = true) }
            "sin pagar" -> servicesForPosada.filter { it.estado.equals("sin pagar", ignoreCase = true) }
            else -> servicesForPosada
        }
    }

    LaunchedEffect(Unit) {
        if (posadaState.posadas.isEmpty()) {
            vm.getPosadas()
        }
    }

    LaunchedEffect(selectedPosada, selectedStatus) {
        selectedPosada?.let {
            // We need both the reservations for the posada and all services to perform the link.
            vm.getReservasByPosada(it.id)
            vm.getServicios()
        }
    }
    CompositionLocalProvider(LocalOverscrollFactory provides null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // Dropdown filters (this part remains the same)
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // Posada Dropdown
                ExposedDropdownMenuBox(
                    expanded = isPosadaDropdownExpanded,
                    onExpandedChange = { isPosadaDropdownExpanded = it }
                ) {
                    OutlinedTextField(
                        value = selectedPosada?.nombre ?: "Seleccionar Sede",
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isPosadaDropdownExpanded) },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = isPosadaDropdownExpanded,
                        onDismissRequest = { isPosadaDropdownExpanded = false }
                    ) {
                        posadaState.posadas.forEach { posada ->
                            DropdownMenuItem(
                                text = { Text(posada.nombre) },
                                onClick = {
                                    selectedPosada = posada
                                    isPosadaDropdownExpanded = false
                                }
                            )
                        }
                    }
                }
                // Status Dropdown
                ExposedDropdownMenuBox(
                    expanded = isStatusDropdownExpanded,
                    onExpandedChange = { isStatusDropdownExpanded = it }
                ) {
                    OutlinedTextField(
                        value = selectedStatus,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isStatusDropdownExpanded) },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = isStatusDropdownExpanded,
                        onDismissRequest = { isStatusDropdownExpanded = false }
                    ) {
                        statusOptions.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    selectedStatus = option
                                    isStatusDropdownExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Content based on state
            when {
                servicioState.isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.padding(top = 40.dp))
                }
                servicioState.error != null -> {
                    Column(
                        modifier = Modifier.padding(top = 40.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Error: ${servicioState.error}",
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center
                        )
                        Button(onClick = { vm.getServicios() }) {
                            Icon(Icons.Default.Refresh, contentDescription = "Reintentar")
                            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                            Text("Reintentar")
                        }
                    }
                }
                selectedPosada == null -> {
                    Text("Por favor, seleccione una sede para ver los servicios.", textAlign = TextAlign.Center, modifier = Modifier.padding(top = 40.dp))
                }
                filteredServices.isEmpty() -> {
                    Text("No hay servicios que coincidan con los filtros.", textAlign = TextAlign.Center, modifier = Modifier.padding(top = 40.dp))
                }
                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        items(filteredServices, key = { it.id }) { servicio ->
                            ServiceCard(
                                servicio = servicio,
                                onAccept = { vm.acceptServicio(servicio.id) },
                                onReject = { vm.rejectServicio(servicio.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ServiceCard(
    servicio: Servicio,
    onAccept: () -> Unit,
    onReject: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            val (statusText, statusColor) = when {
                servicio.estado.equals("pendiente", ignoreCase = true) -> "Pendiente" to Color(0xFFFBC02D)
                servicio.estado.equals("sin pagar", ignoreCase = true) -> "Sin Pagar" to MaterialTheme.colorScheme.error
                else -> servicio.estado.replaceFirstChar { it.uppercase() } to Color.Gray
            }
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = statusColor.copy(alpha = 0.15f),
                contentColor = statusColor
            ) {
                Text(
                    text = statusText,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(servicio.nombreServicio, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(
                    String.format(Locale.US, "$%.2f", calculateServicePrice(servicio.nombreServicio)),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text("Solicitante: John Doe", style = MaterialTheme.typography.bodyMedium)

            if (servicio.estado.equals("pendiente", ignoreCase = true)) {
                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onAccept,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Aceptar")
                    }
                    OutlinedButton(
                        onClick = onReject,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.error)
                    ) {
                        Text("Rechazar", color = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }
    }
}

private fun calculateServicePrice(serviceName: String): Double {
    val lowerCaseName = serviceName.lowercase()
    return when {
        lowerCaseName.contains("psicológica") -> 0.0
        lowerCaseName.contains("dentista") -> 0.0
        lowerCaseName.contains("documento") -> 5.0
        lowerCaseName.contains("desayuno") -> 15.0
        lowerCaseName.contains("comida") -> 15.0
        lowerCaseName.contains("cena") -> 10.0
        lowerCaseName.contains("lavandería") -> 10.0
        lowerCaseName.contains("regadera") -> 10.0
        lowerCaseName.contains("transporte") -> 20.0
        else -> 0.0
    }
}

