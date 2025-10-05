package com.example.template2025.screens

import com.example.template2025.R
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Preview(showBackground = true, widthDp = 360, heightDp = 640)
@Composable
fun Login(modifier: Modifier = Modifier){
    var inputusuario by rememberSaveable {mutableStateOf("") }
    var inputpassword by rememberSaveable { mutableStateOf("") }
    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center)
    {
        Image(painter = painterResource(id = R.drawable.caritas_logo),
            contentDescription = "Logo Sof",
            modifier = Modifier
                .size(200.dp)
                .padding(top = 4.dp)

        )
        Spacer(modifier = Modifier.height(16.dp))


        OutlinedTextField(value = inputusuario,
            onValueChange = {inputusuario = it},
            label = { Text("Enter a user")},
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
        )
        Spacer(modifier = Modifier.height(32.dp))
        OutlinedTextField(value = inputpassword,
            onValueChange = {inputpassword = it},
            label = { Text("Enter a password")},
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
        )
        Spacer(modifier = Modifier.height(32.dp))
        Button(onClick = { /*TODO*/ },
            modifier= Modifier
                .height(32.dp)
                .width(202.dp)


        ) {
            Text(text = "Entrar")
        }
    }


}
@Composable
fun LoginScreen(onLogin: () -> Unit, onGoToRegister: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var pass by remember { mutableStateOf("") }

    Box(
        Modifier.fillMaxSize().background(Color(0xFFFFFF00)),
        contentAlignment = Alignment.Center,

    )
    {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(24.dp)
        ) {
            Text("Iniciar sesión", style = MaterialTheme.typography.headlineSmall)
            OutlinedTextField(email, { email = it }, label = { Text("Email") }, singleLine = true)
            OutlinedTextField(pass, { pass = it }, label = { Text("Contraseña") },
                singleLine = true, visualTransformation = PasswordVisualTransformation()
            )
            Button(onClick = onLogin, modifier = Modifier.fillMaxWidth()) { Text("Entrar") }
            OutlinedButton(onClick = onGoToRegister, modifier = Modifier.fillMaxWidth()) {
                Text("Crear cuenta")
            }
        }
    }
}