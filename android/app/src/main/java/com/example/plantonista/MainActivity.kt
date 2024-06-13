package com.example.plantonista

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import com.example.plantonista.distevents.EventStreamer


class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.sync(applicationContext)
        
        setContent {
            val count = viewModel.count.observeAsState()

            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                Column {
                    Text("Counter")
                    count.value?.let {
                        Text(count.value.toString())
                    }

                    Button(onClick = { viewModel.add(applicationContext, 1) }) {
                        Text(text = "Add 1")
                    }
                }
            }
        }

        startService(Intent(applicationContext, EventStreamer.getEventService()))

        viewModel.sync(applicationContext)
    }
}