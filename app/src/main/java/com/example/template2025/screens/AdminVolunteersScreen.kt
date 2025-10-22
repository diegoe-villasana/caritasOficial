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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
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
import com.example.template2025.model.Voluntario
import com.example.template2025.viewModel.AppViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun AdminVolunteersScreen(
    navController: NavController,
    vm: AppViewModel
) {
    val posadaState by vm.posadaState.collectAsState()
    val voluntarioState by vm.voluntarioState.collectAsState()

    var selectedPosada by remember { mutableStateOf<Posada?>(null) }
    var selectedStatus by remember { mutableStateOf("Todos") }
    var isPosadaDropdownExpanded by remember { mutableStateOf(false) }
    var isStatusDropdownExpanded by remember { mutableStateOf(false) }

    val statusOptions = listOf("Todos", "Pendiente", "Activo")

    val filteredVolunteers = remember(voluntarioState.voluntarios, selectedPosada, selectedStatus) {
        val activeVolunteers = voluntarioState.voluntarios.filter { !it.estado.equals("checkout", ignoreCase = true) }

        val posadaFiltered = if (selectedPosada != null) {
            activeVolunteers.filter { it.posadaId == selectedPosada?.id }
        } else {
            // When no shelter is selected, show nothing.
            emptyList()
        }

        when (selectedStatus.lowercase()) {
            "todos" -> posadaFiltered
            "pendiente" -> posadaFiltered.filter { it.estado.equals("pendiente", ignoreCase = true) }
            "activo" -> posadaFiltered.filter { it.estado.equals("checkin", ignoreCase = true) }
            else -> posadaFiltered
        }
    }

    LaunchedEffect(selectedPosada, selectedStatus) {
        if (posadaState.posadas.isEmpty()) {
            vm.getPosadas()
        }

        vm.getVoluntarios()
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
                voluntarioState.isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.padding(top = 40.dp))
                }
                voluntarioState.error != null -> {
                    Column(
                        modifier = Modifier.padding(top = 40.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Error: ${voluntarioState.error}",
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center
                        )
                        Button(onClick = { vm.getVoluntarios() }) {
                            Icon(Icons.Default.Refresh, contentDescription = "Reintentar")
                            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                            Text("Reintentar")
                        }
                    }
                }
                selectedPosada == null -> {
                    Text("Por favor, seleccione una sede para ver los voluntarios.", textAlign = TextAlign.Center, modifier = Modifier.padding(top = 40.dp))
                }
                filteredVolunteers.isEmpty() -> {
                    Text("No hay voluntarios que coincidan con los filtros.", textAlign = TextAlign.Center, modifier = Modifier.padding(top = 40.dp))
                }
                else -> {
                    val count = filteredVolunteers.size
                    val label = if (count == 1) "voluntario" else "voluntarios"
                    Text(
                        text = "Mostrando $count $label",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray,
                        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                        textAlign = TextAlign.Start
                    )

                    LazyColumn(
                        contentPadding = PaddingValues(vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        items(filteredVolunteers, key = { it.id }) { voluntario ->
                            VolunteerCard(voluntario = voluntario, onAccept = {
                                selectedPosada?.let { posada ->
                                    vm.acceptVoluntario(voluntario.id)
                                }
                            }, onReject = {
                                vm.rejectVoluntario(voluntario.id)
                            }, onFinalize = {
                                vm.finalizeVoluntario(voluntario.id)
                            })
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun VolunteerCard(
    voluntario: Voluntario,
    onAccept: () -> Unit,
    onReject: () -> Unit,
    onFinalize: () -> Unit
) {
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

            val annotatedPhoneString = formatPhoneNumber(voluntario.telefono, "MX") // Assuming MX for now
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
                    text = annotatedPhoneString,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
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
            } else if (voluntario.estado.equals("checkin", ignoreCase = true)) {
                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = onFinalize,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Finalizar Voluntariado")
                }
            }
        }
    }
}

@Composable
private fun formatPhoneNumber(fullPhoneNumber: String, iso: String): AnnotatedString {
    val countryMap = mapOf(
        "MX" to "+52", "US" to "+1", "ES" to "+34","CO" to "+57", "AR" to "+54", "PE" to "+51"
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