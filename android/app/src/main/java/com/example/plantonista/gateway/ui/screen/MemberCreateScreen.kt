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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.plantonista.gateway.ui.theme.PlantonistaTheme
import com.example.plantonista.gateway.ui.viewmodel.MemberCreateViewModel
import com.example.plantonista.state.EmailInUseException

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemberCreateScreen(
    viewModel: MemberCreateViewModel = viewModel(),
    onConfirm : (String?) -> Unit = {},
) {
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
                    var name by remember { mutableStateOf("") }
                    TextField(
                        value = name,
                        onValueChange = {
                            name = it
                        },
                        label = { Text("name") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    var email by remember { mutableStateOf("") }
                    TextField(
                        value = email,
                        onValueChange = {
                            email = it
                        },
                        label = { Text("email") },
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                    )

                    Button(
                        onClick = {
                            var errorMsg: String? = null
                            try {
                                viewModel.createMember(name, email)
                            } catch (e: EmailInUseException) {
                                errorMsg = "email escolhido já está em uso"
                            }

                            onConfirm(errorMsg)
                        },
                        enabled = name.length > 3 && email.length > 3,
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