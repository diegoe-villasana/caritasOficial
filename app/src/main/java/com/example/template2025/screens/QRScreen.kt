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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.dp
import com.example.template2025.R
import com.google.zxing.BarcodeFormat
import com.google.zxing.common.BitMatrix
import com.google.zxing.qrcode.QRCodeWriter
import androidx.core.graphics.createBitmap

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
        Image(painter = painterResource(id = R.drawable.logosof),
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

        Surface(
            modifier = Modifier.size(300.dp),
            color = UltraWhite,

        ) {
            val qr = qrCodeGenerator("www.google.com", 200)
            // TODO: Link de peticion en API (Verificar que es un admin)
            if (qr != null)
                Image(
                    bitmap = qr.asImageBitmap(),
                    contentDescription = "QR de Reserva",
                    contentScale = ContentScale.Fit,
                )
        }
    }
}

@Composable
fun TextField(field: String, value: String){
    Row(modifier = Modifier.padding(top = 5.dp)) {
        Text("$field: ", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Text(value, fontSize = 20.sp)
    }
}

fun qrCodeGenerator(data: String, size: Int) = try {
    val writer = QRCodeWriter()
    val bitMatrix: BitMatrix = writer.encode(data, BarcodeFormat.QR_CODE, size, size)
    val width: Int = bitMatrix.width
    val height: Int = bitMatrix.height
    val pixels = IntArray(width * height)
    for (y in 0 until height) {
        val offset = y * width
        for (x in 0 until width) {
            pixels[offset + x] =
                if (bitMatrix.get(x, y)) 0xFF000000.toInt() else 0xFFFFFFFF.toInt()
        }
    }
    createBitmap(width, height).apply {
        setPixels(pixels, 0, width, 0, 0, width, height)
    }
} catch (e: Exception) {
    null
}