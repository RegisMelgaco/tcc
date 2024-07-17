package com.example.plantonista.gateway.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.plantonista.R
import com.example.plantonista.gateway.ui.viewmodel.ShiftsViewModel

@Composable
fun ShiftsScreen(
    viewModel: ShiftsViewModel = viewModel(),
    navigateShiftCreate: () -> Unit = {},
) {
    val scrollState = rememberScrollState()
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
                .fillMaxHeight()
                .verticalScroll(scrollState)
        ) {
            val members by viewModel.shifts.collectAsStateWithLifecycle()

            Column {
                if (members.isEmpty()) {
                    Row(Modifier.fillMaxWidth(), Arrangement.Center) {
                        Text(
                            text = "Sem plantões cadastrados",
                            style = MaterialTheme.typography.headlineMedium
                                .merge(TextStyle(fontWeight = FontWeight.Light))
                        )
                    }
                }
            }
        }
    }
}