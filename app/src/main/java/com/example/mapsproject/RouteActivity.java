package com.example.mapsproject;

import android.content.Context;
import android.view.KeyEvent;
import android.widget.EditText;
import android.widget.LinearLayout;

public class RouteActivity {
    private Context context;
    private MainActivity mainActivity;
    LinearLayout routeLayout;
    EditText startLocationEditText;
    EditText endLocationEditText;


    public RouteActivity(Context context) {
        this.context = context;
        mainActivity = (MainActivity) context;
        routeLayout = mainActivity.findViewById(R.id.routeLayout);
        startLocationEditText = mainActivity.findViewById(R.id.startLocationEditText);
        endLocationEditText = mainActivity.findViewById(R.id.destinationEditText);
    }

    public void displayRoute(String startLocation, String endLocation) {
        routeLayout.setVisibility(LinearLayout.VISIBLE);
        startLocationEditText.setText(startLocation);
        endLocationEditText.setText(endLocation);
        endLocationEditText.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER));
        endLocationEditText.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_ENTER));

    }


}
