package com.example.template2025.screens

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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.template2025.R
import com.example.template2025.modelInn.Posadas
import com.example.template2025.modelInn.getPosadas
import com.example.template2025.components.PosadasCard
import com.example.template2025.navigation.Route
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import com.example.template2025.viewModel.GuestViewModel
import com.example.template2025.viewModel.ReservationUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GuestScreen(
    navController: NavController,
    viewModel: GuestViewModel = viewModel()
) {
    val uiState = viewModel.formState
    val onStateChange : (GuestScreenState) -> Unit = {viewModel.onFormStateChange(it)}
    val reservationState = viewModel.reservationState
    val posadasList = remember { getPosadas() }
    var showDatePicker by remember { mutableStateOf(false) }

    when(val state = reservationState){
        is ReservationUiState.Loading ->{
            //Mostrar indicador de carga
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center){
                CircularProgressIndicator()
            }
        }
        is ReservationUiState.Success -> {
            //Mostrar dialogo de éxito
            AlertDialog(
                onDismissRequest = {/*No hacer nada, forzar al usuario a hacer algo*/},
                title = {Text("Reserva confirmada")},
                text = {Text("Tu reservación con ID ${state.reservationId} ha sido creada. Se generará tu código QR")},
                confirmButton = {
                    Button(onClick = {
                        // TODO: Navegar a la pantalla del qr
                        navController.navigate("qr/${state.qrCodeUrl}")
                        viewModel.resetReservationState()
                    }) {
                        Text("Ver QR")

                    }
                }
            )
        }
        is ReservationUiState.Error -> {
            //Mostrar dialogo error
            AlertDialog(
                onDismissRequest = {/*No hacer nada, forzar al usuario a hacer algo*/},
                title = {Text("Error en la reservación")},
                text = {Text(state.message)},
                confirmButton = {
                    Button(onClick = {viewModel.resetReservationState()}){
                        Text("Aceptar")
                    }
                }

            )
        }
        is ReservationUiState.Idle -> {
            //No hacer nada en el estado incial
        }
    }


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
                    label = "Posada",
                    selectValue = uiState.selectedPosada?.name ?: "Seleccionar Posada",
                    expanded = uiState.isHeadquarterExpanded,
                    onExpandedChange = { expanded -> onStateChange(uiState.copy(isHeadquarterExpanded = expanded)) },
                    onDismissRequest = { onStateChange(uiState.copy(isHeadquarterExpanded = false)) }
                ) {
                    posadasList.forEach { posada ->
                        DropdownMenuItem(
                            text = { Text(posada.name) },
                            onClick = {
                                onStateChange(uiState.copy(
                                    selectedPosada = posada,
                                    isHeadquarterExpanded = false
                                ))
                            }
                        )
                    }
                }

                uiState.selectedPosada?.let { posadaSeleccionada ->
                    Spacer(modifier = Modifier.height(16.dp))
                    PosadasCard(posadas = posadaSeleccionada, onItemClick = {})
                }


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

                // El diálogo del calendario
                if (showDatePicker) {
                    val datePickerState = rememberDatePickerState()
                    val confirmEnabled = remember(datePickerState.selectedDateMillis) {datePickerState.selectedDateMillis != null
                    }

                    // Creamos un texto dinámico para el headline
                    val headlineText = remember(datePickerState.selectedDateMillis) {
                        val selectedMillis = datePickerState.selectedDateMillis
                        if (selectedMillis != null) {
                            // Si hay una fecha seleccionada, la formateamos
                            val selectedDate = Instant.ofEpochMilli(selectedMillis)
                                .atZone(ZoneId.systemDefault()) // Usamos la zona horaria del dispositivo para el formato
                                .toLocalDate()
                            // Formato amigable como "13 oct. 2025"
                            selectedDate.format(DateTimeFormatter.ofPattern("d MMM yyyy"))
                        } else {
                            // Si no, mostramos el texto por defecto
                            "Fecha de entrada"
                        }
                    }

                    // --- FIN DE LA MODIFICACIÓN ---

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
                                        onStateChange (uiState.copy(
                                            entryDate = selectedDate.format(
                                                DateTimeFormatter.ofPattern("dd/MM/yyyy")
                                            )
                                        ))
                                    }
                                },
                                enabled = confirmEnabled
                            ) {
                                Text("Aceptar") // Texto en español
                            }
                        },
                        dismissButton = {
                            Button(onClick = { showDatePicker = false }) {
                                Text("Cancelar") // Texto en español
                            }
                        }
                    ) {
                        DatePicker(
                            state = datePickerState,
                            title = {
                                Text(
                                    "Selecciona una fecha",
                                    modifier = Modifier.padding(start = 24.dp, top = 16.dp, end = 24.dp)
                                )
                            },
                            // Usamos nuestra variable dinámica aquí
                            headline = {
                                Text(
                                    headlineText,
                                    modifier = Modifier.padding(start = 24.dp, top = 12.dp, end = 24.dp)
                                )
                            },
                            showModeToggle = false
                        )
                    }
                }



                Spacer(modifier = Modifier.height(12.dp))

            }

            // Sección del solicitante
            item {
                PersonInfoSection(
                    personNumber = 1, // Siempre es la persona 1
                    personInfo = uiState.applicantInfo,
                    onPersonInfoChange = { updatedInfo ->
                        onStateChange(uiState.copy(applicantInfo = updatedInfo))
                    }
                )
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
                    onCountChange = {newCount -> onStateChange(uiState.copy(menCount = newCount))}
                )
                Spacer(modifier=Modifier.height(16.dp))

                //Contador Mujeres
                Counter(
                    "Mujeres",
                    count = uiState.womenCount,
                    onCountChange = {newCount -> onStateChange(uiState.copy(womenCount = newCount))}
                )

                Spacer(modifier = Modifier.height(32.dp))


            }


            item {

                Button(
                    onClick = {
                        viewModel.confirmReservation()
                        navController.navigate(Route.Auth.route)

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
    // No necesitamos más la lista de géneros aquí, usamos los RadioButton directamente.

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        // Título de la sección
        Text(
            text = "Información del Solicitante",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        // Campo de Nombre
        OutlinedTextField(
            value = personInfo.fullName,
            onValueChange = { onPersonInfoChange(personInfo.copy(fullName = it)) },
            label = { Text("Nombre y Apellidos") },
            modifier = Modifier.fillMaxWidth()
        )

        // Campo de Teléfono
        PhoneField(
            phone = personInfo.phone,
            onPhoneChange = { newPhone -> onPersonInfoChange(personInfo.copy(phone = newPhone)) },
            selectedCountry = personInfo.country,
            onCountryChange = { newCountry -> onPersonInfoChange(personInfo.copy(country = newCountry)) }
        )

        // --- INICIO DE LA SECCIÓN DE RADIO BUTTONS ---

        Text(
            text = "Género",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Opción: Hombre
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = (personInfo.gender == "Hombre"),
                    onClick = { onPersonInfoChange(personInfo.copy(gender = "Hombre")) },
                    colors = RadioButtonDefaults.colors(
                        selectedColor = Color(0,156, 166), // Color de fondo personalizado (un verde azulado oscuro)
                        unselectedColor = Color.Black
                    )
                )
                Text("Hombre", modifier = Modifier.clickable { onPersonInfoChange(personInfo.copy(gender = "Hombre")) })
            }

            // Opción: Mujer
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = (personInfo.gender == "Mujer"),
                    onClick = { onPersonInfoChange(personInfo.copy(gender = "Mujer")) },
                    colors = RadioButtonDefaults.colors(
                        selectedColor = Color(0,156, 166), // Color de fondo personalizado (un verde azulado oscuro)
                        unselectedColor = Color.Black
                    )
                )
                Text("Mujer", modifier = Modifier.clickable { onPersonInfoChange(personInfo.copy(gender = "Mujer")) })
            }
        }
        // --- FIN DE LA SECCIÓN DE RADIO BUTTONS ---
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

@Composable
fun PhoneField(
    phone: String,
    onPhoneChange: (String) -> Unit,
    selectedCountry: Country,
    onCountryChange: (Country) -> Unit,
    label: String = "Teléfono"
) {
    val countries = remember { getCountries() }
    var countryMenuExpanded by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = phone,
        onValueChange = onPhoneChange,
        label = { Text(label) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
        modifier = Modifier.fillMaxWidth(),
        leadingIcon = {
            // Este es el selector del código de país
            Row(
                modifier = Modifier
                    .clickable { countryMenuExpanded = true }
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(selectedCountry.flag) // Bandera
                Spacer(modifier = Modifier.padding(horizontal = 4.dp))
                Text(selectedCountry.dialCode) // Código (+52)
                Icon(Icons.Default.ArrowDropDown, contentDescription = "Seleccionar país")

                // Menú desplegable con la lista de países
                DropdownMenu(
                    expanded = countryMenuExpanded,
                    onDismissRequest = { countryMenuExpanded = false }
                ) {
                    countries.forEach { country ->
                        DropdownMenuItem(
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(country.flag)
                                    Spacer(modifier = Modifier.padding(horizontal = 8.dp))
                                    Text(country.name)
                                    Spacer(modifier = Modifier.padding(horizontal = 8.dp))
                                    Text(country.dialCode, style = MaterialTheme.typography.bodySmall)
                                }
                            },
                            onClick = {
                                onCountryChange(country)
                                countryMenuExpanded = false
                            }
                        )
                    }
                }
            }
        }
    )
}


data class GuestScreenState(
    val selectedPosada: Posadas? = null,
    val isHeadquarterExpanded: Boolean = false,
    val entryDate: String = "DD/MM/AAAA",
    val applicantInfo: PersonInfo = PersonInfo(gender = "Hombre"), // Dato del solicitante, con un valor por defecto
    val menCount: Int = 0,
    val womenCount: Int = 0
)

data class PersonInfo(
    val fullName: String = "",
    val phone: String = "",
    val gender: String = "",
    val country: Country = Country("México", "+52", "🇲🇽","MX")
)

data class Country(
    val name: String,
    val dialCode: String,
    val flag: String, // Usaremos emojis para las banderas, es lo más fácil
    val isoCode: String
)

fun getCountries(): List<Country> {
    return listOf(
        Country("México", "+52", "🇲🇽","MX"),
        Country("Estados Unidos", "+1", "🇺🇸","US"),
        Country("España", "+34", "🇪🇸","ES"),
        Country("Colombia", "+57", "🇨🇴","CO"),
        Country("Argentina", "+54", "🇦🇷","AR"),
        Country("Perú", "+51", "🇵🇪","PE"),
        // Puedes añadir más países aquí
    )
}



@Preview(
    showBackground = true, // Muestra un fondo blanco para el componente
    device = "id:pixel_6"  // Simula el tamaño de un dispositivo específico (opcional pero útil)

)
@Composable
private fun GuestScreenPreview() {
    //GuestScreen(viewModel = GuestViewModel())
}