package com.example.plantonista.distevents.sqlite

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [EventEntity::class, NodeEntity::class, NodeLocalIP::class, NetworkEntity::class], version = 1, exportSchema = false)
abstract class Database : RoomDatabase() {
    abstract fun eventDao(): EventDao
    abstract fun nodeDao(): NodeDao
    abstract fun networkDao(): NetworkDao

    companion object {
        const val NAME = "dist_events"
    }
}
