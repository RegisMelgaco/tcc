package com.example.plantonista.gateway.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.plantonista.R
import com.example.plantonista.gateway.ui.theme.PlantonistaTheme
import com.example.plantonista.gateway.ui.viewmodel.MemberCreateViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemberCreateScreen(
    viewModel: MemberCreateViewModel = viewModel(),
    back: () -> Unit = {},
) {
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    PlantonistaTheme {
        Scaffold(
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "Cadastrar Membro",
                            style = MaterialTheme.typography.headlineMedium
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = back) {
                            Icon(
                                modifier = Modifier.padding(horizontal = 8.dp),
                                painter = painterResource(R.drawable.baseline_arrow_back_24),
                                contentDescription = "voltar a tela anterior"
                            )
                        }
                    }
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

                    val keyboardController = LocalSoftwareKeyboardController.current

                    Button(
                        onClick = {
                            keyboardController?.hide()

                            viewModel.createMember(name, email, back) {
                                scope.launch {
                                    snackbarHostState.showSnackbar(it)
                                }
                            }
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