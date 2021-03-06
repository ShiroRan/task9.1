package com.example.task91;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    SQLiteDatabase sqlDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sqlDB = openOrCreateDatabase("LocationsDB",MODE_PRIVATE,null);
        sqlDB.execSQL("CREATE TABLE IF NOT EXISTS locations(id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, name VARCHAR, lat REAL, lon REAL);");
        //sqlDB.execSQL("DELETE FROM locations;");
    }

    public void buttonClick(View v) {
        if (v.getId() == R.id.add_location) {
            Intent i = new Intent(this, LocationActivity.class);
            startActivity(i);
        } else if (v.getId() == R.id.show_map) {
            Intent i = new Intent(this, MapsActivity.class);
            i.putExtra("showAll", true);
            startActivity(i);
        }
    }
}