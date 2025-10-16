package com.example.template2025.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalOverscrollFactory
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AdminHomeScreen() {
    CompositionLocalProvider(LocalOverscrollFactory provides null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
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
            OutlinedButton(
                onClick = { /* TODO */},
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .height(60.dp),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, Color.Gray)
            ) {
                Text("Todos los albergues")
                Spacer(modifier = Modifier.weight(1f))
                Icon(Icons.Filled.KeyboardArrowDown,
                    contentDescription = null
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
            Card(
                modifier = Modifier.fillMaxWidth(0.9f),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, Color(0xFF009688)),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFFFFFFF) // light teal-ish background
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Resumen", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

                    Spacer(modifier = Modifier.height(12.dp))
                    SummaryItem("Casa San José", 4, false, 27, false, 2, true, 2)
                    SummaryItem("Casa San Martín", 12, true, 89, true, 4, true, 1)
                    SummaryItem("Casa San María", 8, false, 52, false, 1, false, 0)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            Card(
                modifier = Modifier.fillMaxWidth(0.9f),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, Color(0xFF009688)),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFFFFFFF) // light teal-ish background
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Reservas Recientes", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Casa San José", color = Color(0xFF0088CC), fontWeight = FontWeight.Bold)

                    Spacer(modifier = Modifier.height(8.dp))
                    ReservationCard("John Doe", "Reservada", Color(0xFF80DEEA), "305", "20/Nov/2025", "1")
                    ReservationCard("Juan Cuervo", "Check-In", Color(0xFFA5D6A7), "306", "18/Nov/2025", "2")
                    ReservationCard("Mike Modric", "Check-out", Color(0xFFB39DDB), "302", "10/Nov/2025", "4")
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AdminHomeScreenPreview() {
    AdminHomeScreen()
}

fun getToday(): String {
    val locale = Locale.forLanguageTag("es-MX")
    val date = Date()
    val sdf = SimpleDateFormat("EEEE d 'de' MMMM 'de' yyyy", locale)
    val formattedDate = sdf.format(date)
        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(locale) else it.toString() }

    return formattedDate.replaceFirstChar {
        if (it.isLowerCase()) it.titlecase(locale) else it.toString()
    }
}

@Composable
fun SummaryItem(
    name: String,
    reservas: Int, reservasUp: Boolean,
    ocupacion: Int, ocupacionUp: Boolean,
    voluntarios: Int, voluntariosUp: Boolean,
    transportes: Int
) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(name, color = Color(0xFF0088CC), fontWeight = FontWeight.Bold)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            StatText("Reservas hoy", "$reservas", reservasUp)
            StatText("Ocupación Total", "$ocupacion%", ocupacionUp)
            StatText("Voluntarios", "$voluntarios", voluntariosUp)
            StatText("Transportes pendientes", "$transportes", null)
        }
    }
}

@Composable
fun StatText(label: String, value: String, isUp: Boolean?) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, fontSize = 12.sp)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                value,
                fontWeight = FontWeight.Bold,
                color = when (isUp) {
                    true -> Color(0xFF2E7D32)
                    false -> Color(0xFFC62828)
                    null -> Color.Black
                }
            )
            if (isUp != null) {
                Icon(
                    imageVector = if (isUp) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                    contentDescription = null,
                    tint = if (isUp) Color(0xFF2E7D32) else Color(0xFFC62828)
                )
            }
        }
    }
}

@Composable
fun ReservationCard(
    name: String,
    estado: String,
    estadoColor: Color,
    habitacion: String,
    entrada: String,
    personas: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, Color.Gray),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF8F8F8)
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(name, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                EstadoChip(estado, estadoColor)
                InfoChip("Habitación", habitacion)
                InfoChip("Entrada", entrada)
                InfoChip("Personas", personas)
            }
        }
    }
}

@Composable
fun EstadoChip(text: String, color: Color) {
    Surface(
        shape = RoundedCornerShape(50),
        color = color.copy(alpha = 0.3f),
        border = BorderStroke(1.dp, color)
    ) {
        Text(
            text,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
            color = color.darken(0.5f),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun InfoChip(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, fontSize = 12.sp)
        Surface(
            shape = RoundedCornerShape(50),
            border = BorderStroke(1.dp, Color.Gray)
        ) {
            Text(value, modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp), fontSize = 12.sp)
        }
    }
}

// Helper extension
fun Color.darken(factor: Float): Color {
    return Color(red * (1 - factor), green * (1 - factor), blue * (1 - factor), alpha)
}