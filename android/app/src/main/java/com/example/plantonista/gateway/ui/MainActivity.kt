package com.example.plantonista.gateway.ui

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.plantonista.gateway.ui.ui.theme.PlantonistaTheme
import com.example.plantonista.viewmodel.MainViewModel
import com.google.android.material.R

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.author.value = getPreferences(Context.MODE_PRIVATE).getString(AUTHOR_KEY, "")

        setContent {
            val navController = rememberNavController()

            val hasBack = remember {
                val state = mutableStateOf(false)

                navController.addOnDestinationChangedListener { controller, _, _ ->
                    state.value = controller.previousBackStackEntry != null
                }

                return@remember state
            }

            PlantonistaTheme {
                Scaffold(
                    topBar = {
                        TopAppBar(title = {
                            Text(
                                    text = "Plantonista",
                                    style = MaterialTheme.typography.headlineMedium
                            )
                        },
                        navigationIcon = {
                            if (hasBack.value) {
                                IconButton(onClick = { navController.popBackStack() }) {
                                    Icon(
                                        modifier = Modifier.padding(horizontal = 8.dp),
                                        painter = painterResource(R.drawable.ic_arrow_back_black_24),
                                        contentDescription = "voltar a tela anterior"
                                    )
                                }
                            }
                        })
                    }
                ) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .padding(innerPadding)
                            .padding(all = 16.dp)
                            .fillMaxWidth(),
                    ) {
                        NavHost(
                            navController = navController,
                            startDestination = USER_ROUTE,
                        ) {
                            composable(USER_ROUTE) {
                                UserScreen(
                                    author = viewModel.author,
                                    onChangeAuthor = { viewModel.author.value = it },
                                    navigateTeamList = {
                                        with(getPreferences(MODE_PRIVATE).edit()) {
                                            putString(AUTHOR_KEY, viewModel.author.value)
                                            apply()
                                        }

                                        navController.navigate(TEAM_LIST_ROUTE)
                                    },
                                )
                            }

                            composable(TEAM_LIST_ROUTE) {
                                TeamListScreen(
                                    teams = viewModel.listNetworks(applicationContext),
                                    navigateTeamCreate = { navController.navigate(TEAM_CREATE_ROUTE) },
                                )
                            }

                            composable(TEAM_CREATE_ROUTE) {
                                TeamCreateScreen(
                                    createNetwork = { name ->
                                        viewModel.createNetwork(applicationContext, name)
                                        navController.popBackStack()
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    companion object Routes {
        private const val USER_ROUTE = "user"
        private const val TEAM_LIST_ROUTE = "team_list"
        private const val TEAM_CREATE_ROUTE = "team_create"

        private const val AUTHOR_KEY = "author"
    }
}
