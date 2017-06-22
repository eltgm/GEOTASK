package com.androidjun.eltgm.geotaskandroidjunior;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * AIzaSyA3DNFAfzSMY9SpfoT09aCTsHydrkWaui0 API for map
 */
public class ResultForm extends FragmentActivity implements OnMapReadyCallback {

    private double fromLat, fromLng, toLat, toLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        TextView status = (TextView) findViewById(R.id.mTextAns);

        String json = getIntent().getStringExtra("JSON");
        JSONObject object = null;
        JSONArray legs;

        try {
            object = new JSONObject(json);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            if (object.getString("status").equals("OK"))
                status.setText("Found");
            else
                status.setText("Unknown");


        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            legs = (JSONArray) ((JSONObject) ((JSONArray) object.get("routes")).get(0)).get("legs");
            JSONObject endLocation = (JSONObject) ((JSONObject) legs.get(0)).get("end_location");
            JSONObject startLocation = (JSONObject) ((JSONObject) legs.get(0)).get("start_location");

            toLat = endLocation.getDouble("lat");
            toLng = endLocation.getDouble("lng");
            fromLat = startLocation.getDouble("lat");
            fromLng = startLocation.getDouble("lng");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            map.setMyLocationEnabled(true);
        } else {
            // Show rationale and request permission.
        }
        // Polylines are useful for marking paths and routes on the map.
        LatLng from  = new LatLng(fromLat, fromLng);
        LatLng to = new LatLng(toLat, toLng);
        map.addPolyline(new PolylineOptions().geodesic(true)
                .add(from)
                .add(to)
        );
        if (to.latitude < from.latitude){
            map.moveCamera(CameraUpdateFactory.newLatLngBounds(
                    new LatLngBounds(to, from),
                    100));
        }else{
            map.moveCamera(CameraUpdateFactory.newLatLngBounds(
                    new LatLngBounds(from, to),
                    100));
        }
    }
}
