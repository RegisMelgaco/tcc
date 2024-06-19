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
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp


@Preview
@Composable
fun TeamListScreen(navigateTeamCreate: () -> Unit) {
    Column {
        Column(
            modifier = Modifier.defaultMinSize(minHeight = 400.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Row(Modifier.fillMaxWidth(), Arrangement.Center) {
                Text(
                    text = "Sem times disponíveis",
                    style = MaterialTheme.typography.headlineMedium
                        .merge(TextStyle(fontWeight = FontWeight.Light))
                )
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
