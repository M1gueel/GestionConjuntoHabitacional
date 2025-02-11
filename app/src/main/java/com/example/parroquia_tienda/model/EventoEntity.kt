package com.example.parroquia_tienda.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "eventos")
data class EventoEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nombre: String,
    val personaResponsable: String,
    val fechaEvento: String
)