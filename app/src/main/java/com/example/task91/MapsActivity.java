package com.example.task91;

import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.database.Cursor;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import androidx.appcompat.app.AppCompatActivity;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    Boolean showAll;
    SQLiteDatabase sqlDB;
    Intent i;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        i = getIntent();

        showAll = i.getBooleanExtra("showAll", true);
        sqlDB = openOrCreateDatabase("LocationsDB",MODE_PRIVATE,null);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (showAll == true) {
            // Show all stored locations
            Cursor cursor = sqlDB.rawQuery("SELECT * FROM locations", null);

            while (cursor.moveToNext()) {
                String id = cursor.getString(0);
                String name = cursor.getString(1);
                Double lat = cursor.getDouble(2);
                Double lon = cursor.getDouble(3);
                Log.i("TESTING", id);
                Log.i("TESTING", name);
                Log.i("TESTING", lat.toString());
                Log.i("TESTING", lon.toString());

                // Add to map
                LatLng newPoint = new LatLng(lat, lon);
                mMap.addMarker(new MarkerOptions().position(newPoint).title(name));
            }
            cursor.close();
        }else {
            Bundle bundle = i.getExtras();
            String name = bundle.getString("name");
            Double lat = i.getDoubleExtra("lat", -37.8136);
            Double lon = i.getDoubleExtra("lon", 144.9631);

            if (name == null) {
                name = "Melbourne";
            }

            // Add to map
            LatLng newPoint = new LatLng(lat, lon);
            mMap.addMarker(new MarkerOptions().position(newPoint).title(name));
        }
        // Add a marker in Melbourne and move the camera
        LatLng melbourne = new LatLng(-37.8136, 144.9631);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(melbourne));
    }
}