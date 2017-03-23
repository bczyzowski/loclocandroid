package com.bczyzowski.locator;

import android.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.bczyzowski.locator.fragments.FriendPickerDialogFragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.List;

public class HistoryActivity extends FragmentActivity implements OnMapReadyCallback {

    private static GoogleMap mMap;
    private List<String> friendsNames;
    String chosenFriend;
    String userEmail;
    String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        friendsNames = (List<String>) getIntent().getExtras().get("friends");
        userEmail = getIntent().getExtras().getString("userEmail");
        friendsNames.add(0, userEmail + " (me)");
        token = getIntent().getExtras().getString("token");

        showFriendPickerDialog(friendsNames);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }

    public void showFriendPickerDialog(List<String> friendsNames) {
        DialogFragment newFragment = new FriendPickerDialogFragment(friendsNames, chosenFriend,userEmail,token,this);
        newFragment.setCancelable(false);
        newFragment.show(getFragmentManager(), "friendPicker");
    }


    public static void addMarkerToMap(double lat, double lon, String time) {
        LatLng loc = new LatLng(lat, lon);
        mMap.addMarker(new MarkerOptions().position(loc).title(time.replace("T", " "))).showInfoWindow();
        mMap.moveCamera(CameraUpdateFactory.newLatLng(loc));
    }


    public static void addPolylineToMap(PolylineOptions polyline, List<MarkerOptions> markers) {
        mMap.clear();
        mMap.addPolyline(polyline);
        for (MarkerOptions markerOptions : markers) mMap.addMarker(markerOptions);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(polyline.getPoints().get(0)));
    }
}

