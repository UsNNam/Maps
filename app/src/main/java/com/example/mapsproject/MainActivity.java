package com.example.mapsproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mapsproject.Entity.TravelMode;
import com.example.mapsproject.Fragment.SoloPhotoFragment;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PointOfInterest;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import android.Manifest;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener {

    EditText destinationEditText;
    Context curContext;
    private LinearLayout bottomSheet;
    private GestureDetector gestureDetector;
    private int originalHeight;
    private int expandedHeight;
    private float startY;
    FragmentTransaction ft;
    SearchFragment searchFragment = null;

    PlaceDetailFragment placeDetailFragment = null;

    private ImageButton mapStyleButton;
    private LinearLayout mapSelector;
    private ImageButton mapStyleClose;
    private GridView mapTypeGrid;
    private GridView mapDetailGrid;
    private final String[] mapTypes = {"Default", "Satellite", "Terrain"};
    private final String[] mapDetails = {"Traffic", "3D view"};
    private final Integer[] typeThumbnails = {R.drawable.default_map_type, R.drawable.satellite_map_type, R.drawable.terrain_map_type};
    private final Integer[] detailThumbnails = {R.drawable.traffic_map_detail, R.drawable.threed_map_detail};
    private int currentMapStyle = 0;
    private boolean[] mapDetailList = {false, false};
    private FusedLocationProviderClient fusedLocationProviderClient;

    private SoloPhotoFragment soloPhotoFragment = null;
    public Bundle myBundle;

    SavePlaceFragment savePlaceFragment;
    RelativeLayout homeLayout;
    private Marker markerAdded;
    private SavePlace sp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {

            super.onCreate(savedInstanceState);
            myBundle = savedInstanceState;
            checkAndRequestPermissions();


            setContentView(R.layout.activity_main);
            destinationEditText = findViewById(R.id.destinationEditText);
            curContext = this;
            ft = getSupportFragmentManager().beginTransaction();
            // Tạo và gắn searchFragment
            searchFragment = SearchFragment.newInstance("search_fragment");
            FragmentTransaction ftSearch = getSupportFragmentManager().beginTransaction();
            ftSearch.replace(R.id.searchFragment, searchFragment);
            ftSearch.commit();

            // Tạo và gắn placeDetailFragment
            placeDetailFragment = PlaceDetailFragment.newInstance("place_detail_fragment");
            FragmentTransaction ftPlaceDetail = getSupportFragmentManager().beginTransaction();
            ftPlaceDetail.replace(R.id.placeDetailFragment, placeDetailFragment);
            ftPlaceDetail.commit();

            // Tạo và gắn soloPhotoFragment
            soloPhotoFragment = SoloPhotoFragment.newInstance("solo_Photo_fragment");
            ftPlaceDetail = getSupportFragmentManager().beginTransaction();
            ftPlaceDetail.replace(R.id.soloPhotoFragment, soloPhotoFragment);
            ftPlaceDetail.commit();


            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.hide(placeDetailFragment);
            ft.commit();


            ft = getSupportFragmentManager().beginTransaction();
            ft.hide(soloPhotoFragment);
            ft.commit();

            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
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

            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
            startService(new Intent(this, LocationHistory.class));
        } catch (Exception e) {
            Log.e("Error Main Activity", e.getMessage());
        }

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        homeLayout = (RelativeLayout) findViewById(R.id.home);
        navigation.setSelectedItemId(R.id.navigation_shop);
    }




    private static final int PERMISSION_REQUEST_CODE = 123;

    private void checkAndRequestPermissions() {
        String[] permissions = new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.INTERNET,
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };

        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(permission);
            }
        }

        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), PERMISSION_REQUEST_CODE);
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
//        GlobalVariable.myMap = googleMap;
        LatLng sydney = new LatLng(10.761214, 106.682071);
        googleMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
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
                if (position == currentMapStyle) return;
                if (position == 1) {
                    googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                } else if (position == 2) {
                    googleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                } else {
                    googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                }
                if (position != 0 && mapDetailList[1]) {
                    googleMap.setBuildingsEnabled(false);
                    mapDetailList[1] = false;
                    setMapStyleSelected(mapDetailGrid.getChildAt(1), null);
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
                if (position == 0) if (!mapDetailList[position]) {
                    googleMap.setTrafficEnabled(true);
                    mapDetailList[position] = true;
                    setMapStyleSelected(null, mapDetailGrid.getChildAt(position));
                } else {
                    googleMap.setTrafficEnabled(false);
                    mapDetailList[position] = false;
                    setMapStyleSelected(mapDetailGrid.getChildAt(position), null);
                }
                else if (position == 1) if (!mapDetailList[position]) {
                    if (currentMapStyle != 0) {
                        Toast.makeText(MainActivity.this, "3D view only available in default map style", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    googleMap.setBuildingsEnabled(true);
                    mapDetailList[position] = true;
                    setMapStyleSelected(null, mapDetailGrid.getChildAt(position));
                } else {
                    googleMap.setBuildingsEnabled(false);
                    mapDetailList[position] = false;
                    setMapStyleSelected(mapDetailGrid.getChildAt(position), null);
                }
            }
        });

        googleMap.setOnMapClickListener(this);
        sp = new SavePlace("test");
        sp.setMarkOnMap(googleMap);
        googleMap.setOnPoiClickListener(new GoogleMap.OnPoiClickListener() {
            @Override
            public void onPoiClick(PointOfInterest poi) {
                if (markerAdded == null)
                {
                    markerAdded = GlobalVariable.myMap.addMarker(new MarkerOptions()
                            .position(poi.latLng)
                            .title(poi.name));

                    markerAdded.showInfoWindow();
                }
                else
                {
                    markerAdded.remove();
                    markerAdded=null;
                }

            }
        });

    }

    public void onMsgFromSearchToMain(String sender, PlaceInfo placeInfo) {
        Log.i("sender", sender);
        if (Objects.equals(sender, "SEARCH")) {
            try {

                Log.i("onMsgFromSearchToMain", "onMsgFromSearchToMain");

                ft = getSupportFragmentManager().beginTransaction();
                ft.hide(searchFragment);
                ft.commit();

                ft = getSupportFragmentManager().beginTransaction();
                ft.show(placeDetailFragment);
                ft.commit();

                placeInfo.placeDetailFragment = placeDetailFragment;
                placeDetailFragment.setPlaceInfo(placeInfo);
            } catch (Exception e) {
                Log.e("Error onMsgFromSearchToMain", e.getMessage());
            }
        }
        if (Objects.equals(sender, "PlaceDetailFragment")) {
            try {
                ft = getSupportFragmentManager().beginTransaction();
                ft.show(searchFragment);
                ft.commit();

                ft = getSupportFragmentManager().beginTransaction();
                ft.hide(placeDetailFragment);
                ft.commit();
            } catch (Exception e) {
                Log.e("Error onMsgFromPlaceDetailToMain", e.getMessage());
            }
        }

        if (Objects.equals(sender, "SoloPhotoFragmentOff")) {
            try {
                ft = getSupportFragmentManager().beginTransaction();
                ft.show(placeDetailFragment);
                ft.commit();

                ft = getSupportFragmentManager().beginTransaction();
                ft.hide(soloPhotoFragment);
                ft.commit();
            } catch (Exception e) {
                Log.e("Error onMsgFromPlaceDetailToMain", e.getMessage());
            }
        }

    }

    public void onMsgFromSearchToMain(String sender, PlaceInfo placeInfo, int indexPhoto) {
        if (Objects.equals(sender, "SoloPhotoFragmentOn")) {
            try {
                ft = getSupportFragmentManager().beginTransaction();
                ft.show(soloPhotoFragment);
                ft.commit();

                ft = getSupportFragmentManager().beginTransaction();
                ft.hide(placeDetailFragment);
                ft.commit();

                soloPhotoFragment.setPicture(placeInfo.photoUrl[indexPhoto]);

            } catch (Exception e) {
                Log.e("Error onMsgFromPlaceDetailToMain", e.getMessage());
            }
        }
    }


    private void setMapStyleSelected(View oldItem, View newItem) {
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


    @Override
    public void onMapClick(@NonNull LatLng latLng) {
        if (markerAdded == null )
        {

            markerAdded= GlobalVariable.myMap.addMarker(new MarkerOptions().position(latLng).title("New Marker"));
        }
        else
        {
            markerAdded.remove();
            markerAdded=null;
        }
        // Hiển thị thông tin tọa độ latLng lên log hoặc UI
        Log.i("MapClick", "Lat: " + latLng.latitude + ", Long: " + latLng.longitude);
    }

    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                Task<Location> locationResult = fusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            Location lastKnownLocation = task.getResult();
                            if (lastKnownLocation != null) {
                                GlobalVariable.myMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude()), 15));
                            } else {
                                Toast.makeText(MainActivity.this, "Result is null", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Log.d("test", "Current location is null. Using defaults.");
                            Log.e("test", "Exception: %s", task.getException());
                            GlobalVariable.myMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(0, 0), 15));
                            GlobalVariable.myMap.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage(), e);
        }
    }
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//            Fragment fragment;


            int itemId = item.getItemId();
            if (itemId == R.id.navigation_shop) {
                homeLayout.setVisibility(View.VISIBLE);
                return true;
            } else if (itemId == R.id.navigation_gifts) {
                homeLayout.setVisibility(View.GONE);
                Toast.makeText(curContext, "Search button selected2", Toast.LENGTH_SHORT).show();
                ft = getSupportFragmentManager().beginTransaction();
                savePlaceFragment = SavePlaceFragment.newInstance("save_fragment");
                ft.replace(R.id.fragment_container, savePlaceFragment);
                ft.commit();

                return true;
            } else if (itemId == R.id.navigation_cart) {
                Toast.makeText(curContext, "Search button selected3", Toast.LENGTH_SHORT).show();
                return true;
            } else if (itemId == R.id.navigation_profile) {
                Toast.makeText(curContext, "Search button selected4", Toast.LENGTH_SHORT).show();
                return true;
            }
            return false;
        }
    };


}
