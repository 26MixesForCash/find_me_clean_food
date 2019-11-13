package com.example.findmecleanfood;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ImageView imageView;
    Button searchAroundMeButton, searchByNameButton,
            searchByPostcodeButton, recentlyAddedButton;
    double lat, lon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Allow networking to be done on one thread
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);


        searchAroundMeButton = findViewById(R.id.searchAroundMeButton);
        searchByNameButton = findViewById(R.id.searchByNameButton);
        searchByPostcodeButton = findViewById(R.id.searchByPostcodeButton);
        recentlyAddedButton = findViewById(R.id.recentlyAddedButton);
        imageView = findViewById(R.id.fsaLogo);
        imageView.setImageResource(R.drawable.fsa_logo);


        LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);

        // Check permission to access user location
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission
                (this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    "android.permission.ACCESS_FINE_LOCATION"}, 1);

        }
        else {

            // Access location of user
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    lat = location.getLatitude();
                    lon = location.getLongitude();
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) { }

                @Override
                public void onProviderEnabled(String provider) { }

                @Override
                public void onProviderDisabled(String provider) { }
            });
        }


        // Set onClickListener for each Button on Main Activity screen
        searchAroundMeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Arraylists for restaurant objects, and for lat and lon values, to get average GSP
                ArrayList<Restaurant> aroundMe = new ArrayList<>();
                ArrayList<String> latitudes = new ArrayList<>();
                ArrayList<String> longitudes = new ArrayList<>();

                // Query PHP script's search_location function using location manager's returned lat and lon values
                try {
                    URL url = new URL("http://sandbox.kriswelsh.com/hygieneapi/hygiene.php?op=search_location&lat="+lat+"&long="+lon);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    InputStreamReader isr = new InputStreamReader(connection.getInputStream());
                    BufferedReader brIn = new BufferedReader(isr);
                    String line;


                    while ((line = brIn.readLine()) != null) {
                        JSONArray array = new JSONArray(line);

                        // Create new restaurant in each iteration of for-loop, additionally adding lat and lon values to their respective arraylists
                        for (int i=0; i<array.length(); i++) {
                            JSONObject o = (JSONObject) array.get(i);
                            Restaurant r = new Restaurant
                                    (o.getString("id"),
                                     o.getString("BusinessName"),
                                     o.getString("AddressLine1"),
                                     o.getString("AddressLine2"),
                                     o.getString("AddressLine3"),
                                     o.getString("PostCode"),
                                     o.getString("RatingValue"),
                                     o.getString("RatingDate"),
                                     o.getString("DistanceKM"),
                                     o.getJSONObject("Location").getString("Latitude"),
                                     o.getJSONObject("Location").getString("Longitude"));
                            aroundMe.add(r);
                            latitudes.add(o.getJSONObject("Location").getString("Latitude"));
                            longitudes.add(o.getJSONObject("Location").getString("Longitude"));
                        }
                    }
                    brIn.close();
                    double[] averageGPS = averageGPS(latitudes, longitudes);
                    int zoom = 12;
                    aroundMeActivity(aroundMe, averageGPS, zoom);
                }
                catch (MalformedURLException mue) { mue.printStackTrace(); }
                catch (IOException ioe) { ioe.printStackTrace(); }
                catch (JSONException je) { je.printStackTrace(); }
            }
        });

        // Launch activity to search by restaurant name
        searchByNameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent nameSearchIntent = new Intent(getApplicationContext(), SearchByName.class);
                startActivity(nameSearchIntent);
            }
        });

        // Launch activity to search by postcode
        searchByPostcodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent postcodeSearchIntent = new Intent(getApplicationContext(), SearchByPostcode.class);
                startActivity(postcodeSearchIntent);

            }
        });

        // Launch activity to view recently added items
        recentlyAddedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Arraylist for restaurant objects
                ArrayList<Restaurant> recentlyAdded = new ArrayList<>();
                try {

                    // Query PHP script's show_recent function
                    URL url = new URL("http://sandbox.kriswelsh.com/hygieneapi/hygiene.php?op=show_recent");
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    InputStreamReader isr = new InputStreamReader(connection.getInputStream());
                    BufferedReader brIn = new BufferedReader(isr);

                    String line;
                    while ((line = brIn.readLine()) != null) {
                        JSONArray array = new JSONArray(line);

                        // Create new restaurant in each iteration of for-loop
                        for (int i=0; i<array.length(); i++) {
                            JSONObject o = (JSONObject) array.get(i);
                            Restaurant r = new Restaurant
                                    (o.getString("id"),
                                            o.getString("BusinessName"),
                                            o.getString("AddressLine1"),
                                            o.getString("AddressLine2"),
                                            o.getString("AddressLine3"),
                                            o.getString("PostCode"),
                                            o.getString("RatingValue"),
                                            o.getString("RatingDate"),
                                            null,
                                            o.getJSONObject("Location").getString("Latitude"),
                                            o.getJSONObject("Location").getString("Longitude"));
                            recentlyAdded.add(r);
                        }
                    }
                    brIn.close();

                    // Set latitude, longitude, and zoom to set map 'camera' to take in most of mainland Britain
                    double[] ukGPS = {54.2, -2.7};
                    int zoom = 5;
                    aroundMeActivity(recentlyAdded, ukGPS, zoom);
                }
                catch (MalformedURLException mue) { mue.printStackTrace(); }
                catch (IOException ioe) { ioe.printStackTrace(); }
                catch (JSONException je) { je.printStackTrace(); }
                
            }
        });

    }

    // Set the position of the map 'camera' by getting the average GSP of restaurants
    public double[] averageGPS(ArrayList<String> latitudes, ArrayList<String> longitudes) {
        double latSum = 0;
        double lonSum = 0;

        for (int i=0; i<latitudes.size(); i++) {
            double lat = stringToDouble(latitudes.get(i));
            latSum += lat;
        }

        for (int i=0; i<longitudes.size(); i++) {
            double lon = stringToDouble(longitudes.get(i));
            lonSum += lon;
        }

        return new double[]{ (latSum/latitudes.size()), (lonSum/longitudes.size()) };
    }

    // Convert String to double
    public double stringToDouble(String string) {
        double returned;
        try {
            returned=Double.parseDouble(string);
        } catch (NumberFormatException e) {
            returned=0;
        }
        return returned;
    }

    // Launches activity that displays list of (either) restaurant near me or those recently added
    public void aroundMeActivity(ArrayList<Restaurant> array, double[] gps, int zoom) {
        Intent i = new Intent(this, AroundMeList.class);
        i.putExtra("com.example.findmecleanfood.array", array);
        i.putExtra("com.example.findmecleanfood.gps", gps);
        i.putExtra("com.example.findmecleanfood.zoom", zoom);
        startActivity(i);
    }
}