package com.example.plantonista.distevents

import android.content.Context
import android.os.SystemClock
import android.util.Log
import androidx.room.Room
import com.example.plantonista.distevents.sqlite.Database
import com.example.plantonista.distevents.sqlite.EventDao
import com.example.plantonista.distevents.sqlite.toEntity
import com.example.plantonista.distevents.tcp.EventStreamHead

interface Event<T> {
     fun toData(): EventData
     fun getType(): T
     fun author(): String
     fun getCreatedAt(): Long
}

interface EventFactory<T> {
    fun fromData(data: EventData): Event<T>
}

class EventStreamer<T>(
    context: Context,
    val networkName: String,
    val author: String,
    private val factory: EventFactory<T>,
    private val validator: EventValidator<T>
) {
    private val eventDao: EventDao
    private val othersHeads = mutableMapOf<String, EventStreamHead>()
    private val ownEvents = mutableListOf<IndexedEventData>()
    private var handler: EventHandler<T>? = null

    val networkData: NetworkData

    init {
        val db = Room.databaseBuilder(context, Database::class.java, "dist_events").build()
        eventDao = db.eventDao()
        val networkDao = db.networkDao()

        networkData = networkDao.getByName(networkName).toData()

        ownEvents.addAll(eventDao.getWhereNewer(networkName, author, -1).map { it.toIndexedData() })

        Log.i(TAG, "created with ownEvents $ownEvents")

        updateOthersHeads()
    }

    private fun updateOthersHeads() {
        val stored = eventDao.getEventStreamHead(networkName)
        for (h in stored.filter { it.author != author }) {
            if (!othersHeads.containsKey(h.author)) {
                othersHeads[h.author] = EventStreamHead(networkName, h.author, -1)
            }
        }

        Log.i(TAG, "updated other's heads $othersHeads")
    }

    fun stream(handler: EventHandler<T>, delayMS: Long = DEFAULT_DELAY) {
        if (this.handler != null) return

        this.handler = handler

        val types = handler.keys.map { it.toString() }

        Log.i(TAG, "start streaming with types $types and own events $ownEvents")

        for (d in ownEvents) {
            val event = factory.fromData(d.data)
            handler[event.getType()]?.let { it(event) }
        }

        while (true) {
            updateOthersHeads()

            for(h in othersHeads.values) {
                val events = eventDao.getWhereNewer(networkName, h.author, h.index, types)

                for (entity in events) {
                    val e = factory.fromData(entity.toIndexedData().data)

                    val t = e.getType()
                    handler[t]?.let { it(e) }

                    othersHeads[h.author]?.index = entity.pos
                }
            }

            SystemClock.sleep(delayMS)
        }
    }

    fun submit(event: Event<T>): Boolean {
        if (validator[event.getType()]?.let { it(event) } == false) {
            Log.i(TAG, "submitted event is invalid")

            return false
        }

        val indexedEvent = IndexedEventData(event.toData(), ownEvents.size + 1)

        ownEvents += indexedEvent
        eventDao.insertAll(listOf(indexedEvent.toEntity()))

        if (handler != null) {
            val t = event.getType()
            handler?.get(t)?.let {
                it(event)
            }
        } else {
            ownEvents.add(indexedEvent)
        }

        return true
    }

    companion object {
        const val DEFAULT_DELAY = 60_000L
        private val TAG = EventStreamer::class.simpleName
    }
}

typealias EventHandler<T> = Map<T, (Event<T>) -> Unit>
typealias EventValidator<T> = Map<T, (Event<T>) -> Boolean>