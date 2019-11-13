package com.example.findmecleanfood;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import static android.widget.Toast.LENGTH_SHORT;

public class SearchByPostcode extends AppCompatActivity {

    EditText enterPostcodeEditText;
    Button searchPostcodeButton, clearPostcodeButton, backButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_by_postcode);

        enterPostcodeEditText = findViewById(R.id.enterPostcodeEditText);
        searchPostcodeButton = findViewById(R.id.searchPostcodeButton);
        clearPostcodeButton = findViewById(R.id.clearPostcodeButton);
        backButton = findViewById(R.id.backButton);


        searchPostcodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // User input postcode search item
                String postcode = enterPostcodeEditText.getText()+"";

                // Show toast notifications if restaurant name is invalid
                if (postCodeIsInvalid(postcode)) {
                    makeToast(postcode);
                } else {

                    // Arraylists for restaurant objects, and for lat and lon values, to get average GSP
                    ArrayList<Restaurant> postcodeSearch = new ArrayList<>();
                    ArrayList<String> latitudes = new ArrayList<>();
                    ArrayList<String> longitudes = new ArrayList<>();
                    try {

                        // Query PHP script's search_postcode function appending user input postcode search item
                        URL url = new URL("http://sandbox.kriswelsh.com/hygieneapi/hygiene.php?op=search_postcode&postcode=" + postcode);
                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                        InputStreamReader isr = new InputStreamReader(connection.getInputStream());
                        BufferedReader brIn = new BufferedReader(isr);

                        String line;
                        while ((line = brIn.readLine()) != null) {
                            JSONArray array = new JSONArray(line);

                            // Create new restaurant in each iteration of loop, additionally adding lat and lon values to their respective arraylists
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject o = (JSONObject) array.get(i);
                                Restaurant r = new Restaurant(
                                        o.getString("id"),
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
                                postcodeSearch.add(r);
                                latitudes.add(o.getJSONObject("Location").getString("Latitude"));
                                longitudes.add(o.getJSONObject("Location").getString("Longitude"));
                            }
                        }
                        brIn.close();
                        double[] averageGPS = averageGPS(latitudes, longitudes);
                        int zoom = 13;
                        postcodeSearchActivity(postcodeSearch, averageGPS, zoom);

                    } catch (MalformedURLException mue) {
                        mue.printStackTrace();
                    } catch (IOException ioe) {
                        ioe.printStackTrace();
                    } catch (JSONException je) {
                        je.printStackTrace();
                    }
                }
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBack();
            }
        });

        // Clear text
        clearPostcodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enterPostcodeEditText.setText("");
            }
        });

    }

    // Check user-entered postcode is valid
    public boolean postCodeIsInvalid(String postcode) {
        return postcode.length() == 0 || postcode.length()<=2 || Character.isDigit(postcode.charAt(0));
    }

    // Toast notifications in event of invalid postcode entered
    public void makeToast(String postcode) {
        if (postcode.length() == 0) {
            Toast blank = Toast.makeText(getApplicationContext(),R.string.postcode_blank,Toast.LENGTH_LONG);
            blank.show();
        } else if (postcode.length()<=2) {
            Toast tooShort = Toast.makeText(getApplicationContext(), R.string.too_short, Toast.LENGTH_LONG);
            tooShort.show();
        } else if (Character.isDigit(postcode.charAt(0))) {
            Toast invalid = Toast.makeText(getApplicationContext(), R.string.invalid_postcode, Toast.LENGTH_LONG);
            invalid.show();
        }
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

    // Method to calculate and return average GPS of restaurants in search
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

    public void goBack() {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }

    // Launch activity with returned search, passing arraylist of restaurant objects, plus position and zoom of map 'camera'
    public void postcodeSearchActivity(ArrayList<Restaurant> array, double[] gps, int zoom) {
        Intent i = new Intent(this, PostcodeSearchList.class);
        i.putExtra("com.example.findmecleanfood.array", array);
        i.putExtra("com.example.findmecleanfood.gps", gps);
        i.putExtra("com.example.findmecleanfood.zoom", zoom);
        startActivity(i);
    }
}
