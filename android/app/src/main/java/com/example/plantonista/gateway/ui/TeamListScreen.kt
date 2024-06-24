package com.example.plantonista.gateway.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.example.plantonista.distevents.NetworkData


@Preview
@Composable
fun TeamListScreen(teams: LiveData<List<NetworkData>> = liveData{}, navigateTeamCreate: () -> Unit = {}) {
    Column {
        val teams = teams.observeAsState(listOf())

        Column(
            modifier = Modifier.defaultMinSize(minHeight = 400.dp),
            verticalArrangement =
            if(teams.value.isEmpty())
                Arrangement.Center
                else Arrangement.Top
        ) {
            if (teams.value.isEmpty()) {
                Row(Modifier.fillMaxWidth(), Arrangement.Center) {
                    Text(
                        text = "Sem times disponíveis",
                        style = MaterialTheme.typography.headlineMedium
                            .merge(TextStyle(fontWeight = FontWeight.Light))
                    )
                }
            } else {
                for (t in teams.value)
                    Row(Modifier.fillMaxWidth(), Arrangement.Center) {
                        Text(
                            text = t.name,
                            style = MaterialTheme.typography.headlineMedium
                                .merge(TextStyle(fontWeight = FontWeight.Light))
                        )
                    }
            }
        }

        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Button(onClick = navigateTeamCreate ) {
                    Text(text = "Criar time")
                }
            }
        }
    }
}
