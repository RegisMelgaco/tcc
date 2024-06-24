package com.example.plantonista.gateway.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun UserScreen(
    author: LiveData<String> = liveData {},
    onChangeAuthor: (String) -> Unit = {},
    navigateTeamList : () -> Unit = {},
) {
    Column{
        val email: String by author.observeAsState("")

        Text(
            text = "Usuário",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(vertical = 16.dp)
        )

        TextField(
            value = email,
            onValueChange = onChangeAuthor,
            label = { -> Text("email") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = navigateTeamList,
            enabled = email.length > 3,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            Text(text = "confirmar")
        }
    }
}