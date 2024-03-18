package com.example.mapsproject;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.google.android.gms.maps.model.LatLng;

import java.util.Calendar;

public class LocationHistory extends Service {
    private static final String TAG = "LocationService";
    private static final long INTERVAL = 5 * 1000;
    private Handler handler;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(locationRunnable, INTERVAL);
    }

    private Runnable locationRunnable = new Runnable() {
        @Override
        public void run() {
            saveLocation();
            handler.postDelayed(this, INTERVAL);
        }
    };

    private void saveLocation() {
        Location currentLocation = GlobalVariable.myMap.getMyLocation();
        Calendar currentTime = Calendar.getInstance();
        GlobalVariable.LocationHistory.add(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()));
        if (currentLocation != null) {
            Log.d(TAG, currentTime.get(Calendar.DAY_OF_MONTH) + "/" + currentTime.get(Calendar.MONTH) + 1 + "/" + currentTime.get(Calendar.YEAR)+ " " + currentTime.get(Calendar.HOUR_OF_DAY) + ":" + currentTime.get(Calendar.MINUTE) + " : " + currentLocation.getLatitude() + " " + currentLocation.getLongitude());
        }
        else {
            Log.d(TAG, "current location is null");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(locationRunnable);
    }
}