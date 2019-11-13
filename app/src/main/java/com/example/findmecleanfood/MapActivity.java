package com.example.findmecleanfood;


import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;

import java.util.ArrayList;

public class MapActivity extends AppCompatActivity {

    private MapView mapView;
    Button backButton, homeButton;
    double lat, lon;
    Icon iconAwaiting, icon0, icon1, icon2, icon3, icon4, icon5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, getString(R.string.mapbox_api_key));
        setContentView(R.layout.activity_map);

        ArrayList<Restaurant> restaurantList = (ArrayList<Restaurant>) getIntent().getSerializableExtra("com.example.findmecleanfood.array");
        double[] myGPS = getIntent().getDoubleArrayExtra("com.example.findmecleanfood.gps");
        lat = myGPS[0];
        lon = myGPS[1];
        int zoom = getIntent().getIntExtra("com.example.findmecleanfood.zoom", 0);

        backButton = findViewById(R.id.backButton);
        homeButton = findViewById(R.id.homeButton);
        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(MapboxMap mapboxMap) {
                mapboxMap.setCameraPosition(
                        new CameraPosition.Builder()
                                .target(new LatLng(lat, lon)).zoom(zoom).build()
                    );

                for (int i=0; i<restaurantList.size(); i++) {
                    String name = restaurantList.get(i).getName();
                    String address = restaurantList.get(i).getAddressLine2()+" "+
                                     restaurantList.get(i).getAddressLine3()+" "+
                                     restaurantList.get(i).getPostCode();
                    String latStr = restaurantList.get(i).getLatitude();
                    String lonStr = restaurantList.get(i).getLongitude();
                    int rating = stringToInt(restaurantList.get(i).getRatingValue());

                    mapboxMap.addMarker(new MarkerOptions()
                                .position(new LatLng(stringToDouble(latStr), stringToDouble(lonStr)))
                                .title(name)
                                .snippet(address))
                                .setIcon(setIcon(rating));
                    }

            }
        });

        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goHome();
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    public void goHome() {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }

    public double stringToDouble(String string) {
        double returned;
        try {
            returned=Double.parseDouble(string);
        } catch (NumberFormatException e) {
            returned=0;
        }
        return returned;
    }

    public int stringToInt(String string) {
        int returned;
        try {
            returned=Integer.parseInt(string);
        } catch (NumberFormatException e) {
            returned=0;
        }
        return returned;
    }

    public Icon setIcon(int rating) {

        IconFactory iconFactory = IconFactory.getInstance(MapActivity.this);
        //Drawable iconDrawable = ContextCompat.getDrawable(MapActivity.this, R.drawable.rating5small);
        iconAwaiting = iconFactory.fromResource(R.drawable.icon_rating_awaiting);
        icon0 = iconFactory.fromResource(R.drawable.icon_rating0);
        icon1 = iconFactory.fromResource(R.drawable.icon_rating1);
        icon2 = iconFactory.fromResource(R.drawable.icon_rating2);
        icon3 = iconFactory.fromResource(R.drawable.icon_rating3);
        icon4 = iconFactory.fromResource(R.drawable.icon_rating4);
        icon5 = iconFactory.fromResource(R.drawable.icon_rating5);

        switch (rating) {
            case -1: return iconAwaiting;
            case 0: return icon0;
            case 1: return icon1;
            case 2: return icon2;
            case 3: return icon3;
            case 4: return icon4;
            case 5: return icon5;
        }
        return null;
    }
}
