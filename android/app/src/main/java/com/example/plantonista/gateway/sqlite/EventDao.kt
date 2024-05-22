package com.example.plantonista.gateway.sqlite

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface EventDao {
    @Query("SELECT * FROM event")
    fun getAll(): List<EventEntity>

    @Insert
    fun insertAll(vararg events: EventEntity)
}
