package com.example.mapsproject;

import static androidx.core.content.ContextCompat.startActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mapsproject.TaxiActivity;
import com.example.mapsproject.Entity.TravelMode;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.material.internal.TextWatcherAdapter;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;

public class RouteActivity {
    private Context context;
    private MainActivity mainActivity;
    LinearLayout routeLayout;
    static EditText startLocationEditText;
    static EditText endLocationEditText;
    ImageButton backButtonDirection;
    LinearLayout searchLayout;

    Button directionByCar;
    Button directionByMotor;
    Button taxiPriceButton;

    TextView summaryTextView;

    ImageButton addLocationButton;

    TextInputLayout addLocationTextInputLayout;
    EditText addLocationEditText;

    private ListView suggestionsListView;
    private ArrayAdapter<String> adapter;
    private List<String> suggestions;


    @SuppressLint("RestrictedApi")
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

        addLocationButton = mainActivity.findViewById(R.id.addWayPointButton);
        addLocationTextInputLayout = mainActivity.findViewById(R.id.wayPointInputLayout);
        addLocationEditText = mainActivity.findViewById(R.id.wayPointEditText);

        suggestionsListView = mainActivity.findViewById(R.id.suggestionsListView);

        startLocationEditText.addTextChangedListener(new TextWatcherAdapter() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
            @Override
            public void afterTextChanged(Editable s) {
                try{
                    mainActivity.searchFragment.callApiSearchTextNew(s.toString(), startLocationEditText,
                            suggestionsListView);

                }catch (Exception e){
                    Log.e("Error After text changed", e.getMessage());
                }
            }
        });

        endLocationEditText.addTextChangedListener(new TextWatcherAdapter() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
            @Override
            public void afterTextChanged(Editable s) {
                mainActivity.searchFragment.callApiSearchTextNew(s.toString(), endLocationEditText,
                        suggestionsListView);
            }
        });

        addLocationEditText.addTextChangedListener(new TextWatcherAdapter() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
            @Override
            public void afterTextChanged(Editable s) {
                mainActivity.searchFragment.callApiSearchTextNew(s.toString(), addLocationEditText,
                        suggestionsListView);
            }
        });


        addLocationButton.setOnClickListener(v -> {
                addLocationTextInputLayout.setVisibility(LinearLayout.VISIBLE);
                addLocationButton.setVisibility(LinearLayout.GONE);

        });

        //Enter on keyboard when mouse focus on wayPointEditText
        addLocationEditText.setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                new HttpRequestTask(context, TravelMode.DRIVE).execute();
                return true;
            }
            return false;
        });

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

            if(routeInfoArr.length <= 1){
                Toast.makeText(context, "Please select a route", Toast.LENGTH_SHORT).show();
                return;
            }

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

    static public void attachSelectedStartLocation(Place place, String locationString){
        startLocationEditText.setText(locationString);
    }

    static public void attachSelectedEndLocation(Place place, String locationString){
        endLocationEditText.setText(locationString);
    }

    public void displayRouteInfo(){
        startLocationEditText.setText("");
        endLocationEditText.setText("");
        addLocationEditText.setText("");
        searchLayout.setVisibility(LinearLayout.GONE);
        routeLayout.setVisibility(LinearLayout.VISIBLE);
        addLocationButton.setVisibility(LinearLayout.VISIBLE);
        addLocationTextInputLayout.setVisibility(LinearLayout.GONE);
    }


}
