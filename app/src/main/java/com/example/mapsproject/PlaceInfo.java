package com.example.mapsproject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class PlaceInfo {

    public static boolean stopOuter = false;
    public static List<PlaceInfo> placeInfoList;

    static {
        placeInfoList = new ArrayList<>();
    }

    public PlaceInfo thisOne= null;
    public PlaceDetailFragment placeDetailFragment = null;
    public boolean stopInner = false;
    public static MainActivity mainActivity;
    public static CustomResultSearchAdapter adapter;
    public Place place;
    public Bitmap[] photos = null;

    public int curLoad = 0;

    public PlaceInfo(Place place, PlacesClient placesClient) {
        thisOne = this;
        Log.i("DetailInfo", place.toString());
        placeInfoList.add(this);
        this.place = place;
        // Gọi phương thức fetchPhotoMetadata trong một AsyncTask để tránh lỗi NetworkOnMainThreadException
        new Thread(new Runnable() {
            @Override
            public void run() {
                new FetchPhotoMetadataTask().execute(placesClient, place);
            }
        }).start();
    }

    public String getLatLngString() {
        String result = place.getLatLng().toString();
        String[] parts = result.split("\\(");
        String[] parts2 = parts[1].split("\\)");
        return parts2[0];
    }

    private class FetchPhotoMetadataTask extends AsyncTask<Object, Void, Bitmap[]> {

        @Override
        protected Bitmap[] doInBackground(Object... params) {
            PlacesClient placesClient = (PlacesClient) params[0];
            Place place = (Place) params[1];

            // Gọi phương thức fetchPhotoMeteData trong luồng nền
            return fetchPhotoMeteData(placesClient, place);
        }

        @Override
        protected void onPostExecute(Bitmap[] result) {
            // Xử lý kết quả ở đây, ví dụ: cập nhật giao diện người dùng
            if (result != null) {
                // Cập nhật UI với dữ liệu từ result
                photos = result;
            } else {
                // Xử lý lỗi
            }
        }
    }

    private Bitmap[] fetchPhotoMeteData(PlacesClient placesClient, Place place) {
        OkHttpClient client = new OkHttpClient();

        // Thay thế "API_KEY" bằng khóa API của bạn và "PLACE_ID_TO_QUERY" bằng ID của địa điểm cụ thể bạn muốn lấy thông tin
        String apiKey = "AIzaSyC4eQTS4oxvsgONLXCsbeBFUp68WhXYTJ0";

        String placeId = place.getId();

        // Xây dựng URL của API
        String url = "https://places.googleapis.com/v1/places/" + placeId +
                "?fields=id,displayName,photos" +
                "&key=" + apiKey;
        // Tạo một HTTP request
        Request request = new Request.Builder()
                .url(url)
                .header("Content-Type", "application/json")
                .header("X-Goog-Api-Key", apiKey)
                .header("X-Goog-FieldMask", "id,displayName,photos")
                .build();

        // Thực hiện request và nhận response
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }

            // Lấy dữ liệu từ response
            String responseData = response.body().string();
            System.out.println("Response Data:\n" + responseData);
            Log.i("Response Data", responseData);

            JsonObject jsonObject = JsonParser.parseString(responseData).getAsJsonObject();

            if (jsonObject.has("photos")) {
                // Lấy mảng JSON từ trường "photos"
                JsonArray photosArray = jsonObject.getAsJsonArray("photos");

                // Duyệt qua từng đối tượng JSON trong mảng "photos"
                photos = new Bitmap[photosArray.size()];
                ExecutorService executor = Executors.newFixedThreadPool(32); // Số lượng thread tối đa là 5

                for (int i = 0; i < photosArray.size(); i++) {
                    int finalI = i;
                    executor.submit(new Runnable() {
                        @Override
                        public void run() {
                            if (stopInner) {
                                return;
                            }
                            JsonObject photoObject = photosArray.get(finalI).getAsJsonObject();

                            // Lấy giá trị của trường "photoReference" trong đối tượng photoObject
                            String name = photoObject.get("name").getAsString();

                            // Sử dụng giá trị photoReference theo ý của bạn
                            Log.i("PhotoNamexyz", name);
                            Bitmap photo = callGooglePlacesMediaApi(name, apiKey);

                            if (photo != null) {
                                curLoad++;
                                photos[finalI] = photo;
                                Log.i("PhotoNamexyz", photo.toString() + " " + finalI);
                                mainActivity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (placeDetailFragment != null) {
                                            Log.i("abcxyz", "run: " + placeDetailFragment);
                                            placeDetailFragment.loadImage(thisOne);
                                        }
                                        adapter.notifyDataSetChanged();
                                    }
                                });
                            }
                        }
                    });
                }

                executor.shutdown(); // Đảm bảo tất cả các task đã được submit sẽ được thực thi xong trước khi tắt ExecutorService
            } else {
                System.out.println("Không tìm thấy trường 'photos'");
            }
            return photos;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Bitmap callGooglePlacesMediaApi(String name, String apiKey) {
        OkHttpClient client = new OkHttpClient();

        // Xây dựng URL của API
        String apiUrl = "https://places.googleapis.com/v1/" + name + "/media?key=" + apiKey + "&maxHeightPx=300&maxWidthPx=300";
        Log.i("URLxxx", apiUrl);

        // Tạo một HTTP request
        Request request = new Request.Builder()
                .url(apiUrl)
                .header("Content-Type", "application/json")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {

                // Lấy dữ liệu từ response dưới dạng mảng byte
                byte[] imageData = response.body().bytes();

                // Chuyển đổi mảng byte thành đối tượng Bitmap
                return BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
            } else {
                // Xử lý lỗi khi response không thành công
                System.err.println("Error: " + response.code() + " - " + response.message());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void setImage(ImageView imageView) {
        if (photos != null) {
            imageView.setImageBitmap(photos[0]);
        }
    }

    public static void stop() {
        stopOuter = true;
        for (PlaceInfo placeInfo : placeInfoList) {
            placeInfo.stopInner = true;
        }
        placeInfoList.removeAll(placeInfoList);
    }
}
