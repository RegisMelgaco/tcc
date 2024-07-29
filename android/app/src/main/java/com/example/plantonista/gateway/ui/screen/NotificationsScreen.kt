package com.example.plantonista.gateway.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.plantonista.R
import com.example.plantonista.event.AcceptShiftExchangeRequestEvent
import com.example.plantonista.gateway.ui.theme.PlantonistaTheme
import com.example.plantonista.gateway.ui.viewmodel.NotificationsViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import java.text.DateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(
    back: () -> Unit = {},
    viewModel: NotificationsViewModel = viewModel(),
    ) {
    viewModel.setup(LocalContext.current)

    var selectedExchange by remember { mutableStateOf<AcceptShiftExchangeRequestEvent?>(null) }
    if (selectedExchange != null)
        AlertDialog(
            title = {
                Text(text = "tem certeza que deseja confirmar a troca de plantão?")
            },
            onDismissRequest = {selectedExchange = null},
            confirmButton = {
                Button(onClick = {
                    viewModel.confirm(selectedExchange!!)
                    selectedExchange = null
                }) {
                    Text(text = "confirmar")
                }
            },
        )

    PlantonistaTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "Notificações",
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
                    verticalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxHeight()
                ) {
                    val acceptShiftExchangeRequests by viewModel.acceptShiftExchangeRequests.collectAsStateWithLifecycle(
                        listOf()
                    )

                    Column {
                        if (acceptShiftExchangeRequests.isEmpty()) {
                            Row(Modifier.fillMaxWidth(), Arrangement.Center) {
                                Text(
                                    text = "Sem notificações disponíveis",
                                    style = MaterialTheme.typography.headlineMedium
                                        .merge(TextStyle(fontWeight = FontWeight.Light))
                                )
                            }
                        } else {
                            Column {
                                for (r in acceptShiftExchangeRequests) {
                                    Row(
                                        Modifier
                                            .fillMaxWidth()
                                            .padding(top = 16.dp)
                                    ) {
                                        Row(Modifier.fillMaxWidth().padding(16.dp)) {
                                            Column {
                                                Text(text = "${viewModel.getAuthorName(r)} deseja trocar com você pelo plantão de ${formatDate(r.shiftStartUnix)}")
                                            }
                                            Column(Modifier.weight(1f), horizontalAlignment = Alignment.End) {
                                                Button(onClick = { selectedExchange = r }) {
                                                    Text(text = "confirmar")
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}