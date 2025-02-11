package com.example.parroquia_tienda.model

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface DeudorDao {
    @Query("SELECT * FROM deudores")
    fun getAllDeudores(): Flow<List<DeudorEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDeudor(deudor: DeudorEntity)

    @Update
    suspend fun updateDeudor(deudor: DeudorEntity)

    @Delete
    suspend fun deleteDeudor(deudor: DeudorEntity)

    @Query("SELECT * FROM deudores WHERE id = :deudorId")
    fun getDeudorById(deudorId: Int): Flow<DeudorEntity>
}