package com.example.quizappzeesh.Broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;

public class MyBroadCast extends BroadcastReceiver {

     ConnectionInterface connectionInterface;
    boolean isConnected = false;

    @Override
    public void onReceive(Context context, Intent intent) {
//        initialising interface..
        connectionInterface = (ConnectionInterface) context;

        if (intent.getExtras() == null){
            return;
        }

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.getState() == NetworkInfo.State.CONNECTED){
            isConnected = networkInfo.getType() == ConnectivityManager.TYPE_MOBILE || networkInfo.getType() == ConnectivityManager.TYPE_WIFI;
        } else{
            isConnected = false;
        }

        connectionInterface.connectivity(isConnected);
    }

    public interface ConnectionInterface{
        void connectivity(boolean isConnected);
    }
}
