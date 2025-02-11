package com.example.parroquia_tienda.model

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        ResidenteEntity::class,
        VehiculoEntity::class,
        EventoEntity::class,
        DeudorEntity::class
    ],
    version = 4,
    exportSchema = false
)
abstract class ConjuntoDatabase : RoomDatabase() {
    abstract fun residenteDao(): ResidenteDao
    abstract fun vehiculoDao(): VehiculoDao
    abstract fun eventoDao(): EventoDao
    abstract fun deudorDao(): DeudorDao

    companion object {
        @Volatile
        private var INSTANCE: ConjuntoDatabase? = null

        fun getDatabase(context: Context): ConjuntoDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ConjuntoDatabase::class.java,
                    "conjunto_database"
                )
                    .fallbackToDestructiveMigration() // Add this for version update
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

