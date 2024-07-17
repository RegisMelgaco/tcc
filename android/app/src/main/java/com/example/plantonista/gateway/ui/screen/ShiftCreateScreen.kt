package com.example.plantonista.gateway.ui.screen

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccessTime
import androidx.compose.material.icons.rounded.CalendarToday
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.plantonista.R
import com.example.plantonista.entity.Member
import com.example.plantonista.gateway.ui.theme.PlantonistaTheme
import com.example.plantonista.gateway.ui.viewmodel.ShiftCreateViewModel
import kotlinx.coroutines.launch
import java.text.DateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun ShiftCreateScreen(
    viewModel: ShiftCreateViewModel = viewModel(),
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
                            text = "Criar Plantão",
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

                    val selectedMember = remember { mutableIntStateOf(0) }
                    val members = viewModel.members.collectAsState()
                    MemberSelect(members.value, selectedMember)

                    val dateStart = rememberDatePickerState(System.currentTimeMillis())
                    StartDate(state = dateStart)

                    val timeStart = rememberTimePickerState(is24Hour = true)
                    StartTime(state = timeStart)

                    var durationHours by remember { mutableIntStateOf(12) }
                    TextField(
                        value = durationHours.toString(),
                        onValueChange = { durationHours = it.toInt() },
                        label = { Text(text = "duração em horas") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .padding(top = 8.dp)
                            .fillMaxWidth()
                    )

                    val keyboardController = LocalSoftwareKeyboardController.current
                    Button(
                        onClick = {
                            keyboardController?.hide()

                            val start = dateStart.selectedDateMillis?.let {
                                it / 1000 + (timeStart.minute * 60) + (timeStart.hour * 3600)
                            } ?: 0 // using unix time

                            viewModel.createShift(selectedMember.intValue, start, durationHours * 60, back) {
                                scope.launch {
                                    snackbarHostState.showSnackbar(it)
                                }
                            }
                        },
                        enabled = dateStart.selectedDateMillis != null,
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemberSelect(
    members: List<Member>,
    selectedMemberIndex: MutableState<Int>,
) {
    var expanded by remember { mutableStateOf(false) }
    var i by remember { selectedMemberIndex }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp)
    ) {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = {
                expanded = !expanded
            }
        ) {
            TextField(
                value = "${members.getOrNull(i)?.name} | ${members.getOrNull(i)?.email}",
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                for(j in members.indices) {
                    val member = members.getOrNull(j)
                    DropdownMenuItem(
                        text = { Text(text = "${member?.name} | ${member?.email}") },
                        onClick = {
                            i = j
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StartDate(state: DatePickerState) {
    val showDialog = remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp)
    ) {
        TextField(
            value = DateFormat.getDateInstance(DateFormat.SHORT, Locale("PT", "br")).format(Date(state.selectedDateMillis!! + (3 * 3600000))).toString(),
            onValueChange = {},
            readOnly = true,
            trailingIcon = {
                Icon(
                    imageVector = Icons.Rounded.CalendarToday,
                    contentDescription = "Icone de Data",
                    modifier = Modifier.size(24.dp)
                )
           },
            interactionSource = remember { MutableInteractionSource() }
                .also { interactionSource ->
                    LaunchedEffect(interactionSource) {
                        interactionSource.interactions.collect {
                            if (it is PressInteraction.Release) {
                                showDialog.value = true
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
        )
    }

    when {
        showDialog.value -> {
            DatePickerDialog(
                onDismissRequest = { showDialog.value = false },
                confirmButton = {
                    Button(onClick = {
                        showDialog.value = false
                    }) {
                        Text(text = "OK")
                    }
                },
                dismissButton = {
                    Button(onClick = {
                        showDialog.value = false
                    }) {
                        Text(text = "Cancel")
                    }
                }
            ) {
                DatePicker(state = state)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StartTime(state: TimePickerState) {
    val showDialog = remember { mutableStateOf(false) }

    val time = Date((state.hour * 3_600_000L) + (state.minute * 60_000))

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp)
    ) {
        TextField(
            value = DateFormat.getTimeInstance(DateFormat.SHORT, Locale("PT", "br")).format(time.time  + (3 * 3600000)),
            onValueChange = {},
            readOnly = true,
            trailingIcon = {
                Icon(
                    imageVector = Icons.Rounded.AccessTime,
                    contentDescription = "Icone de Horário",
                    modifier = Modifier.size(24.dp)
                )
            },
            interactionSource = remember { MutableInteractionSource() }
                .also { interactionSource ->
                    LaunchedEffect(interactionSource) {
                        interactionSource.interactions.collect {
                            if (it is PressInteraction.Release) {
                                showDialog.value = true
                            }
                        }
                    }
                },
            modifier = Modifier.fillMaxWidth()
        )
    }

    when {
        showDialog.value -> {
            BasicAlertDialog(
                onDismissRequest = { showDialog.value = false },
            ) {
                Card {
                    Column {
                        Row{
                            Spacer(modifier = Modifier.weight(1f))
                            TimePicker(state = state, Modifier.padding(top = 24.dp))
                            Spacer(modifier = Modifier.weight(1f))
                        }
                        Row(
                            horizontalArrangement = Arrangement.End,
                        ) {
                            Button(modifier = Modifier
                                .padding(horizontal = 16.dp)
                                .padding(bottom = 8.dp)
                                .weight(1f),
                                onClick = {
                                    showDialog.value = false
                                }) {
                                Text(text = "OK")
                            }
                        }
                    }
                }
            }
        }
    }
}