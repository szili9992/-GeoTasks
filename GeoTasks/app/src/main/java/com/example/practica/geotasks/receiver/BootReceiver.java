package com.example.practica.geotasks.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.practica.geotasks.service.OnBootStartService;

/**
 * Created by szili on 2016-08-29.
 */
public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, Intent intent) {
        Intent i = new Intent(context, OnBootStartService.class);
        context.startService(i);
    }


}
