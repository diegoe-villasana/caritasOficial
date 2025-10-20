package com.example.template2025.components


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
// Make sure these imports are present
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.template2025.model.Posada
import coil3.compose.AsyncImage

@Composable
fun PosadasCard(
    posadas : Posada,
    onItemClick :(Posada) -> Unit

) {
    Card(
        modifier = Modifier
            .padding(12.dp)
            .fillMaxWidth()
            .clickable { onItemClick(posadas) },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)

    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = posadas.imageUrl,
                contentDescription="Posadas Image",
                modifier = Modifier.size(128.dp)
            )
            Column(Modifier.padding(start = 8.dp)) {
                Text(posadas.nombre, style = MaterialTheme.typography.titleLarge)

                // Updated Text for "Dirección"
                Text(
                    buildAnnotatedString {
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append("Dirección\n")
                        }
                        append(posadas.direccion)
                    },
                    style = MaterialTheme.typography.bodyMedium
                )

                // Updated Text for "Teléfono(s)"
                Text(
                    buildAnnotatedString {
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append("Teléfono(s)\n")
                        }
                        append(posadas.telefono)
                    },
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PosadasCardPreview() {
    val samplePosada = Posada(
        id = 1,
        nombre = "Posada del Sol",
        direccion = "Av. Siempre Viva 123",
        telefono = "555-1234, 555-5678",
        imageUrl = "https://www.caritas.org.mx/wp-content/uploads/2018/06/caritas-posada-del-peregrino.jpg",
        isActive = 1,
        capacidadTotal = 0,
        capacidadDisponible = 0,
        createdAt = "",
        updatedAt = ""
    )
    PosadasCard(posadas = samplePosada, onItemClick = {})
}
