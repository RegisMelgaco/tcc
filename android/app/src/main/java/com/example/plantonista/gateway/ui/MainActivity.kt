package com.example.plantonista.gateway.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.plantonista.Configs
import com.example.plantonista.distevents.Tracker
import com.example.plantonista.gateway.ui.screen.MemberCreateScreen
import com.example.plantonista.gateway.ui.screen.ShiftCreateScreen
import com.example.plantonista.gateway.ui.screen.TeamCreateScreen
import com.example.plantonista.gateway.ui.screen.TeamListScreen
import com.example.plantonista.gateway.ui.screen.TeamScreen
import com.example.plantonista.gateway.ui.screen.UserScreen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val navController = rememberNavController()
            val scope = rememberCoroutineScope()
            val back: () -> Unit = {
                scope.launch { navController.popBackStack() }
            }

            NavHost(
                navController = navController,
                startDestination = USER_ROUTE,
            ) {
                composable(USER_ROUTE) {
                    UserScreen(
                        onConfirm = { username ->
                            navController.navigate(TEAM_LIST_ROUTE)
                            lifecycleScope.launch(Dispatchers.IO) {
                                Tracker(applicationContext, Configs.TRACKER_ADDRESS).syncEvents(username)
                            }
                        },
                    )
                }

                composable(TEAM_LIST_ROUTE) {
                    TeamListScreen(
                        navigateTeamCreate = { navController.navigate(TEAM_CREATE_ROUTE) },
                        navigateTeam = { team ->
                            navController.navigate("team/${team.name}")
                        },
                        back = back,
                    )
                }

                composable(TEAM_CREATE_ROUTE) {
                    TeamCreateScreen(back = back)
                }

                composable(TEAM_ROUTE,
                    arguments = listOf(
                        navArgument("name") { type = NavType.StringType },
                    )
                ) { navBackStackEntry ->
                    TeamScreen(
                        name = navBackStackEntry.arguments?.getString("name")!!,
                        back = back,
                        navigateMemberCreate = {
                            navController.navigate(MEMBER_CREATE_ROUTE)
                        },
                        navigateShiftCreate = {
                            navController.navigate(SHIFT_CREATE_ROUTE)
                        },
                    )
                }
                composable(MEMBER_CREATE_ROUTE) {
                    MemberCreateScreen(back = back)
                }
                composable(SHIFT_CREATE_ROUTE) {
                    ShiftCreateScreen(back = back)
                }
            }
        }
    }

    companion object {
        private const val USER_ROUTE = "user"
        private const val TEAM_LIST_ROUTE = "team_list"
        private const val TEAM_CREATE_ROUTE = "team_create"
        private const val TEAM_ROUTE = "team/{name}"
        private const val SHIFT_CREATE_ROUTE = "shift_create"
        private const val MEMBER_CREATE_ROUTE = "member_create"
    }
}
