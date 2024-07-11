package com.example.plantonista.gateway.preferences

import android.content.Context

private fun getPrefs(context: Context) = context.getSharedPreferences("", Context.MODE_PRIVATE)

fun getUsername(context: Context) = getPrefs(context).getString(USER_KEY, "") ?: ""

fun setUsername(context: Context, value: String) {
    with(getPrefs(context).edit()) {
        putString(USER_KEY, value)
        apply()
    }
}


private const val USER_KEY = "user"
