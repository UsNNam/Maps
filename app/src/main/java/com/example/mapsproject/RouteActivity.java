package com.example.mapsproject;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Context;
import android.content.Intent;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.mapsproject.TaxiActivity;
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
    Button taxiPriceButton;

    TextView summaryTextView;


    public RouteActivity(Context context) {
        this.context = context;
        mainActivity = (MainActivity) context;
        routeLayout = mainActivity.findViewById(R.id.routeLayout);
        searchLayout = ((MainActivity) context).findViewById(R.id.searchLayout);
        startLocationEditText = mainActivity.findViewById(R.id.startLocationEditText);
        endLocationEditText = mainActivity.findViewById(R.id.destinationEditText);
        backButtonDirection = mainActivity.findViewById(R.id.backButtonDirection);
        taxiPriceButton = mainActivity.findViewById(R.id.taxiPriceButton);
        summaryTextView = mainActivity.findViewById(R.id.summaryTextView);

        directionByCar = mainActivity.findViewById(R.id.directionByCar);
        directionByMotor = mainActivity.findViewById(R.id.directionByMotor);

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

        taxiPriceButton.setOnClickListener( v -> {
            Intent intent = new Intent(mainActivity, TaxiActivity.class);
            String routeInfo = summaryTextView.getText().toString();
            String[] routeInfoArr = routeInfo.split(" km");

            intent.putExtra("distance", Double.parseDouble(routeInfoArr[0]));
            startActivity(context, intent,null);
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
