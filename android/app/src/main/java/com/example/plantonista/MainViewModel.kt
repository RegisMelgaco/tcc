package com.example.plantonista

import android.content.Context
import android.os.Build
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.plantonista.distevents.EventStreamer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel: ViewModel() {
    private var streamer: EventStreamer<MathEvent>? = null
    private val author = Build.BRAND

    val count: LiveData<Int>
        get() = _count
    private val _count by lazy {
        MutableLiveData(0)
    }

    fun add(context: Context, value: Int) {
        getStreamer(context).add(AddEvent(value, now(), author))
    }

    fun sync(context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            val streamer = getStreamer(context)

            streamer.replayEvents(0)
            streamer.syncEvents(5_000)
        }
    }

    private val handler = { event: MathEvent ->
        when (event.type()) {
            MathEventType.Add -> {
                _count.value = _count.value?.plus((event as AddEvent).value)
            }
        }
    }

    private fun getStreamer(context: Context): EventStreamer<MathEvent> {
        val s = streamer ?: EventStreamer(
            context,
            EventFactory(),
            handler,
            "http://192.168.1.254:3000/",
            author,
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