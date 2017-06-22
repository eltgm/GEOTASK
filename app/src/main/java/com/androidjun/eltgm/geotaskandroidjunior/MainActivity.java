package com.androidjun.eltgm.geotaskandroidjunior;

import android.content.Intent;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.FilterQueryProvider;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.model.GeocodingResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/*
** AIzaSyCRpXyJ4hTTq5291gCk1qILPtlZUSD0r2o for GEOAPI
 */

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap googleMap;
    private static final GeoApiContext geoContext = new GeoApiContext().setApiKey("AIzaSyCRpXyJ4hTTq5291gCk1qILPtlZUSD0r2o");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final AutoCompleteTextView from = (AutoCompleteTextView) findViewById(R.id.mFrom);
        final AutoCompleteTextView to = (AutoCompleteTextView) findViewById(R.id.mTo);

        from.setThreshold(1);
        to.setThreshold(1);

        String[] fromS = {"name"};
        int[] toS = { android.R.id.text1 };

        final SimpleCursorAdapter a = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_2, null, fromS, toS, 0);
        SimpleCursorAdapter b = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_2, null, fromS, toS, 0);

        a.setStringConversionColumn(1);
        b.setStringConversionColumn(1);

        FilterQueryProvider providerFrom = new FilterQueryProvider() {
            @Override
            public Cursor runQuery(CharSequence constraint) {
                // run in the background thread

                if (constraint == null) {
                    return null;
                }
                String[] columnNames = { BaseColumns._ID, "name"};
                MatrixCursor c = new MatrixCursor(columnNames);
                try {
                    GeocodingResult[] results =  GeocodingApi.geocode(geoContext,
                            constraint.toString()).await();

                    for (int i = 0; i < results.length ; i++) {
                        c.newRow().add(i).add(results[i].formattedAddress);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return c;
            }
        };

        a.setFilterQueryProvider(providerFrom);
        from.setAdapter(a);

        b.setFilterQueryProvider(providerFrom);
        to.setAdapter(b);

        from.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if ((keyEvent != null && keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER) && i == KeyEvent.ACTION_DOWN){

                    new Thread(new Runnable() {
                        @Override
                        public void run() {

                            final RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                            String url = "https://maps.googleapis.com/maps/api/geocode/json?address=" + from.getText().toString().replace(" ", "+") +
                                    "&key=AIzaSyChJRNEw_iScJxkriEDv4jvgE2YYy1Y6Hg";

                            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                                    new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {
                                            String json = response;
                                            JSONObject object = null;
                                            JSONObject geometry;

                                            try {
                                                object = new JSONObject(json);
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }

                                            try {
                                                geometry = (JSONObject) ((JSONObject) ((JSONArray) object.get("results")).get(0)).get("geometry");
                                                JSONObject endLocation = (JSONObject) geometry.get("location");

                                                double toLat = endLocation.getDouble("lat");
                                                double toLng = endLocation.getDouble("lng");

                                                LatLng to = new LatLng(toLat, toLng);

                                                googleMap.addMarker(new MarkerOptions().position(to).title("Отсюда"));
                                                googleMap.moveCamera(CameraUpdateFactory.newLatLng(to));
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Toast.makeText(getApplicationContext(), "Error " + error.toString(), Toast.LENGTH_LONG).show();
                                }
                            });
                            queue.add(stringRequest);
                        }
                    }).start();

                    return true;
                }
                return false;
            }
        });
        to.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if ((keyEvent != null && keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER) && i == KeyEvent.ACTION_DOWN){

                    new Thread(new Runnable() {
                        @Override
                        public void run() {

                            final RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                            String url = "https://maps.googleapis.com/maps/api/geocode/json?address=" + to.getText().toString().replace(" ", "+") +
                                    "&key=AIzaSyChJRNEw_iScJxkriEDv4jvgE2YYy1Y6Hg";

                            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                                    new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {
                                            String json = response;
                                            JSONObject object = null;
                                            JSONObject geometry;

                                            try {
                                                object = new JSONObject(json);
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }

                                            try {
                                                geometry = (JSONObject) ((JSONObject) ((JSONArray) object.get("results")).get(0)).get("geometry");
                                                JSONObject endLocation = (JSONObject) geometry.get("location");

                                                double toLat = endLocation.getDouble("lat");
                                                double toLng = endLocation.getDouble("lng");

                                                LatLng to = new LatLng(toLat, toLng);

                                                googleMap.addMarker(new MarkerOptions().position(to).title("Сюда"));
                                                googleMap.moveCamera(CameraUpdateFactory.newLatLng(to));
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Toast.makeText(getApplicationContext(), "Error " + error.toString(), Toast.LENGTH_LONG).show();
                                }
                            });
                            queue.add(stringRequest);
                        }
                    }).start();

                    return true;
                }
                return false;
            }
        });

        Button mButton = (Button) findViewById(R.id.button);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new GetDirectionTask().execute("https://maps.googleapis.com/maps/api/directions/json?origin=" +
                from.getText().toString().replace(' ','+') + "&destination=" +
                to.getText().toString().replace(' ','+') + "&key=AIzaSyBp0UGZ1TGS1owOBQzwfrRfLj8MVYrJ250");
            }
        });

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map2);

        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Toast.makeText(getApplicationContext(), "HELLYEEE", Toast.LENGTH_LONG).show();
        this.googleMap = googleMap;
    }

    private class GetDirectionTask extends AsyncTask<String,Void,Void>{
        @Override
        protected Void doInBackground(String... urls) {
            final RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            String url = urls[0];

            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Intent intent = new Intent(MainActivity.this, ResultForm.class);
                            intent.putExtra("JSON", response);
                            startActivity(intent);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getApplicationContext(), "Error " + error.toString(), Toast.LENGTH_LONG).show();
                }
            });
            queue.add(stringRequest);

            return null;
        }
    }
}
