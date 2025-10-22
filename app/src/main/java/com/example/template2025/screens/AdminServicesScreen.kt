package com.example.template2025.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalOverscrollFactory
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.template2025.viewModel.AppViewModel
import java.util.Locale

private data class Servicio(
    val id: Int,
    val posadaId: Int,
    val nombreSolicitante: String,
    val nombreServicio: String,
    val precio: Double,
    val estado: String
)

private val sampleServices = listOf(
    Servicio(1, 1, "Juan Perez", "Transporte", 150.00, "pendiente"),
    Servicio(2, 1, "Maria Garcia", "Comida", 75.50, "sin pagar"),
    Servicio(3, 2, "Carlos Lopez", "Lavandería", 50.00, "pendiente"),
    Servicio(4, 1, "Ana Martinez", "Comida", 85.00, "pagado"), // This one will be filtered out
    Servicio(5, 2, "Luis Hernandez", "Transporte", 25.00, "sin pagar")
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class) // Added ExperimentalFoundationApi
@Composable
fun AdminServicesScreen(
    navController: NavController,
    vm: AppViewModel
) {
    val posadaState by vm.posadaState.collectAsState()
    var selectedPosada by remember { mutableStateOf<Posada?>(null) }
    var selectedStatus by remember { mutableStateOf("Todos") }
    var isPosadaDropdownExpanded by remember { mutableStateOf(false) }
    var isStatusDropdownExpanded by remember { mutableStateOf(false) }

    val statusOptions = listOf("Todos", "Pendiente", "Sin Pagar")

    val filteredServices = remember(selectedPosada, selectedStatus) {
        val activeServices = sampleServices.filter { !it.estado.equals("pagado", ignoreCase = true) }
        val posadaFiltered = if (selectedPosada != null) {
            activeServices.filter { it.posadaId == selectedPosada?.id }
        } else {
            emptyList()
        }

        when (selectedStatus.lowercase()) {
            "todos" -> posadaFiltered
            "pendiente" -> posadaFiltered.filter { it.estado.equals("pendiente", ignoreCase = true) }
            "sin pagar" -> posadaFiltered.filter { it.estado.equals("sin pagar", ignoreCase = true) }
            else -> posadaFiltered
        }
    }

    LaunchedEffect(Unit) {
        if (posadaState.posadas.isEmpty()) {
            vm.getPosadas()
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

            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
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

            when {
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
                            ServiceCard(servicio = servicio)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ServiceCard(servicio: Servicio) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            val (statusText, statusColor) = when {
                servicio.estado.equals("pendiente", ignoreCase = true) -> "Pendiente" to Color(0xFFFBC02D)
                servicio.estado.equals("sin pagar", ignoreCase = true) -> "Sin Pagar" to MaterialTheme.colorScheme.primary
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
                    String.format(Locale.US, "$%.2f", servicio.precio),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text("Solicitante: ${servicio.nombreSolicitante}", style = MaterialTheme.typography.bodyMedium)

            if (servicio.estado.equals("pendiente", ignoreCase = true)) {
                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { /* TODO: Implement Accept action */ },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Aceptar")
                    }
                    OutlinedButton(
                        onClick = { /* TODO: Implement Reject action */ },
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
