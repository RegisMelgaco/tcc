package com.example.plantonista.gateway.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Paid
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.plantonista.R
import com.example.plantonista.gateway.ui.theme.PlantonistaTheme
import com.example.plantonista.gateway.ui.viewmodel.TeamViewModel
import kotlinx.coroutines.Dispatchers

const val MEMBERS_ROUTE = "members"
const val SHIFTS_ROUTE = "shifts"
const val PAYMENT_ROUTE = "payment"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeamScreen(
    viewModel: TeamViewModel = viewModel(),
    name: String = "UTI 3",
    back: () -> Unit = {},
    navigateMemberCreate: () -> Unit = {},
    navigateShiftCreate: () -> Unit = {},
) {
    val navController = rememberNavController()
    val context = LocalContext.current

    LaunchedEffect(Dispatchers.IO) {
        viewModel.setup(context, name)
    }

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
            },
            bottomBar = {
                BottomAppBar(
                    actions = {
                        Row (horizontalArrangement = Arrangement.SpaceAround, modifier = Modifier.fillMaxWidth()) {
                            IconButton(onClick = {
                                navController.navigate(MEMBERS_ROUTE) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }) {
                                Icon(Icons.Filled.Person, contentDescription = "Membros")
                            }
                            IconButton(onClick = {
                                navController.navigate(SHIFTS_ROUTE) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }) {
                                Icon(
                                    Icons.Filled.Schedule,
                                    contentDescription = "Plantões",
                                )
                            }
                            IconButton(onClick = {
                                navController.navigate(PAYMENT_ROUTE) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }) {
                                Icon(
                                    Icons.Filled.Paid,
                                    contentDescription = "Pagamento",
                                )
                            }
                        }
                    }
                )
            }
        ) { padding ->
            Column(modifier = Modifier.padding(padding)) {
                NavHost(navController = navController, startDestination = SHIFTS_ROUTE) {
                    composable(MEMBERS_ROUTE) {
                        TeamMemberListScreen(
                            navigateMemberCreate = navigateMemberCreate,
                        )
                    }
                    composable(SHIFTS_ROUTE) {
                        ShiftsScreen(
                            navigateShiftCreate = navigateShiftCreate
                        )
                    }
                    composable(PAYMENT_ROUTE) {
                        PaymentScreen()
                    }
                }
            }
        }
    }
}


