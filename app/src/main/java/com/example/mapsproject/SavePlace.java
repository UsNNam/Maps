package com.example.mapsproject;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;

public class SavePlace {
    private double longtitude;
    private double langtitude;
    private String id;
    ArrayList<HashMap<String,Object>> favoriteplace;
    SavePlaceDB sp;
    private Context context;
    SavePlace(String username, Context context)
    {

        this.sp = new SavePlaceDB(username, context);
        favoriteplace = new ArrayList<>();
    };

    public void setMarkOnMap(@NonNull GoogleMap googleMap) {
        sp.readData(new SavePlaceDB.FirestoreCallback() {
            @Override
            public void onCallback(ArrayList<HashMap<String, Object>> list) {
                Log.d("TAG", list + " longtitude latitude");
                for (HashMap i : list) {
                    LatLng parsedPosition = new LatLng((Double) i.get("latitude"), (Double) i.get("longitude"));
                    Log.d("TAG", parsedPosition + " longtitude latitude");
                    Marker temp = googleMap.addMarker(new MarkerOptions().position(parsedPosition).title(i.get("placeName").toString()));
                    temp.setTag(i.get("placeID").toString());
                    temp.showInfoWindow();
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
                    if (Double.compare(geo.latitude , (Double) j.get("latitude"))==0 &&
                            Double.compare( geo.longitude ,(Double) j.get("longtitude"))==0)
                    {
                        Log.d(TAG, "run here ["+geo + "] , "+ j.get("latitude")+ ", "+j.get("longtitude") );
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
    public void removeSavePlace(String id, String name, Double latitude, Double longitude, View v)
    {
        sp.removeSavePlace(id, name, latitude, longitude);
        v.setSelected(false);
    }

    public void addSavePlaceV2(String id, String name, Double latitude, Double longitude,  View v) {
        sp.createSavePlaceV2(id, name, latitude, longitude);
        v.setSelected(true);
    }
}

