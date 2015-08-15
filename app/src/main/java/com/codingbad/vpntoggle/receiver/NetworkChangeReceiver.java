package com.codingbad.vpntoggle.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.codingbad.vpntoggle.service.NetworkManagerIntentService;

public class NetworkChangeReceiver extends BroadcastReceiver {
    public NetworkChangeReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        NetworkManagerIntentService.startActionChange(context);
    }
}
