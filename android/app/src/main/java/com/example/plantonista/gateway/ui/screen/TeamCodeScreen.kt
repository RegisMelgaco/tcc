package com.example.plantonista.gateway.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.plantonista.R
import com.example.plantonista.gateway.ui.remember.rememberQrBitmapPainter
import com.example.plantonista.gateway.ui.theme.PlantonistaTheme
import com.example.plantonista.gateway.ui.viewmodel.TeamCodeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeamCodeScreen(
    back: () -> Unit = {},
    viewModel: TeamCodeViewModel = viewModel()
) {
    val snackbarHostState = remember { SnackbarHostState() }

    PlantonistaTheme {
        Scaffold(
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "Entrar no Time",
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
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(padding)
                    .fillMaxWidth()
                    .fillMaxHeight(),
                ) {
                val bitmap = rememberQrBitmapPainter(content = viewModel.qrCodeData)

                Image(painter = bitmap, contentDescription = "QR code para ingressar no time")
            }
        }
    }
}
