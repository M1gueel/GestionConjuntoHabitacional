package com.example.parroquia_tienda.model

data class Deudor(
    val id: Int,
    var nombre: String,
    var mesesDeuda: Int,
    var deudaTotal: Double,
    val parroquiaId: Int? = null // Optional association with a parroquia
)