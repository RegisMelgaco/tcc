package com.example.plantonista.gateway.ui.screen

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.plantonista.R
import com.example.plantonista.distevents.NetworkData
import com.example.plantonista.gateway.ui.theme.PlantonistaTheme
import com.example.plantonista.gateway.ui.viewmodel.TeamCreateViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun TeamCreateScreen(
    viewModel: TeamCreateViewModel = viewModel(),
    back: () -> Unit = {}
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snack = remember {
        SnackbarHostState()
    }
    PlantonistaTheme {
        Scaffold(
            snackbarHost = {SnackbarHost(hostState = snack)},
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "Plantonista",
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
            Column(
                Modifier
                    .padding(padding)
                    .fillMaxHeight()) {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxHeight()
                ) {
                    Text(
                        text = "Criar time",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(vertical = 16.dp),
                    )

                    var name by remember{ mutableStateOf("") }
                    Row(Modifier.fillMaxWidth(), Arrangement.Center) {
                        TextField(
                            value = name,
                            onValueChange = { name = it },
                            label = { Text("Nome do time") },
                            modifier = Modifier
                                .fillMaxWidth(),
                        )
                    }

                    val keyboardController = LocalSoftwareKeyboardController.current

                    Button(
                        onClick = {
                            keyboardController?.hide()

                            scope.launch(Dispatchers.IO) {
                                try {
                                    viewModel.createNetwork(context, NetworkData(name))
                                    scope.launch(Dispatchers.Main) {
                                        back()
                                    }
                                } catch (e: Exception) {
                                    Log.e("TeamCreateScreen", "failed to create network: $e")

                                    snack.showSnackbar("falha ao criar time, verifique rede ou tente outro nome")
                                }
                            }
                        }, enabled = name.length > 3,
                        modifier = Modifier
                            .padding(top = 16.dp)
                            .fillMaxWidth()
                    ) {
                        Text(text = "confirmar")
                    }
                }
            }
        }
    }
}