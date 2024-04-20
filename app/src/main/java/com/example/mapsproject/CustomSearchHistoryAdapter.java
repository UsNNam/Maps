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
    List<String> placeNames;
    List<String> placeAddresses;

    private PlacesClient placesClient;
    public CustomSearchHistoryAdapter(Context context, int layoutToBeInflated, List<String> placeids, List<String> placeNames, List<String> placeAddress)
    {
        super(context, layoutToBeInflated, placeids);
        this.placeIds = placeids;
        this.placeNames = placeNames;
        this.placeAddresses = placeAddress;
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

        placeName.setText(placeNames.get(position));
        placeAddress.setText(placeAddresses.get(position));

        return convertView;
    }
}
