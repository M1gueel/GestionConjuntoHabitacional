package com.example.parroquia_tienda.model


import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface VehiculoDao {
    @Query("SELECT * FROM vehiculos WHERE residenteId = :residenteId")
    fun getVehiculosByResidente(residenteId: Int): Flow<List<VehiculoEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVehiculo(vehiculo: VehiculoEntity)

    @Update
    suspend fun updateVehiculo(vehiculo: VehiculoEntity)

    @Delete
    suspend fun deleteVehiculo(vehiculo: VehiculoEntity)

    @Query("SELECT * FROM vehiculos WHERE id = :vehiculoId LIMIT 1")
    fun getVehiculoById(vehiculoId: Int): Flow<VehiculoEntity>

}
