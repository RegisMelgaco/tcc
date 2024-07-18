package com.example.plantonista.gateway.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
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
                        text = "Sem times disponíveis",
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

                        Row(Modifier.padding(vertical = 8.dp, horizontal = 16.dp)) {
                            Column {
                                Text(text = "plant.:")
                                Text(text = "início:")
                                Text(text = "fim:")
                            }
                            Column {
                                Text(text = member)
                                Text(text = start)
                                Text(text = end)
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
