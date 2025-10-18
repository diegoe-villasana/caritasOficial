package com.example.template2025.screens

import com.example.template2025.R
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.compose.foundation.background
import androidx.compose.runtime.*
import androidx.compose.ui.res.painterResource
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text

import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.unit.sp

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange

import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.TextFieldDefaults

import androidx.compose.ui.platform.LocalContext
import java.util.*

import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerBox() {
    val context = LocalContext.current
    var fecha by remember { mutableStateOf("") }

    val calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)

    val datePickerDialog = DatePickerDialog(
        context,
        { _: DatePicker, y: Int, m: Int, d: Int ->
            fecha = "$d/${m + 1}/$y"
        }, year, month, day
    )

    OutlinedTextField(
        value = fecha,
        onValueChange = {},
        readOnly = true,
        label = { Text("Fecha") },
        trailingIcon = {
            IconButton(onClick = { datePickerDialog.show() }) {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = "Seleccionar fecha"
                )
            }
        },
        modifier = Modifier.fillMaxWidth()
            .padding(horizontal = 60.dp)
    )
}

@Composable
fun Nombre(){
    var nombre by remember{ mutableStateOf("") }
    val PrimaryBlue = Color(0xFF0097A7)
    val PrimaryBlueDark = Color(0xFF00796B)
    val TextColor = Color(0xFF212121)
    OutlinedTextField(
        value = nombre,
        onValueChange={nombre = it},
        label={Text("Nombre")},
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
        unfocusedContainerColor = Color.Transparent,
        disabledContainerColor = Color.Transparent,
        focusedIndicatorColor = PrimaryBlue,
        unfocusedIndicatorColor = PrimaryBlueDark,
        cursorColor = PrimaryBlue,
        focusedLabelColor = PrimaryBlue,
        unfocusedLabelColor = TextColor
    ))
}

@Composable
fun Telefono(){
    var telefono by remember{ mutableStateOf("") }
    val PrimaryBlue = Color(0xFF0097A7)
    val PrimaryBlueDark = Color(0xFF00796B)
    val TextColor = Color(0xFF212121)
    OutlinedTextField(
        value = telefono,
        onValueChange={telefono = it},
        label={Text("Telefono")},
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            disabledContainerColor = Color.Transparent,
            focusedIndicatorColor = PrimaryBlue,
            unfocusedIndicatorColor = PrimaryBlueDark,
            cursorColor = PrimaryBlue,
            focusedLabelColor = PrimaryBlue,
            unfocusedLabelColor = TextColor
        ))
}
@Composable
fun Personas(personas: String, onPersonasChange: (String) -> Unit,modifier: Modifier = Modifier) {
    val PrimaryBlue = Color(0xFF0097A7)
    val PrimaryBlueDark = Color(0xFF00796B)
    val TextColor = Color(0xFF212121)
    OutlinedTextField(
        value = personas,
        onValueChange = onPersonasChange,
        label = { Text("Personas") },
        singleLine = true,
        modifier = Modifier
            .width(130.dp)
            .height(65.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            disabledContainerColor = Color.Transparent,
            focusedIndicatorColor = PrimaryBlue,
            unfocusedIndicatorColor = PrimaryBlueDark,
            cursorColor = PrimaryBlue,
            focusedLabelColor = PrimaryBlue,
            unfocusedLabelColor = TextColor
        )
    )
}







@Preview(showBackground = true, widthDp = 400, heightDp = 640)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Preview(){
    reservas()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun reservas(){
    val PrimaryBlue = Color(0xFF0097A7)
    val PrimaryBlueDark = Color(0xFF00796B)
    val LightGrayBackground = Color(0xFFF5F5F5)
    val TextColor = Color(0xFF212121)
    val UltraWhite = Color(0xFFFFFFFF)


    val scrollState = rememberScrollState()
    var personas by remember { mutableStateOf("") }
    var sexo by remember {mutableStateOf("")}
    var servicio by remember {mutableStateOf("")}
    var Hora by remember{mutableStateOf("")}
    var Ubicacion by remember {mutableStateOf("")}

    var expandedservicio by remember { mutableStateOf(false) }
    var expandedSexo by remember { mutableStateOf(false) }
    var expandedHora by remember { mutableStateOf(false) }
    var expandedUbicacion by remember {mutableStateOf(false)}


    val opcionServicio = listOf("Casa", "Ducha", "Transporte")
    val opcionUbicacion = listOf("Izquierda","Derecha")
    val opcionsexo = listOf("Hombre", "Mujer")
    val opcionHora = listOf(
        "6:00", "6:30",
        "7:00", "7:30",
        "8:00", "8:30",
        "9:00", "9:30",
        "10:00", "10:30",
        "11:00", "11:30",
        "12:00", "12:30",
        "13:00", "13:30",
        "14:00", "14:30",
        "15:00", "15:30",
        "16:00", "16:30",
        "17:00", "17:30",
        "18:00", "18:30",
        "19:00", "19:30",
        "20:00"
    )

    Column(
        modifier = Modifier
        .fillMaxSize()
        .verticalScroll(scrollState)
            .background(UltraWhite),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top


            ){
        Image(painter = painterResource(id = R.drawable.caritas_logo),
            contentDescription = "Logo Sof",
            modifier = Modifier
                .size(300.dp)
                .padding(bottom = 20.dp),


        )

        Spacer(Modifier.height(1.dp))
        Text("Servicios",
            color = Color.Black,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontSize = 20.sp
            ))
        Text("Al confirmar se generara un QR para presentar al Transportista",
            color = Color.Black,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontSize = 12.sp
            ))
        Spacer(Modifier.height(16.dp))

        ExposedDropdownMenuBox(expanded = expandedservicio, onExpandedChange ={expandedservicio = !expandedservicio}, modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = servicio,
                onValueChange = {},
                readOnly = true,
                label = {Text("Servicio")},
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedservicio)
                },
                modifier = Modifier
                    .menuAnchor()

                    .height(70.dp)
                    .padding(horizontal = 60.dp)
                    ,
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = LightGrayBackground,
                unfocusedContainerColor = Color.Transparent,
                disabledContainerColor = Color.Transparent,
                focusedIndicatorColor = PrimaryBlue,
                unfocusedIndicatorColor = PrimaryBlueDark,
                            cursorColor = PrimaryBlue,
                            focusedLabelColor = PrimaryBlue,
                            unfocusedLabelColor = TextColor

            )

            )
            ExposedDropdownMenu(
                expanded = expandedservicio,
                onDismissRequest = {expandedservicio = false},
                modifier = Modifier.exposedDropdownSize()
            ) { opcionServicio.forEach{opcionServicio -> DropdownMenuItem(
                text = {Text(opcionServicio)},
                onClick = {
                    servicio = opcionServicio
                    expandedservicio = false

                }
            )
            }
            }

        }
        Spacer(modifier = Modifier.height(16.dp))
        if (servicio == "Transporte"){

            ExposedDropdownMenuBox(expanded = expandedUbicacion, onExpandedChange ={expandedUbicacion = !expandedUbicacion}, modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = Ubicacion,
                    onValueChange = {},
                    readOnly = true,
                    label = {Text("Ubicacion")},
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedUbicacion)
                    },
                    modifier = Modifier
                        .menuAnchor()

                        .height(70.dp)
                        .padding(horizontal = 60.dp)
                    ,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = LightGrayBackground,
                        unfocusedContainerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,
                        focusedIndicatorColor = PrimaryBlue,
                        unfocusedIndicatorColor = PrimaryBlueDark,
                        cursorColor = PrimaryBlue,
                        focusedLabelColor = PrimaryBlue,
                        unfocusedLabelColor = TextColor

                    )

                )
                ExposedDropdownMenu(
                    expanded = expandedUbicacion,
                    onDismissRequest = {expandedUbicacion = false},
                    modifier = Modifier.exposedDropdownSize()
                ) { opcionUbicacion.forEach{opcionUbicacion -> DropdownMenuItem(
                    text = {Text(opcionUbicacion)},
                    onClick = {
                        Ubicacion = opcionUbicacion
                        expandedUbicacion = false

                    }
                )
                }
                }

            }
        }



        ExposedDropdownMenuBox(expanded  = expandedHora, onExpandedChange ={expandedHora = !expandedHora}, modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = Hora,
                onValueChange = {},
                readOnly = true,
                label = { Text("Hora") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedHora)
                },
                modifier = Modifier
                    .menuAnchor()
                    .height(70.dp)
                    .padding(horizontal = 60.dp)

                    ,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = LightGrayBackground,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    focusedIndicatorColor = PrimaryBlue,
                    unfocusedIndicatorColor = PrimaryBlueDark,
                    cursorColor = PrimaryBlue,
                    focusedLabelColor = PrimaryBlue,
                    unfocusedLabelColor = TextColor

                )
            )
            ExposedDropdownMenu(
                expanded = expandedHora,
                onDismissRequest = { expandedHora = false },
                modifier = Modifier.exposedDropdownSize()
            ) {
                opcionHora.forEach { opcionHora ->
                    DropdownMenuItem(
                        text = { Text(opcionHora) },
                        onClick = {
                            Hora = opcionHora
                            expandedHora = false

                        }
                    )
                }
            }



        }




    Spacer(Modifier.height(16.dp))

        DatePickerBox()
        Spacer(Modifier.height(16.dp))
        Button(
            onClick = {},
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF0097A7)
            )
        ) {
            Text("Confirmar")
        }

        Spacer(Modifier.height(16.dp))
        val numPersonas = personas.toIntOrNull() ?: 0
        Column(
            modifier = Modifier.fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)


        ) {
        repeat(numPersonas) {index ->
            Text(text = "Persona ${index + 1}", style = MaterialTheme.typography.titleMedium)
            Nombre()
            Telefono()
            Spacer(Modifier.height(30.dp))
            ExposedDropdownMenuBox(expanded = expandedSexo, onExpandedChange ={expandedSexo = !expandedSexo}, modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = sexo,
                    onValueChange = {},
                    readOnly = true,
                    label = {Text("sexo")},
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedSexo)
                    },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                        .height(30.dp)
                        .size(40.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = LightGrayBackground,
                        unfocusedContainerColor = Color.Transparent,
                        disabledContainerColor = LightGrayBackground,
                        focusedIndicatorColor = PrimaryBlue,
                        unfocusedIndicatorColor = PrimaryBlueDark,
                        cursorColor = PrimaryBlue,
                        focusedLabelColor = PrimaryBlue,
                        unfocusedLabelColor = TextColor

                    )

                    )
                ExposedDropdownMenu(
                    expanded = expandedSexo,
                    onDismissRequest = {expandedSexo = false},
                    modifier = Modifier.exposedDropdownSize()
                ) { opcionsexo.forEach{opcionSexo -> DropdownMenuItem(
                    text = {Text(opcionSexo)},
                    onClick = {
                        sexo = opcionSexo
                        expandedSexo = false

                    }
                )
                }
                }

            }

        }}
}
}


