package com.example.quizappzeesh.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo

class MyBroadCast : BroadcastReceiver() {

    var connectionInterface: ConnectionInterface? = null
    var isConnected = false

    override fun onReceive(context: Context, intent: Intent) {
//        initialising interface..
        connectionInterface = context as ConnectionInterface
        if (intent.extras == null) {
            return
        }
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        isConnected = if (networkInfo != null && networkInfo.state == NetworkInfo.State.CONNECTED) {
            networkInfo.type == ConnectivityManager.TYPE_MOBILE || networkInfo.type == ConnectivityManager.TYPE_WIFI
        } else {
            false
        }
        connectionInterface!!.connectivity(isConnected)
    }

    interface ConnectionInterface {
        fun connectivity(isConnected: Boolean)
    }
}