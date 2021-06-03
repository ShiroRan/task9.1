package com.example.task91;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteFragment;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import java.util.Arrays;

public class LocationActivity extends AppCompatActivity implements OnMapReadyCallback {

    SQLiteDatabase sqlDB;
    Double lat;
    Double lon;
    private GoogleMap mMap;
    public Place cPlace;
    SupportMapFragment mapFragment;
    private FusedLocationProviderClient fusedLocationClient;
    Location loc;
    AutocompleteSupportFragment autocompleteFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addloca);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map2);
        mapFragment.getMapAsync((OnMapReadyCallback) this);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        String apiKey = "AIzaSyDJQRSkpTdTbFxKo13Z8mSOcZxeNetQcGk";
        sqlDB = openOrCreateDatabase("LocationsDB", MODE_PRIVATE, null);

        /**
         * Initialize Places. For simplicity, the API key is hard-coded. In a production
         * environment we recommend using a secure mechanism to manage API keys.
         */
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), apiKey);
        }

        // Create a new Places client instance.
        PlacesClient placesClient = Places.createClient(this);

        // Initialize the AutocompleteSupportFragment.
        autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS));

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                cPlace = place;
                // TODO: Get info about the selected place.
                Log.i("TESTING", "Place: " + place.getName() + ", " + place.getId() + ", Lat :: Lon: " + place.getLatLng());

                lat = place.getLatLng().latitude;
                lon = place.getLatLng().longitude;
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i("TESTING", "An error occurred: " + status);
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        findViewById(R.id.map1).setVisibility(View.GONE);
    }

    public void savePlace(View v) {
        EditText et = findViewById(R.id.editTextTextPersonName);
        if (et.getText().equals("") || lat == null || lon == null) {
            et.requestFocus();
            Toast.makeText(this, "Please set a name", Toast.LENGTH_LONG);
        } else {
            sqlDB.execSQL("INSERT INTO locations (name, lat, lon) VALUES (\"" + et.getText().toString() + "\", " + lat + ", " + lon + ");");
            Intent i = new Intent(this, ShowMap.class);
            i.putExtra("showAll", false);
            i.putExtra("name", et.getText().toString());
            i.putExtra("lat", lat);
            i.putExtra("lon", lon);
            startActivity(i);
            lat = null;
            lon = null;
        }
    }

    public void getLocation(View v) {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            //return;
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            loc = location;
                            lat = loc.getLatitude();
                            lon = loc.getLongitude();
                        } else {
                            loc = null;
                        }
                    }
                });

        if (loc == null) {
            LatLng location = new LatLng(-37.8136, 144.9631);
            lat = location.latitude;
            lon = location.longitude;
        }

        autocompleteFragment.setText("Current Location");
        cPlace = null;

    }

    public void cancel(View v) {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }

    public void hideMap(View v) {
        findViewById(R.id.map1).setVisibility(View.GONE);
    }

    public void showMap(View v) {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            //return;
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            loc = location;
                        } else {
                            loc = null;
                        }
                    }
                });



        // Add a marker in Sydney and move the camera
        EditText et = findViewById(R.id.editTextTextPersonName);
        LatLng location = new LatLng(-37.8136, 144.9631);

        try {
            try {
                location = new LatLng(cPlace.getLatLng().latitude, cPlace.getLatLng().longitude);
            } catch (Exception e) {
                try {
                    location = new LatLng(loc.getLatitude(), loc.getLongitude());
                } catch (Exception exception) {

                }
            }
            mMap.addMarker(new MarkerOptions().position(location).title(et.getText().toString()));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(location));
        } catch (Exception e) {

        } finally {
            findViewById(R.id.map1).setVisibility(View.VISIBLE);
        }
    }
}
