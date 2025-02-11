package com.example.parroquia_tienda.model

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface EventoDao {
    @Query("SELECT * FROM eventos")
    fun getAllEventos(): Flow<List<EventoEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvento(evento: EventoEntity)

    @Update
    suspend fun updateEvento(evento: EventoEntity)

    @Delete
    suspend fun deleteEvento(evento: EventoEntity)

    @Query("SELECT * FROM eventos WHERE id = :eventoId")
    fun getEventoById(eventoId: Int): Flow<EventoEntity>
}