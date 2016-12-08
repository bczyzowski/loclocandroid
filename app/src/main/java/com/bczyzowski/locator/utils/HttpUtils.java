package com.bczyzowski.locator.utils;

import android.content.Context;
import android.location.Location;

import com.bczyzowski.locator.model.User;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.entity.StringEntity;

/**
 * Created by bczyz on 05.12.2016.
 */

public class HttpUtils {

   /* public static final String URL_LOGIN = "http://10.0.2.2:8080/Locator/api/user";
    public static final String URL_LOCATION = "http://10.0.2.2:8080/Locator/api/location/send";
    public static final String URL_GETFRIENDS = "http://10.0.2.2:8080/Locator/api/user/friends";*/

    public static final String URL_LOGIN = "http://192.168.1.110:8080/Locator/api/user";
    public static final String URL_LOCATION = "http://192.168.1.110:8080/Locator/api/location/send";
    public static final String URL_GETFRIENDS = "http://192.168.1.110:8080/Locator/api/user/friends";

    private static AsyncHttpClient client = new AsyncHttpClient();

    public static void postLogin(Context context,String email,String password, AsyncHttpResponseHandler responseHandler){

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("email",email);
            jsonObject.put("password",password);
            StringEntity entity = new StringEntity(jsonObject.toString());
            client.setResponseTimeout(3000);
            client.post(context,URL_LOGIN,entity,"application/json",responseHandler);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public static void postLocation(Context context, User user, Location location, AsyncHttpResponseHandler responseHandler){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("latitude",location.getLatitude());
            jsonObject.put("longitude",location.getLongitude());
            jsonObject.put("email",user.getEmail());
            jsonObject.put("authToken",user.getToken());
            StringEntity entity = new StringEntity(jsonObject.toString());
            client.post(context,URL_LOCATION,entity,"application/json",responseHandler);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }

    public static void getUserFriends(Context context, User user, AsyncHttpResponseHandler responseHandler){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("email",user.getEmail());
            jsonObject.put("token",user.getToken());
            jsonObject.put("password",user.getPassword());
            StringEntity entity = new StringEntity(jsonObject.toString());
            client.post(context,URL_GETFRIENDS,entity,"application/json",responseHandler);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }
}
