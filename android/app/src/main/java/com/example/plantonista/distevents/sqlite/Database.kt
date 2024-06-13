package com.example.plantonista.distevents.sqlite

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [EventEntity::class, NodeEntity::class, NodeLocalIP::class], version = 1, exportSchema = false)
abstract class Database : RoomDatabase() {
    abstract fun eventDao(): EventDao
    abstract fun nodeDao(): NodeDao

    companion object {
        const val NAME = "dist_events"
    }
}
