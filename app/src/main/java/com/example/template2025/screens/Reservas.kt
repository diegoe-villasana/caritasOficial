package com.example.template2025.screens

import com.example.template2025.R
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.compose.foundation.background
import androidx.compose.ui.res.painterResource
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.ui.viewinterop.AndroidView
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext

import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.unit.sp

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.DateRange
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.TextFieldDefaults
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Polyline
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavController
import java.util.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.input.KeyboardType
import com.example.template2025.composables.PhoneField
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.config.Configuration.*


fun decodePoly(encoded: String): List<GeoPoint> {
    val poly = mutableListOf<GeoPoint>()
    var index = 0
    val len = encoded.length
    var lat = 0
    var lng = 0

    while (index < len) {
        var b: Int
        var shift = 0
        var result = 0
        do {
            b = encoded[index++].code - 63
            result = result or (b and 0x1f shl shift)
            shift += 5
        } while (b >= 0x20)
        val dlat = if (result and 1 != 0) (result shr 1).inv() else (result shr 1)
        lat += dlat

        shift = 0
        result = 0
        do {
            b = encoded[index++].code - 63
            result = result or (b and 0x1f shl shift)
            shift += 5
        } while (b >= 0x20)
        val dlng = if (result and 1 != 0) (result shr 1).inv() else (result shr 1)
        lng += dlng

        val p = GeoPoint(lat / 1E5, lng / 1E5)
        poly.add(p)
    }
    return poly
}



@Composable
fun MapWithRoute(
    encodedPolyline: String?,
    origin: String,
    destination: String
) {
    val context = LocalContext.current

    val mapView = remember {
        getInstance().load(context, context.getSharedPreferences("osm", 0))

        MapView(context).apply {
            setTileSource(TileSourceFactory.MAPNIK)
            setBuiltInZoomControls(true)
            setMultiTouchControls(true)
            controller.setZoom(13.0)
            controller.setCenter(GeoPoint(25.6866, -100.3109))

            this.setClipToOutline(true)
        }
    }

    LaunchedEffect(encodedPolyline) {
        if (!encodedPolyline.isNullOrBlank()) {
            try {
                val routePoints = decodePoly(encodedPolyline)
                mapView.overlays.clear()

                val newPolyline = Polyline(mapView)
                newPolyline.setPoints(routePoints)
                newPolyline.color = Color.Blue.toArgb()
                newPolyline.width = 7f
                mapView.overlays.add(newPolyline)
                if (routePoints.isNotEmpty()) {
                    val startPoint = routePoints.first()
                    mapView.controller.animateTo(startPoint)
                }

                mapView.invalidate()
            } catch (e: Exception) {
                println("Error al decodificar polyline o dibujar mapa: ${e.message}")
            }
        }
    }

    AndroidView(
        factory = { mapView },
        update = {
            it.invalidate()
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)

            .clipToBounds()
    )
}

val LOCATION_COORDINATES = mapOf(
    "Sede Central" to "25.6866,-100.3109",
    "Hospital IMSS 25" to "25.7000,-100.3000",
    "Hospital IMSS 34" to "25.7500,-100.3500",
    "Hospital general zona 22" to "25.6500,-100.2500",
    "otros" to ""
)


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerBox(reserva: String, onReservaChange: (String) -> Unit) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)

    val datePickerDialog = DatePickerDialog(
        context,
        { _: DatePicker, y: Int, m: Int, d: Int ->
            val fechaSeleccionada = "$y-${m + 1}-$d"
            onReservaChange(fechaSeleccionada)
        }, year, month, day
    )

    OutlinedTextField(
        value = reserva,
        onValueChange = {},
        readOnly = true,
        label = { Text("Fecha") },
        trailingIcon = {
            IconButton(onClick = { datePickerDialog.show() }) {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = "Seleccionar fecha"
                )
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 60.dp)
    )
}




/////////////////




data class Distance(val text: String, val value: Int)
data class Duration(val text: String, val value: Int)

data class RouteResponse(
    val routes: List<Route>
)

data class Route(
    val legs: List<Leg>,

    val overview_polyline: PolylineData
)

data class Leg(
    val startLocation: Location,
    val endLocation: Location,
    val steps: List<Step>,
    val duration: Duration?,
    val distance: Distance?
)

data class Step(
    val polyline: PolylineData
)

data class PolylineData(
    val points: String
)

data class Location(
    val lat: Double,
    val lng: Double
)

interface RoutesApi {
    @GET("json")
    suspend fun getRoute(
        @Query("origin") origin: String,
        @Query("destination") destination: String,
        @Query("travelMode") travelMode: String = "DRIVE",
        @Query("key") apiKey: String
    ): RouteResponse
}
/////////////////
@Composable
fun Nombre(){
    var nombre by remember{ mutableStateOf("") }
    val PrimaryBlue = Color(0xFF0097A7)
    val PrimaryBlueDark = Color(0xFF00796B)
    val TextColor = Color(0xFF212121)
    OutlinedTextField(
        value = nombre,
        onValueChange={nombre = it},
        label={Text("Nombre")},
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            disabledContainerColor = Color.Transparent,
            focusedIndicatorColor = PrimaryBlue,
            unfocusedIndicatorColor = PrimaryBlueDark,
            cursorColor = PrimaryBlue,
            focusedLabelColor = PrimaryBlue,
            unfocusedLabelColor = TextColor
        ))
}

@Composable
fun Telefono(telefono: String, onTelefonoChange: (String) -> Unit){
    val PrimaryBlue = Color(0xFF0097A7)
    val PrimaryBlueDark = Color(0xFF00796B)
    val TextColor = Color(0xFF212121)
    OutlinedTextField(
        value = telefono,
        onValueChange = onTelefonoChange,
        label={Text("Telefono")},
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            disabledContainerColor = Color.Transparent,
            focusedIndicatorColor = PrimaryBlue,
            unfocusedIndicatorColor = PrimaryBlueDark,
            cursorColor = PrimaryBlue,
            focusedLabelColor = PrimaryBlue,
            unfocusedLabelColor = TextColor
        ))
}
@Composable
fun Personas(personas: String, onPersonasChange: (String) -> Unit,modifier: Modifier = Modifier) {
    val PrimaryBlue = Color(0xFF0097A7)
    val PrimaryBlueDark = Color(0xFF00796B)
    val TextColor = Color(0xFF212121)
    OutlinedTextField(
        value = personas,
        onValueChange = onPersonasChange,
        label = { Text("Personas") },
        singleLine = true,
        modifier = Modifier
            .width(130.dp)
            .height(65.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            disabledContainerColor = Color.Transparent,
            focusedIndicatorColor = PrimaryBlue,
            unfocusedIndicatorColor = PrimaryBlueDark,
            cursorColor = PrimaryBlue,
            focusedLabelColor = PrimaryBlue,
            unfocusedLabelColor = TextColor
        )
    )
}







@Preview(showBackground = true, widthDp = 400, heightDp = 640)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Preview(){
    val navController = rememberNavController()
    // Y se lo pasamos
    reservas(navController = navController)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun reservas(navController: NavController){
    val PrimaryBlue = Color(0xFF0097A7)
    val PrimaryBlueDark = Color(0xFF00796B)
    val LightGrayBackground = Color(0xFFF5F5F5)
    val TextColor = Color(0xFF212121)
    val UltraWhite = Color(0xFFFFFFFF)




    var routeDurationText by remember { mutableStateOf<String?>(null) }
    var routeDistanceText by remember { mutableStateOf<String?>(null) }
    var encodedPolyline by remember { mutableStateOf<String?>(null) }



    val LOCATION_COORDINATES = mapOf(
        "Sede Central" to "25.6691, -100.3129",
        "Hospital IMSS 25" to "25.6599, -100.2783",
        "Hospital IMSS 34" to "25.7500,-100.3500",
        "Hospital general zona 22" to "25.6500,-100.2500",
        "Hospital general de zona no 67" to "25.792, -100.140",
        "Hospital General zona 2" to "25.6700, -100.2959",
        "UMAE Hospital de Especialidades" to "25.7005, -100.3440",
        "UMAE Hospital clinica 23" to "25.6749, -100.3217",
        "UMAE Hospital de Cardiologia" to "25.7000, -100.3448",

        "otros" to "",
        "Posada del Peregrino" to "25.689, -100.315",
        "Posada del Peregrino Divina Providencia" to "25.675, -100.315",
        "Posada del Peregrino Apodaca" to "25.783, -100.223"


    )


    val scrollState = rememberScrollState()
    var personas by remember { mutableStateOf("") }
    var sexo by remember {mutableStateOf("")}
    var servicio by remember {mutableStateOf("")}
    var Hora by remember{mutableStateOf("")}
    var Ubicacion by remember {mutableStateOf("")}
    var Destino by remember { mutableStateOf("") }
    var Origen by remember {mutableStateOf("")}

    var numTel by remember { mutableStateOf("") }
    var expandedservicio by remember { mutableStateOf(false) }
    var expandedSexo by remember { mutableStateOf(false) }
    var expandedHora by remember { mutableStateOf(false) }
    var expandedUbicacion by remember {mutableStateOf(false)}
    var expandedDestino by remember {mutableStateOf(false)}
    var expandedOrigen by remember {mutableStateOf(false)}
    var servicio_pet by remember { mutableStateOf("") }
    var reserva by remember { mutableStateOf("") }
    var idusuario by remember { mutableStateOf("1") }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val opcionServicio = listOf("Psicologo", "Ducha", "Transporte", "Comida","Medico")
    val opcionDestino = listOf("Hospital IMSS 25, Hospital IMSS 34, Hospital general zona 22")
    val opcionUbicacion = listOf("Sede Central,otros ")
    val destinoHospitales = listOf("Hospital IMSS 25", "Hospital IMSS 34", "Hospital general zona 22")
    val destinoPosadadelPeregrino = listOf("UMAE Hospital de Especialidades", "UMAE Hospital clinica 23", "UMAE Hospital de Cardiologia")
    val destinoPosadaAPodaca = listOf("Hospital general de zona no 67")
    val destinoDivinaProvidencia = listOf("Hospital General zona 2")
    val destinoSedes = listOf("Posada del Peregrino", "Posada del Peregrino Divina Providencia", "Posada del Peregrino Apodaca")
    val opcionsexo = listOf("Hombre", "Mujer")
    val opcionOrigen=listOf("Sede Central","otros", "Hospital IMSS 25", "Hospital IMSS 34", "Hospital general zona 22","Posada del Peregrino", "Posada del Peregrino Divina Providencia", "Posada del Peregrino Apodaca")
    val opcionHora = listOf(
        "6:00", "6:30",
        "7:00", "7:30",
        "8:00", "8:30",
        "9:00", "9:30",
        "10:00", "10:30",
        "11:00", "11:30",
        "12:00", "12:30",
        "13:00", "13:30",
        "14:00", "14:30",
        "15:00", "15:30",
        "16:00", "16:30",
        "17:00", "17:30",
        "18:00", "18:30",
        "19:00", "19:30",
        "20:00"
    )

    var telefono by rememberSaveable { mutableStateOf("") }
    var paisSeleccionado by remember { mutableStateOf(getCountries().first { it.isoCode == "MX" }) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .background(UltraWhite),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top


    ){
        Image(
            painter = painterResource(id = R.drawable.logo_caritas),
            contentDescription = "Logo Sof",
            modifier = Modifier
                .size(300.dp)
                .padding(bottom = 20.dp),


            )

        Spacer(Modifier.height(1.dp))
        Text("Servicios",
            color = Color.Black,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontSize = 20.sp
            ))
        Text("Al confirmar se generara una solicitud del servicio correspondiente",
            color = Color.Black,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontSize = 12.sp
            ))
        Spacer(Modifier.height(16.dp))

        ExposedDropdownMenuBox(
            expanded = expandedservicio,
            onExpandedChange ={expandedservicio = !expandedservicio},
            modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = servicio,
                onValueChange = {},
                readOnly = true,
                label = {Text("Servicio")},
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedservicio)
                },
                modifier = Modifier
                    .menuAnchor()
                    .height(70.dp)
                    .padding(horizontal = 60.dp)
                ,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = LightGrayBackground,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    focusedIndicatorColor = PrimaryBlue,
                    unfocusedIndicatorColor = PrimaryBlueDark,
                    cursorColor = PrimaryBlue,
                    focusedLabelColor = PrimaryBlue,
                    unfocusedLabelColor = TextColor

                )

            )
            ExposedDropdownMenu(
                expanded = expandedservicio,
                onDismissRequest = {expandedservicio = false},
                modifier = Modifier.exposedDropdownSize()
            ) { opcionServicio.forEach{opcionServicio -> DropdownMenuItem(
                text = {Text(opcionServicio)},
                onClick = {
                    servicio = opcionServicio
                    expandedservicio = false

                }
            )
            }
            }

        }
        Spacer(modifier = Modifier.height(16.dp))
        if (servicio == "Transporte"){

            ExposedDropdownMenuBox(expanded = expandedOrigen, onExpandedChange ={expandedOrigen = !expandedOrigen}, modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = Origen,
                    onValueChange = {},
                    readOnly = true,
                    label = {Text("Origen")},
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedOrigen)
                    },
                    modifier = Modifier
                        .menuAnchor()

                        .height(70.dp)
                        .padding(horizontal = 60.dp)
                    ,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = LightGrayBackground,
                        unfocusedContainerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,
                        focusedIndicatorColor = PrimaryBlue,
                        unfocusedIndicatorColor = PrimaryBlueDark,
                        cursorColor = PrimaryBlue,
                        focusedLabelColor = PrimaryBlue,
                        unfocusedLabelColor = TextColor

                    )

                )
                ExposedDropdownMenu(
                    expanded = expandedOrigen,
                    onDismissRequest = {expandedOrigen = false},
                    modifier = Modifier.exposedDropdownSize()
                ) { opcionOrigen.forEach{opcionOrigen -> DropdownMenuItem(
                    text = {Text(opcionOrigen)},
                    onClick = {
                        Origen = opcionOrigen
                        expandedUbicacion = false

                    }
                )
                }
                }

            }
            Spacer(Modifier.height(16.dp))

            ExposedDropdownMenuBox(expanded = expandedUbicacion, onExpandedChange ={expandedUbicacion = !expandedUbicacion}, modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = Ubicacion,
                    onValueChange = {},
                    readOnly = true,
                    label = {Text("Destino")},
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedUbicacion)
                    },
                    modifier = Modifier
                        .menuAnchor()

                        .height(70.dp)
                        .padding(horizontal = 60.dp)
                    ,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = LightGrayBackground,
                        unfocusedContainerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,
                        focusedIndicatorColor = PrimaryBlue,
                        unfocusedIndicatorColor = PrimaryBlueDark,
                        cursorColor = PrimaryBlue,
                        focusedLabelColor = PrimaryBlue,
                        unfocusedLabelColor = TextColor

                    )

                )
                ExposedDropdownMenu(
                    expanded = expandedUbicacion,
                    onDismissRequest = {expandedUbicacion = false},
                    modifier = Modifier.exposedDropdownSize()
                ) { if(Origen == "Posada del Peregrino Divina Providencia"){
                    destinoDivinaProvidencia.forEach{destinoDivinaProvidencia -> DropdownMenuItem(
                        text = {Text(destinoDivinaProvidencia)},
                        onClick = {
                            Ubicacion = destinoDivinaProvidencia
                            expandedUbicacion = false

                        }

                    )
                    }
                }
                else if (Origen == "Posada del Peregrino"){
                        destinoPosadadelPeregrino.forEach { destinoPosadadelPeregrino -> DropdownMenuItem(
                            text = {Text(destinoPosadadelPeregrino)},
                            onClick = {
                                Ubicacion = destinoPosadadelPeregrino
                                expandedUbicacion = false
                            }
                        ) }
                }
                else if (Origen == "Posada del Peregrino Apodaca"){
                    destinoPosadaAPodaca.forEach { destinoPosadaAPodaca -> DropdownMenuItem(
                        text = {Text(destinoPosadaAPodaca)},
                        onClick = {
                            Ubicacion = destinoPosadaAPodaca
                            expandedUbicacion = false
                        }
                    ) }
                }
                else{
                    destinoSedes.forEach { destinoSedes -> DropdownMenuItem(
                        text = {Text(destinoSedes)},
                        onClick = {
                            Ubicacion = destinoSedes
                            expandedUbicacion = false
                        }
                    ) }
                }


                }
            }


            val apiKey = "AIzaSyCxxbh57tee9SLsWvxFGR__O4cWWQ_Z02k"

            LaunchedEffect(Origen, Ubicacion) {

                if (Origen.isNotBlank() && Ubicacion.isNotBlank() && Origen != "otros" && Ubicacion != "otros") {

                    val originCoord = LOCATION_COORDINATES[Origen]
                    val destinationCoord = LOCATION_COORDINATES[Ubicacion]

                    if (!originCoord.isNullOrBlank() && !destinationCoord.isNullOrBlank()) {
                        try {
                            val retrofit = Retrofit.Builder()
                                .baseUrl("https://maps.googleapis.com/maps/api/directions/")
                                .addConverterFactory(GsonConverterFactory.create())
                                .build()

                            val api = retrofit.create(RoutesApi::class.java)

                            val response = api.getRoute(
                                origin = originCoord,
                                destination = destinationCoord,
                                apiKey = apiKey
                            )

                            val route = response.routes.firstOrNull()
                            val leg = route?.legs?.firstOrNull()
                            val poly = route?.overview_polyline?.points

                            encodedPolyline = poly
                            routeDurationText = leg?.duration?.text
                            routeDistanceText = leg?.distance?.text

                        } catch (e: Exception) {
                            println("Error al obtener la ruta: ${e.message}")
                            encodedPolyline = null
                            routeDurationText = null
                            routeDistanceText = null
                        }
                    }
                } else {
                    encodedPolyline = null
                    routeDurationText = null
                    routeDistanceText = null
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        PhoneField(
            phone = telefono,
            onPhoneChange = { telefono = it },
            selectedCountry = paisSeleccionado,
            onCountryChange = { paisSeleccionado = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 60.dp)
        )

        Spacer(Modifier.height(16.dp))

        ExposedDropdownMenuBox(expanded  = expandedHora, onExpandedChange ={expandedHora = !expandedHora}, modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = Hora,
                onValueChange = {},
                readOnly = true,
                label = { Text("Hora") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedHora)
                },
                modifier = Modifier
                    .menuAnchor()
                    .height(70.dp)
                    .padding(horizontal = 60.dp)

                ,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = LightGrayBackground,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    focusedIndicatorColor = PrimaryBlue,
                    unfocusedIndicatorColor = PrimaryBlueDark,
                    cursorColor = PrimaryBlue,
                    focusedLabelColor = PrimaryBlue,
                    unfocusedLabelColor = TextColor

                )
            )
            ExposedDropdownMenu(
                expanded = expandedHora,
                onDismissRequest = { expandedHora = false },
                modifier = Modifier.exposedDropdownSize()
            ) {
                opcionHora.forEach { opcionHora ->
                    DropdownMenuItem(
                        text = { Text(opcionHora) },
                        onClick = {
                            Hora = opcionHora
                            expandedHora = false

                        }
                    )
                }
            }



        }

        Spacer(Modifier.height(16.dp))

        var reserva by remember { mutableStateOf("") }

        DatePickerBox(
            reserva = reserva,
            onReservaChange = { nuevaFecha -> reserva = nuevaFecha }
        )

        Spacer(Modifier.height(16.dp))




        Button(
            onClick = {
                coroutineScope.launch {
                    try {

                        val telefonoCompleto = "${paisSeleccionado.dialCode}${telefono}"


                        val request = ReservaRequest(
                            servicio = servicio,
                            num_tel = telefonoCompleto,
                            fecha = reserva
                        )


                        val response = Peticiones.api.enviarReserva(request)

                        Toast.makeText(
                            context,
                            if (response.success) "Se hizo la solicitud del servicio ${servicio}" else "No se pudo agendar el servicio",
                            Toast.LENGTH_LONG
                        ).show()

                    } catch (e: Exception) {
                        Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                }
            },
        ) {
            Text("Confirmar Reserva")
        }




        if (servicio == "Transporte" && Origen.isNotBlank() && Ubicacion.isNotBlank()) {


            if (routeDurationText != null && routeDistanceText != null) {
                Spacer(Modifier.height(16.dp))


                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Column {
                        Text(
                            text = "**Distancia:** $routeDistanceText",
                            color = PrimaryBlue,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = "**Tiempo Estimado:** $routeDurationText",
                            color = PrimaryBlue,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }


            Spacer(Modifier.height(16.dp))


            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                MapWithRoute(
                    encodedPolyline = encodedPolyline,
                    origin = Origen,
                    destination = Ubicacion
                )
            }
        }

        Spacer(Modifier.height(16.dp))
        val numPersonas = personas.toIntOrNull() ?: 0
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            repeat(numPersonas) {index ->
                Text(text = "Persona ${index + 1}", style = MaterialTheme.typography.titleMedium)
                Nombre()
                Spacer(Modifier.height(30.dp))
                ExposedDropdownMenuBox(expanded = expandedSexo, onExpandedChange ={expandedSexo = !expandedSexo}, modifier = Modifier.fillMaxWidth()) {

                }
            }
        }
    }
}