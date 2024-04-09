package com.example.mapsproject;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.app.Activity;
import android.content.Context;
import android.location.Address;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.common.api.ApiException;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.util.Arrays;
import java.util.List;

public class CustomSearchHistoryAdapter extends ArrayAdapter<String> {
    Context context;
    List<String> placeIds;
    private PlacesClient placesClient;
    public CustomSearchHistoryAdapter(Context context, int layoutToBeInflated, List<String> placeids)
    {
        super(context, layoutToBeInflated, placeids);
        this.placeIds = placeids;
        this.context = context;
        placesClient = Places.createClient(context);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        convertView = inflater.inflate(R.layout.search_history_item, null);

        TextView placeName = (TextView) convertView.findViewById(R.id.placeName);
        TextView placeAddress = (TextView) convertView.findViewById(R.id.placeAddress);

        List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS);
        FetchPlaceRequest request = FetchPlaceRequest.newInstance(placeIds.get(position), placeFields);

        placesClient.fetchPlace(request).addOnSuccessListener((response) -> {
            Place place = response.getPlace();
            placeName.setText(place.getName());
            placeAddress.setText(place.getAddress());
        }).addOnFailureListener((exception) -> {
            Log.e("CustomSearchHistoryAdapter", "Place not found: " + exception.getMessage());
        });

        return convertView;
    }
}
