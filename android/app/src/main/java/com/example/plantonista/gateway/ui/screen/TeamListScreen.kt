package com.example.plantonista.gateway.ui.screen

import androidx.compose.foundation.clickable
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
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.plantonista.R
import com.example.plantonista.distevents.NetworkData
import com.example.plantonista.gateway.ui.theme.PlantonistaTheme
import com.example.plantonista.gateway.ui.viewmodel.TeamListViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun TeamListScreen(
    viewModel: TeamListViewModel = viewModel(),
    back: () -> Unit = {},
    navigateTeamCreate: () -> Unit = {},
    navigateTeam: (NetworkData) -> Unit = {},
) {
    viewModel.updateTeams(LocalContext.current)

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
            Column(Modifier.padding(padding).fillMaxHeight()) {
                Column(
                    verticalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.padding(horizontal = 16.dp)
                        .fillMaxHeight()
                ) {
                    val teams by viewModel.teams.collectAsStateWithLifecycle()

                    Column {
                        if (teams.isEmpty()) {
                            Row(Modifier.fillMaxWidth(), Arrangement.Center) {
                                Text(
                                    text = "Sem times disponíveis",
                                    style = MaterialTheme.typography.headlineMedium
                                        .merge(TextStyle(fontWeight = FontWeight.Light))
                                )
                            }
                        } else {
                            Column {
                                for (t in teams) {
                                    Row(
                                        Modifier
                                            .fillMaxWidth()
                                            .padding(top = 16.dp)
                                    ) {
                                        Text(
                                            AnnotatedString(t.name),
                                            style = MaterialTheme.typography.headlineMedium
                                                .merge(TextStyle(fontWeight = FontWeight.Light)),
                                            modifier = Modifier.clickable {
                                                navigateTeam(t)
                                            }
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
                            onClick = navigateTeamCreate,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 16.dp)
                        ) {
                            Text(text = "Criar time")
                        }
                    }
                }
            }
        }
    }
}
