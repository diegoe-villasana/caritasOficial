package com.example.template2025.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.*
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
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
    var selectedAlbergue by remember { mutableStateOf(alberguesCaritas.first()) }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // --- THIS IS THE FIX ---
    // Wrap everything in a Box to provide a layout context for .align()
    Box(modifier = Modifier.fillMaxSize()) {

        // The Column with the form is the main content, centered.
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .padding(top = 80.dp),
            horizontalAlignment = Alignment.CenterHorizontally // Center the content within the column
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

            // This Column is for the RadioButton group, aligned to the start
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
                            .padding(horizontal = 8.dp), // Use horizontal padding
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
                    val phoneMock = "4421943806"
                    val posadaIdMock = 1
                    scope.launch {
                        try {
                            val req = VoluntarioRegistroRequest(phone = phoneMock, posada_id = posadaIdMock)
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

        // The IconButton is now a child of the Box and can be aligned correctly.
        IconButton(
            onClick = { onBack() },
            modifier = Modifier
                .align(Alignment.TopStart) // <-- This now works correctly
                .padding(start = 16.dp, top = 32.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.arrow_back),
                contentDescription = "Back"
            )
        }
    }
}