package com.example.template2025.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalOverscrollFactory
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.template2025.model.Reserva
import com.example.template2025.navigation.Route
import com.example.template2025.viewModel.AppViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun AdminHomeScreen(
    vm: AppViewModel,
    navController: NavHostController
) {
    val posadaState by vm.posadaState.collectAsState()
    val reservaState by vm.reservaState.collectAsState()

    var isDropdownExpanded by remember { mutableStateOf(false) }
    var selectedPosadaName by remember { mutableStateOf("") }
    val posadaOptions = listOf("Todos los albergues") + posadaState.posadas.map { it.nombre }

    LaunchedEffect(Unit) {
        vm.getPosadas()
    }

    LaunchedEffect(posadaState.posadas) {
        if (posadaState.posadas.isNotEmpty() && selectedPosadaName.isEmpty()) {
            selectedPosadaName = "Todos los albergues"
        }
    }

    LaunchedEffect(selectedPosadaName) {
        if (selectedPosadaName.isEmpty()) {
            return@LaunchedEffect
        }

        if (selectedPosadaName == "Todos los albergues") {
            vm.getReservas()
        } else {
            val selectedPosada = posadaState.posadas.find { it.nombre == selectedPosadaName }
            selectedPosada?.let {
                vm.getReservasByPosada(it.id)
            }
        }
    }

    if (posadaState.isLoading && posadaState.posadas.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    posadaState.error?.let { error ->
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Error al cargar los albergues: $error",
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(16.dp)
                )

                Button(
                    onClick = { vm.getPosadas() },
                    shape = RoundedCornerShape(8.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Reintentar",
                        modifier = Modifier.size(ButtonDefaults.IconSize)
                    )
                    Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                    Text("Reintentar")
                }
            }
        }
        return
    }

    CompositionLocalProvider(LocalOverscrollFactory provides null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .background(MaterialTheme.colorScheme.background),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                getToday(),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))
            // --- Dropdown remains here as it only depends on posadaState, which is already loaded ---
            ExposedDropdownMenuBox(
                expanded = isDropdownExpanded,
                onExpandedChange = { isDropdownExpanded = it },
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .align(Alignment.CenterHorizontally)
            ) {
                OutlinedTextField(
                    value = selectedPosadaName,
                    onValueChange = {},
                    readOnly = true,
                    singleLine = true,
                    textStyle = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Medium
                    ),
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = isDropdownExpanded)
                    },
                    shape = RoundedCornerShape(8.dp),
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedBorderColor = Color.Gray.copy(alpha = 0.5f),
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                        focusedTextColor = MaterialTheme.colorScheme.onSurface
                    ),
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = isDropdownExpanded,
                    onDismissRequest = { isDropdownExpanded = false },
                    modifier = Modifier
                        .exposedDropdownSize(matchTextFieldWidth = true)
                        .background(MaterialTheme.colorScheme.surface)
                ) {
                    posadaOptions.forEach { option ->
                        Column {
                            DropdownMenuItem(
                                text = { Text(option, maxLines = 1) },
                                onClick = {
                                    selectedPosadaName = option
                                    isDropdownExpanded = false
                                }
                            )
                            if (option != posadaOptions.last()) {
                                HorizontalDivider(
                                    color = Color.Gray.copy(alpha = 0.2f),
                                    thickness = 1.dp
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            when {
                reservaState.isLoading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                reservaState.error != null -> {
                    Text(
                        text = "Error: ${reservaState.error}",
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    )
                }
                else -> {
                    Card(
                        modifier = Modifier.fillMaxWidth(0.9f),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Resumen", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(12.dp))

                            if (selectedPosadaName == "Todos los albergues") {
                                posadaState.posadas.forEach { posada ->
                                    val reservasForPosada = reservaState.reservas.filter { it.posadaId == posada.id }
                                    val pending = reservasForPosada.count { it.estado.equals("pendiente", ignoreCase = true) }
                                    val registered = reservasForPosada.size - pending

                                    Text(posada.nombre, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

                                    SummaryItem(
                                        occupied = posada.capacidadTotal - posada.capacidadDisponible,
                                        unoccupied = posada.capacidadDisponible,
                                        totalSpaces = posada.capacidadTotal,
                                        reservasPendientes = pending,
                                        reservasRegistradas = registered,
                                        volunteersCount = 0 // WIP
                                    )
                                }
                            } else {
                                val selectedPosada = posadaState.posadas.find { it.nombre == selectedPosadaName }
                                selectedPosada?.let { posada ->

                                    val pending = reservaState.reservas.count { it.estado.equals("pendiente", ignoreCase = true) }
                                    val registered = reservaState.reservas.size - pending

                                    SummaryItem(
                                        occupied = posada.capacidadTotal - posada.capacidadDisponible,
                                        unoccupied = posada.capacidadDisponible,
                                        totalSpaces = posada.capacidadTotal,
                                        reservasPendientes = pending,
                                        reservasRegistradas = registered,
                                        volunteersCount = 0 // WIP
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(0.9f),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Reservas Recientes", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(8.dp))

                            if (reservaState.reservas.isEmpty()) {
                                Text(
                                    "No hay reservas recientes.",
                                    modifier = Modifier.align(Alignment.CenterHorizontally),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.Gray
                                )
                            } else {
                                val recentReservas = reservaState.reservas
                                    .sortedByDescending { it.createdAt }
                                    .take(5)

                                recentReservas.forEach { reserva ->
                                    val posadaName =
                                        posadaState.posadas.find { it.id == reserva.posadaId }?.nombre
                                            ?: "Albergue desconocido"

                                    ReservationCard(
                                        navController = navController,
                                        reserva = reserva,
                                        posadaName = posadaName
                                    )
                                }
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(40.dp))
        }
    }

}

@Preview(showBackground = true)
@Composable
fun AdminHomeScreenPreview() {
    AdminHomeScreen(vm = viewModel(), navController = rememberNavController())
}

fun getToday(): String {
    val locale = Locale.forLanguageTag("es-MX")
    val date = Date()
    val sdf = SimpleDateFormat("EEEE d 'de' MMMM 'de' yyyy", locale)
    val formattedDate = sdf.format(date)
        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(locale) else it.toString() }

    return formattedDate.replaceFirstChar {
        if (it.isLowerCase()) it.titlecase(locale) else it.toString()
    }
}

@Composable
fun SummaryItem(
    occupied: Int,
    unoccupied: Int,
    totalSpaces: Int,
    reservasPendientes: Int,
    reservasRegistradas: Int,
    volunteersCount: Int // WIP
) {
    val occupiedColor = Color(0xFFD32F2F)
    val freeColor = Color(0xFF388E3C)
    val pendingColor = Color(0xFFFBC02D)
    val registeredColor = MaterialTheme.colorScheme.primary

    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        com.google.accompanist.flowlayout.FlowRow(
            modifier = Modifier.fillMaxWidth(),
            mainAxisSpacing = 8.dp,
            crossAxisSpacing = 8.dp,
            mainAxisAlignment = com.google.accompanist.flowlayout.FlowMainAxisAlignment.SpaceEvenly
        ) {
            StatBox(label = "Ocupados", value = "$occupied", valueColor = occupiedColor)
            StatBox(label = "Libres", value = "$unoccupied", valueColor = freeColor)
            StatBox(label = "Total", value = "$totalSpaces")
        }

        Spacer(modifier = Modifier.height(12.dp))

        com.google.accompanist.flowlayout.FlowRow(
            modifier = Modifier.fillMaxWidth(),
            mainAxisSpacing = 8.dp,
            crossAxisSpacing = 8.dp,
            mainAxisAlignment = com.google.accompanist.flowlayout.FlowMainAxisAlignment.SpaceEvenly
        ) {
            StatBox(label = "Pendientes", value = "$reservasPendientes", valueColor = pendingColor)
            StatBox(label = "Registradas", value = "$reservasRegistradas", valueColor = registeredColor)
            StatBox(label = "Voluntarios", value = "$volunteersCount")
        }

        Spacer(modifier = Modifier.height(12.dp))

        Spacer(modifier = Modifier.height(10.dp))
        HorizontalDivider(color = Color.Gray.copy(alpha = 0.3f))
    }
}

@Composable
fun ReservationCard(
    navController: NavHostController,
    reserva: Reserva,
    posadaName: String,
) {
    val statusColor = if (reserva.estado.equals("pendiente", ignoreCase = true)) {
        Color(0xFFFBC02D)
    } else {
        MaterialTheme.colorScheme.primary
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable {
                navController.navigate(Route.AdminReservationsDetail.createRoute(reserva.id))
            },
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, Color.Gray.copy(alpha = 0.3f)),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = statusColor.copy(alpha = 0.15f),
                    contentColor = statusColor
                ) {
                    Text(
                        text = reserva.estado.replaceFirstChar { it.uppercase() },
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = reserva.nombreSolicitante,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )

            Text(
                text = posadaName,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Normal,
                color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.7f)
            )

            Text(
                text = formatReservationDate(reserva.fechaEntrada),
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                modifier = Modifier.padding(top = 2.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = Color.Gray.copy(alpha = 0.1f))
            Spacer(modifier = Modifier.height(12.dp))

            com.google.accompanist.flowlayout.FlowRow(
                modifier = Modifier.fillMaxWidth(),
                mainAxisSpacing = 8.dp,
                crossAxisSpacing = 8.dp,
                mainAxisAlignment = com.google.accompanist.flowlayout.FlowMainAxisAlignment.Start
            ) {
                StatBox(label = "Total", value = "${reserva.totalPersonas}")
                StatBox(label = "Hombres", value = "${reserva.hombresCount}")
                StatBox(label = "Mujeres", value = "${reserva.mujeresCount}")
            }
        }
    }
}

@Composable
fun StatBox(
    label: String,
    value: String,
    valueColor: Color = MaterialTheme.colorScheme.onSurface
) {
    Surface(
        modifier = Modifier.sizeIn(minWidth = 90.dp),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, Color.Gray.copy(alpha = 0.2f)),
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = valueColor
            )
        }
    }
}

private fun formatReservationDate(dateString: String): String {
    val inputLocale = Locale.getDefault()
    val outputLocale = Locale.forLanguageTag("es-MX")

    val outputFormat = SimpleDateFormat("dd / MMM / yyyy", outputLocale)

    val inputParsers = listOf(
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", inputLocale), // Handles "2025-10-20T06:00:00.000Z"
        SimpleDateFormat("yyyy-MM-dd HH:mm:ss", inputLocale),           // Handles "2025-10-20 06:00:00"
        SimpleDateFormat("yyyy-MM-dd", inputLocale)                     // Handles "2025-10-20"
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