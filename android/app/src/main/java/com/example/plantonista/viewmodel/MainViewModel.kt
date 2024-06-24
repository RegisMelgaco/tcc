package com.example.plantonista.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.plantonista.distevents.EventStreamer
import com.example.plantonista.distevents.Network
import com.example.plantonista.distevents.NetworkData
import com.example.plantonista.distevents.Tracker
import com.example.plantonista.event.EventFactory
import com.example.plantonista.event.PlantonistaEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel: ViewModel() {
    val author = MutableLiveData("")
    val selectedNetwork = MutableLiveData<NetworkData>()

    fun syncEvents(context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            val streamer = getStreamer(context)

            streamer.replayEvents(0)
            streamer.syncEvents(getNetwork(context), 5_000)
        }
    }

    fun createNetwork(context: Context, data: NetworkData) {
        CoroutineScope(Dispatchers.IO).launch {
            getTracker(context).createNetwork(data)
        }
    }

    fun listNetworks(context: Context): LiveData<List<NetworkData>> {
        val ld = MutableLiveData<List<NetworkData>>()

        viewModelScope.launch(Dispatchers.IO) {
            val nets = getTracker(context).listNetworks()

            viewModelScope.launch(Dispatchers.Main) {
                ld.value = nets
            }
        }

        return ld
    }

    private val handler = { _: PlantonistaEvent -> }

    private var streamer: EventStreamer<PlantonistaEvent>? = null
    private fun getStreamer(context: Context): EventStreamer<PlantonistaEvent> {
        val s = streamer ?: EventStreamer(
            context,
            EventFactory(),
            handler
        )
        if (streamer != null) {
            streamer = s
        }

        return s
    }

    private var tracker: Tracker? = null
    private fun getTracker(context: Context): Tracker {
        val t = tracker ?: Tracker(context, "http://192.168.1.254:3000/")
        if (tracker == null) {
            tracker = t
        }

        return t
    }

    private var network: Network? = null
    private fun getNetwork(context: Context): Network {
        val n = network ?: getTracker(context).getNetwork(selectedNetwork.value!!.name, author.value!!)
        if (network == null) {
            network = n
        }

        return n
    }

    companion object {
        private val TAG = MainViewModel::class.java.simpleName
    }
}