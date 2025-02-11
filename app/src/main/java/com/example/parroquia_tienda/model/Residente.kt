package com.example.parroquia_tienda.model

data class Residente(
    val id: Int,
    var nombre: String,
    var numeroDeCasa: String,
    var fechaRegistro: String,
    var esPropietario: Boolean,
    var numeroDeMiembros: Int,
    val vehiculos: MutableList<Vehiculo> = mutableListOf()
)
