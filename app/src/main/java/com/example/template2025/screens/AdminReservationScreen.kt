package com.example.template2025.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalOverscrollFactory
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QrCodeScanner
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.template2025.components.Servicio
import com.example.template2025.model.Posada
import com.example.template2025.model.Reserva
import com.example.template2025.navigation.Route
import com.example.template2025.viewModel.AppViewModel
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.text.contains


private val sampleServices = listOf(
    Servicio(
        "Transporte",
        "22 / Oct / 2025",
        "14:30",
        150.00
    ),
    Servicio(
        "Comida",
        "22 / Oct / 2025",
        "20:00",
        75.50
    ),
    Servicio(
        "Lavandería",
        "23 / Oct / 2025",
        "11:00",
        50.00
    ),
    Servicio(
        "Comida",
        "23 / Oct / 2025",
        "12:30",
        85.00
    ),
    Servicio(
        "Transporte",
        "24 / Oct / 2025",
        "09:00",
        25.00
    )
)

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun AdminReservationScreen(
    navController: NavController,
    vm: AppViewModel = viewModel()
) {
    val posadaState by vm.posadaState.collectAsState()
    val reservaState by vm.reservaState.collectAsState()

    var selectedPosada by remember { mutableStateOf<Posada?>(null) }
    var selectedStatus by remember { mutableStateOf("Todos") }

    var isPosadaDropdownExpanded by remember { mutableStateOf(false) }
    var isStatusDropdownExpanded by remember { mutableStateOf(false) }

    val statusOptions = listOf("Todos", "Pendiente", "Registrada")

    val filteredReservas = remember(reservaState.reservas, selectedStatus) {
        val activeReservas = reservaState.reservas.filter { !it.estado.equals("checkout", ignoreCase = true) }

        when (selectedStatus.lowercase()) {
            "todos" -> activeReservas
            "pendiente" -> reservaState.reservas.filter { it.estado.equals("pendiente", ignoreCase = true) }
            "registrada" -> reservaState.reservas.filter { it.estado.equals("checkin", ignoreCase = true) }
            else -> activeReservas
        }
    }

    LaunchedEffect(Unit) {
        if (posadaState.posadas.isEmpty()) {
            vm.getPosadas()
        }
    }

    LaunchedEffect(selectedPosada) {
        selectedPosada?.let {
            vm.getReservasByPosada(it.id)
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
                        placeholder = { Text("Seleccionar Sede") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isPosadaDropdownExpanded) },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
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
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
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
                    Text("Por favor, seleccione una sede para ver las reservaciones.", textAlign = TextAlign.Center, modifier = Modifier.padding(top = 40.dp))
                }
                reservaState.isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.padding(top = 40.dp))
                }
                reservaState.error != null -> {
                    Text("Error: ${reservaState.error}", color = MaterialTheme.colorScheme.error, textAlign = TextAlign.Center, modifier = Modifier.padding(top = 40.dp))
                }
                filteredReservas.isEmpty() -> {
                    Text("No hay reservaciones que coincidan con los filtros.", textAlign = TextAlign.Center, modifier = Modifier.padding(top = 40.dp))
                }
                else -> {
                    Button(
                        onClick = {
                            navController.navigate(Route.QRScanner.route) {
                                popUpTo(Route.AdminReservations.route) { inclusive = true }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.QrCodeScanner, contentDescription = "Escanear QR")
                        Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                        Text("Escanear Código QR")
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    val count = filteredReservas.size
                    val label = if (count == 1) "reservación" else "reservaciones"
                    Text(
                        text = "Mostrando $count $label",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Start
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    LazyColumn(
                        contentPadding = PaddingValues(vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        items(filteredReservas, key = { it.id }) { reserva ->
                            DetailedReservationCard(
                                reserva = reserva,
                                onCardClick = {
                                    navController.navigate(Route.AdminReservationsDetail.createRoute(reserva.id))
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun DetailedReservationCard(
    reserva: Reserva,
    onCardClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onCardClick),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(reserva.nombreSolicitante, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                val (statusText, statusColor) = when {
                    reserva.estado.equals("pendiente", ignoreCase = true) -> "Pendiente" to Color(0xFFFBC02D)
                    reserva.estado.equals("checkin", ignoreCase = true) -> "Registrado" to MaterialTheme.colorScheme.primary
                    else -> reserva.estado.replaceFirstChar { it.uppercase() } to Color.Gray
                }
                CardInfoChip("Estado", statusText, statusColor)
                CardInfoChip("Entrada", formatReservationDate(reserva.fechaEntrada))
                CardInfoChip("Personas", reserva.totalPersonas.toString())
            }

            if (!reserva.estado.equals("pendiente", ignoreCase = true)) {
                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(8.dp))

                val groupedServices = sampleServices.groupBy { it.nombre }
                val totalServices = sampleServices.sumOf { it.precio }

                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    groupedServices.forEach { (serviceType, services) ->
                        val count = services.size
                        val subtotal = services.sumOf { it.precio }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "$serviceType (x$count)",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = String.format(Locale.US, "$%.2f", subtotal),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    HorizontalDivider(color = Color.Gray.copy(alpha = 0.3f))
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Total de Servicios",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = String.format(Locale.US, "$%.2f", totalServices),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

        }
    }
}

@Composable
fun CardInfoChip(label: String, value: String, valueColor: Color = Color.Unspecified) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
        Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, color = valueColor)
    }
}

private fun formatReservationDate(dateString: String): String {
    val outputLocale = Locale.forLanguageTag("es-MX")
    val outputFormat = SimpleDateFormat("dd / MMM / yyyy", outputLocale)

    val inputParsers = listOf(
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault()),
        SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()),
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    )

    for (parser in inputParsers) {
        try {
            if ("'Z'" in parser.toPattern()) {
                parser.timeZone = java.util.TimeZone.getTimeZone("UTC")
            }
            val date = parser.parse(dateString)
            if (date != null) {
                val formattedDate = outputFormat.format(date)
                val parts = formattedDate.split(" ")
                if (parts.size == 5) {
                    return "${parts[0]} ${parts[1]} ${parts[2].replaceFirstChar { it.uppercase(outputLocale) }} ${parts[3]} ${parts[4]}"
                }
                return formattedDate
            }
        } catch (_: Exception) {

        }
    }
    return dateString
}
