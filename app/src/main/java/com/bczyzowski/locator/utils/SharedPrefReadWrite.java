package com.bczyzowski.locator.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.bczyzowski.locator.model.Location;
import com.bczyzowski.locator.model.User;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by bczyz on 05.12.2016.
 */

public class SharedPrefReadWrite {


    public static void saveUserToSharedPref(User user,Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("user", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("userEmail", user.getEmail());
        editor.putString("userPassword", user.getPassword());
        editor.putString("userToken", user.getToken());
        editor.commit();
    }

    public static User getUserFromSharedPref(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("user", MODE_PRIVATE);
        String email = sharedPreferences.getString("userEmail", "");
        if (!email.equals("")) {
            String password = sharedPreferences.getString("userPassword", "");
            String token = sharedPreferences.getString("userToken", "");
            User user = new User(email, password, token);
            return user;
        } else {
            return null;
        }
    }

    public static void saveLastLocToSharedPref(Location location, Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("loc", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("latitude",String.valueOf(location.getLatitude()));
        editor.putString("longitude",String.valueOf(location.getLongitude()));
        editor.putString("accuracy",String.valueOf(location.getAccuracy()));
        editor.commit();
    }

    public static Location getLastLocToSharedPref(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("loc", MODE_PRIVATE);
        String latitude = sharedPreferences.getString("latitude","");
        if(!latitude.equals("")){
            String longitude = sharedPreferences.getString("longitude","");
            String accuracy = sharedPreferences.getString("accuracy","");
            Location lastLocation = new Location(Double.valueOf(latitude),Double.valueOf(longitude),Float.valueOf(accuracy));
            return lastLocation;
        }else{
            return null;
        }
    }


    public static void clearSharedPref(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences("user", MODE_PRIVATE);
        sharedPreferences.edit().clear().apply();
        SharedPreferences sharedPreferences2 = context.getSharedPreferences("loc", MODE_PRIVATE);
        sharedPreferences.edit().clear().apply();
    }
}
