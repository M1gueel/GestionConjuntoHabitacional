package com.example.parroquia_tienda.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "residentes")
data class ResidenteEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nombre: String,
    val numeroDeCasa: String,
    val fechaRegistro: String,
    val esPropietario: Boolean,
    var numeroDeMiembros: Int
)