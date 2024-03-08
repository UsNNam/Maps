package com.example.mapsproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;

import java.util.List;


public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    EditText destinationEditText;
    Context curContext;
    private LinearLayout bottomSheet;
    private GestureDetector gestureDetector;
    private int originalHeight;
    private int expandedHeight;
    private float startY;
    FragmentTransaction ft;
    SearchFragment searchFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
            destinationEditText = findViewById(R.id.destinationEditText);
            curContext = this;
            ft = getSupportFragmentManager().beginTransaction();
            searchFragment = SearchFragment.newInstance("search_fragment");
            ft.replace(R.id.searchFragment, searchFragment);
            ft.commit();

            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);


            bottomSheet = findViewById(R.id.bottomSheet);
            originalHeight = getResources().getDimensionPixelSize(R.dimen.bottom_sheet_original_height);
            expandedHeight = getResources().getDimensionPixelSize(R.dimen.bottom_sheet_expanded_height);

            bottomSheet.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            startY = event.getRawY();
                            return true;
                        case MotionEvent.ACTION_MOVE:
                            float deltaY = (event.getRawY() - startY) / 10;
                            int newHeight = (int) (bottomSheet.getHeight() - deltaY);
                            if (newHeight < originalHeight) {
                                newHeight = originalHeight;
                            } else if (newHeight > expandedHeight) {
                                newHeight = expandedHeight;
                            }
                            bottomSheet.getLayoutParams().height = newHeight;
                            bottomSheet.requestLayout();
                            return true;
                    }
                    return false;
                }
            });
        } catch (Exception e) {
            Log.e("Error Main Activity", e.getMessage());
        }

    }

    private void expandBottomSheet() {
        bottomSheet.getLayoutParams().height = expandedHeight;
        bottomSheet.requestLayout();
    }

    private void collapseBottomSheet() {
        bottomSheet.getLayoutParams().height = originalHeight;
        bottomSheet.requestLayout();
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        GlobalVariable.myMap = googleMap;
        LatLng sydney = new LatLng(10.761214, 106.682071);
        googleMap.addMarker(new MarkerOptions()
                .position(sydney)
                .title("Marker in Sydney"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        // Zoom in the Google Map, people don't need to zoom in manually
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));

        destinationEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    try {
                        new HttpRequestTask(curContext).execute();
                    } catch (Exception e) {
                        Log.e("Error HTTP", e.getMessage());
                    }
                    return true; // Return true to consume the event
                }
                return false; // Return false to pass the event to other listeners
            }
        });


    }
}
