package com.example.plantonista.distevents.sqlite

import android.util.Log
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.plantonista.distevents.IndexedEventData
import com.example.plantonista.distevents.tcp.SyncEventsRequest

@Dao
interface EventDao {
    @Query("SELECT * FROM event")
    fun getAll(): List<EventEntity>

    @Query("SELECT * FROM event WHERE pos > :index AND author = :author AND networkName = :networkName AND type IN (:types) ORDER BY pos ASC")
    fun getWhereNewer(networkName: String, author: String, index: Int, types: List<String>): List<EventEntity>

    @Query("SELECT * FROM event WHERE pos > :index AND author = :author AND networkName = :networkName ORDER BY pos ASC")
    fun getWhereNewer(networkName: String, author: String, index: Int): List<EventEntity>

    @Insert
    fun insertAll(events: List<EventEntity>)

    @Query("SELECT networkName, author, MAX(pos) as pos FROM event WHERE networkName = :networkName GROUP BY author")
    fun getEventStreamHead(networkName: String): List<EventStreamHead>

    @Query("SELECT * FROM event WHERE pos > :index AND networkName = :networkName AND author = :author")
    fun getEventsByEventStreamHead(networkName: String, index: Int, author: String): List<EventEntity>
}

fun getNewEvents(eventDao: EventDao, request: SyncEventsRequest): List<IndexedEventData> {
    val ownHeadAuthors = eventDao.
        getEventStreamHead(request.networkName).
        map { it.author }

    val requestHeadAuthors = request.heads.map { it.author }

    val missingHeads = ownHeadAuthors.
        filter { it !in requestHeadAuthors }.
        map { com.example.plantonista.distevents.tcp.EventStreamHead(request.networkName, it, 0) }

    val heads = request.heads + missingHeads

    val res = mutableListOf<IndexedEventData>()
    for (head in heads) {
        res.addAll(
            eventDao
                .getEventsByEventStreamHead(request.networkName, head.index, head.author)
                .map { it.toIndexedData() }
        )
    }

    Log.d("ESSE", "$missingHeads $res")

    return res
}
