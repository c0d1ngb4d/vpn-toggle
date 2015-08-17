package com.codingbad.vpntoggle.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;

import com.codingbad.vpntoggle.service.NetworkManagerIntentService;

public class NetworkChangeReceiver extends BroadcastReceiver {
    public NetworkChangeReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            NetworkManagerIntentService.startActionInit(context);
        } else {
            if (intent.getAction().equals("android.net.conn.CONNECTIVITY_CHANGE")) {
                NetworkInfo networkInfo = (NetworkInfo) intent
                        .getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
                if ( networkInfo.getState() == NetworkInfo.State.CONNECTED
                        //is not a failover for another network
                            && !intent.getExtras().getBoolean(ConnectivityManager.EXTRA_IS_FAILOVER)) {
                    NetworkManagerIntentService.startActionChange(context);
                }
            }
        }
    }
}
