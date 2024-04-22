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
import com.google.android.libraries.places.api.model.Place;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class LocationHistory extends Service {
    private static final String TAG = "LocationService";
    private static final long INTERVAL = 60 * 1000;
    private Handler handler;
    FirebaseFirestore db;
    CollectionReference colRef;
    private DocumentReference docRef;

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
        this.docRef = db.collection("LocationHistory").document(GlobalVariable.userName);
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
        SessionManager session = new SessionManager(getApplicationContext());
        if (!session.isLoggedIn())
            return;

        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            GlobalVariable.myMap.setMyLocationEnabled(true);
        }
        else {
            return;
        }
        Location currentLocation = GlobalVariable.myMap.getMyLocation();
        Calendar currentTime = Calendar.getInstance();
        if (currentLocation != null) {
            GlobalVariable.LocationHistory.add(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()));
            String saveField = "History";
            this.docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.exists()) {
                        ArrayList<String> historyArray = (ArrayList<String>) documentSnapshot.get(saveField);

                        if (historyArray == null) {
                            historyArray = new ArrayList<>();
                        }
                        historyArray.add(currentTime.get(Calendar.DAY_OF_MONTH) + "-" + currentTime.get(Calendar.MONTH) + "-" + currentTime.get(Calendar.YEAR) + " " + currentLocation.getLatitude() + " " + currentLocation.getLongitude());
                        Map<String, Object> updates = new HashMap<>();
                        updates.put(saveField, historyArray);
                        docRef.update(updates).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "Document updated successfully!");
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d(TAG, "Error updating document", e);
                            }
                        });
                    } else {
                        Log.d(TAG, "Document does not exist, create a new one");
                        ArrayList<String> historyArray = new ArrayList<>();
                        historyArray.add(currentTime.get(Calendar.DAY_OF_MONTH) + "-" + currentTime.get(Calendar.MONTH) + "-" + currentTime.get(Calendar.YEAR) + " " + currentLocation.getLatitude() + " " + currentLocation.getLongitude());


                        Map<String, Object> data = new HashMap<>();
                        data.put(saveField, historyArray);

                        docRef.set(data)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d(TAG, "Document created successfully!");
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w(TAG, "Error creating document", e);
                                    }
                                });
                    }
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