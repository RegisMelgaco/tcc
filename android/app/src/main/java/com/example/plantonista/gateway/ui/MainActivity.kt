package com.example.plantonista.gateway.ui

import android.os.Bundle
import android.util.Log
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
import com.example.plantonista.gateway.ui.screen.NotificationsScreen
import com.example.plantonista.gateway.ui.screen.ShiftCreateScreen
import com.example.plantonista.gateway.ui.screen.TeamCodeScreen
import com.example.plantonista.gateway.ui.screen.TeamCreateScreen
import com.example.plantonista.gateway.ui.screen.TeamListScreen
import com.example.plantonista.gateway.ui.screen.TeamScreen
import com.example.plantonista.gateway.ui.screen.UserScreen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d(TAG, "${intent.data?.path}")

        if (intent.data?.path == "/enter_team") {
            Log.d(TAG, intent.data?.queryParameterNames.toString())
            Log.d(TAG, intent.data?.getQueryParameter("name") ?: "nil")
            Log.d(TAG, intent.data?.getQueryParameter("secret") ?: "nil")

            val name = intent.data!!.getQueryParameter("name")!!
            val secret = intent.data!!.getQueryParameter("secret")!!

            lifecycleScope.launch(Dispatchers.IO) {
                Tracker(applicationContext, Configs.TRACKER_ADDRESS).saveNetwork(name, secret)
            }
        }

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
                        onConfirm = {
                            navController.navigate(TEAM_LIST_ROUTE)
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
                        navigateTeamCode = {
                            navController.navigate(TEAM_CODE_ROUTE)
                        },
                        navigateNotifications = {
                            navController.navigate(NOTIFICATIONS_ROUTE)
                        }
                    )
                }
                composable(MEMBER_CREATE_ROUTE) {
                    MemberCreateScreen(back = back)
                }
                composable(SHIFT_CREATE_ROUTE) {
                    ShiftCreateScreen(back = back)
                }
                composable(TEAM_CODE_ROUTE) {
                    TeamCodeScreen(back = back)
                }
                composable(NOTIFICATIONS_ROUTE) {
                    NotificationsScreen(back)
                }
            }
        }
    }

    companion object {
        private val TAG = MainActivity::class.simpleName
        private const val USER_ROUTE = "user"
        private const val TEAM_LIST_ROUTE = "team_list"
        private const val TEAM_CREATE_ROUTE = "team_create"
        private const val TEAM_ROUTE = "team/{name}"
        private const val TEAM_CODE_ROUTE = "team_code"
        private const val SHIFT_CREATE_ROUTE = "shift_create"
        private const val MEMBER_CREATE_ROUTE = "member_create"
        private const val NOTIFICATIONS_ROUTE = "notifications"
    }
}
