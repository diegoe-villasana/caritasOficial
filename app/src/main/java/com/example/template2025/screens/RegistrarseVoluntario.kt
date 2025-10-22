package com.example.template2025.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

val alberguesCaritas = listOf(
    "Albergue San Juan Bosco",
    "Albergue Madre Teresa de Calcuta",
    "Albergue Cáritas Santa Catarina",
    "Albergue Cáritas San Nicolás",
    "Albergue La Sagrada Familia"
)

@Composable
fun RegistroVoluntarioView() {
    val alberguesCaritas = listOf(
        "Albergue San Juan Bosco",
        "Albergue Madre Teresa de Calcuta",
        "Albergue Cáritas Santa Catarina",
        "Albergue Cáritas San Nicolás",
        "Albergue La Sagrada Familia"
    )
    var nombre by remember { mutableStateOf("") }
    var selectedAlbergue by remember { mutableStateOf(alberguesCaritas.first()) }

    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "Registro de Voluntario", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Nombre completo") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Selecciona un albergue:")
        alberguesCaritas.forEach { albergue ->
            Row(
                Modifier
                    .fillMaxWidth()
                    .selectable(
                        selected = (albergue == selectedAlbergue),
                        onClick = { selectedAlbergue = albergue }
                    )
                    .padding(8.dp)
            ) {
                RadioButton(
                    selected = (albergue == selectedAlbergue),
                    onClick = { selectedAlbergue = albergue }
                )
                Text(text = albergue, modifier = Modifier.padding(start = 8.dp))
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { /* Acción de registro aquí */ },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Registrar como voluntario")
        }
    }
}
