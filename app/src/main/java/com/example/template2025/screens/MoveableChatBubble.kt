package com.example.template2025.screens

import androidx.compose.runtime.Composable
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

@Composable
fun MovableChatBubble() {
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }
    var showChat by remember { mutableStateOf(false) }

    Box(Modifier.fillMaxSize()) {
        if (showChat) {
            // Chat expandido, movible
            Box(
                Modifier
                    .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
                    .background(MaterialTheme.colorScheme.primaryContainer, shape = RoundedCornerShape(16.dp))
                    .size(300.dp, 400.dp)
            ) {
                Column {
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        IconButton(onClick = { showChat = false }) {
                            Icon(Icons.Default.Close, contentDescription = "Cerrar chat")
                        }
                    }
                    ChatScreen(Modifier.weight(1f))
                }
            }
        } else {
            // Burbuja flotante movible
            IconButton(
                onClick = { showChat = true },
                modifier = Modifier
                    .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
                    .background(MaterialTheme.colorScheme.primary, shape = CircleShape)
                    .size(56.dp)
                    .pointerInput(Unit) {
                        detectDragGestures { change, dragAmount ->
                            change.consume()
                            offsetX += dragAmount.x
                            offsetY += dragAmount.y
                        }
                    }
            ) {
                Icon(Icons.Default.Chat, contentDescription = "Abrir chat", tint = MaterialTheme.colorScheme.onPrimary)
            }
        }
    }
}
