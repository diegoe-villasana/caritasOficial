package com.example.template2025.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.template2025.R
import com.example.template2025.model.ApiClient
import com.example.template2025.model.VoluntarioRegistroRequest
import kotlinx.coroutines.launch

@Composable
fun RegistroVoluntarioView(
    onRegistered: () -> Unit = {},
    onBack: () -> Unit = {}
) {
    val alberguesCaritas = listOf(
        "Posada del Peregrino",
        "Posada del Peregrino \"Divina Providencia\"",
        "Posada del Peregrino Apodaca",
    )
    var nombre by remember { mutableStateOf("") }
    var numTel by remember { mutableStateOf("") }
    var selectedAlbergue by remember { mutableStateOf(alberguesCaritas.first()) }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxSize()) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .padding(top = 80.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Registro de Voluntario", style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre completo") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = numTel,
                onValueChange = {
                    if (it.all { char -> char.isDigit() } && it.length <= 10) {
                        numTel = it
                    }
                },
                label = { Text("Número de Teléfono") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            Column(modifier = Modifier.fillMaxWidth()) {
                Text(text = "Selecciona un albergue:")
                alberguesCaritas.forEach { albergue ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = (albergue == selectedAlbergue),
                                onClick = { selectedAlbergue = albergue }
                            )
                            .padding(horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (albergue == selectedAlbergue),
                            onClick = { selectedAlbergue = albergue }
                        )
                        Text(text = albergue, modifier = Modifier.padding(start = 8.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    val posadaId = when (selectedAlbergue) {
                        "Posada del Peregrino" -> 1
                        "Posada del Peregrino \"Divina Providencia\"" -> 2
                        "Posada del Peregrino Apodaca" -> 3
                        else -> 1
                    }

                    scope.launch {
                        try {
                            val req = VoluntarioRegistroRequest(
                                nombre = nombre,
                                phone = numTel,
                                posadaId = posadaId
                            )
                            val resp = ApiClient.publicApi.registrarVoluntario(req)

                            if (resp.isSuccessful) {
                                Toast.makeText(context, "Registro enviado correctamente", Toast.LENGTH_SHORT).show()
                                onRegistered()
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

        IconButton(
            onClick = { onBack() },
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