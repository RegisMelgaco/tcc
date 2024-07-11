package com.example.plantonista.gateway.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.plantonista.gateway.preferences.getUsername
import com.example.plantonista.gateway.preferences.setUsername

class UserViewModel : ViewModel() {
    fun loadLastUsername(context: Context): String = getUsername(context)

    fun saveUsername(context: Context, value: String) {
        setUsername(context, value)
    }
}