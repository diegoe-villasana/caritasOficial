package com.example.template2025.screens

import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.template2025.model.ApiClient
import com.example.template2025.model.VoluntarioRegistroRequest
import kotlinx.coroutines.launch

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
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "Registro de Voluntario", style = MaterialTheme.typography.headlineSmall)
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
            onClick = {
                // MOCK: phone y posada_id
                val phoneMock = "4421943806"
                val posadaIdMock = 1
                scope.launch {
                    try {
                        val req = VoluntarioRegistroRequest(phone = phoneMock, posada_id = posadaIdMock)
                        val resp = ApiClient.publicApi.registrarVoluntario(req)
                        if (resp.isSuccessful) {
                            Toast.makeText(context, "Registro enviado correctamente", Toast.LENGTH_SHORT).show()
                        } else {
                            val err = resp.errorBody()?.string() ?: "HTTP ${resp.code()}"
                            Toast.makeText(context, "Error al enviar: $err", Toast.LENGTH_LONG).show()
                        }
                    } catch (e: Exception) {
                        Toast.makeText(context, "Excepción: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Registrar como voluntario")
        }
    }
}