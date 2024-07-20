package com.example.plantonista.gateway.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.plantonista.gateway.distevents.GlobalStreamer

class TeamCodeViewModel: ViewModel() {
    val qrCodeData: String
        get() {
            val net = GlobalStreamer.networkData

            return """plantonista://regismelgaco.co/enter_team?name="${net.name}"&secret="${net.secret}""""
        }
}