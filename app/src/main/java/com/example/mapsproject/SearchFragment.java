package com.example.mapsproject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PointOfInterest;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.api.net.SearchByTextRequest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class SearchFragment extends Fragment implements TextWatcher, ActivityCompat.OnRequestPermissionsResultCallback, GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnMyLocationClickListener, OnMapReadyCallback {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private boolean permissionDenied = false;
    private Marker markerAdded;
    private GoogleMap googleMap;

    private EditText searchLocation;

    private PlacesClient placesClient;

    private PlaceInfo[] places;

    private ListView listView;

    private MainActivity mainActivity;
    private Context context;
    private LinearLayout listViewSearchResult;
    private Button backButton;
    private Place[] placeList;

    String apiKey;
    private LoadingDialog loadingDialog;

    public static SearchFragment newInstance(String strArg1) {
        SearchFragment fragment = new SearchFragment();
        Bundle bundle = new Bundle();
        bundle.putString("arg1", strArg1);
        fragment.setArguments(bundle);
        return fragment;
    }// newInstance

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        mainActivity = (MainActivity) getActivity();
        context = getActivity();
        PlaceInfo.mainActivity = mainActivity;
        // Define a variable to hold the Places API key.
        apiKey = getResources().getString(R.string.api_key);

        // Log an error if apiKey is not set.
        if (TextUtils.isEmpty(apiKey) || apiKey.equals("DEFAULT_API_KEY")) {
            Log.e("Places test", "No api key");
            return;
        }
        // Initialize the SDK
        Places.initializeWithNewPlacesApiEnabled(mainActivity.getApplicationContext(), apiKey);

        // Create a new PlacesClient instance
        placesClient = Places.createClient(context);

        SupportMapFragment mapFragment = (SupportMapFragment) mainActivity.getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        loadingDialog = new LoadingDialog(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LinearLayout search_fragment = (LinearLayout) inflater.inflate(R.layout.search_places, container, false);


        listViewSearchResult = (LinearLayout) search_fragment.findViewById(R.id.listViewSearchResult);
        listViewSearchResult.setVisibility(View.GONE);
        listView = (ListView) search_fragment.findViewById(R.id.listView);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PlaceInfo placeInfo = places[position];
                Log.i("Placestest111111111", "Place selected: " + position);
                mainActivity.onMsgFromSearchToMain("SEARCH", placeInfo);
            }
        });

        // EditText for search location
        searchLocation = search_fragment.findViewById(R.id.search);
        searchLocation.addTextChangedListener(this);
        backButton = (Button) search_fragment.findViewById(R.id.back);

        // Button
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listViewSearchResult.setVisibility(View.GONE);
            }
        });
        // Button for key word

        Button restaurant = (Button) search_fragment.findViewById(R.id.restaurant);
        Button hotel = (Button) search_fragment.findViewById(R.id.hotel);
        Button gas = (Button) search_fragment.findViewById(R.id.gas);
        Button coffee = (Button) search_fragment.findViewById(R.id.coffee);
        Button groceries = (Button) search_fragment.findViewById(R.id.groceries);
        Button shopping = (Button) search_fragment.findViewById(R.id.shopping);
        View.OnClickListener buttonClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int id = v.getId();
                if (id == R.id.restaurant) {
                    searchLocation.setText("restaurant");
                } else if (id == R.id.hotel) {
                    searchLocation.setText("hotel");
                } else if (id == R.id.gas) {
                    searchLocation.setText("gas");
                } else if (id == R.id.coffee) {
                    searchLocation.setText("coffee");
                } else if (id == R.id.groceries) {
                    searchLocation.setText("groceries");
                } else if (id == R.id.shopping) {
                    searchLocation.setText("shopping");
                }
                callApiSearchText();

            }
        };
        restaurant.setOnClickListener(buttonClickListener);
        hotel.setOnClickListener(buttonClickListener);
        gas.setOnClickListener(buttonClickListener);
        coffee.setOnClickListener(buttonClickListener);
        groceries.setOnClickListener(buttonClickListener);
        shopping.setOnClickListener(buttonClickListener);
        loadingDialog.createLoadingDialog();
        return search_fragment;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        // Code này sẽ được thực thi trước khi nội dung của EditText thay đổi
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        // Code này sẽ được thực thi khi nội dung của EditText đang thay đổi
    }

    @Override
    public void afterTextChanged(Editable s) {
        // Code này sẽ được thực thi sau khi nội dung của EditText thay đổi
// Kiểm tra xem EditText có dữ liệu hay không
        if (TextUtils.isEmpty(s.toString())) {
            // Nếu EditText không có dữ liệu, hãy tắt sự kiện nhấn phím Enter
            searchLocation.setOnEditorActionListener(null);
        } else {
            // Nếu EditText có dữ liệu, hãy bật sự kiện nhấn phím Enter
            searchLocation.setOnEditorActionListener(new OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_DONE || (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                        // Xử lý sự kiện khi nhấn phím "Done" trên bàn phím
                        try {
                            InputMethodManager imm = (InputMethodManager) mainActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(searchLocation.getWindowToken(), 0);
                            callApiSearchText();
                        } catch (Exception e) {
                            Log.e("Error Search Place: ", e.getMessage());
                        }


                        return true; // true để báo hiệu rằng sự kiện đã được xử lý
                    }
                    return false; // false để báo hiệu rằng sự kiện chưa được xử lý và chuyển tiếp nó
                }
            });
        }
    }

    private void callApiSearchText() {
        try {
            PlaceInfo.stop();
            String locationText = searchLocation.getText().toString();
            Log.i("Places test", "callApiSearchText   1: " + locationText);
            // Restrict the areas
            Location currentLocation = googleMap.getMyLocation();
            if (currentLocation == null) {
                return;
            } else {
            }
            LatLng currentLatLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
            final List<Place.Field> placeFields = Arrays.asList(Place.Field.EDITORIAL_SUMMARY, Place.Field.ID, Place.Field.NAME, Place.Field.PHONE_NUMBER, Place.Field.PHOTO_METADATAS, Place.Field.ADDRESS, Place.Field.LAT_LNG, Place.Field.RATING, Place.Field.USER_RATINGS_TOTAL, Place.Field.WEBSITE_URI, Place.Field.PRICE_LEVEL, Place.Field.CURRENT_OPENING_HOURS, Place.Field.PHONE_NUMBER, Place.Field.PRICE_LEVEL, Place.Field.OPENING_HOURS, Place.Field.REVIEWS, Place.Field.EDITORIAL_SUMMARY);
            Log.i("Places test", "callApiSearchText   2: " + locationText);

            final SearchByTextRequest searchByTextRequest = SearchByTextRequest.builder(locationText, placeFields).setMaxResultCount(10).build();
            Log.i("Places test", "callApiSearchText   3: " + locationText);

            placesClient.searchByText(searchByTextRequest).addOnSuccessListener(response -> {
                try {
                    Log.i("Places test", "callApiSearchText   4: " + locationText);

                    placeList = response.getPlaces().toArray(new Place[0]);
                    places = new PlaceInfo[placeList.length];
                    for (Place place : placeList) {

                        Log.i("Places test", "Place found: " + place.toString());

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    places[response.getPlaces().indexOf(place)] = new PlaceInfo(place, placesClient);
                                } catch (Exception e) {
                                    Log.e("Error Search Place API thread: ", e.getMessage());
                                }

                            }
                        }).start();
                    }
                    CustomResultSearchAdapter adapter = new CustomResultSearchAdapter(mainActivity, R.layout.search_result, places, placesClient);

                    PlaceInfo.adapter = adapter;

                    mainActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                listView.setAdapter(adapter);
                            } catch (Exception e) {
                                Log.e("Error set search place adapter: ", e.getMessage());
                            }

                        }
                    });
                    listViewSearchResult.setVisibility(View.VISIBLE);
                } catch (Exception e) {
                    Log.e("Error Search Place API: ", e.getMessage());
                }


            }).addOnFailureListener((exception) -> {
                Log.e("Places test", "Place not found: " + exception.getMessage());
            });
        } catch (Exception e) {
            Log.e("Error Search Place API: ", e.getMessage());
        }

    }
    private void callApiSearchText2(String locationText) {
        try {
            PlaceInfo.stop();

            Log.i("Places test", "callApiSearchText   1: " + locationText);
            // Restrict the areas
            Location currentLocation = googleMap.getMyLocation();
            if (currentLocation == null) {
                return;
            } else {
            }
            LatLng currentLatLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
            final List<Place.Field> placeFields = Arrays.asList(Place.Field.EDITORIAL_SUMMARY, Place.Field.ID, Place.Field.NAME, Place.Field.PHONE_NUMBER, Place.Field.PHOTO_METADATAS, Place.Field.ADDRESS, Place.Field.LAT_LNG, Place.Field.RATING, Place.Field.USER_RATINGS_TOTAL, Place.Field.WEBSITE_URI, Place.Field.PRICE_LEVEL, Place.Field.CURRENT_OPENING_HOURS, Place.Field.PHONE_NUMBER, Place.Field.PRICE_LEVEL, Place.Field.OPENING_HOURS, Place.Field.REVIEWS, Place.Field.EDITORIAL_SUMMARY);
            Log.i("Places test", "callApiSearchText   2: " + locationText);

            final SearchByTextRequest searchByTextRequest = SearchByTextRequest.builder(locationText, placeFields).setMaxResultCount(1).build();
            Log.i("Places test", "callApiSearchText   3: " + locationText);
            loadingDialog.showDialog();
            placesClient.searchByText(searchByTextRequest).addOnSuccessListener(response -> {
                try {
                    Log.i("Places test", "callApiSearchText   4: " + locationText);

                    placeList = response.getPlaces().toArray(new Place[0]);
                    places = new PlaceInfo[placeList.length];
                    markerAdded.setTag(response.getPlaces().get(0).getId());
                    Log.d("TESTMARK",response.getPlaces().get(0).getId() + " VVVV " );
                    for (Place place : placeList) {

                        Log.i("Places test", "Place found: " + place.toString());

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    places[response.getPlaces().indexOf(place)] = new PlaceInfo(place, placesClient);
                                } catch (Exception e) {
                                    Log.e("Error Search Place API thread: ", e.getMessage());
                                }

                            }
                        }).start();
                    }
                    loadingDialog.hideDialog();
                    CustomResultSearchAdapter adapter = new CustomResultSearchAdapter(mainActivity, R.layout.search_result, places, placesClient);

                    PlaceInfo.adapter = adapter;

                    mainActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                listView.setAdapter(adapter);

                            } catch (Exception e) {
                                Log.e("Error set search place adapter: ", e.getMessage());
                            }

                        }
                    });
                    listViewSearchResult.setVisibility(View.VISIBLE);
                } catch (Exception e) {
                    Log.e("Error Search Place API: ", e.getMessage());
                }


            }).addOnFailureListener((exception) -> {
                Log.e("Places test", "Place not found: " + exception.getMessage());
            });
        } catch (Exception e) {
            Log.e("Error Search Place API: ", e.getMessage());
        }

    }
    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (googleMap != null) {
                googleMap.setMyLocationEnabled(true);
            }
        } else {
            ActivityCompat.requestPermissions(mainActivity, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }


    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.googleMap = googleMap;
        GlobalVariable.myMap = googleMap;
        enableMyLocation();
        googleMap.setOnMyLocationButtonClickListener(this);
        googleMap.setOnMyLocationClickListener(this);
        LatLng hanoi = new LatLng(21.028511, 105.804817);
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(hanoi)      // Đặt vị trí camera mới
                .zoom(12)           // Đặt mức độ zoom
                .bearing(0)         // Đặt hướng của camera
                .tilt(30)           // Đặt góc nghiêng của camera
                .build();           // Tạo CameraPosition từ builder
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));

        googleMap.setOnPoiClickListener(new GoogleMap.OnPoiClickListener() {
            @Override
            public void onPoiClick(PointOfInterest poi) {
                if (markerAdded == null)
                {
                    markerAdded = GlobalVariable.myMap.addMarker(new MarkerOptions()
                            .position(poi.latLng)
                            .title(poi.name));
                    markerAdded.setTag(poi.placeId);

                    markerAdded.showInfoWindow();
//                    MarkResultPlaceId mr = new MarkResultPlaceId(mainActivity);
                    Log.d("TESTPOI",poi.latLng.latitude +  " " + poi.latLng.longitude + " test " + poi.name +" "+ poi.placeId);
                    searchLocation.setText(poi.name);
                    callApiPlaceDetail(poi.placeId, poi.name);
                    markerAdded.showInfoWindow();
                }
                else
                {
                    searchLocation.setText(null);
                    listViewSearchResult.setVisibility(View.GONE);
                    markerAdded.remove();
                    markerAdded=null;
                }

            }
        });
        googleMap.setOnMapClickListener(this::onMapClick);
    }


    @Override
    public void onMyLocationClick(@NonNull Location location) {
        Toast.makeText(context, "Current location:\n" + location, Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(context, "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
    }


    public void onMapClick(@NonNull LatLng latLng) {
        List <Address> addresses = new ArrayList<>();

        Geocoder geo = new Geocoder(mainActivity);
        if (markerAdded == null )
        {
            markerAdded= GlobalVariable.myMap.addMarker(new MarkerOptions().position(latLng));
            try {
                addresses=geo.getFromLocation(latLng.latitude, latLng.longitude, 1);
                Log.d("TESTGEO", addresses.get(0).getAddressLine(0));
                Log.d("TESTGEO", addresses.get(0).getFeatureName());
                Log.d("test", "leeeeeee");
                searchLocation.setText(addresses.get(0).getAddressLine(0));
                callApiSearchText2(addresses.get(0).getAddressLine(0));
                markerAdded.setTitle(addresses.get(0).getAddressLine(0));
                markerAdded.showInfoWindow();

            } catch (IOException e) {
                Log.d("test", "Loi do o day");
                throw new RuntimeException(e);
            }
        }
        else
        {
            searchLocation.setText(null);
            listViewSearchResult.setVisibility(View.GONE);
            markerAdded.remove();
            markerAdded=null;
        }
        // Hiển thị thông tin tọa độ latLng lên log hoặc UI
        Log.i("MapClick", "Lat: " + latLng.latitude + ", Long: " + latLng.longitude);

//        MarkResultPlaceId mr = new MarkResultPlaceId(MainActivity.this);
//        mr.execute(latLng.latitude, latLng.longitude);
    }
    public void onMsgFromMainToSearch (String sender, String placeID, String addressName, Double latitude, Double longitude)
    {
        if (Objects.equals(sender, "SP2MAIN"))
        {
            searchLocation.setText(addressName);
            Log.d("TESTDETAIL", addressName);

            callApiPlaceDetail(placeID, addressName);
//            callApiSearchText2(addressName);
            LatLng latlng = new LatLng(latitude,longitude);
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng,15.0f));

        }
    }
    @SuppressLint("SuspiciousIndentation")
    private void callApiPlaceDetail (String placeID, String placeName) {
        List<Place.Field> placeFields = Arrays.asList( Place.Field.ID, Place.Field.NAME, Place.Field.PHONE_NUMBER,  Place.Field.ADDRESS, Place.Field.LAT_LNG, Place.Field.RATING, Place.Field.USER_RATINGS_TOTAL, Place.Field.WEBSITE_URI, Place.Field.PRICE_LEVEL, Place.Field.CURRENT_OPENING_HOURS);
        FetchPlaceRequest request = FetchPlaceRequest.newInstance(placeID, placeFields);

        Log.d ("TESTDETAIL", "CHAY O DAY");
        placesClient.fetchPlace(request).addOnSuccessListener((response) -> {
            Place place = response.getPlace();
            places = new PlaceInfo[1];
                Log.d("TESTDETAIL", "Place detail: " + place.toString());
                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        try {
                            places[0] = new PlaceInfo(place, placesClient);
                        } catch (Exception e) {
                            Log.e("Error Search Place API thread: ", e.getMessage());
                        }

                    }
                }).start();
            Log.d("TESTDETAIL", "LATLNG " + place.getLatLng());

            CustomResultSearchAdapter adapter = new CustomResultSearchAdapter(mainActivity, R.layout.search_result, places, placesClient);

            PlaceInfo.adapter = adapter;

            mainActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        listView.setAdapter(adapter);
                    } catch (Exception e) {
                        Log.e("Error set search place adapter: ", e.getMessage());
                    }

                }
            });
            listViewSearchResult.setVisibility(View.VISIBLE);

        }).addOnFailureListener((exception) -> {
            if (exception instanceof ApiException) {
                final ApiException apiException = (ApiException) exception;
                Log.e("TEST", "Place not found: " + exception.getMessage());
                final int statusCode = apiException.getStatusCode();
                callApiSearchText2(placeName);
                // TODO: Handle error with given status code.
            }
            Log.d ("TESTDETAIL", "CHAY O DAY FAIL");
        });
    }
}
