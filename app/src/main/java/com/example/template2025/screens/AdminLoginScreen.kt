package com.example.template2025.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.template2025.R

@Composable
fun AdminLoginScreen(
    isLoading: Boolean ,
    error: String?,
    onErrorDismiss: () -> Unit,
    onBack: () -> Unit = {},
    onLogin: (email: String, password: String) -> Unit = { _, _ -> }
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        IconButton(
            onClick = { if (!isLoading) onBack() },
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 16.dp, top = 32.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.arrow_back),
                contentDescription = "Back"
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly,
        ) {
            var user by remember { mutableStateOf("") }
            var password by remember { mutableStateOf("") }

            Image(
                painter = painterResource(id = R.drawable.logo_caritas),
                contentDescription = null,
                modifier = Modifier
                    .size(300.dp)
                    .padding(top = 4.dp)
            )

            Text(
                text = "Inicio de sesión",
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = "Administrador",
                style = MaterialTheme.typography.bodyLarge
            )

            Box(
                contentAlignment = Alignment.Center
            ) {
                Column (
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.alpha(if (isLoading) 0.8f else 1f)
                ) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        // This height should be enough for one or two lines of error text.
                        // Adjust as needed.
                        modifier = Modifier.height(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        error?.let { errorMessage ->
                            Text(
                                text = errorMessage,
                                color = Color.Red,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }

                    OutlinedTextField(
                        value = user,
                        onValueChange = { user = it },
                        label = { Text("Usuario") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(0.8f),
                        enabled = !isLoading
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    PasswordTextField(
                        password = password,
                        onPasswordChange = { password = it },
                        enabled = !isLoading
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    if (isLoading) {
                        CircularProgressIndicator()
                    } else {
                        Button(
                            onClick = {
                                onErrorDismiss()
                                onLogin(user, password)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF003B5C)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth(0.7f)
                        ) {
                            Text(text = "Iniciar sesión", color = Color.White)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PasswordTextField(
    password: String,
    onPasswordChange: (String) -> Unit,
    enabled: Boolean
) {
    var passwordVisible by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = password,
        onValueChange = onPasswordChange,
        label = { Text("Password") },
        singleLine = true,
        modifier = Modifier.fillMaxWidth(0.8f),
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            val image = if (passwordVisible)
                Icons.Default.Visibility
            else
                Icons.Default.VisibilityOff

            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                Icon(imageVector = image, contentDescription = null)
            }
        },
        enabled = enabled
    )
}

@Preview(showBackground = true, name = "Default State")
@Composable
fun AdminLoginScreenPreview() {
    AdminLoginScreen(isLoading = false, error = null, onErrorDismiss = {})
}

@Preview(showBackground = true, name = "Loading State")
@Composable
fun AdminLoginScreenLoadingPreview() {
    AdminLoginScreen(isLoading = true, error = null, onErrorDismiss = {})
}

@Preview(showBackground = true, name = "Error State")
@Composable
fun AdminLoginScreenErrorPreview() {
    AdminLoginScreen(isLoading = false, error = "Usuario o contraseña incorrectos.", onErrorDismiss = {})
}