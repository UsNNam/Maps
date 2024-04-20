package com.example.mapsproject;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.common.api.ApiException;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import com.google.android.libraries.places.api.Places;
public class MarkResultDetail {
    String placeID = "";
    PlacesClient placesClient;
    Context context;
    public String api_key= "AIzaSyC4eQTS4oxvsgONLXCsbeBFUp68WhXYTJ0";
    MarkResultDetail(Context context)
    {
        Places.initialize(context,api_key );
        this.context=context;
        placesClient = Places.createClient(this.context);
    }
    public void fetchResultDetail(String placeID,  MarkResultDetailCallback callback) {
        List<Place.Field> placeFields = Arrays.asList( Place.Field.ID, Place.Field.NAME, Place.Field.PHONE_NUMBER,  Place.Field.ADDRESS, Place.Field.LAT_LNG, Place.Field.RATING, Place.Field.USER_RATINGS_TOTAL, Place.Field.WEBSITE_URI, Place.Field.PRICE_LEVEL, Place.Field.CURRENT_OPENING_HOURS);
        FetchPlaceRequest request = FetchPlaceRequest.newInstance(placeID, placeFields);

        Log.d ("TESTDETAIL", "CHAY O DAY");
        placesClient.fetchPlace(request).addOnSuccessListener((response) -> {
            Place place = response.getPlace();
            try {
                callback.onCallback(place);
                Log.d ("TESTDETAIL", "CHAY O SUCCESS "+place );
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }).addOnFailureListener((exception) -> {
            if (exception instanceof ApiException) {
                final ApiException apiException = (ApiException) exception;
                Log.e("TEST", "Place not found: " + exception.getMessage());
                final int statusCode = apiException.getStatusCode();
                // TODO: Handle error with given status code.
            }
            Log.d ("TESTDETAIL", "CHAY O DAY FAIL");
        });
    }
    public interface MarkResultDetailCallback
    {
        void onCallback(Place place) throws IOException ;
    }
}
