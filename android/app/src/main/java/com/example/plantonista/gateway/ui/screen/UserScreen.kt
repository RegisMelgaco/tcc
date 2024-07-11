package com.example.plantonista.gateway.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.plantonista.gateway.ui.theme.PlantonistaTheme
import com.example.plantonista.gateway.ui.viewmodel.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun UserScreen(
    viewModel: UserViewModel = viewModel(),
    onConfirm : (username: String) -> Unit = {},
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    LaunchedEffect(scope) {
        viewModel.loadLastUsername(context)
    }

    PlantonistaTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "Plantonista",
                            style = MaterialTheme.typography.headlineMedium
                        )
                    },
                )
            }
        ) { padding ->
            Column(Modifier.padding(padding)) {
                Column(Modifier.padding(horizontal = 16.dp)) {
                    Text(
                        text = "Usuário",
                        style = MaterialTheme.typography.headlineLarge,
                        modifier = Modifier.padding(vertical = 16.dp)
                    )

                    var username by remember { mutableStateOf(viewModel.loadLastUsername(context)) }
                    TextField(
                        value = username,
                        onValueChange = {
                            username = it
                        },
                        label = { Text("email") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Button(
                        onClick = {
                            viewModel.saveUsername(context, username)
                            onConfirm(username)
                        },
                        enabled = username.length > 3,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp)
                    ) {
                        Text(text = "confirmar")
                    }
                }
            }
        }
    }
}