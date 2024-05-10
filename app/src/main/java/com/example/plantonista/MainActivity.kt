package com.example.plantonista

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.Text
import androidx.lifecycle.lifecycleScope
import com.example.plantonista.gateway.tcp.TCPClient
import com.example.plantonista.gateway.tcp.TCPServer
import com.example.plantonista.state.GivePositionEvent
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Text("Hello world!")
        }

        startService(Intent(applicationContext, TCPServer::class.java))

        Log.d("MainActivity", "onCreate")

        viewModel.sync()
    }
}