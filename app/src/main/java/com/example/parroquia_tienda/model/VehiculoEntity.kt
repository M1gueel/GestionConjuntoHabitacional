package com.example.parroquia_tienda.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "vehiculos",
    foreignKeys = [
        ForeignKey(
            entity = ResidenteEntity::class,
            parentColumns = ["id"],
            childColumns = ["residenteId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("residenteId")]
)
data class VehiculoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val residenteId: Int,
    val marca: String,
    val modelo: String,
    var placa: String,
    var numeroDeEstacionamiento: Int
)
