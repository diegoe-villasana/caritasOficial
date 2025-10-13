package com.example.template2025.screens

import android.app.DatePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.template2025.R
import com.example.template2025.modelInn.Posadas
import com.example.template2025.modelInn.getPosadas
import com.example.template2025.components.PosadasCard
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GuestScreen() {
    var uiState by remember { mutableStateOf(GuestScreenState()) }
    val posadasList = remember { getPosadas() }

    Scaffold(modifier = Modifier.fillMaxSize()) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .padding(horizontal = 24.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Seccion 1, cabecera y seleccion de sede y fecha
            item {
                Spacer(modifier = Modifier.height(56.dp))
                Text(
                    "Nueva Reserva",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Al confirmar, se genera un QR para presentarlo en la posada.",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(24.dp))

                // Selector de sede
                DropdownField(
                    label = "Sede",
                    selectValue = uiState.selectedPosada?.name ?: "Seleccionar sede",
                    expanded = uiState.isHeadquarterExpanded,
                    onExpandedChange = { expanded -> uiState = uiState.copy(isHeadquarterExpanded = expanded) },
                    onDismissRequest = { uiState = uiState.copy(isHeadquarterExpanded = false) }
                ) {
                    posadasList.forEach { posada ->
                        DropdownMenuItem(
                            text = { Text(posada.name) },
                            onClick = {
                                uiState = uiState.copy(
                                    selectedPosada = posada,
                                    isHeadquarterExpanded = false
                                )
                            }
                        )
                    }
                }

                uiState.selectedPosada?.let { posadaSeleccionada ->
                    Spacer(modifier = Modifier.height(16.dp))
                    PosadasCard(posadas = posadaSeleccionada, onItemClick = {})
                }

                var showDatePicker by remember { mutableStateOf(false) }

                Spacer(modifier = Modifier.height(16.dp))

                // 1. Envolvemos el campo de texto en un Box
                Box(modifier = Modifier.fillMaxWidth()) {
                    // El campo de texto ya no necesita ser clicable
                    OutlinedTextField(
                        value = uiState.entryDate,
                        onValueChange = { /* No hace nada */ },
                        label = { Text("Fecha de entrada") },
                        readOnly = true,
                        leadingIcon = {
                            Icon(
                                painter = painterResource(id = R.drawable.icon_calendar),
                                contentDescription = "Seleccionar fecha"
                            )
                        },
                        // Lo hacemos que ocupe todo el ancho del Box
                        modifier = Modifier.fillMaxWidth()
                    )

                    // 2. Ponemos una "capa" transparente y clicable encima
                    Box(
                        modifier = Modifier
                            .matchParentSize() // Ocupa el mismo tamaño que el OutlinedTextField
                            .clickable {
                                showDatePicker = true
                            } // Esta es la capa que captura el clic
                    )
                }

                // El diálogo del calendario (este código ya estaba bien)
                if (showDatePicker) {
                    val datePickerState = rememberDatePickerState()
                    val confirmEnabled = remember(datePickerState.selectedDateMillis) {
                        datePickerState.selectedDateMillis != null
                    }
                    DatePickerDialog(
                        onDismissRequest = { showDatePicker = false },
                        confirmButton = {
                            Button(
                                onClick = {
                                    showDatePicker = false
                                    datePickerState.selectedDateMillis?.let { millis ->
                                        val selectedDate = Instant
                                            .ofEpochMilli(millis)
                                            .atZone(ZoneId.of("UTC"))
                                            .toLocalDate()
                                        uiState = uiState.copy(
                                            entryDate = selectedDate.format(
                                                DateTimeFormatter.ofPattern("dd/MM/yyyy")
                                            )
                                        )
                                    }
                                },
                                enabled = confirmEnabled
                            ) {
                                Text("Confirmar")
                            }
                        },
                        dismissButton = {
                            Button(onClick = { showDatePicker = false }) {
                                Text("Cancelar")
                            }
                        }
                    ) {
                        DatePicker(state = datePickerState)
                    }
                }


                Spacer(modifier = Modifier.height(16.dp))

                var personCountText by remember { mutableStateOf(uiState.personCount.toString()) }


                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Información del Solicitante",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Secciones de persona
            items(uiState.personCount) { idx ->
                PersonInfoSection(
                    personNumber = idx + 1,
                    personInfo = uiState.personDetails[idx],
                    onPersonInfoChange = { updated ->
                        val newList = uiState.personDetails.toMutableList()
                        newList[idx] = updated
                        uiState = uiState.copy(personDetails = newList)
                    }
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
            // Sección 3: Conteo de Personas
            item {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Total de Personas",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))

                //Contador hombres
                Counter(
                    "Hombres",
                    count = uiState.menCount,
                    onCountChange = {newCount -> uiState = uiState.copy(menCount = newCount)}
                )
                Spacer(modifier=Modifier.height(16.dp))

                //Contador Mujeres
                Counter(
                    "Mujeres",
                    count = uiState.womenCount,
                    onCountChange = {newCount -> uiState = uiState.copy(womenCount = newCount)}
                )

                Spacer(modifier = Modifier.height(32.dp))


            }


            item {

                Button(
                    onClick = {
                        // TODO: Navegar a pantalla de confirmación / generar QR con uiState
                        // uiState.selectedPosada contiene la sede
                        // uiState.personDetails contiene la data de las personas
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0,156, 166), // Color de fondo personalizado (un verde azulado oscuro)
                        contentColor = Color.White)

                ) {
                    Text("Confirmar reserva")
                }

                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

data class GuestScreenState(
    val selectedPosada: Posadas? = null,
    val isHeadquarterExpanded: Boolean = false,
    val personCount: Int = 1,
    val entryDate: String = "DD/MM/AAAA",
    val personDetails: List<PersonInfo> = listOf(PersonInfo()),
    val menCount: Int = 0,
    val womenCount: Int = 0
)

@Composable
fun DropdownField(
    label: String,
    selectValue: String,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    onDismissRequest: () -> Unit,
    dropdownContent: @Composable (ColumnScope.() -> Unit)
) {
    Column {
        OutlinedTextField(
            value = selectValue,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Abrir selector",
                    modifier = Modifier.clickable { onExpandedChange(true) }
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onExpandedChange(true) }
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = onDismissRequest,
            modifier = Modifier.fillMaxWidth(0.9f)
        ) {
            dropdownContent()
        }
    }
}

@Composable
fun PersonInfoSection(
    personNumber: Int,
    personInfo: PersonInfo,
    onPersonInfoChange: (PersonInfo) -> Unit
) {
    var genderExpanded by remember { mutableStateOf(false) }
    val genders = listOf("Masculino", "Femenino", "Otro")

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        if (personNumber > 1) {
            Text(
                "Información de la Persona $personNumber",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
        OutlinedTextField(
            value = personInfo.fullName,
            onValueChange = { onPersonInfoChange(personInfo.copy(fullName = it)) },
            label = { Text("Nombre y Apellidos") },
            placeholder = { Text("Nombres y apellidos") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = personInfo.phone,
            onValueChange = { onPersonInfoChange(personInfo.copy(phone = it)) },
            label = { Text("Teléfono") },
            placeholder = { Text("+52") },
            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Phone),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun Counter(
    label: String,
    count: Int,
    onCountChange: (Int) -> Unit,
    modifier: Modifier = Modifier
){
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween

    ){
        Text(
            label,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)

        ){
            //Boton -
            Button(
                onClick = {onCountChange((count-1).coerceAtLeast(0))},
                shape = MaterialTheme.shapes.small,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0,59, 92), // Color de fondo personalizado (un verde azulado oscuro)
                    contentColor = MaterialTheme.colorScheme.onPrimary

                )
            ){
                Text("-",style = MaterialTheme.typography.titleLarge)
            }
            Text(
                text = count.toString(),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            //Boton +
            Button(
                onClick = {onCountChange(count+1)},
                shape = MaterialTheme.shapes.small,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(255,127, 50), // Color de fondo personalizado (un verde azulado oscuro)
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ){
                Text("+",style = MaterialTheme.typography.titleLarge)
            }


        }
    }

}


data class PersonInfo(
    val fullName: String = "",
    val phone: String = ""
)


@Preview(
    showBackground = true, // Muestra un fondo blanco para el componente
    device = "id:pixel_6"  // Simula el tamaño de un dispositivo específico (opcional pero útil)

)
@Composable
private fun GuestScreenPreview() {
    GuestScreen()
}