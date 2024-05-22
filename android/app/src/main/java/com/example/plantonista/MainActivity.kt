package com.example.plantonista

import android.content.Intent
import android.net.wifi.WifiManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.plantonista.gateway.tcp.TCPClient
import com.example.plantonista.gateway.tcp.TCPServer
import java.net.Inet4Address
import java.net.NetworkInterface
import java.net.SocketException


class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var addresses = ""

        try {
            val en = NetworkInterface.getNetworkInterfaces()
            while (en.hasMoreElements()) {
                val intf = en.nextElement()
                val enumIpAddr = intf.getInetAddresses()
                while (enumIpAddr.hasMoreElements()) {
                    val inetAddress = enumIpAddr.nextElement()
                    if (!inetAddress.isLoopbackAddress && inetAddress is Inet4Address) {
                        addresses = "$addresses\n${inetAddress.getHostAddress()}"
                    }
                }
            }
        } catch (ex: SocketException) {
            ex.printStackTrace()
        }

        Log.i(TAG, "endereços: $addresses")

        setContent {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                Text("Endereços")
                Text(addresses)
            }
        }

        startService(Intent(applicationContext, TCPServer::class.java))

        Log.d("MainActivity", "onCreate")

        viewModel.sync()
    }

    companion object {
        private val TAG = MainActivity::class.java.simpleName
    }
}