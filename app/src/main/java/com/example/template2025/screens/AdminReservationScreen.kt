package com.example.template2025.screens

import android.content.Intent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalOverscrollFactory
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.template2025.navigation.Route
import kotlin.jvm.java

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AdminReservationScreen(navController: NavController) {
    CompositionLocalProvider(LocalOverscrollFactory provides null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                getToday(),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth(0.9f)
            ) {
                OutlinedButton(
                    onClick = {},
                    modifier = Modifier
                        .fillMaxWidth(0.5f)
                        .height(60.dp),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, Color.Gray)
                ) {
                    Text("Sedes")
                    Spacer(modifier = Modifier.weight(1f))
                    Icon(Icons.Filled.KeyboardArrowDown,
                        contentDescription = null
                    )
                }
                OutlinedButton(
                    onClick = {},
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, Color.Gray)
                ) {
                    Text("Status")
                    Spacer(modifier = Modifier.weight(1f))
                    Icon(Icons.Filled.KeyboardArrowDown,
                        contentDescription = null
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .padding(4.dp)
                    .border(1.dp, Color(0xFF00A0A0), RoundedCornerShape(12.dp))
                    .heightIn(min = 200.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFFFFFFF)
                )
            ) {
                LazyColumn(
                    contentPadding = PaddingValues(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(5) { index -> // Replace 5 with your reservations.size
                        ReservationCard(
                            name = "John Doe",
                            room = "305",
                            date = "20/Nov/2025",
                            qr = "Generado",
                            cleaning = "No",
                            id = "JD-1-jk2638",
                            OnViewDetails = {
                                navController.navigate(Route.AdminReservationsDetail.route)
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AdminReservationScreenPreview() {
    val navController = rememberNavController()
    AdminReservationScreen(navController)
}

@Composable
fun ReservationCard(
    name: String,
    room: String,
    date: String,
    qr: String,
    cleaning: String,
    id: String,
    OnViewDetails: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color(0xFF00A0A0), RoundedCornerShape(12.dp))
            .padding(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF8F8F8)
        )
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(name, fontWeight = FontWeight.Bold)
                Text(
                    text = "Ver detalles",
                    textDecoration = TextDecoration.Underline,
                    color = Color(0xFF007F89),
                    modifier = Modifier.clickable {
                        OnViewDetails()
                    }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Estado: Reservada")
                Text("Habitación: $room")
                Text("Entrada: $date")
                Text("Personas: 1")
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Limpieza: $cleaning")
                Text("QR: $qr")
                Text("Identificador: $id")
            }
        }
    }
}