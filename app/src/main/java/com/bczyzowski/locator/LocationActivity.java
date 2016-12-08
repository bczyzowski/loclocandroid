package com.bczyzowski.locator;

import android.*;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.view.SubMenu;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bczyzowski.locator.model.Location;
import com.bczyzowski.locator.model.User;
import com.bczyzowski.locator.services.GpsService;
import com.bczyzowski.locator.utils.HttpUtils;
import com.bczyzowski.locator.utils.SharedPrefReadWrite;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.vision.text.Text;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class LocationActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback {

    private BroadcastReceiver broadcastReceiver;
    private GoogleMap map;
    private Intent gpsService;
    private NavigationView navigationView;
    private TextView navHeaderUserData;
    private User user;
    private List<String> friendsNames = new ArrayList<>();
    private List<Location> friendsLocations = new ArrayList<>();
    private boolean isMyLocationFocused = true;
    private Location lastUserLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        user = SharedPrefReadWrite.getUserFromSharedPref(getApplicationContext());
        updateUserFriendsList();

        System.out.println("User in location activity : " + user);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goBackToMyOwnLocation();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        setUserDataInNavHeader();
        navigationView.setNavigationItemSelectedListener(this);

        MapFragment mapFragment = MapFragment.newInstance();
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.content_location, mapFragment);
        fragmentTransaction.commit();
        mapFragment.getMapAsync(this);

        if (!runtimePermissions()) {
            gpsService = new Intent(getApplicationContext(), GpsService.class);
            startService(gpsService);
        }

    }

    private void setUserDataInNavHeader() {
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        LinearLayout linearLayout = (LinearLayout) navigationView.getHeaderView(0);
        navHeaderUserData = (TextView) linearLayout.getChildAt(2);
        navHeaderUserData.setText(user.getEmail());
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
        moveTaskToBack(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.location, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.log_off) {
            unregisterReceiver(broadcastReceiver);
            stopService(gpsService);
            SharedPrefReadWrite.clearSharedPref(getApplicationContext());
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            finish();
            return true;
        }
        if (id == R.id.shutdown) {
            unregisterReceiver(broadcastReceiver);
            stopService(gpsService);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        isMyLocationFocused = false;

        System.out.println("cliecked on : " + id);

        Location location = friendsLocations.get(id);
        updateLocOnMap(location.getLatitude(), location.getLongitude());
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        restoreLastSavedLocation();
    }

    private void updateLocOnMap(double lat, double lon) {
        map.clear();
        LatLng loc = new LatLng(lat, lon);
        map.addMarker(new MarkerOptions().position(loc).title("Your actual position"));
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(loc, 16));
    }


    private void updateLocOnMap(double lat, double lon, float acc) {
        if(lat !=0){
            map.clear();
            LatLng loc = new LatLng(lat, lon);
            CircleOptions circleOptions = new CircleOptions();
            circleOptions.center(loc);
            circleOptions.radius(acc);
            circleOptions.fillColor(Color.BLUE);
            map.addMarker(new MarkerOptions().position(loc).title("Your actual position"));
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(loc, 16));
            map.addCircle(circleOptions);
        }
    }

    private boolean runtimePermissions() {
        if (Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
            return true;
        }
        return false;
    }

    private void getLocationFromBroadcast() {
        if (broadcastReceiver == null) {
            broadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    double lastUserLat = intent.getExtras().getDouble("latitude");
                    double lastUserLon = intent.getExtras().getDouble("longitude");
                    float lastUserAcc = intent.getExtras().getFloat("accuracy");
                    System.out.println("new loc " + lastUserLat + " " + lastUserLon);
                    if (isMyLocationFocused) updateLocOnMap(lastUserLat,lastUserLon,lastUserAcc);
                    lastUserLocation = new Location(lastUserLat,lastUserLon,lastUserAcc);
                    SharedPrefReadWrite.saveLastLocToSharedPref(lastUserLocation,getApplicationContext());
                }
            };
        }
        registerReceiver(broadcastReceiver, new IntentFilter("locationUpdate"));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            if (!(grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED)) {
                runtimePermissions();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getLocationFromBroadcast();
    }

    private void updateUserFriendsList() {
        friendsNames.clear();
        friendsLocations.clear();
        HttpUtils.getUserFriends(this, user, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                try {
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject jsonObject = (JSONObject) response.get(i);
                        System.out.println(jsonObject.get("name"));
                        System.out.println(jsonObject.get("latitude"));
                        System.out.println(jsonObject.get("longitude"));
                        friendsNames.add(jsonObject.getString("name"));
                        friendsLocations.add(new Location(jsonObject.getDouble("latitude"), jsonObject.getDouble("longitude")));
                    }
                    createFriendsSubmenu();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Toast.makeText(getApplicationContext(), "Error : problem with updating friends", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void goBackToMyOwnLocation() {
        isMyLocationFocused = true;
        getLocationFromBroadcast();
        restoreLastSavedLocation();
    }

    private void restoreLastSavedLocation(){
        lastUserLocation = SharedPrefReadWrite.getLastLocToSharedPref(getApplicationContext());
        if(lastUserLocation!=null){
            updateLocOnMap(lastUserLocation.getLatitude(),lastUserLocation.getLongitude(),lastUserLocation.getAccuracy());
        }
    }

    private void createFriendsSubmenu() {
        SubMenu friends = navigationView.getMenu().addSubMenu("Friends");
        for (String name : friendsNames) {
            friends.add(name);
        }
    }
}
