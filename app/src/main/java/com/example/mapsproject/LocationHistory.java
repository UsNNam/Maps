package com.example.mapsproject;

import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class LocationHistory extends Service {
    private static final String TAG = "LocationService";
    private static final long INTERVAL = 5 * 60 * 1000;
    private Handler handler;
    FirebaseFirestore db;
    CollectionReference colRef;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        this.db= FirebaseFirestore.getInstance();
        this.colRef = db.collection("LocationHistory");
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
        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            GlobalVariable.myMap.setMyLocationEnabled(true);
        }
        else {
            return;
        }
        Location currentLocation = GlobalVariable.myMap.getMyLocation();
//        Calendar currentTime = Calendar.getInstance();
        if (currentLocation != null) {
            GlobalVariable.LocationHistory.add(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()));

            Map<String, Object> data = new HashMap<>();
            data.put("Latitude", currentLocation.getLatitude());
            data.put("Longtitude", currentLocation.getLongitude());
            data.put("DateTime", new Date());

            colRef.add(data)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Log.d(TAG, "Document added with ID: " + documentReference.getId());
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Error adding document");
                        }
                    });
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