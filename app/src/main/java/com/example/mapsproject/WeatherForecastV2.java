package com.example.mapsproject;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.Gson;

import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.DefaultAsyncHttpClient;
import org.asynchttpclient.Response;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public class WeatherForecastV2 {
    private FusedLocationProviderClient fusedLocationClient;
    Context context;
    Gson gson = new Gson();
    WeatherDataV2 weatherData;
//    private double longitude;

//    public double getLongitude() {
//        return longitude;
//    }
//
//    public void setLongitude(double longitude) {
//        this.longitude = longitude;
//    }
//
//    public double getLatitude() {
//        return latitude;
//    }
//
//    public void setLatitude(double latitude) {
//        this.latitude = latitude;
//    }

//    private double latitude;

    WeatherForecastV2(Context context)
    {
        this.context=context;
    }

    public void getInfo (Double latitude, Double longitude)   throws IOException {
        Gson gson = new Gson();
        Log.d(TAG,  " chay gson ");
        String urlString = "http://api.weatherapi.com/v1/forecast.json?key=ee2ac5cf5785462898b114549241904&q=" + Double.toString(latitude)+","+Double.toString(longitude) +"&days=1&aqi=no&alerts=no";
        Log.d("TESTWEATHER", urlString);
        CompletableFuture.supplyAsync(() -> {
            try (AsyncHttpClient client = new DefaultAsyncHttpClient()) {
                Response response = client.prepare("GET", urlString)
                        .setHeader("accept", "application/json")
                        .execute()
                        .get();
                return gson.fromJson(response.getResponseBody(), WeatherDataV2.class);
            } catch (Exception e) {
                e.printStackTrace();
                Log.d("TESTWEATHER", "FAILEDD " + e.getMessage());
                return null;
            }
        }).thenAccept(response -> {
            if (response != null) {
                weatherData=response;
            } else {
                // Xử lý lỗi
            }
        }).join();
    }

//    public CompletableFuture<String> getLastLocation() {
//        CompletableFuture<String> future = new CompletableFuture<>();
//        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
//        String[] stringArray = new String[2];
//        if (ActivityCompat.checkSelfPermission(context,
//                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
//        {
//            Toast.makeText(context, "Location permission is required", Toast.LENGTH_SHORT).show();
//            return future;
//        }
//        fusedLocationClient.getLastLocation()
//                .addOnSuccessListener((MainActivity) context, new OnSuccessListener<Location>() {
//                    @Override
//                    public void onSuccess(Location location) {
//                        if (location != null) {
//                            // Got last known location
//                            latitude = location.getLatitude();
//                            longitude = location.getLongitude();
//                            Toast.makeText(context, "Latitude: " + latitude + ", Longitude: " + longitude, Toast.LENGTH_SHORT).show();
//                            future.complete(latitude+","+longitude);
//                        } else {
//                            Toast.makeText(context, "Location is null", Toast.LENGTH_SHORT).show();
//                            future.complete("");
//                        }
//                    }
//                });
//        return future;
//    }
}
