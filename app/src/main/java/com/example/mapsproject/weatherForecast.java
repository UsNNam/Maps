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

public class weatherForecast
{
    private FusedLocationProviderClient fusedLocationClient;
    Context context;
    Gson gson = new Gson();
    WeatherData weatherData;
    private double longitude, latitude;

        weatherForecast(Context context)
        {
            this.context=context;
        }

    public void getInfo ()   throws IOException {
        Gson gson = new Gson();
        Log.d(TAG,  " chay gson ");
        String urlString = "https://api.tomorrow.io/v4/weather/forecast?location=" + Double.toString(latitude)+"%2C%20"+Double.toString(longitude) +"&timesteps=1h&apikey=tdwpx8pUNl9n3eyx2hOsT35dS4lWL7EO";
        Log.d("TESTWEATHER", urlString);
        CompletableFuture.supplyAsync(() -> {
            try (AsyncHttpClient client = new DefaultAsyncHttpClient()) {

                Response response = client.prepare("GET", urlString)
                        .setHeader("accept", "application/json")
                        .execute()
                        .get();
                return gson.fromJson(response.getResponseBody(), WeatherData.class);
            } catch (Exception e) {
                e.printStackTrace();
                Log.d("TESTWEATHER", "FAILEDD " + e.getMessage());
                return null;
            }
        }).thenAccept(weatherdata -> {
            if (weatherdata != null) {
                weatherData=weatherdata;
            } else {
                // Xử lý lỗi
            }
        }).join();
    }

        private CompletableFuture<String> getLastLocation() {
            CompletableFuture<String> future = new CompletableFuture<>();
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
            String[] stringArray = new String[2];
            if (ActivityCompat.checkSelfPermission(context,
                    android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            {
                Toast.makeText(context, "Location permission is required", Toast.LENGTH_SHORT).show();
                return future;
            }
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener((MainActivity) context, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                // Got last known location
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();

                                Toast.makeText(context, "Latitude: " + latitude + ", Longitude: " + longitude, Toast.LENGTH_SHORT).show();
                                future.complete(latitude+","+longitude);
                            } else {
                                Toast.makeText(context, "Location is null", Toast.LENGTH_SHORT).show();
                                future.complete("");
                            }
                        }
                    });
            return future;
        }



}
