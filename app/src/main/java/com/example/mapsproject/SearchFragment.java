package com.example.mapsproject;

import static android.app.Activity.RESULT_OK;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
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
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.example.mapsproject.BackGroundTask.OpenAIApi;
import com.example.mapsproject.BackGroundTask.OpenAIVisionApiHandleImage;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.api.net.SearchByTextRequest;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SearchFragment extends Fragment implements TextWatcher, ActivityCompat.OnRequestPermissionsResultCallback, GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnMyLocationClickListener, OnMapReadyCallback {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private static final int REQUEST_SELECT_IMAGE = 2;

    private boolean permissionDenied = false;

    private GoogleMap googleMap;

    public EditText searchLocation;

    private PlacesClient placesClient;

    private PlaceInfo[] places;

    private ListView listView;

    private MainActivity mainActivity;
    private Context context;
    private LinearLayout listViewSearchResult;
    private Button backButton;
    private Place[] placeList;
    private final int REQ_CODE_SPEECH_INPUT = 100;

    private ImageButton camera;
    private ImageButton mic;
    String apiKey;

    static final int REQUEST_IMAGE_CAPTURE = 1;
    String currentPhotoPath;

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
                Log.i("Placestest1111111", "Place selected: " + position);
                mainActivity.onMsgFromSearchToMain("SEARCH", placeInfo);
            }
        });

        // EditText for search location
        searchLocation = search_fragment.findViewById(R.id.search);
        searchLocation.addTextChangedListener(this);
        backButton = (Button) search_fragment.findViewById(R.id.back);

        // Mic Record
        mic = search_fragment.findViewById(R.id.mic);
        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
            mic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    askSpeechInput();

                }
            });
        } else {
            ActivityCompat.requestPermissions(mainActivity, new String[]{android.Manifest.permission.RECORD_AUDIO}, 1);
        }

        camera = search_fragment.findViewById(R.id.camera);
        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            camera.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    selectImageFromGallery();

                }
            });
        } else {
            ActivityCompat.requestPermissions(mainActivity, new String[]{android.Manifest.permission.CAMERA}, 1);
            ActivityCompat.requestPermissions(mainActivity, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);

        }

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
        return search_fragment;
    }

    // Mic Record
    private void askSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "vi-VN"); // Đặt ngôn ngữ thành tiếng Việt
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Nói đi"); // Hiển thị thông báo để người dùng biết khi nói
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                "Say something…");
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(context.getApplicationContext(),
                    "Sorry! Your device doesn't support speech input",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try {
            switch (requestCode) {
                case REQ_CODE_SPEECH_INPUT: {
                    if (resultCode == RESULT_OK && null != data) {

                        ArrayList<String> result = data
                                .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                        String rawText = result.get(0);

                        OpenAIApi openAIApi = new OpenAIApi(context, this);
                        openAIApi.execute(rawText);
                    }
                    break;
                }
                case REQUEST_IMAGE_CAPTURE: {
                    if (resultCode == RESULT_OK) {

                        // Ảnh đã được chụp thành công, tiến hành mã hóa và upload
                        String encodedImage = resizeAndEncodeImage(currentPhotoPath);
                        if (encodedImage != null) {
                            // Tiến hành upload ảnh (sử dụng Retrofit, Volley, hoặc thư viện HTTP khác)
                            // Ví dụ: myUploader.uploadImage(encodedImage);
                            OpenAIVisionApiHandleImage openAIApi = new OpenAIVisionApiHandleImage(context, this);
                            openAIApi.execute(encodedImage);
                        }
                    }
                    break;
                }
                case REQUEST_SELECT_IMAGE: {
                    if (resultCode == RESULT_OK && null != data) {

                        Uri selectedImageUri = data.getData();
                        currentPhotoPath = getAbsolutePath(selectedImageUri);
// Ảnh đã được chụp thành công, tiến hành mã hóa và upload
                        String encodedImage = resizeAndEncodeImage(currentPhotoPath);
                        if (encodedImage != null) {
                            // Tiến hành upload ảnh (sử dụng Retrofit, Volley, hoặc thư viện HTTP khác)
                            // Ví dụ: myUploader.uploadImage(encodedImage);
                            OpenAIVisionApiHandleImage openAIApi = new OpenAIVisionApiHandleImage(context, this);
                            openAIApi.execute(encodedImage);
                        }

                    }
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getAbsolutePath(Uri uri) {
        // Kiểm tra URI scheme
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            // Nếu là một URI content (thường từ trình quản lý file của hệ thống)
            // Sử dụng phương thức getContentResolver().query() để lấy đường dẫn thực tế của tệp
            String[] projection = {MediaStore.Images.Media.DATA};
            Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);
            if (cursor != null) {
                int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                if (cursor.moveToFirst()) {
                    String filePath = cursor.getString(columnIndex);
                    cursor.close();
                    return filePath;
                }
                cursor.close();
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            // Nếu là một URI file (thường từ bộ nhớ đệm hoặc bộ nhớ trong)
            // Lấy đường dẫn từ URI trực tiếp
            return uri.getPath();
        }
        return null;
    }

    // Camera
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(context.getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(context,
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                try {
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void selectImageFromGallery() {
        // Tạo Intent để mở trình chọn ảnh
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_SELECT_IMAGE);
    }


    private File createImageFile() throws IOException {
        // Tạo tệp ảnh
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Lưu đường dẫn tệp ảnh
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    public static String resizeAndEncodeImage(String imagePath) {
        // Đọc bức ảnh từ đường dẫn
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, options);

        // Tính toán tỉ lệ thu nhỏ
        int scaleFactor = 1;
        while ((options.outWidth / scaleFactor) > 2048
                || (options.outHeight / scaleFactor) > 2048) {
            scaleFactor *= 2;
        }

        // Tạo bitmap thu nhỏ
        options.inJustDecodeBounds = false;
        options.inSampleSize = scaleFactor;
        Bitmap resizedBitmap = BitmapFactory.decodeFile(imagePath, options);

        // Cắt ảnh ở giữa nếu cần
       /* int width = resizedBitmap.getWidth();
        int height = resizedBitmap.getHeight();
        int x = 0;
        int y = 0;

        if (width > 1048) {
            x = (width - 1048) / 2;
            width = 1048;
        }
        if (height > 4048) {
            y = (height - 4048) / 2;
            height = 4048;
        }
        resizedBitmap = Bitmap.createBitmap(resizedBitmap, x, y, width, height);*/

        // Chuyển bitmap thành chuỗi Base64
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] byteArray = baos.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
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

    public void callApiSearchText() {
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

        LatLng sydney = new LatLng(-33.852, 151.211);
        googleMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

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
}
