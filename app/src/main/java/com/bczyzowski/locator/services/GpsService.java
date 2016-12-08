package com.bczyzowski.locator.services;


import android.Manifest;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Icon;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;

import com.bczyzowski.locator.R;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


/**
 * Created by Bartosz on 01.12.2016.
 */

public class GpsService extends Service {

    private long locTimeInterval = 10000;
    private LocationListener locationListener;
    private LocationManager locationManager;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                System.out.println("Zmiana lokalizacji " + location.getLongitude() + " " + location.getLatitude());
                Intent intent = new Intent("locationUpdate");
                intent.putExtra("longitude", location.getLongitude());
                intent.putExtra("latitude", location.getLatitude());
                intent.putExtra("accuracy", location.getAccuracy());
                sendBroadcast(intent);
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        };

        locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, locTimeInterval, 1, locationListener);


        Intent notificationIntent = new Intent(getApplicationContext(), com.bczyzowski.locator.LocationActivity.class);
        notificationIntent.setAction(Intent.ACTION_MAIN);
        notificationIntent.setAction(Intent.CATEGORY_LAUNCHER);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);


        //NotificationCompat.Action action = new NotificationCompat.Action.Builder(R.mipmap.ic_highlight_off_black_24dp,"Disable").build();

        Notification notification = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("LocLoc")
           //     .addAction(action)
                .setContentIntent(pendingIntent).build();

        startForeground(666, notification);
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        if (locationManager != null) {
            locationManager.removeUpdates(locationListener);
        }
    }

}
