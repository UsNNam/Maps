package com.example.mapsproject;

import android.content.Context;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.example.mapsproject.Entity.TravelMode;
import com.google.android.gms.maps.model.MarkerOptions;

public class RouteActivity {
    private Context context;
    private MainActivity mainActivity;
    LinearLayout routeLayout;
    EditText startLocationEditText;
    EditText endLocationEditText;
    ImageButton backButtonDirection;
    LinearLayout searchLayout;

    Button directionByCar;
    Button directionByMotor;
    Button directionByTransit;


    public RouteActivity(Context context) {
        this.context = context;
        mainActivity = (MainActivity) context;
        routeLayout = mainActivity.findViewById(R.id.routeLayout);
        searchLayout = ((MainActivity) context).findViewById(R.id.searchLayout);
        startLocationEditText = mainActivity.findViewById(R.id.startLocationEditText);
        endLocationEditText = mainActivity.findViewById(R.id.destinationEditText);
        backButtonDirection = mainActivity.findViewById(R.id.backButtonDirection);

        directionByCar = mainActivity.findViewById(R.id.directionByCar);
        directionByMotor = mainActivity.findViewById(R.id.directionByMotor);
        directionByTransit = mainActivity.findViewById(R.id.directionByTransit);

        backButtonDirection.setOnClickListener(v -> {
            routeLayout.setVisibility(LinearLayout.GONE);
            searchLayout.setVisibility(LinearLayout.VISIBLE);
        });

        directionByCar.setOnClickListener(v -> {
            new HttpRequestTask(context, TravelMode.DRIVE).execute();
        });

        directionByMotor.setOnClickListener(v -> {
            new HttpRequestTask(context, TravelMode.MOTOR).execute();
        });

        directionByTransit.setOnClickListener(v -> {
            new HttpRequestTask(context, TravelMode.TRANSIT).execute();
        });
    }

    public void displayRoute(String startLocation, String endLocation) {
        searchLayout.setVisibility(LinearLayout.GONE);
        routeLayout.setVisibility(LinearLayout.VISIBLE);
        startLocationEditText.setText(startLocation);
        endLocationEditText.setText(endLocation);
        new HttpRequestTask(context, TravelMode.DRIVE).execute();

    }


}
