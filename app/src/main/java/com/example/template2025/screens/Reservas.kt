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
        modifier = Modifier.width(150.dp)
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
            .height(55.dp),
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
    var sede by remember { mutableStateOf("") }
    var personas by remember { mutableStateOf("") }
    var sexo by remember {mutableStateOf("")}
    var servicio by remember {mutableStateOf("")}

    var expandedservicio by remember { mutableStateOf(false) }
    var expandedsede by remember { mutableStateOf(false) }
    var expandedSexo by remember { mutableStateOf(false) }

    val opcionServicio = listOf("Casa", "Ducha")
    val opcionSede = listOf("Ducha", "Psicologo")
    val opcionsexo = listOf("Hombre", "Mujer")
    Column(
        modifier = Modifier
        .fillMaxSize()
        .verticalScroll(scrollState)
            .background(UltraWhite),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top


            ){
        Image(painter = painterResource(id = R.drawable.logosof),
            contentDescription = "Logo Sof",
            modifier = Modifier
                .size(300.dp)
                .padding(bottom = 20.dp),


        )

        Spacer(Modifier.height(16.dp))
        Text("Servicios",
            color = Color.Black,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontSize = 20.sp
            ))
        Spacer(Modifier.height(16.dp))

        ExposedDropdownMenuBox(expanded = expandedsede, onExpandedChange ={expandedsede = !expandedsede}, modifier = Modifier.fillMaxWidth(0.9f)) {
            OutlinedTextField(
                value = sede,
                onValueChange = {},
                readOnly = true,
                label = {Text("Sede")},
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedsede)
                },
                modifier = Modifier
                    .menuAnchor()

                    .height(50.dp)
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
                expanded = expandedsede,
                onDismissRequest = {expandedsede = false},
                modifier = Modifier.exposedDropdownSize()
            ) { opcionSede.forEach{opcionSede -> DropdownMenuItem(
                text = {Text(opcionSede)},
                onClick = {
                    sede = opcionSede
                    expandedsede = false

                }
            )
            }
            }

        }
        Spacer(modifier = Modifier.height(16.dp))

    Row (modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp),
        ){

        ExposedDropdownMenuBox(expanded  = expandedservicio, onExpandedChange ={expandedservicio = !expandedservicio}, modifier = Modifier.weight(1f)) {
            OutlinedTextField(
                value = servicio,
                onValueChange = {},
                readOnly = true,
                label = {Text("Servicios")},
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedservicio)
                },
                modifier = Modifier
                    .menuAnchor()

                    .width(150.dp)
                    .height(50.dp),
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

        Personas(personas = personas, onPersonasChange = { personas = it },modifier = Modifier.weight(1f))
    }

    Spacer(Modifier.height(16.dp))
    Row (modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp),
    horizontalArrangement = Arrangement.spacedBy(5.dp)){
        DatePickerBox()
        Spacer(modifier = Modifier.width(70.dp))

        DatePickerBox()}
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


