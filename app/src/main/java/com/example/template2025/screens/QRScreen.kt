package com.example.template2025.screens

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.dp
import androidx.core.graphics.createBitmap
import com.example.template2025.R
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.google.zxing.qrcode.QRCodeWriter
import com.journeyapps.barcodescanner.BarcodeEncoder
import androidx.core.graphics.set
import androidx.navigation.NavController

@Composable
fun QRScreen(
    navController: NavController,
    qrCodeUrl: String?,
    posadaName: String?,
    personCount: String?,
    entryDate: String?,
    phone: String?
){
    val qrBitmap = remember(qrCodeUrl) {
        if (qrCodeUrl != null) {
            generateQrBitmap(qrCodeUrl)
        } else {
            // Return a placeholder or empty bitmap if URL is null
            createBitmap(1, 1, Bitmap.Config.ARGB_8888)
        }
    }

    val UltraWhite = Color(0xFFFFFFFF)

    Box(modifier = Modifier
        .background(Color.White),
        contentAlignment = Alignment.TopStart
    ){
        Column(modifier = Modifier
            .fillMaxSize()
            .background(UltraWhite)
            .verticalScroll(rememberScrollState())
            .padding(top = 80.dp, start = 24.dp, end = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        )
        {
            Image(painter = painterResource(id = R.drawable.caritas_logo),
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
                TextField("Posada", posadaName ?: "No disponible")
                TextField("Personas", personCount ?: "N/A")
                TextField("Entrada", entryDate ?: "No disponible")
                TextField("Teléfono", phone ?: "No disponible")
            }

            Text("Muestre el siguiente código QR al momento de llegar a su estadía",
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .padding(top = 20.dp),
                textAlign = TextAlign.Center,
                fontSize = 20.sp
            )
            // Display the generated QR Code
            if (qrCodeUrl != null) {
                Image(
                    bitmap = qrBitmap.asImageBitmap(),
                    contentDescription = "Código QR de la reserva",
                    modifier = Modifier.size(250.dp)
                )
            }
        }
        IconButton(
            onClick = { navController.navigateUp() },
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

@Composable
fun TextField(field: String, value: String) {
    Row(modifier = Modifier.padding(top = 5.dp)) {
        Text("$field: ", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Text(value, fontSize = 20.sp)
    }
}

private fun generateQrBitmap(content: String): Bitmap {
    val writer = QRCodeWriter()
    val bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, 512, 512)
    val width = bitMatrix.width
    val height = bitMatrix.height
    val bitmap = createBitmap(width, height, Bitmap.Config.RGB_565)
    for (x in 0 until width) {
        for (y in 0 until height) {
            bitmap[x, y] =
                if (bitMatrix[x, y]) android.graphics.Color.BLACK else android.graphics.Color.WHITE
        }
    }
    return bitmap
}