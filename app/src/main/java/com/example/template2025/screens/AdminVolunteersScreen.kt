package com.example.template2025.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalOverscrollFactory
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.template2025.model.Posada
import com.example.template2025.viewModel.AppViewModel
import kotlin.text.append

private data class Voluntario(
    val id: Int,
    val posadaId: Int,
    val nombre: String,
    val telefono: String,
    val estado: String
)

private val sampleVolunteers = listOf(
    Voluntario(1, 1, "Elena Rodriguez", "+528112345678", "pendiente"),
    Voluntario(2, 2, "Roberto Morales", "+528187654321", "pendiente"),
    Voluntario(3, 1, "Sofia Castillo", "+528111223344", "checkin"),
    Voluntario(4, 2, "Javier Nunez", "+528155667788", "checkout")
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun AdminVolunteersScreen(
    navController: NavController,
    vm: AppViewModel
) {
    val posadaState by vm.posadaState.collectAsState()
    var selectedPosada by remember { mutableStateOf<Posada?>(null) }
    var selectedStatus by remember { mutableStateOf("Todos") }
    var isPosadaDropdownExpanded by remember { mutableStateOf(false) }
    var isStatusDropdownExpanded by remember { mutableStateOf(false) }

    val statusOptions = listOf("Todos", "Pendiente", "Check-in")

    val filteredVolunteers = remember(selectedPosada, selectedStatus) {
        val activeVolunteers = sampleVolunteers.filter { !it.estado.equals("checkout", ignoreCase = true) }
        val posadaFiltered = if (selectedPosada != null) {
            activeVolunteers.filter { it.posadaId == selectedPosada?.id }
        } else {
            emptyList()
        }

        when (selectedStatus.lowercase()) {
            "todos" -> posadaFiltered
            "pendiente" -> posadaFiltered.filter { it.estado.equals("pendiente", ignoreCase = true) }
            "check-in" -> posadaFiltered.filter { it.estado.equals("checkin", ignoreCase = true) }
            else -> posadaFiltered
        }
    }

    LaunchedEffect(Unit) {
        if (posadaState.posadas.isEmpty()) {
            vm.getPosadas()
        }
    }

    // --- OVERSCROLL FIX WRAPPER ---
    CompositionLocalProvider(LocalOverscrollFactory provides null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // --- Dropdown Filters ---
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
                    Text("Por favor, seleccione una sede para ver los voluntarios.", textAlign = TextAlign.Center, modifier = Modifier.padding(top = 40.dp))
                }
                filteredVolunteers.isEmpty() -> {
                    Text("No hay voluntarios que coincidan con los filtros.", textAlign = TextAlign.Center, modifier = Modifier.padding(top = 40.dp))
                }
                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        items(filteredVolunteers, key = { it.id }) { voluntario ->
                            VolunteerCard(voluntario = voluntario)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun VolunteerCard(voluntario: Voluntario) {
    val uriHandler = LocalUriHandler.current

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            val (statusText, statusColor) = when {
                voluntario.estado.equals("pendiente", ignoreCase = true) -> "Pendiente" to Color(0xFFFBC02D)
                voluntario.estado.equals("checkin", ignoreCase = true) -> "Activo" to MaterialTheme.colorScheme.primary
                else -> voluntario.estado.replaceFirstChar { it.uppercase() } to Color.Gray
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

            Text(voluntario.nombre, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))

            val annotatedPhoneString = formatPhoneNumber(voluntario.telefono, "MX")
            var textLayoutResult by remember { mutableStateOf<TextLayoutResult?>(null) }

            Row(
                modifier = Modifier
                    .pointerInput(Unit) {
                        detectTapGestures { offset ->
                            textLayoutResult?.let { layoutResult ->
                                val position = layoutResult.getOffsetForPosition(offset)
                                annotatedPhoneString.getLinkAnnotations(position, position)
                                    .firstOrNull()?.let { link ->
                                        if (link.item is LinkAnnotation.Url) {
                                            uriHandler.openUri((link.item as LinkAnnotation.Url).url)
                                        }
                                    }
                            }
                        }
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Teléfono: ",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
                Text(
                    text = annotatedPhoneString,
                    style = MaterialTheme.typography.bodyMedium,
                    onTextLayout = { result ->
                        textLayoutResult = result
                    }
                )
            }
            if (voluntario.estado.equals("pendiente", ignoreCase = true)) {
                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { /* TODO: Accept action */ },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Aceptar")
                    }
                    OutlinedButton(
                        onClick = { /* TODO: Deny action */ },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.error)
                    ) {
                        Text("Rechazar", color = MaterialTheme.colorScheme.error)
                    }
                }
            } else if (voluntario.estado.equals("checkin", ignoreCase = true)) {
                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { /* TODO: Finalize action */ },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Finalizar")
                }
            }
        }
    }
}

@Composable
private fun formatPhoneNumber(fullPhoneNumber: String, iso: String): AnnotatedString {
    val countryMap = mapOf(
        "MX" to "+52", "US" to "+1", "ES" to "+34",
        "CO" to "+57", "AR" to "+54", "PE" to "+51"
    )

    val dialCode = countryMap[iso.uppercase()] ?: ""
    val localNumber = fullPhoneNumber.removePrefix(dialCode).filter { it.isDigit() }

    val formattedLocalNumber = when (localNumber.length) {
        12 -> localNumber.replaceFirst(Regex("(\\d{3})(\\d{3})(\\d{3})(\\d{3})"), "$1 $2 $3 $4")
        11 -> localNumber.replaceFirst(Regex("(\\d{3})(\\d{4})(\\d{4})"), "$1 $2 $3")
        10 -> localNumber.replaceFirst(Regex("(\\d{2})(\\d{4})(\\d{4})"), "$1 $2 $3")
        9  -> localNumber.replaceFirst(Regex("(\\d{3})(\\d{3})(\\d{3})"), "$1 $2 $3")
        8  -> localNumber.replaceFirst(Regex("(\\d{4})(\\d{4})"), "$1 $2")
        7  -> localNumber.replaceFirst(Regex("(\\d{3})(\\d{4})"), "$1 $2")
        else -> localNumber
    }

    val displayText = "$dialCode $formattedLocalNumber"

    return buildAnnotatedString {
        append(displayText)
        addStyle(
            style = SpanStyle(
                color = MaterialTheme.colorScheme.primary,
                textDecoration = TextDecoration.Underline
            ),
            start = 0,
            end = displayText.length
        )
        addLink(
            url = LinkAnnotation.Url("tel:$fullPhoneNumber"),
            start = 0,
            end = displayText.length
        )
    }
}
