package com.example.parroquia_tienda.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "deudores")
data class DeudorEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nombre: String,
    val mesesDeuda: Int,
    val deudaTotal: Double
)