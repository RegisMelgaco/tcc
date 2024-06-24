package com.example.plantonista.distevents.sqlite

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface EventDao {
    @Query("SELECT * FROM event")
    fun getAll(): List<EventEntity>

    @Query("SELECT * FROM event WHERE createdAt >= :createdAt ORDER BY createdAt ASC")
    fun getWhereNewer(createdAt: Long): List<EventEntity>

    @Insert
    fun insertAll(events: List<EventEntity>)

    @Query("SELECT author, createdAt FROM event GROUP BY author HAVING MAX(createdAt)")
    fun getEventStreamHead(): List<EventStreamHead>

    @Query("SELECT * FROM event WHERE createdAt = :createdAt AND author = :author")
    fun getEventsByEventStreamHead(createdAt: Long, author: String): List<EventEntity>
}
