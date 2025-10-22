package com.example.template2025.screens

import android.util.Log
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.template2025.R
import com.example.template2025.components.PosadasCard
import com.example.template2025.model.Posada
import com.example.template2025.navigation.Route
import com.example.template2025.viewModel.AppViewModel
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import com.example.template2025.viewModel.GuestViewModel
import com.example.template2025.viewModel.ReservationUiState
import java.time.temporal.ChronoUnit
import com.example.template2025.avisoprivacidad.AVISO_PRIVACIDAD



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GuestScreen(
    navController: NavController,
    vm: AppViewModel,
    gvm: GuestViewModel = viewModel()
) {
    val uiState = gvm.formState
    val onStateChange : (GuestScreenState) -> Unit = {gvm.onFormStateChange(it)}
    val reservationState = gvm.reservationState
    val posadaState by vm.posadaState.collectAsState()
    var showDatePicker by remember { mutableStateOf(false) }
    var selectedPosadaName by remember { mutableStateOf("") }


    LaunchedEffect(Unit) {
        vm.getPosadas()
    }

    LaunchedEffect(posadaState.posadas) {
        if (posadaState.posadas.isNotEmpty() && selectedPosadaName.isEmpty()) {
            selectedPosadaName = "Todos los albergues"
        }
    }

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
                            state.qr_token?.let { url ->
                                val uiState = gvm.formState // Get the form state
                                val totalPersonas = uiState.menCount + uiState.womenCount
                                val telefono = "${uiState.applicantInfo.country.dialCode} ${uiState.applicantInfo.phone}"

                                // Use the updated helper to build the route with all data
                                val route = Route.QrCode.createRoute(
                                    qrCodeUrl = url,
                                    posada = uiState.selectedPosada?.nombre ?: "N/A",
                                    personas = totalPersonas.toString(),
                                    fecha = uiState.entryDate,
                                    telefono = telefono
                                )
                                navController.navigate(route)
                                gvm.resetReservationState()
                            }
                        },
                        enabled = state.qrCodeUrl != null
                    ) {
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
                    Button(onClick = {gvm.resetReservationState()}){
                        Text("Aceptar")
                    }
                }

            )
        }
        is ReservationUiState.Idle -> {
            //No hacer nada en el estado incial
        }
    }


    Scaffold(modifier = Modifier.fillMaxSize(), containerColor = Color.White) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .padding(horizontal = 24.dp)
                .fillMaxSize()
                .background(Color.White),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
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
                    selectValue = uiState.selectedPosada?.nombre ?: "Seleccionar Posada",
                    expanded = uiState.isHeadquarterExpanded,
                    isError = uiState.selectedPosadaError != null,
                    supportingText = {
                        if (uiState.selectedPosadaError != null) {
                            Text(
                                uiState.selectedPosadaError,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    },
                    onExpandedChange = { expanded ->
                        onStateChange(
                            uiState.copy(
                                isHeadquarterExpanded = expanded
                            )
                        )
                    },
                    onDismissRequest = { onStateChange(uiState.copy(isHeadquarterExpanded = false)) },
                ) {
                    posadaState.posadas.map { posadasList ->
                        DropdownMenuItem(
                            text = { Text(posadasList.nombre) },
                            onClick = {
                                onStateChange(
                                    uiState.copy(
                                        selectedPosada = posadasList,
                                        isHeadquarterExpanded = false
                                    )
                                )
                            }
                        )

                    }

                }
                uiState.selectedPosada?.let { posadaSeleccionada ->
                    Spacer(modifier = Modifier.height(16.dp))
                    PosadasCard(posadas = posadaSeleccionada, onItemClick = {})
                }
            }
            item {

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
                    val datePickerState = rememberDatePickerState(
                        selectableDates = object : SelectableDates {
                            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                                return utcTimeMillis >= Instant.now().truncatedTo(ChronoUnit.DAYS).toEpochMilli()
                            }
                        }
                    )
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
                    },
                    onGenderChange = {oldGender, newGender ->
                        var men = uiState.menCount
                        var women = uiState.womenCount

                        if(oldGender == "Hombre") men = (men-1).coerceAtLeast(0)
                        if(oldGender == "Mujer") women = (women-1).coerceAtLeast(0)
                        if(newGender == "Hombre") men++
                        if(newGender == "Mujer") women++
                        onStateChange(uiState.copy(
                            applicantInfo = uiState.applicantInfo.copy(gender = newGender),
                            menCount = men,
                            womenCount = women
                        ))
                    },
                    fullNameError = uiState.fullNameError,
                    phoneError = uiState.phoneError,
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

                //calcular el total de guests si se ha alcanzado el limite
                val totalGuests = uiState.menCount + uiState.womenCount
                val capacity = vm.posadaState.value.posadas.firstOrNull()?.capacidadDisponible?: 0
                val isCapacityReached = if(capacity > 0) totalGuests >= capacity else false


                //Contador hombres
                Counter(
                    "Hombres",
                    count = uiState.menCount,
                    //flag para deshabilitar el boton de suma si se ha alcanzado la capacidad
                    isIncrementEnabled = !isCapacityReached,
                    minCount = if(uiState.applicantInfo.gender =="Hombre") 1 else 0,
                    onCountChange = {newCount ->
                        if(!isCapacityReached || newCount < uiState.menCount) {
                            onStateChange(uiState.copy(menCount = newCount))
                        }
                    }
                )
                Spacer(modifier=Modifier.height(16.dp))

                //Contador Mujeres
                Counter(
                    "Mujeres",
                    count = uiState.womenCount,
                    isIncrementEnabled = !isCapacityReached,
                    minCount = if(uiState.applicantInfo.gender =="Mujer") 1 else 0,
                    onCountChange = {newCount ->
                        if(!isCapacityReached || newCount < uiState.womenCount) {
                            onStateChange(uiState.copy(womenCount = newCount))
                        }
                    }
                )
                if(capacity > 0){
                    Text(
                        text="Capacidad disponible: $capacity",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top=8.dp),
                        textAlign = TextAlign.Center,
                        color = if(isCapacityReached) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface.copy(alpha=0.6f)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
            item{
                PrivacyPolicyRow(
                    checked = uiState.hasAcceptedPrivacyPolicy,
                    onCheckedChange = {isChecked ->
                        onStateChange(uiState.copy(hasAcceptedPrivacyPolicy = isChecked))
                    }
                )

            }

            item {
                Button(
                    onClick = {
                        gvm.confirmReservation()

                    },
                    enabled = uiState.hasAcceptedPrivacyPolicy,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0,156, 166), // Color de fondo personalizado (un verde azulado oscuro)
                        contentColor = Color.White)

                ) {
                    if(reservationState is ReservationUiState.Loading){
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    }else{
                        Text("Confirmar reserva")

                    }

                }

                Spacer(modifier = Modifier.height(40.dp))
            }
        }
        Box(modifier = Modifier
            .background(Color.White),
            contentAlignment = Alignment.Center
        ){
            IconButton(
                onClick = { navController.navigateUp() },
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



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownField(
    label: String,
    selectValue: String,
    expanded: Boolean,
    isError: Boolean,
    supportingText: @Composable (() -> Unit)?,
    onExpandedChange: (Boolean) -> Unit,
    onDismissRequest: () -> Unit,
    dropdownContent: @Composable (ColumnScope.() -> Unit)
) {
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = onExpandedChange
    ) {
        OutlinedTextField(
            value = selectValue,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            isError = isError,
            supportingText = supportingText,
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = {onExpandedChange(false)}
        ){
            dropdownContent()

        }

    }
}

@Composable
fun PersonInfoSection(
    personNumber: Int,
    personInfo: PersonInfo,
    onPersonInfoChange: (PersonInfo) -> Unit,
    fullNameError: String?,
    phoneError: String?,
    onGenderChange: (String, String) -> Unit, // (oldGender, newGender)

) {

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
            onValueChange = {
                    newValue ->
                // Filtra para que solo acepte letras y espacios.
                val filteredValue = newValue.filter { it.isLetter() || it.isWhitespace() }

                // Limita la longitud.
                val limitedValue = filteredValue.take(70)

                // Actualiza el estado con el valor limpio.
                onPersonInfoChange(personInfo.copy(fullName = limitedValue))
            },
            label = { Text("Nombre y Apellidos") },
            isError = fullNameError != null,
            // Puedes darle un color al texto de error
            supportingText = { if (fullNameError != null) Text(fullNameError, color = MaterialTheme.colorScheme.error) },
            modifier = Modifier.fillMaxWidth(),
        )

        // Campo de Teléfono
        PhoneField(
            phone = personInfo.phone,
            onPhoneChange = { newPhone -> onPersonInfoChange(personInfo.copy(phone = newPhone)) },
            selectedCountry = personInfo.country,
            onCountryChange = { newCountry -> onPersonInfoChange(personInfo.copy(country = newCountry)) }
        )
        if (phoneError != null) {
            Text(
                phoneError,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp)
            )
        }

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
                    onClick = {
                        val oldGender = personInfo.gender
                        val newGender = "Hombre"
                        if (oldGender != newGender) {
                            onGenderChange(oldGender, newGender)
                        }
                    },
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
                    onClick = {
                        val oldGender = personInfo.gender
                        val newGender = "Mujer"
                        if (oldGender != newGender) {
                            onGenderChange(oldGender, newGender)
                        }
                    },
                    colors = RadioButtonDefaults.colors(
                        selectedColor = Color(0,156, 166), // Color de fondo personalizado (un verde azulado oscuro)
                        unselectedColor = Color.Black
                    )
                )
                Text("Mujer", modifier = Modifier.clickable { onPersonInfoChange(personInfo.copy(gender = "Mujer")) })
            }
        }
    }
}

@Composable
fun Counter(
    label: String,
    count: Int,
    onCountChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    isIncrementEnabled: Boolean = true,
    minCount: Int = 1
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
                onClick = {onCountChange((count-1).coerceAtLeast(minCount))},
                shape = MaterialTheme.shapes.small,
                enabled = count > minCount,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0,59, 92), // Color de fondo personalizado (un verde azulado oscuro)
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    disabledContainerColor = Color(223,223,223).copy(alpha=0.5f)

                )
            ){
                Text("-",style = MaterialTheme.typography.titleLarge)
            }
            Text(
                text = count.toString(),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.widthIn(min=36.dp),
                textAlign = TextAlign.Center
            )
            //Boton +
            Button(
                onClick = {onCountChange(count+1)},
                enabled = isIncrementEnabled,
                shape = MaterialTheme.shapes.small,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(255,127, 50), // Color de fondo personalizado (un verde azulado oscuro)
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    disabledContainerColor = Color(255,127,50).copy(alpha=0.5f)
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
        onValueChange = {
            newValue ->
            //Filtra para que solo contenga digitos
            val filteredValue = newValue.filter { it.isDigit() }
            //Limita la longitud a 10 chars
            val limitedValue = filteredValue.take(10)
            //Llama al evento onPhoneChange con el valor limpio
            onPhoneChange(limitedValue)
        },
        label = { Text(label) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
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

@Composable
fun PrivacyPolicyRow(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    policyText: String = AVISO_PRIVACIDAD,
    linkColor: Color = Color(0, 156, 166),
    acceptOnDialogConfirm: Boolean = false // si true, marcará el checkbox al aceptar
) {
    var showDialog by remember { mutableStateOf(false) }

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = { isChecked ->
                onCheckedChange(isChecked)
            },
            colors = CheckboxDefaults.colors(
                checkedColor = linkColor,
                uncheckedColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )

        val annotatedString = buildAnnotatedString {
            append("He leído y acepto el ")
            pushStringAnnotation(tag = "POLICY", annotation = "open")
            withStyle(
                style = SpanStyle(
                    color = linkColor,
                    fontWeight = FontWeight.Bold
                )
            ) {
                append("Aviso de privacidad")
            }
            pop()
        }

        ClickableText(
            text = annotatedString,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.onSurface
            ),
            onClick = { offset ->
                annotatedString
                    .getStringAnnotations(tag = "POLICY", start = offset, end = offset)
                    .firstOrNull()
                    ?.let {
                        Log.d("PrivacyPolicy", "El usuario quiere ver el aviso de privacidad.")
                        showDialog = true
                    }
            }
        )
    }

    Spacer(modifier = Modifier.height(16.dp))

    if (showDialog) {
        val scrollState = rememberScrollState()

        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Aviso de Privacidad") },
            text = {
                // Contenido desplazable, con altura controlada para no forzar el layout del diálogo
                Column(
                    modifier = Modifier
                        .heightIn(min = 100.dp, max = 360.dp)
                        .verticalScroll(scrollState)
                        .fillMaxWidth()
                ) {
                    Text(policyText)
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (acceptOnDialogConfirm) onCheckedChange(true)
                        showDialog = false
                    }
                ) { Text("Aceptar") }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Cerrar")
                }
            }
        )
    }
}


data class GuestScreenState(
    val selectedPosada: Posada? = null,
    val isHeadquarterExpanded: Boolean = false,
    val entryDate: String = "DD/MM/AAAA",
    val applicantInfo: PersonInfo = PersonInfo(gender = "Hombre"), // Dato del solicitante, con un valor por defecto
    val menCount: Int = 1,
    val womenCount: Int = 0,
    val hasAcceptedPrivacyPolicy: Boolean = false,
    //CAMPOS PARA LOS ERRORES
    val selectedPosadaError: String? = null,
    val entryDateError: String? = null,
    val guestCountError: String? = null,
    val fullNameError: String? = null,
    val phoneError: String? = null,
    val genderError: String? = null
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


//@Preview(showBackground = true, widthDp = 360, heightDp = 640)

