package com.bczyzowski.locator.utils;

import android.content.Context;

import com.bczyzowski.locator.model.Location;
import com.bczyzowski.locator.model.User;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Objects;

import cz.msebera.android.httpclient.entity.StringEntity;

public class HttpUtils {

    //emulator
   /* public static final String URL_LOGIN = "http://10.0.2.2:8080/locloc/api/user";
    public static final String URL_LOCATION = "http://10.0.2.2:8080/locloc/api/location/send";
    public static final String URL_GETFRIENDS = "http://10.0.2.2:8080/locloc/api/user/friends";
    public static final String URL_REGISTER = "http://10.0.2.2:8080/locloc/api/user/register";
    public static final String NEW_FRIEND = "http://10.0.2.2:8080/locloc/api/user/newfriend";
    public static final String HISTORY = "http://10.0.2.2:8080/locloc/api/location/history";*/

    //physical device
   /* public static final String URL_LOGIN = "http://192.168.1.106:8080/locloc/api/user";
    public static final String URL_LOCATION = "http://192.168.1.106:8080/locloc/api/location/send";
    public static final String URL_GETFRIENDS = "http://192.168.1.106:8080/locloc/api/user/friends";
    public static final String URL_REGISTER = "http://192.168.1.106:8080/locloc/api/user/register";
    public static final String NEW_FRIEND = "http://192.168.1.106:8080/locloc/api/user/newfriend";
    public static final String HISTORY = "http://192.168.1.106:8080/locloc/api/location/history";*/

    //server
    public static final String URL_LOGIN = "http://79.137.36.192:8080/locloc/api/user";
    public static final String URL_LOCATION = "http://79.137.36.192:8080/locloc/api/location/send";
    public static final String URL_ALL_LOCATION = "http://79.137.36.192:8080/locloc/api/location/sendAll";
    public static final String URL_GETFRIENDS = "http://79.137.36.192:8080/locloc/api/user/friends";
    public static final String URL_REGISTER = "http://79.137.36.192:8080/locloc/api/user/register";
    public static final String NEW_FRIEND = "http://79.137.36.192:8080/locloc/api/user/newfriend";
    public static final String HISTORY = "http://79.137.36.192:8080/locloc/api/location/history";

    private static AsyncHttpClient client = new AsyncHttpClient();

    public static void postLogin(Context context, String email, String password, AsyncHttpResponseHandler responseHandler) {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("email", email);
            jsonObject.put("password", password);
            StringEntity entity = new StringEntity(jsonObject.toString());
            client.setResponseTimeout(5000);
            client.post(context, URL_LOGIN, entity, "application/json", responseHandler);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public static void postLocation(Context context, User user, Location location, AsyncHttpResponseHandler responseHandler) {
        JSONObject jsonObject;
        try {
            jsonObject = locationWraper(user, location);
            StringEntity entity = new StringEntity(jsonObject.toString());
            client.post(context, URL_LOCATION, entity, "application/json", responseHandler);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }

    public static void postAllLocations(Context context, User user, List<Object> locations, AsyncHttpResponseHandler responseHandler) {
        JSONArray jsonArray = new JSONArray();
        try {
            for (Object l : locations) {
                JSONObject jsonObject = locationWraper(user, (Location) l);
                jsonArray.put(jsonObject);
            }
            StringEntity entity = new StringEntity(jsonArray.toString());
            client.post(context, URL_ALL_LOCATION, entity, "application/json", responseHandler);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public static void getUserFriends(Context context, User user, AsyncHttpResponseHandler responseHandler) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("email", user.getEmail());
            jsonObject.put("token", user.getToken());
            StringEntity entity = new StringEntity(jsonObject.toString());
            client.post(context, URL_GETFRIENDS, entity, "application/json", responseHandler);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }

    public static void signup(Context context, String firstName, String lastName, String email, String password, AsyncHttpResponseHandler responseHandler) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("firstName", firstName);
            jsonObject.put("lastName", lastName);
            jsonObject.put("email", email);
            jsonObject.put("password", password);
            StringEntity entity = new StringEntity(jsonObject.toString());
            client.setResponseTimeout(3000);
            client.post(context, URL_REGISTER, entity, "application/json", responseHandler);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public static void addNewFriend(Context context, String userEmail, String friendEmail, AsyncHttpResponseHandler responseHandler) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("userEmail", userEmail);
            jsonObject.put("newFriendEmail", friendEmail);
            StringEntity entity = new StringEntity(jsonObject.toString());
            client.post(context, NEW_FRIEND, entity, "application/json", responseHandler);
        } catch (JSONException e) {
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }

    public static void retrieveHistory(Context context, String userEmail, String friendEmail, String token, int day, int month, int year, AsyncHttpResponseHandler responseHandler) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("email", userEmail);
            jsonObject.put("friendEmail", friendEmail);
            jsonObject.put("token", token);
            jsonObject.put("day", day);
            jsonObject.put("month", month);
            jsonObject.put("year", year);
            StringEntity entity = new StringEntity(jsonObject.toString());
            client.post(context, HISTORY, entity, "application/json", responseHandler);
        } catch (JSONException e) {
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private static JSONObject locationWraper(User user, Location location) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("latitude", location.getLatitude());
            jsonObject.put("longitude", location.getLongitude());
            jsonObject.put("email", user.getEmail());
            jsonObject.put("authToken", user.getToken());
            jsonObject.put("time", location.getTime().toString());
            jsonObject.put("accuracy", location.getAccuracy());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject;
    }
}
