package com.example.plantonista.viewmodel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.plantonista.distevents.EventStreamer
import com.example.plantonista.event.EventFactory
import com.example.plantonista.event.PlantonistaEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel: ViewModel() {
    private var streamer: EventStreamer<PlantonistaEvent>? = null
    val author = MutableLiveData("")

    fun sync(context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            val streamer = getStreamer(context)

            streamer.replayEvents(0)
            streamer.syncEvents(5_000)
        }
    }

    private val handler = { _: PlantonistaEvent -> }

    private fun getStreamer(context: Context): EventStreamer<PlantonistaEvent> {
        val s = streamer ?: EventStreamer(
            context,
            EventFactory(),
            handler,
            "http://192.168.1.254:3000/",
            author.value ?: "",
        )
        if (streamer != null) {
            streamer = s
        }

        return s
    }

    private fun now() = System.currentTimeMillis()

    companion object {
        private val TAG = MainViewModel::class.java.simpleName
    }
}