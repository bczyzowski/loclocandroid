package com.bczyzowski.locator.services;


import android.Manifest;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;

import com.bczyzowski.locator.R;
import com.bczyzowski.locator.model.User;
import com.bczyzowski.locator.utils.SharedPrefReadWrite;

import net.danlew.android.joda.JodaTimeAndroid;

import org.joda.time.LocalDateTime;


public class GpsService extends Service {

    private long locTimeInterval = 600000;
    private LocationListener locationListener;
    private LocationManager locationManager;
    private User user;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        user = SharedPrefReadWrite.getUserFromSharedPref(getApplication());
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        JodaTimeAndroid.init(getApplicationContext());
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                com.bczyzowski.locator.model.Location loc = new com.bczyzowski.locator.model.Location(location.getLatitude(), location.getLongitude(), location.getAccuracy(), LocalDateTime.now());
                SharedPrefReadWrite.saveLastLocToSharedPref(loc, getApplicationContext());
                Intent intent = new Intent("locationUpdate");
                sendBroadcast(intent);

                Intent locServiceIntent = new Intent(getBaseContext(), LocationSenderService.class);
                locServiceIntent.putExtra("loc", loc);
                startService(locServiceIntent);
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
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, locTimeInterval, 3, locationListener);


        Intent notificationIntent = new Intent(getApplicationContext(), com.bczyzowski.locator.LocationActivity.class);
        notificationIntent.setAction(Intent.ACTION_MAIN);
        notificationIntent.setAction(Intent.CATEGORY_LAUNCHER);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);

        Notification notification = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.white)
                .setContentTitle("LocLoc")
                .setContentIntent(pendingIntent).build();

        startForeground(666, notification);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (locationManager != null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            locationManager.removeUpdates(locationListener);
        }
    }

}
