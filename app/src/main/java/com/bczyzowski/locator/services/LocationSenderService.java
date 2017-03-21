package com.bczyzowski.locator.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.bczyzowski.locator.model.Location;
import com.bczyzowski.locator.model.User;
import com.bczyzowski.locator.utils.HttpUtils;
import com.bczyzowski.locator.utils.SharedPrefReadWrite;
import com.bczyzowski.locator.utils.TinyDB;
import com.loopj.android.http.TextHttpResponseHandler;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class LocationSenderService extends IntentService {

    private User user;
    private ArrayList<Object> locationList;
    private Handler handler;
    private TinyDB tinyDB;


    public LocationSenderService() {
        super("LocationSenderService");
        Log.d("LocationSenderService", "constructor");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        user = SharedPrefReadWrite.getUserFromSharedPref(getApplication());
        handler = new Handler();
        tinyDB = new TinyDB(getApplicationContext());
        locationList = tinyDB.getListObject("locations", Location.class);
        Log.d("LocationSenderService", "onCreate");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d("LocationSenderService", "onHandleIntent");
        final Location location = (Location) intent.getExtras().get("loc");
        if (isOnline()) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    if (locationList.size() > 0) {
                        sendAllLocationsToServer();
                    } else {
                        sendLocationToServer(location);
                    }

                }
            });
        } else {
            locationList.add(location);
            tinyDB.putListObject("locations", locationList);
        }
    }

    private boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public void sendLocations() {
        for (int i = 0; i < locationList.size(); i++) {
            sendLocationToServer((Location) locationList.get(i));
        }
    }

    private void sendLocationToServer(final Location location) {
        HttpUtils.postLocation(getApplicationContext(), user, location, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Toast.makeText(getApplicationContext(), "Location not saved", Toast.LENGTH_SHORT).show();
                locationList.add(location);
                tinyDB.putListObject("locations", locationList);
                Log.d("locsend", "fail - loc add");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                Toast.makeText(getApplicationContext(), "Location saved", Toast.LENGTH_SHORT).show();
                locationList.remove(location);
                tinyDB.putListObject("locations", locationList);
                Log.d("locsend", "succ - loc rem");
                Log.d("locsend", location.toString());
                Log.d("locsend", locationList.toString());
            }
        });
    }

    private void sendAllLocationsToServer() {
        HttpUtils.postAllLocations(getApplicationContext(), user, locationList, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Toast.makeText(getApplicationContext(), "Locations not saved", Toast.LENGTH_SHORT).show();
                Log.d("locsend", "fail - locs add");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                Toast.makeText(getApplicationContext(), "Locations saved", Toast.LENGTH_SHORT).show();
                locationList.clear();
                tinyDB.putListObject("locations", locationList);
            }
        });
    }
}
