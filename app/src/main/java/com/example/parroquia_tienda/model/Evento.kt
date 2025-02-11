package com.example.parroquia_tienda.model

data class Evento(
    val id: Int,
    var nombre: String,
    var personaResponsable: String,
    var fechaEvento: String,
)