package com.example.parroquia_tienda.model

data class Vehiculo(
    val id: Int,
    var marca: String,
    var modelo: String,
    var placa: String,
    var color: String,
    var numeroDeEstacionamiento: Int
)
