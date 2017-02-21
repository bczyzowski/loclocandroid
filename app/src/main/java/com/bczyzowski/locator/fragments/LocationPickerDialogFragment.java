package com.bczyzowski.locator.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;

import com.bczyzowski.locator.HistoryActivity;
import com.bczyzowski.locator.model.Location;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.joda.time.LocalDateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class LocationPickerDialogFragment extends DialogFragment {

    CharSequence[] locations;
    List<Location> retrievedLocations = new ArrayList<>();

    public LocationPickerDialogFragment() {
    }

    public LocationPickerDialogFragment(JSONArray data) {
        locations = new CharSequence[data.length() + 1]; // + 1, pierwsza pozycja pokazuje wszystkie lokalizacje i rysuje polaczenia
        try {
            locations[0] = "Show all";
            for (int i = 0; i < data.length(); i++) {
                JSONObject object = data.getJSONObject(i);
                LocalDateTime time = LocalDateTime.parse(object.getString("date"));
                locations[i + 1] = time.toString();
                retrievedLocations.add(new Location(object.getDouble("lat"), object.getDouble("lon"), 0, time));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Choose location")
                .setItems(locations, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            // pokaz wszystkie lokalizacje
                            PolylineOptions polylineOptions = createPolylineOptFromAllLocations(retrievedLocations);
                            List<MarkerOptions> markers = createMarkersFromAllLocations(retrievedLocations);
                            HistoryActivity.addPolylineToMap(polylineOptions, markers);
                        } else {
                            Location tmp = retrievedLocations.get(which - 1);
                            HistoryActivity.addMarkerToMap(tmp.getLatitude(), tmp.getLongitude(), tmp.getTime().toString());
                        }
                    }
                });
        return builder.create();
    }
    private PolylineOptions createPolylineOptFromAllLocations(List<Location> locations) {
        PolylineOptions polyline = new PolylineOptions();
        for (Location loc : locations) {
            LatLng tmp = new LatLng(loc.getLatitude(), loc.getLongitude());
            polyline.add(tmp);
        }
        polyline.width(5).color(Color.RED);
        return polyline;
    }

    private List<MarkerOptions> createMarkersFromAllLocations(List<Location> locations) {
        List<MarkerOptions> resultList = new ArrayList<>();
        for (Location loc : locations) {
            LatLng latLng = new LatLng(loc.getLatitude(), loc.getLongitude());
            resultList.add(new MarkerOptions().position(latLng));
        }
        return resultList;
    }

}
