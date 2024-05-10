package com.example.plantonista

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.plantonista.gateway.tcp.TCPClient
import com.example.plantonista.state.GivePositionEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel: ViewModel() {
    fun sync() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                Log.d("MainViewModel", "sync")

                val newEvents = TCPClient(5_000).sync("127.0.0.1", 2001, arrayOf(GivePositionEvent("rodolfo", 2)))

                Log.d(TAG, "new events: ${newEvents.toList()}")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    companion object {
        private val TAG = MainViewModel::class.java.simpleName
    }
}