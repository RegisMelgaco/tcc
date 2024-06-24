package com.example.plantonista.distevents.sqlite

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface NetworkDao {
    @Insert
    fun create(network: NetworkEntity)

    @Query("SELECT * FROM network")
    fun getAll(): List<NetworkEntity>

    @Query("SELECT * FROM network WHERE name = :name")
    fun getByName(name: String): NetworkEntity
}