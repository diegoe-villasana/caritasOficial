package com.example.template2025.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.dp
import com.example.template2025.R

@Preview(showBackground = true, widthDp = 360, heightDp = 640)
@Composable
fun QRScreen(){

    val UltraWhite = Color(0xFFFFFFFF)


    Column(modifier = Modifier
        .fillMaxSize()
        .background(UltraWhite)
        .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center)
    {
        Image(painter = painterResource(id = R.drawable.logo_caritas),
            contentDescription = "Logo Sof",
            modifier = Modifier
                .size(200.dp)
                .padding(top = 4.dp)

        )

        Text("RESERVA CREADA",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp)

        Column(
            modifier = Modifier
                .width(300.dp)
                .background(UltraWhite)
                .padding(top = 10.dp),
        ) {
            // TODO: Usar valores de la reserva creada en la anterior pantalla
            TextField("Personas", "3")
            TextField("Entrada", "04-10-2025")
            TextField("Telefono", "+52 81 1111 111")
        }

        Text("Muestre el siguiente código QR al momento de llegar a su estadía",
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(top = 20.dp),
            textAlign = TextAlign.Center,
            fontSize = 20.sp
        )

    }
}

@Composable
fun TextField(field: String, value: String) {
    Row(modifier = Modifier.padding(top = 5.dp)) {
        Text("$field: ", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Text(value, fontSize = 20.sp)
    }
}