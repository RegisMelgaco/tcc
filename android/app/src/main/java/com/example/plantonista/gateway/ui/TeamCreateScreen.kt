package com.example.plantonista.gateway.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.plantonista.distevents.NetworkData

@Preview
@Composable
fun TeamCreateScreen(createNetwork: (NetworkData) -> Unit = {}) {
    Column{
        Text(
            text = "Criar time",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(vertical = 16.dp),
        )

        val name = remember {
            mutableStateOf("")
        }

        Row(Modifier.fillMaxWidth(), Arrangement.Center) {
            TextField(
                value = name.value,
                onValueChange = { it: String -> name.value = it },
                label = { Text("Nome do time") },
                modifier = Modifier.fillMaxWidth()
            )
        }

        Button(onClick = {
            createNetwork(NetworkData(name.value))
        }, enabled = name.value.length > 3
        ) {
            Text(text = "confirmar")
        }
    }
}