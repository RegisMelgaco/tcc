package com.example.plantonista.gateway.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.plantonista.R
import com.example.plantonista.gateway.ui.viewmodel.ShiftsViewModel
import java.text.DateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ShiftsScreen(
    viewModel: ShiftsViewModel = viewModel(),
    navigateShiftCreate: () -> Unit = {},
) {
    viewModel.setup(LocalContext.current)

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = navigateShiftCreate,
            ) {
                Icon(
                    modifier = Modifier.padding(horizontal = 8.dp),
                    painter = painterResource(R.drawable.baseline_add_24),
                    contentDescription = "Add shift"
                )
            }
        }
    ) { padding ->
        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .padding(padding)
        ) {
            val shifts by viewModel.shifts.collectAsState()

            if (shifts.isEmpty()) {
                Row(Modifier.fillMaxWidth(), Arrangement.Center) {
                    Text(
                        text = "Sem Plantões Disponíveis",
                        style = MaterialTheme.typography.headlineMedium
                            .merge(TextStyle(fontWeight = FontWeight.Light))
                    )
                }
            } else {
                val initialIndex by viewModel.initialIndex.collectAsState(0)
                val lazyListState = rememberLazyListState(initialIndex)

                LazyColumn(state = lazyListState) {
                    items(shifts.size, key = { shifts[it].startUnix }) { i ->
                        val shift = shifts[i]
                        val start = formatDate(shift.startMill)
                        val end = formatDate(shift.endMill)
                        val member = viewModel.member(shift)
                        val context = LocalContext.current

                        val alertTitle = when {
                            viewModel.canOpenExchangeRequest(shift) -> "Deseja ofertar seu plantão a outro membro?"
                            viewModel.canCancelExchangeRequest(shift) -> "Deseja cancelar ofertar do seu plantão a outro membro?"
                            viewModel.canAcceptExchangeRequest(shift) -> "Deseja receber o plantão de outro membro?"
                            else -> ""
                        }
                        val onConfirmAlert: () -> Unit = when {
                            viewModel.canOpenExchangeRequest(shift) -> ({ viewModel.openExchangeRequest(context, shift) })
                            viewModel.canCancelExchangeRequest(shift) -> ({ viewModel.cancelExchangeRequest(context, shift) })
                            viewModel.canAcceptExchangeRequest(shift) -> ({ viewModel.acceptExchangeRequest(context, shift) })
                            else -> ({})
                        }

                        var isOpenExchangeDialog by remember { mutableStateOf(false) }
                        if (isOpenExchangeDialog)
                            AlertDialog(
                                title = {
                                    Text(text = alertTitle)
                                },
                                onDismissRequest = {isOpenExchangeDialog = false},
                                confirmButton = {
                                    Button(onClick = {
                                        onConfirmAlert()
                                        isOpenExchangeDialog = false
                                    }) {
                                        Text(text = "confirmar")
                                    }
                                },
                            )

                        Row(Modifier.padding(vertical = 8.dp, horizontal = 16.dp)) {
                            Column {
                                Text(text = "plant.:")
                                Text(text = "início:")
                                Text(text = "fim:")
                            }
                            Column(Modifier.padding(start = 4.dp)) {
                                Text(text = member)
                                Text(text = start)
                                Text(text = end)
                            }

                            val shiftButtonText = when {
                                viewModel.canOpenExchangeRequest(shift) -> "ofertar"
                                viewModel.canCancelExchangeRequest(shift) -> "cancelar oferta"
                                viewModel.canAcceptExchangeRequest(shift) -> "receber"
                                else -> ""
                            }

                            if (shiftButtonText.isNotBlank())
                                Column(horizontalAlignment = Alignment.End, modifier = Modifier.weight(1f)) {
                                    Button(onClick = {isOpenExchangeDialog = true}) {
                                        Text(text = shiftButtonText)
                                    }
                                }
                        }
                    }
                }
            }
        }
    }
}

fun formatDate(v: Long): String =
    DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, Locale("PT", "br")).format(Date(v))
