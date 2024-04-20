package com.example.mapsproject;

import static android.content.ContentValues.TAG;
import static androidx.core.content.ContextCompat.startActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.model.OpeningHours;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class CustomResultSearchAdapter extends ArrayAdapter<PlaceInfo> {
    private final Context context;
    private final PlaceInfo[] places;
    private SavePlace sp;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private boolean [] isSaved ;

    public CustomResultSearchAdapter(Context context, int layoutToBeInflated, PlaceInfo[] places, PlacesClient placesClient) {
        super(context, layoutToBeInflated, places);
        this.context = context;
        this.places = places;
        isSaved = new boolean[places.length];
        Log.i("CustomResultSearchAdapter", "Constructor");
        sp = new SavePlace("test", context);


            SavePlaceDB db = new SavePlaceDB("test",context);
            db.readData(new SavePlaceDB.FirestoreCallback() {
                @Override
                public void onCallback(ArrayList<HashMap<String, Object>> list) {
                    for (int i=0; i< places.length; i++) {
                        String placeID = places[i].place.getId();

                        for (HashMap j : list) {
                            Log.d(TAG, "chay o day ");
                            if (placeID.equals(j.get("placeID"))) {
                                Log.d("TEST", "run here [" + placeID + "] , " + j.get("placeID") );
                                isSaved[i] = true;
                                break;
                            }
                            else isSaved[i]=false;
                            Log.d("TEST", "run here [" + placeID + "] , " + j.get("placeID") );
                        }
                    }
                }
            });
        Log.d("TEST", "Bi bat dong bo " );
        for (int i=0; i<isSaved.length; i++) {
            Log.d("TEST", "Item2: " + isSaved[i]);
        }
    }
    static public CompletableFuture<String> getLastLocation(
                                                     Context context) {
        FusedLocationProviderClient fusedLocationClient;
        CompletableFuture<String> future = new CompletableFuture<>();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
        String[] stringArray = new String[2];
        if (ActivityCompat.checkSelfPermission(context,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            Toast.makeText(context, "Location permission is required", Toast.LENGTH_SHORT).show();
            return future;
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener((MainActivity) context, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            // Got last known location
                            double latitude = location.getLatitude();
                            double longitude = location.getLongitude();
                            String result = String.valueOf(latitude) + "," + String.valueOf(longitude);
                            Toast.makeText(context, "Latitude: " + latitude + ", Longitude: " + longitude, Toast.LENGTH_SHORT).show();
                            future.complete(result);
                        } else {
                            Toast.makeText(context, "Location is null", Toast.LENGTH_SHORT).show();
                            future.complete("");
                        }
                    }
                });
        return future;
    }


    public static class ViewHolder {
        TextView name;
        TextView rating, totalRating;
        LinearLayout layout;
        TextView price, status;
        LinearLayout photoList;
        RatingBar ratingBar;
        TextView addition;

        Button call, direct, share, save;
    }




    @NonNull
    @SuppressLint("SetTextI18n")
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        try {
            ViewHolder holder;
            if (convertView == null) {
                LayoutInflater inflater = ((Activity) context).getLayoutInflater();
                convertView = inflater.inflate(R.layout.search_result, null);

                holder = new ViewHolder();
                holder.name = (TextView) convertView.findViewById(R.id.name);
                holder.rating = (TextView) convertView.findViewById(R.id.rating);
                holder.price = (TextView) convertView.findViewById(R.id.price);
                holder.status = (TextView) convertView.findViewById(R.id.status);
                holder.ratingBar = (RatingBar) convertView.findViewById(R.id.ratingBar);
                holder.totalRating = (TextView) convertView.findViewById(R.id.totalRating);
                holder.photoList = (LinearLayout) convertView.findViewById(R.id.viewgroup);
                holder.addition = (TextView) convertView.findViewById(R.id.addition);
                holder.call = (Button) convertView.findViewById(R.id.call);
                holder.direct = (Button) convertView.findViewById(R.id.direct);
                holder.share = (Button) convertView.findViewById(R.id.share);
                holder.layout = (LinearLayout) convertView.findViewById(R.id.linearLayout);

                holder.save = (Button) convertView.findViewById(R.id.save);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.direct.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("LatLng String1", places[position].getLatLngString());
                    RouteActivity routeActivity = new RouteActivity(context);
                    getLastLocation(context).thenAccept(result -> {
                        Log.d("LatLng String2", result);
                        routeActivity.displayRoute(result, places[position].getLatLngString());
                    });
                    LinearLayout searchLayout = ((MainActivity) context).findViewById(R.id.searchLayout);
                    searchLayout.setVisibility(LinearLayout.GONE);
                    //Location location = GoogleMap.getMyLocation();
                }
            });

            Place cur = places[position].place;

            holder.layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MainActivity mainActivity = (MainActivity) context;
                    Log.i("Placestest111111111", "Click");
                    mainActivity.onMsgFromSearchToMain("SEARCH", places[position]);
                }
            });

            Log.i("Place1", cur.toString());
            Log.i("Place1", cur.getName());


            //Name
            if (cur.getName() != null) {
                holder.name.setText(cur.getName());
            }

            // Rating
            if (cur.getRating() != null) {
                Log.i("Place1", String.valueOf(cur.getRating()));
                holder.rating.setText(String.valueOf(cur.getRating()));
                holder.ratingBar.setRating(cur.getRating().floatValue());
            }

            //Total User Ratings
            if (cur.getUserRatingsTotal() != null) {
                Log.i("Place2", String.valueOf(cur.getUserRatingsTotal()));
                holder.totalRating.setText("(" + String.valueOf(cur.getUserRatingsTotal()) + ")");
            }
//        for(Review review: Objects.requireNonNull(cur.getReviews())){
//            Log.i("Reviewsss",review.toString());
//        }
            //Price Level
            if (cur.getPriceLevel() != null) {
                Log.i("Place3", String.valueOf(cur.getPriceLevel()));

                if (cur.getPriceLevel() == 0) holder.price.setText("Price Level: Lowest Price");
                else if (cur.getPriceLevel() == 1) holder.price.setText("Price Level: Inexpensive");
                else if (cur.getPriceLevel() == 2) holder.price.setText("Price Level: Moderate");
                else if (cur.getPriceLevel() == 3) holder.price.setText("Price Level: Expensive");
                else if (cur.getPriceLevel() == 4)
                    holder.price.setText("Price Level: Very Expensive");
                else holder.price.setText("Price Level: Unknown");
            } else {
                holder.price.setText("Price Level: Unknown");
            }

            //Phone
            if (cur.getPhoneNumber() != null) {
                holder.call.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_DIAL);
                        intent.setData(Uri.parse("tel:" + cur.getPhoneNumber()));
                        startActivity(context, intent, null);
                    }
                });
            } else {
                holder.call.setVisibility(View.GONE);
            }

            //Share
            if (cur.getWebsiteUri() != null) {
                holder.share.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.setType("text/plain");
                        intent.putExtra(Intent.EXTRA_TEXT, cur.getWebsiteUri().toString());
                        startActivity(context, Intent.createChooser(intent, "Share"), null);
                    }
                });
            } else {
                holder.share.setVisibility(View.GONE);
            }


            //Opening Hours
            holder.addition.setText("");
            holder.status.setText("Closed ");
            holder.status.setTextColor(context.getResources().getColor(R.color.red));

            if (cur.getCurrentOpeningHours() != null) {
                OpeningHours openingHours = cur.getCurrentOpeningHours();
                List<String> week = openingHours.getWeekdayText();

                for (int i = 0; i < week.size(); i++) {
                    Log.i("Weekday", week.get(i));
                }

                LocalDate currentDate = LocalDate.now();

                Log.i("LocalDate", currentDate.toString());
                // Lấy thông tin về thứ của hôm nay
                DayOfWeek dayOfWeek = currentDate.getDayOfWeek();
                int day = dayOfWeek.getValue() - 1;

                Log.i("Today", week.get(day));

                handlerTime(holder, week, day);
            }

            // Fetch Photos
            holder.photoList.removeAllViews();
            if (places[position].photos != null) {


                Log.i("PhotoAdapter", position + " " + places[position].photos.length);

                for (int i = 0; i < places[position].photos.length; i++) {
                    final View singleFrame = LayoutInflater.from(context).inflate(R.layout.photo_item, null);
                    ImageView photo = (ImageView) singleFrame.findViewById(R.id.photo);

                    int finalI = i;
                    ((MainActivity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            photo.setImageBitmap(places[position].photos[finalI]);
                        }
                    });


                    holder.photoList.addView(singleFrame);
                }
            }
            Log.d("TEST", "ISSAVED "+ isSaved[position]);
            holder.save.setSelected(isSaved[position]);
            holder.save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String ID=cur.getId();
                    String placeName = cur.getName();
                    Toast.makeText(context, ID, Toast.LENGTH_SHORT).show();
                    String[] geometry = places[position].getLatLngString().split(",", 2);
//                sp = datasource.createSavePlace(Double.parseDouble(geometry[0]), Double.parseDouble(geometry[1]));
                    if (v.isSelected()==false)
                    {
//                        sp.addSavePlace(Double.parseDouble(geometry[1]), Double.parseDouble(geometry[0]),v);
                        sp.addSavePlaceV2(ID,placeName, Double.parseDouble(geometry[0]), Double.parseDouble(geometry[1]),v);
                        Toast.makeText(context, "Save", Toast.LENGTH_SHORT).show();
                        isSaved[position]=true;
                    }
                    else
                    {
                       sp.removeSavePlace(ID,placeName, Double.parseDouble(geometry[0]), Double.parseDouble(geometry[1]), v);
                        Toast.makeText(context, "Remove", Toast.LENGTH_SHORT).show();
                        isSaved[position]=false;
                    }


                }
            });





            return convertView;

        } catch (Exception e) {
            Log.i("Exception", e.toString() + " " + e.getMessage());
            if (places[position].photos != null) {
                Log.i("Exception", position + " " + places[position].photos.length);
            } else {
                Log.i("Exception", "null");
            }
        }

        //holder.status.setText(places[position].getCurrentOpeningHours().toString());
        return convertView;
    }

    @SuppressLint("ResourceAsColor")
    private void handlerTime(ViewHolder holder, List<String> week, int day) {
        String today = week.get(day);
        if (today.contains("Open 24 hours")) {
            holder.status.setText("Open 24 hours");
            holder.status.setTextColor(context.getResources().getColor(R.color.green));
            return;
        }

        String[] part = today.split(": ", 2);

        String openingHours = part[1];
        Log.i("OpeningHoursxxxx", openingHours);
        String[] times = openingHours.split("–");

        Log.i("OpenTime", times[0]);
        Log.i("OpenTime", times[1]);
        Log.i("OpenTime", String.valueOf(times[0].length()));
        if (times[0].length() <= 8) {
            times[0] = "0" + times[0];
            Log.i("OpenTime", times[0]);

        }
        if (times[1].length() <= 8) {
            times[1] = "0" + times[1];
            Log.i("OpenTime", times[1]);

        }

        try {
            String openTimeString = times[0].replace(" ", "").replace(" ", "");
            String closeTimeString = times[1].replace(" ", "").replace(" ", "");


            LocalTime now = LocalTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm", Locale.ENGLISH);            // Loại bỏ các ký tự không mong muốn
            String formattedTime = now.format(formatter);

            Log.i("FormattedTime", formattedTime);

            int openTime = convertTime(openTimeString);
            int closeTime = convertTime(closeTimeString);
            int currentTime = convertTime(formattedTime);

            if (currentTime >= openTime && currentTime <= closeTime) {
                holder.status.setText("Open ");
                holder.status.setTextColor(context.getResources().getColor(R.color.green));
                holder.addition.setText("Closes " + times[1]);
            } else {
                holder.status.setText("Closed ");
                holder.status.setTextColor(context.getResources().getColor(R.color.red));
                holder.addition.setText("Opens " + times[0]);

            }

//        if (now.isAfter(openTime) && now.isBefore(closeTime)) {
//            holder.status.setText("Open ");
//            holder.status.setTextColor(R.color.green);
//
//            holder.addition.setText("Closes " + times[1]);}
//        } else {
//            holder.status.setText("Closed ");
//            holder.status.setTextColor(context.getResources().getColor(R.color.red));
//            holder.addition.setText("Opens " + times[0]);
//        }
        } catch (Exception e) {
            Log.i("Exceptionxxx", e.toString());
        }
    }

    private int convertTime(String time) {
        if (time == null || time.length() < 5) return 0;


        if (time.equals("12:00 AM")) return 0;


        int hour, minute;
        if (time.length() > 5) {

            String tail = time.substring(5);
            String head = time.substring(0, 5);
            String[] parts = head.split(":");
            hour = Integer.parseInt(parts[0]);
            minute = Integer.parseInt(parts[1]);
            if (tail.equals("PM")) {
                hour += 12;
            }
        } else {
            String head = time;
            String[] parts = head.split(":");
            hour = Integer.parseInt(parts[0]);
            minute = Integer.parseInt(parts[1]);

        }
        return hour * 60 + minute;
    }

}
