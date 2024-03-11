package com.example.mapsproject;

import android.content.Context;
import android.view.KeyEvent;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

public class RouteActivity {
    private Context context;
    private MainActivity mainActivity;
    LinearLayout routeLayout;
    EditText startLocationEditText;
    EditText endLocationEditText;
    ImageButton backButtonDirection;
    LinearLayout searchLayout;


    public RouteActivity(Context context) {
        this.context = context;
        mainActivity = (MainActivity) context;
        routeLayout = mainActivity.findViewById(R.id.routeLayout);
        searchLayout = ((MainActivity) context).findViewById(R.id.searchLayout);
        startLocationEditText = mainActivity.findViewById(R.id.startLocationEditText);
        endLocationEditText = mainActivity.findViewById(R.id.destinationEditText);
        backButtonDirection = mainActivity.findViewById(R.id.backButtonDirection);

        backButtonDirection.setOnClickListener(v -> {
            routeLayout.setVisibility(LinearLayout.GONE);
            searchLayout.setVisibility(LinearLayout.VISIBLE);
        });
    }

    public void displayRoute(String startLocation, String endLocation) {
        searchLayout.setVisibility(LinearLayout.GONE);
        routeLayout.setVisibility(LinearLayout.VISIBLE);
        startLocationEditText.setText(startLocation);
        endLocationEditText.setText(endLocation);
        new HttpRequestTask(context).execute();

    }


}
