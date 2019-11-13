package com.example.findmecleanfood;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class RestaurantListAdapter extends ArrayAdapter<Restaurant> {

    private Context aContext;
    private int aResource;

    public RestaurantListAdapter(Context context, int resource, ArrayList<Restaurant> restaurants) {
        super(context, resource, restaurants);
        aContext = context;
        aResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Get restaurant information required for display
        String name = getItem(position).getName();
        String addressLine1 = getItem(position).getAddressLine1();
        String addressLine2 = getItem(position).getAddressLine2();
        String addressLine3 = getItem(position).getAddressLine3();
        String postcode = getItem(position).getPostCode();
        String ratingValue = getItem(position).getRatingValue();
        String distance = getItem(position).getDistance();


        LayoutInflater inflater = LayoutInflater.from(aContext);
        convertView = inflater.inflate(aResource, parent, false);

        TextView tvName = convertView.findViewById(R.id.name);
        TextView tvAddressLine1 = convertView.findViewById(R.id.addressLine1);
        TextView tvAddressLine2 = convertView.findViewById(R.id.addressLine2);
        TextView tvAddressLine3 = convertView.findViewById(R.id.addressLine3);
        TextView tvPostcode = convertView.findViewById(R.id.postcode);
        TextView tvDistance = convertView.findViewById(R.id.distance);
        ImageView ratingImage = convertView.findViewById(R.id.ratingImage);

        // Set restaurant values
        tvName.setText(name);
        tvAddressLine1.setText(addressLine1);
        tvAddressLine2.setText(addressLine2);
        tvAddressLine3.setText(addressLine3);
        tvPostcode.setText(postcode);

        // Ensure distance is only displayed in 'Search around me' search
        if (isNotNullOrEmpty(distance)) {
            tvDistance.setText("Distance: "+distance.substring(0, 4)+" km");
        } else {
            tvDistance.setText("");
        }

        // Assign correct ratings picture
        int rating = stringToInt(ratingValue);
        switch (rating) {
            case -1: ratingImage.setImageResource(R.drawable.rating_awaiting); break;
            case 0: ratingImage.setImageResource(R.drawable.rating0); break;
            case 1: ratingImage.setImageResource(R.drawable.rating1); break;
            case 2: ratingImage.setImageResource(R.drawable.rating2); break;
            case 3: ratingImage.setImageResource(R.drawable.rating3); break;
            case 4: ratingImage.setImageResource(R.drawable.rating4); break;
            case 5: ratingImage.setImageResource(R.drawable.rating5); break;
        }

        return convertView;
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

    public static boolean isNotNullOrEmpty(String string) {

        return (string!=null && !string.isEmpty());
    }
}
