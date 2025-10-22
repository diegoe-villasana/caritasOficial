package com.example.template2025.screens

import android.widget.Toast
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalOverscrollFactory
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.template2025.viewModel.AppViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.TextLayoutResult
import com.example.template2025.components.Servicios
import com.example.template2025.navigation.Route


private val sampleServices = listOf(
    Servicios(
        "Transporte",
        "22 / Oct / 2025",
        "14:30",
        150.00
    ),
    Servicios(
        "Comida",
        "22 / Oct / 2025",
        "20:00",
        75.50
    ),
    Servicios(
        "Lavandería",
        "23 / Oct / 2025",
        "11:00",
        50.00
    ),
    Servicios(
        "Comida",
        "23 / Oct / 2025",
        "12:30",
        85.00
    ),
    Servicios(
        "Transporte",
        "24 / Oct / 2025",
        "09:00",
        25.00
    )
)

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun AdminReservationDetailScreen(
    navController: NavController,
    vm: AppViewModel,
    reservaId: Int
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var showCancelDialog by remember { mutableStateOf(false) }
    var showFinalizeDialog by remember { mutableStateOf(false) }
    var showPayDialog by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = reservaId) {
        vm.fetchReservaById(reservaId)
    }

    val detailState by vm.reservaDetailState.collectAsState()
    val reserva = detailState.reserva
    val posada = reserva?.let { res -> vm.posadaState.value.posadas.find { it.id == res.posadaId } }

    if (showCancelDialog) {
        AlertDialog(onDismissRequest = { showCancelDialog = false },
            containerColor = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(16.dp),
            title = {
                Text(
                    "Confirmar Cancelación",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleLarge
                )
            },
            text = {
                Text(
                    "¿Estás seguro de que deseas cancelar esta reserva? Esta acción no se puede deshacer.",
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    Button(
                        onClick = {
                            scope.launch {
                                vm.cancelReserva(reservaId) { success, message ->
                                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                                    if (success) {
                                        navController.popBackStack()
                                    }
                                }
                            }
                            showCancelDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Sí, cancelar reserva")
                    }

                    Button(
                        onClick = { showCancelDialog = false },
                        colors = ButtonDefaults.outlinedButtonColors(),
                        border = BorderStroke(1.dp, Color.Gray.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("No, mantener")
                    }
                }
            }
        )
    }

    if (showFinalizeDialog) {
        AlertDialog(
            onDismissRequest = { showFinalizeDialog = false },
            containerColor = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(16.dp),
            title = {
                Text(
                    "Confirmar Finalización",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleLarge
                )
            },
            text = {
                Text(
                    "¿Estás seguro de que deseas finalizar esta reserva (Check-out)?",
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            scope.launch {
                                vm.updateReservaEstado(reservaId, "checkout") { success, message ->
                                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                                    if (success) {
                                        navController.popBackStack()
                                    }
                                }
                            }
                            showFinalizeDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF0000)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Sí, finalizar reserva")
                    }
                    Button(
                        onClick = { showFinalizeDialog = false },
                        colors = ButtonDefaults.outlinedButtonColors(),
                        border = BorderStroke(1.dp, Color.Gray.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("No, mantener activa")
                    }
                }
            }
        )
    }

    if (showPayDialog) {
        AlertDialog(onDismissRequest = { showPayDialog = false },
            containerColor = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(16.dp),
            title = { Text("Confirmar Pago", fontWeight = FontWeight.Bold) },
            text = { Text("¿Confirmas que se ha recibido el pago para esta reserva?") },
            confirmButton = {
                Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = {
                            scope.launch {
                                vm.updatePagado(reservaId) { success, message ->
                                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                                }
                            }
                            showPayDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("Sí, Marcar como Pagado") }
                    Button(
                        onClick = { showPayDialog = false },
                        colors = ButtonDefaults.outlinedButtonColors(),
                        border = BorderStroke(1.dp, Color.Gray.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("Cancelar") }
                }
            }
        )
    }

    CompositionLocalProvider(LocalOverscrollFactory provides null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp, horizontal = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Volver"
                    )
                }
                Text(
                    text = "Detalles de la Reserva",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold
                )
            }

            when {
                detailState.isLoading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 100.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                detailState.error != null -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 100.dp, start = 16.dp, end = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Error: ${detailState.error}",
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center
                        )
                    }
                }
                reserva != null -> {
                    Column(
                        modifier = Modifier
                            .padding(20.dp)
                            .fillMaxWidth()
                    ) {

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            // Main Status Tag (Unchanged)
                            val (statusText, statusColor) = when {
                                reserva.estado.equals("pendiente", ignoreCase = true) -> "Pendiente" to Color(0xFFFBC02D)
                                reserva.estado.equals("checkin", ignoreCase = true) -> "Registrado" to MaterialTheme.colorScheme.primary
                                else -> reserva.estado.replaceFirstChar { it.uppercase() } to Color.Gray
                            }
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = statusColor.copy(alpha = 0.15f),
                                contentColor = statusColor
                            ) {
                                Text(
                                    text = statusText,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                )
                            }
                            if (reserva.estado.equals("checkin", ignoreCase = true)) {
                                val paymentStatus = if (reserva.pagado == 1) "Pagado" else "Pendiente de Pago"
                                val paymentColor = if (reserva.pagado == 1) Color(0xFF388E3C) else Color(0xFFD32F2F)
                                Surface(
                                    shape = RoundedCornerShape(16.dp),
                                    color = paymentColor.copy(alpha = 0.15f),
                                    contentColor = paymentColor
                                ) {
                                    Text(
                                        text = paymentStatus,
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = posada?.nombre ?: "Albergue no encontrado",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.secondary
                        )

                        Spacer(modifier = Modifier.height(16.dp))
                        HorizontalDivider()
                        Spacer(modifier = Modifier.height(16.dp))

                        InfoItem("Solicitante", reserva.nombreSolicitante)

                        val annotatedPhoneString = formatPhoneNumber(reserva.telefono, reserva.paisIso)
                        val uriHandler = LocalUriHandler.current

                        Column(modifier = Modifier.padding(vertical = 6.dp)) {
                            Text(
                                text = "Teléfono",
                                style = MaterialTheme.typography.labelLarge,
                                color = Color.Gray,
                                fontWeight = FontWeight.Medium)

                            var textLayoutResult by remember { mutableStateOf<TextLayoutResult?>(null) }

                            Text(
                                text = annotatedPhoneString,
                                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                                onTextLayout = { result ->
                                    textLayoutResult = result
                                },
                                modifier = Modifier.pointerInput(Unit) {
                                    detectTapGestures { offset ->
                                        textLayoutResult?.let { layoutResult ->
                                            val position = layoutResult.getOffsetForPosition(offset)
                                            annotatedPhoneString.getLinkAnnotations(position, position)
                                                .firstOrNull()?.let { link ->
                                                    if (link.item is LinkAnnotation.Url) {
                                                        uriHandler.openUri((link.item as LinkAnnotation.Url).url)
                                                    }
                                                }
                                        }
                                    }
                                }
                            )
                        }

                        InfoItem("País de Origen", reserva.paisIso)
                        InfoItem("Género", reserva.generoSolicitante.replaceFirstChar { it.uppercase() })
                        InfoItem("Fecha de Entrada", formatReservationDate(reserva.fechaEntrada))

                        Spacer(modifier = Modifier.height(12.dp))
                        HorizontalDivider()
                        Spacer(modifier = Modifier.height(12.dp))

                        InfoItem("Total de Personas", reserva.totalPersonas.toString())
                        InfoItem("Hombres", reserva.hombresCount.toString())
                        InfoItem("Mujeres", reserva.mujeresCount.toString())

                        Spacer(modifier = Modifier.height(12.dp))

                        if (reserva.estado.equals("pendiente", ignoreCase = true)) {
                            Spacer(modifier = Modifier.height(24.dp))
                            HorizontalDivider(thickness = 2.dp)
                            Spacer(modifier = Modifier.height(24.dp))

                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Button(
                                    onClick = {
                                        navController.navigate(Route.QRScanner.route)
                                    },
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.primary
                                    ),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.QrCodeScanner,
                                        contentDescription = "Escanear",
                                        modifier = Modifier.size(ButtonDefaults.IconSize)
                                    )
                                    Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                                    Text("Escanear Reserva")
                                }

                                Button(
                                    onClick = { showCancelDialog = true },
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.error
                                    ),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Cancelar",
                                        modifier = Modifier.size(ButtonDefaults.IconSize)
                                    )
                                    Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                                    Text("Cancelar Reserva")
                                }
                            }
                        } else if (reserva.estado.equals("checkin", ignoreCase = true)) {
                            Spacer(modifier = Modifier.height(24.dp))
                            HorizontalDivider(thickness = 2.dp)
                            Spacer(modifier = Modifier.height(16.dp))

                            Text("Servicios", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(8.dp))

                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                val groupedServices = sampleServices.groupBy { it.nombre }
                                groupedServices.forEach { (serviceType, services) ->
                                    ExpandableServiceCard(serviceType = serviceType, services = services)
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                            val total = sampleServices.sumOf { it.precio }
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp),
                                horizontalArrangement = Arrangement.End,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Total General", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.size(16.dp))
                                Text(
                                    String.format(Locale.US, "$%.2f", total),
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Medium
                                )
                            }

                            Spacer(modifier = Modifier.height(24.dp))

                            Button(
                                onClick = { showPayDialog = true },
                                enabled = reserva.pagado != 1,
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    disabledContainerColor = Color.Gray.copy(alpha = 0.5f)
                                ),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Confirmar Pago de Servicios")
                            }

                            Button(
                                onClick = { showFinalizeDialog = true },
                                enabled = reserva.pagado == 1,
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.error,
                                    disabledContainerColor = Color.Gray.copy(alpha = 0.5f)
                                ),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Finalizar",
                                    modifier = Modifier.size(ButtonDefaults.IconSize)
                                )
                                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                                Text("Finalizar Reserva")
                            }
                        }
                    }
                }
                else -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 100.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No se encontró la reserva.")
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }

}

@Composable
private fun InfoItem(label: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 6.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = Color.Gray,
            fontWeight = FontWeight.Medium
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.SemiBold
        )
    }
}

// In ExpandableServiceCard...
@Composable
private fun ExpandableServiceCard(
    serviceType: String,
    services: List<Servicios>
) {
    var expanded by remember { mutableStateOf(false) }
    val subtotal = services.sumOf { it.precio }
    val rotationAngle by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        label = "rotation"
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.15f)
        ),
        onClick = { expanded = !expanded }
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(serviceType, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = if (expanded) "Colapsar" else "Expandir",
                        modifier = Modifier.rotate(rotationAngle)
                    )
                }
                Text(
                    String.format(Locale.US, "$%.2f", subtotal),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
            }

            if (expanded) {
                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(12.dp))

                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    services.forEach { service ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "${service.fecha} a las ${service.hora}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.Gray
                            )
                            Text(
                                String.format(Locale.US, "$%.2f", service.precio),
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun formatReservationDate(dateString: String): String {
    val outputLocale = Locale.forLanguageTag("es-MX")
    val outputFormat = SimpleDateFormat("dd / MMM / yyyy", outputLocale)

    val inputParsers = listOf(
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault()),
        SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()),
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    )

    for (parser in inputParsers) {
        try {
            if ("'Z'" in parser.toPattern()) {
                parser.timeZone = java.util.TimeZone.getTimeZone("UTC")
            }
            val date = parser.parse(dateString)
            if (date != null) {
                val formattedDate = outputFormat.format(date)
                val parts = formattedDate.split(" ")
                if (parts.size == 5) {
                    return "${parts[0]} ${parts[1]} ${parts[2].replaceFirstChar { it.uppercase(outputLocale) }} ${parts[3]} ${parts[4]}"
                }
                return formattedDate
            }
        } catch (_: Exception) {

        }
    }
    return dateString
}

@Composable
private fun formatPhoneNumber(fullPhoneNumber: String, iso: String): AnnotatedString {
    val countryMap = mapOf(
        "MX" to "+52", "US" to "+1", "ES" to "+34","CO" to "+57", "AR" to "+54", "PE" to "+51"
    )

    val dialCode = countryMap[iso.uppercase()] ?: ""
    val localNumber = fullPhoneNumber.removePrefix(dialCode).filter { it.isDigit() }

    val formattedLocalNumber = when (localNumber.length) {
        12 -> localNumber.replaceFirst(Regex("(\\d{3})(\\d{3})(\\d{3})(\\d{3})"), "$1 $2 $3 $4")
        11 -> localNumber.replaceFirst(Regex("(\\d{3})(\\d{4})(\\d{4})"), "$1 $2 $3")
        10 -> localNumber.replaceFirst(Regex("(\\d{2})(\\d{4})(\\d{4})"), "$1 $2 $3")
        9  -> localNumber.replaceFirst(Regex("(\\d{3})(\\d{3})(\\d{3})"), "$1 $2 $3")
        8  -> localNumber.replaceFirst(Regex("(\\d{4})(\\d{4})"), "$1 $2")
        7  -> localNumber.replaceFirst(Regex("(\\d{3})(\\d{4})"), "$1 $2")
        else -> localNumber
    }

    val displayText = "$dialCode $formattedLocalNumber"


    return buildAnnotatedString {
        append(displayText)
        addStyle(
            style = SpanStyle(
                color = MaterialTheme.colorScheme.primary,
                textDecoration = TextDecoration.Underline
            ),
            start = 0,
            end = displayText.length
        )

        addLink(
            url = LinkAnnotation.Url("tel:$fullPhoneNumber"),
            start = 0,
            end = displayText.length
        )
    }
}