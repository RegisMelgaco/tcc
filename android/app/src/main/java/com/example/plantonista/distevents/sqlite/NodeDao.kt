package com.example.plantonista.distevents.sqlite

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction

@Dao
interface NodeDao {
    @Delete
    fun deleteNodes(nodes: List<NodeEntity>)

    @Insert
    fun insertNodes(nodes: List<NodeEntity>)

    @Insert
    fun insertLocalIPs(localIPs: List<NodeLocalIP>)

    @Query("SELECT * FROM node ORDER BY updatedAt DESC LIMIT 1")
    fun getLast(): NodeEntity

    @Transaction
    @Query("SELECT * FROM node")
    fun getAll(): List<NodeWithLocalIPs>
}