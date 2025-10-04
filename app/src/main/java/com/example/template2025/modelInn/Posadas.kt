package com.example.template2025.modelInn
import com.example.template2025.components.PosadasCard

data class Posadas(
    val id: Int,
    val name: String,
    val dir: String,
    val tel: String,
    val url : String,

)

fun getPosadas(): List<Posadas> = listOf(
    Posadas(1,"Posada del Peregrino","Simón Bolívar No. 190 Sur,\n Col. Chepevera esq. con Calle Robertson, \n Monterrey, N.L.", "(81)13.40.22.08 y (81) 83. 46.35.74","https://www.caritas.org.mx/wp-content/uploads/2018/06/caritas-posada-del-peregrino.jpg",),
    Posadas(2,"Posada del Peregrino \"Divina Providencia\"","Florencio Antillón No. 1221-A\n entre Matamoros y Platón Sánchez, Centro de Monterrey.","Tel: (81) 83.40.43.05 y (81) 83. 43. 80.19","https://www.caritas.org.mx/wp-content/uploads/2018/06/caritas-la-divina-providencia.jpg"))
