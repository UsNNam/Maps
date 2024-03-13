package com.example.mapsproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.mapsproject.Entity.TravelMode;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


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

    private ImageButton mapStyleButton;
    private LinearLayout mapSelector;
    private ImageButton mapStyleClose;
    private GridView mapTypeGrid;
    private GridView mapDetailGrid;
    private final String[] mapTypes = {"Default", "Satellite", "Terrain"};
    private final String[] mapDetails = {"Traffic"};
    private final Integer[] typeThumbnails = {R.drawable.default_map_type, R.drawable.satellite_map_type, R.drawable.terrain_map_type};
    private final Integer[] detailThumbnails = {R.drawable.traffic_map_detail};
    private int currentMapStyle = 0;
    private boolean[] mapDetailList = {false};

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

            mapSelector = (LinearLayout) findViewById(R.id.mapStyleSelector);
            mapStyleButton = (ImageButton) findViewById(R.id.mapStyleButton);
            mapStyleClose = (ImageButton) findViewById(R.id.mapStyleClose);
            mapSelector = (LinearLayout) findViewById(R.id.mapStyleSelector);
            mapTypeGrid = (GridView) findViewById(R.id.mapTypeGrid);
            mapDetailGrid = (GridView) findViewById(R.id.mapDetailGrid);

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
                        new HttpRequestTask(curContext, TravelMode.DRIVE).execute();
                    } catch (Exception e) {
                        Log.e("Error HTTP", e.getMessage());
                    }
                    return true; // Return true to consume the event
                }
                return false; // Return false to pass the event to other listeners
            }
        });

        mapStyleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapSelector.setVisibility(View.VISIBLE);
            }
        });

        mapStyleClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapSelector.setVisibility(View.INVISIBLE);
            }
        });

        mapTypeGrid.setAdapter(new MapTypeImageAdapter(this, typeThumbnails, mapTypes));

        //thay đổi kiểu hiển thị của style hiện tại trong bảng lựa chọn khi gridView đã vẽ xong hết item
        mapTypeGrid.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                View currentStyle = mapTypeGrid.getChildAt(currentMapStyle);
                setMapStyleSelected(null, currentStyle);
                //loại bỏ để tránh gọi lại không cần thiết
                mapTypeGrid.removeOnLayoutChangeListener(this);
            }
        });
        mapTypeGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == currentMapStyle)
                    return;
                if (position == 1) {
                    googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                } else if (position == 2) {
                    googleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                } else {
                    googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                    googleMap.setBuildingsEnabled(true);
                }
                View oldItem = mapTypeGrid.getChildAt(currentMapStyle);
                currentMapStyle = position;

                setMapStyleSelected(oldItem, view);
            }
        });
        mapDetailGrid.setAdapter(new MapTypeImageAdapter(this, detailThumbnails, mapDetails));
        mapDetailGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0)
                    if (!mapDetailList[position]) {
                        googleMap.setTrafficEnabled(true);
                        mapDetailList[position] = true;
                        setMapStyleSelected(null, mapDetailGrid.getChildAt(position));
                    } else {
                        googleMap.setTrafficEnabled(false);
                        mapDetailList[position] = false;
                        setMapStyleSelected(mapDetailGrid.getChildAt(position), null);
                    }
            }
        });

    }

    private void setMapStyleSelected (View oldItem, View newItem) {
        if (newItem != null) {
            LinearLayout newBorder = (LinearLayout) newItem.findViewById(R.id.itemBorder);
            TextView newLabel = (TextView) newItem.findViewById(R.id.label);

            newBorder.setBackgroundResource(R.drawable.map_style_border);
            newLabel.setTextColor(Color.parseColor("#ff33b5e5"));
        }

        if (oldItem != null) {
            LinearLayout oldBorder = (LinearLayout) oldItem.findViewById(R.id.itemBorder);
            TextView oldLabel = (TextView) oldItem.findViewById(R.id.label);

            oldBorder.setBackgroundResource(0);
            oldLabel.setTextColor(Color.BLACK);
        }
    }
}
