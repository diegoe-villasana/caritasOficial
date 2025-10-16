package com.example.template2025.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

@Composable
fun AdminReservationDetailScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
    ) {
        // 🔙 Back Button (Top)
        IconButton(onClick = { navController.popBackStack() }) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back"
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 🔳 Reservation Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, Color(0xFF00A0A0), RoundedCornerShape(12.dp))
                .padding(8.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFF8F8F8)
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Reserva de John Doe",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                InfoItem("Teléfono", "+52 871 969 8546")
                InfoItem("Nombre completo", "John Doe Donaldson")
                InfoItem("Personas", "1")
                InfoItem("Número de habitación", "305")
                InfoItem("Sexo", "Hombre")
                InfoItem("Fecha de reserva", "20/Nov/2025")
                InfoItem("Identificador", "JD-jk2638")

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = { /* TODO: message logic */ },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF007F89)
                        )
                    ) {
                        Text("Enviar Mensaje")
                    }
                    Button(
                        onClick = { /* TODO: cancel logic */ },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Red
                        )
                    ) {
                        Text("Cancelar Reserva")
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AdminReservationDetailScreenPreview() {
    val navController = rememberNavController()
    AdminReservationDetailScreen(navController)
}

@Composable
fun InfoItem(label: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 2.dp)) {
        Text(text = label, color = Color(0xFF007F89), fontWeight = FontWeight.SemiBold)
        Text(text = value)
    }
}

