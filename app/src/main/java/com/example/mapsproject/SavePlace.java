package com.example.mapsproject;

import static android.content.ContentValues.TAG;

import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;

public class SavePlace {
    private double longtitude;
    private double langtitude;

    ArrayList<HashMap<String,Object>> favoriteplace;
    SavePlaceDB sp;
    SavePlace(String username)
    {
        this.sp = new SavePlaceDB(username);
        favoriteplace = new ArrayList<>();
    };

    public void setMarkOnMap(@NonNull GoogleMap googleMap) {
        sp.readData(new SavePlaceDB.FirestoreCallback() {
            @Override
            public void onCallback(ArrayList<HashMap<String, Object>> list) {
                Log.d("TAG", list + " longitude latitude");
                for (HashMap<String, Object> i : list) {
                    // Retrieve the values and check for null
                    Object longitudeObj = i.get("longitude");
                    Object latitudeObj = i.get("latitude");

                    if (longitudeObj == null || latitudeObj == null) {
                        Log.d("TAG", "Longitude or latitude is null");
                        continue; // Skip this iteration if either value is null
                    }

                    // Now safe to cast since nulls are handled
                    double longitude = (Double) longitudeObj;
                    double latitude = (Double) latitudeObj;

                    LatLng parsedPosition = new LatLng(latitude, longitude);
                    Log.d("TAG", parsedPosition + " longitude latitude");
                    googleMap.addMarker(new MarkerOptions().position(parsedPosition).title("New Marker"));
                }
            }
        });
    }

    public void setSelectedButton(LatLng geo, CustomResultSearchAdapter.ViewHolder holder)
    {
        sp.readData(new SavePlaceDB.FirestoreCallback() {
            @Override
            public void onCallback(ArrayList<HashMap<String, Object>> list) {
                for (HashMap j : list)
                {
                    Log.d(TAG, "chay o day ");
                    if (geo.latitude == (Double) j.get("latitude") &&
                            geo.longitude == (Double) j.get("longtitude"))
                    {
                        Log.d(TAG, "run here");
                        holder.save.setSelected(true);
                        break;
                    }
                }
            }
        });
    }
    public void addSavePlace(Double latitude, Double longtitude, View v)
    {
        sp.createSavePlace(latitude, longtitude);
        v.setSelected(true);
    }
    public void removeSavePlace(Double latitude, Double longtitude, View v)
    {
        sp.removeSavePlace(latitude, longtitude);
        v.setSelected(false);
    }
    public double getLangtitude() {
        return langtitude;
    }

    public double getLongtitude() {
        return longtitude;
    }



    public void setLangtitude(double langtitude) {
        this.langtitude = langtitude;
    }

    public void setLongtitude(double longtitude) {
        this.longtitude = longtitude;
    }


}

