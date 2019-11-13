package com.example.findmecleanfood;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

public class PostcodeSearchList extends AppCompatActivity {

    Button viewInMapButton, backButton, homeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_postcode_search_list);

        viewInMapButton = findViewById(R.id.viewInMapButton);
        backButton = findViewById(R.id.backButton);
        homeButton = findViewById(R.id.homeButton);

        ListView listView = findViewById(R.id.listView);

        // Get arraylist, GPS, and zoom value from previous activity
        ArrayList<Restaurant> restaurantList = (ArrayList<Restaurant>) getIntent().getSerializableExtra("com.example.findmecleanfood.array");
        double[] gps = getIntent().getDoubleArrayExtra("com.example.findmecleanfood.gps");
        int zoom = getIntent().getIntExtra("com.example.findmecleanfood.zoom", 0);

        // Launch restaurant list adapter
        RestaurantListAdapter rlAdapter = new RestaurantListAdapter(this, R.layout.adapter_view_layout, restaurantList);
        listView.setAdapter(rlAdapter);

        // Launch map activity
        viewInMapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewInMap(restaurantList, gps, zoom);
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goHome();
            }
        });

        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goHome();
            }
        });

    }

    // Launch map activity, passing same: arraylist of restaurant objects, plus position and zoom of map 'camera'
    public void viewInMap(ArrayList<Restaurant> array, double[] gps, int zoom) {
        Intent i = new Intent(this, MapActivity.class);
        i.putExtra("com.example.findmecleanfood.array", array);
        i.putExtra("com.example.findmecleanfood.gps", gps);
        i.putExtra("com.example.findmecleanfood.zoom", zoom);
        startActivity(i);
    }

    public void goHome() {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }
}
