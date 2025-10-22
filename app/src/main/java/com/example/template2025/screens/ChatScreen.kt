package com.example.template2025.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Date
import java.util.Locale
import java.util.UUID

@Composable
fun ChatScreen(modifier: Modifier = Modifier) {
    val messages = remember {
        mutableStateListOf(
            ChatMessage("¡Hola! En breve te atenderemos, por favor deja tu consulta en un solo mensaje.", fromMe = false, time = currentTimeString()),
        )
    }
    var input by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    // Autoscroll cuando cambian los mensajes
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) listState.animateScrollToItem(0)
    }

    Column(modifier = modifier) {
        LazyColumn(
            modifier = Modifier.fillMaxWidth().weight(1f),
            state = listState,
            reverseLayout = true,
            contentPadding = PaddingValues(vertical = 8.dp, horizontal = 12.dp)
        ) {
            items(items = messages.asReversed(), key = { it.id }) { msg ->
                ChatBubble(message = msg)
                Spacer(Modifier.height(6.dp))
            }
        }

        MessageInputBar(
            value = input,
            onValueChange = { input = it },
            onSend = {
                val text = input.trim()
                if (text.isNotEmpty()) {
                    messages.add(ChatMessage(text, fromMe = true, time = currentTimeString()))
                    input = ""
                    scope.launch {
                        delay(600)
                        messages.add(ChatMessage("En un momento un administrador se pondrá en contacto contigo.", fromMe = false, time = currentTimeString()))
                    }
                }
            }
        )
    }
}


data class ChatMessage(
    val text: String,
    val fromMe: Boolean,
    val time: String,
    val id: String = UUID.randomUUID().toString()
)

@Composable
private fun ChatBubble(message: ChatMessage) {
    val bubbleColor = if (message.fromMe)
        MaterialTheme.colorScheme.primaryContainer
    else
        MaterialTheme.colorScheme.surfaceVariant

    val textColor = if (message.fromMe)
        MaterialTheme.colorScheme.onPrimaryContainer
    else
        MaterialTheme.colorScheme.onSurfaceVariant

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (message.fromMe) Arrangement.End else Arrangement.Start
    ) {
        Surface(
            color = bubbleColor,
            shape = RoundedCornerShape(
                topStart = 16.dp, topEnd = 16.dp,
                bottomEnd = if (message.fromMe) 4.dp else 16.dp,
                bottomStart = if (message.fromMe) 16.dp else 4.dp
            ),
            tonalElevation = 2.dp,
            shadowElevation = 1.dp
        ) {
            Column(Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                Text(
                    text = message.text,
                    style = MaterialTheme.typography.bodyMedium,
                    color = textColor
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = message.time,
                    style = MaterialTheme.typography.labelSmall,
                    color = textColor.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Composable
private fun MessageInputBar(
    value: String,
    onValueChange: (String) -> Unit,
    onSend: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .imePadding()
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            modifier = Modifier.weight(1f),
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text("Escribe un mensaje...") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
            keyboardActions = KeyboardActions(onSend = { onSend() }),
            shape = RoundedCornerShape(24.dp)
        )
        Spacer(Modifier.width(8.dp))
        FilledIconButton(
            onClick = onSend,
            enabled = value.isNotBlank(),
            shape = CircleShape
        ) {
            Icon(Icons.Filled.Send, contentDescription = "Enviar")
        }
    }
}


private fun currentTimeString(): String {
    val sdf = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault())
    return sdf.format(java.util.Date())
}
