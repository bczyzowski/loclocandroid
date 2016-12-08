package com.bczyzowski.locator.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.bczyzowski.locator.services.GpsService;

/**
 * Created by bczyz on 05.12.2016.
 */

public class StartGpsServiceAtBoot extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            Intent serviceIntent = new Intent(context, GpsService.class);
            context.startService(serviceIntent);
        }
    }
}