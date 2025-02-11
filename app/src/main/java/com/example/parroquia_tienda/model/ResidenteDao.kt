package com.example.parroquia_tienda.model

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ResidenteDao {
    @Query("SELECT * FROM residentes")
    fun getAllResidentes(): Flow<List<ResidenteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertResidente(residente: ResidenteEntity)

    @Update
    suspend fun updateResidente(residente: ResidenteEntity)

    @Delete
    suspend fun deleteResidente(residente: ResidenteEntity)

    @Query("SELECT * FROM residentes WHERE id = :residenteId")
    fun getResidenteById(residenteId: Int): Flow<ResidenteEntity>
}