package com.example.plantonista.gateway.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.plantonista.gateway.ui.viewmodel.TeamMemberListViewModel


@Composable
fun TeamMemberListScreen(
    viewModel: TeamMemberListViewModel = viewModel(),
    navigateMemberCreate: () -> Unit = {},
) {
    Column(
        verticalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxHeight()
    ) {
        val members by viewModel.members.collectAsStateWithLifecycle()

        Column {
            if (members.isEmpty()) {
                Row(Modifier.fillMaxWidth(), Arrangement.Center) {
                    Text(
                        text = "Sem membros disponíveis",
                        style = MaterialTheme.typography.headlineMedium
                            .merge(TextStyle(fontWeight = FontWeight.Light))
                    )
                }
            } else {
                Column {
                    for (m in members) {
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .padding(top = 16.dp)
                        ) {
                            Text(
                                AnnotatedString("${m.name} | ${m.email}"),
                                style = MaterialTheme.typography.headlineMedium
                                    .merge(TextStyle(fontWeight = FontWeight.Light)),
                            )
                        }
                    }
                }
            }
        }

        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.padding(bottom = 32.dp)
        ) {
            Button(
                onClick = navigateMemberCreate,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            ) {
                Text(text = "Criar membros")
            }
        }
    }
}