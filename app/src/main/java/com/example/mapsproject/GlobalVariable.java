package com.example.mapsproject;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;

import java.util.ArrayList;

public class GlobalVariable {
    public static GoogleMap myMap;
    public static Polyline polyline;
    public static Polyline locationHistoryLine;
    public  static ArrayList<LatLng> LocationHistory = new ArrayList<>();


}
