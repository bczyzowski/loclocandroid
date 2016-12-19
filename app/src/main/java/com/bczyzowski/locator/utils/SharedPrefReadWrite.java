package com.bczyzowski.locator.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.bczyzowski.locator.model.Location;
import com.bczyzowski.locator.model.User;

import org.joda.time.LocalDateTime;

import static android.content.Context.MODE_PRIVATE;


public class SharedPrefReadWrite {


    public static void saveUserToSharedPref(User user,Context context,String[] firstAndLastName) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("user", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("userEmail", user.getEmail());
        editor.putString("userPassword", user.getPassword());
        editor.putString("userToken", user.getToken());
        if(firstAndLastName.length>0){
            editor.putString("firstName",firstAndLastName[0]);
            editor.putString("lastName",firstAndLastName[1]);
        }
        editor.apply();
    }

    public static String getUserFirstAndLastNameFromSharedPref(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences("user", MODE_PRIVATE);
        String firstName = sharedPreferences.getString("firstName","");
        String lastName = sharedPreferences.getString("lastName","");
        return firstName + " " + lastName;
    }

    public static User getUserFromSharedPref(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("user", MODE_PRIVATE);
        String email = sharedPreferences.getString("userEmail", "");
        if (!email.equals("")) {
            String password = sharedPreferences.getString("userPassword", "");
            String token = sharedPreferences.getString("userToken", "");
            return new User(email, password, token);
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
        editor.putString("time",location.getTime().toString());
        editor.apply();
    }

    public static Location getLastLocToSharedPref(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("loc", MODE_PRIVATE);
        String latitude = sharedPreferences.getString("latitude","");
        if(!latitude.equals("")){
            String longitude = sharedPreferences.getString("longitude","");
            String accuracy = sharedPreferences.getString("accuracy","");
            LocalDateTime time= LocalDateTime.parse(sharedPreferences.getString("time",""));
            return new Location(Double.valueOf(latitude),Double.valueOf(longitude),Float.valueOf(accuracy),time);
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
