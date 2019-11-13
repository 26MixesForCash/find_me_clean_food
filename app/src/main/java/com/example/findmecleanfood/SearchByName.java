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

public class SearchByName extends AppCompatActivity {

    EditText enterNameEditText;
    Button searchNameButton, clearButton, backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_by_name);

        enterNameEditText = findViewById(R.id.enterNameEditText);
        searchNameButton = findViewById(R.id.searchNameButton);
        clearButton = findViewById(R.id.clearNameButton);
        backButton = findViewById(R.id.backButton);


        searchNameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // User input restaurant search item
                String name = enterNameEditText.getText()+"";

                // Show toast notifications if restaurant name is invalid
                if (nameIsInvalid(name)) {
                    makeToast(name);
                } else {

                    // Arraylist for restaurant objects
                    ArrayList<Restaurant> nameSearch = new ArrayList<>();
                    try {

                        // Query PHP script's search_name function, appending user input restaurant search item
                        URL url = new URL("http://sandbox.kriswelsh.com/hygieneapi/hygiene.php?op=search_name&name=" + name);
                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                        InputStreamReader isr = new InputStreamReader(connection.getInputStream());
                        BufferedReader brIn = new BufferedReader(isr);

                        String line;
                        while ((line = brIn.readLine()) != null) {
                            JSONArray array = new JSONArray(line);

                            // New restaurant in each iteration of for-loop
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
                                nameSearch.add(r);
                            }
                        }

                        // Set latitude, longitude, and zoom to set map 'camera' to take in most of mainland Britain
                        double[] ukGPS = {54.2, -2.7};
                        int zoom = 5;
                        nameSearchActivity(nameSearch, ukGPS, zoom);
                        brIn.close();

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

        // Clears EditText field
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enterNameEditText.setText("");
            }
        });

        // Back
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBack();
            }
        });

    }

    // Check user restaurant input is valid
    public boolean nameIsInvalid(String name) {
        return name.length() == 0 || name.length()<=2;
    }

    // Toast notifications in event of invalid restaurant name
    public void makeToast(String name) {
        if (name.length() == 0) {
            Toast blank = Toast.makeText(getApplicationContext(),R.string.name_blank,Toast.LENGTH_LONG);
            blank.show();
        } else if (name.length()<=2) {
            Toast tooShort = Toast.makeText(getApplicationContext(), R.string.too_short, Toast.LENGTH_LONG);
            tooShort.show();
        }
    }

    // Back method
    public void goBack() {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }

    // Launch activity with returned search, passing arraylist of restaurant objects, plus position and zoom of map 'camera'
    public void nameSearchActivity(ArrayList<Restaurant> array, double[] gps, int zoom) {
        Intent i = new Intent(this, NameSearchList.class);
        i.putExtra("com.example.findmecleanfood.array", array);
        i.putExtra("com.example.findmecleanfood.gps", gps);
        i.putExtra("com.example.findmecleanfood.zoom", zoom);
        startActivity(i);
    }
}
