package com.example.mapsproject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mapsproject.CustomAdapter.CustomReviewAdapter;
import com.google.android.libraries.places.api.model.OpeningHours;
import com.google.android.libraries.places.api.model.Period;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.Review;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public class PlaceDetailFragment extends Fragment {
    MainActivity mainActivity;
    Context context = null;
    PlaceInfo placeInfo;
    TextView name;
    TextView rating, totalRating, rating1;
    ProgressBar progress5star, progress4star, progress3star, progress2star, progress1star;

    TextView price, status, description;
    LinearLayout photoList, dayOfWeek;
    RatingBar ratingBar;
    TextView addition, address, phoneNumber, website;
    Button direct, share, back;
    ImageView directAddress, call, accessWebsite;

    LinearLayout addressLayout, phoneLayout, websiteLayout, descriptionLayout;
    View addressLine, phoneLine, websiteLine;
    RecyclerView reviewList;

    public static PlaceDetailFragment newInstance(String strArg) {
        PlaceDetailFragment fragment = new PlaceDetailFragment();
        Bundle args = new Bundle();
        args.putString("Place Detail Fragment", strArg);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            context = getActivity(); // use this reference to invoke main callbacks
            mainActivity = (MainActivity) getActivity();
        } catch (IllegalStateException e) {
            throw new IllegalStateException("MainActivity must implement callbacks");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LinearLayout layout_blue = (LinearLayout) inflater.inflate(R.layout.search_detail, container, false);

        name = layout_blue.findViewById(R.id.name);
        rating = layout_blue.findViewById(R.id.rating);
        totalRating = layout_blue.findViewById(R.id.totalRating);
        price = layout_blue.findViewById(R.id.price);
        status = layout_blue.findViewById(R.id.status);
        photoList = layout_blue.findViewById(R.id.viewgroup);
        ratingBar = layout_blue.findViewById(R.id.ratingBar);
        addition = layout_blue.findViewById(R.id.addition);
        call = layout_blue.findViewById(R.id.call);
        direct = (Button) layout_blue.findViewById(R.id.direct);
        share = layout_blue.findViewById(R.id.share);
        dayOfWeek = (LinearLayout) layout_blue.findViewById(R.id.dayOfWeek);
        back = (Button) layout_blue.findViewById(R.id.back);
        address = layout_blue.findViewById(R.id.address);
        phoneNumber = layout_blue.findViewById(R.id.phoneNumber);
        directAddress = layout_blue.findViewById(R.id.direct1);
        website = layout_blue.findViewById(R.id.website);
        accessWebsite = layout_blue.findViewById(R.id.accessWebsite);
        description = layout_blue.findViewById(R.id.description);
        rating1 = layout_blue.findViewById(R.id.rating1);
        reviewList = layout_blue.findViewById(R.id.reviewList);

        progress5star = layout_blue.findViewById(R.id.progress5star);
        progress4star = layout_blue.findViewById(R.id.progress4star);
        progress3star = layout_blue.findViewById(R.id.progress3star);
        progress2star = layout_blue.findViewById(R.id.progress2star);
        progress1star = layout_blue.findViewById(R.id.progress1star);


        addressLayout = layout_blue.findViewById(R.id.addressLayout);
        phoneLayout = layout_blue.findViewById(R.id.phoneLayout);
        websiteLayout = layout_blue.findViewById(R.id.websiteLayout);
        descriptionLayout = layout_blue.findViewById(R.id.descriptionLayout);

        addressLine = layout_blue.findViewById(R.id.lineAddress);
        phoneLine = layout_blue.findViewById(R.id.linePhone);
        websiteLine = layout_blue.findViewById(R.id.lineWebsite);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.onMsgFromSearchToMain("PlaceDetailFragment", null);
            }
        });

        directAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Continue in here

                Toast.makeText(context, "Direct to the place", Toast.LENGTH_SHORT).show();
            }
        });

        return layout_blue;
    }

    @SuppressLint("SetTextI18n")
    public void setPlaceInfo(PlaceInfo placeInfo) {
        Log.i("setPlaceInfo", " " + placeInfo.place.getName());
        Place cur = placeInfo.place;

        Log.i("Place1", cur.toString());
        Log.i("Place1", cur.getName());

        if (cur.getReviews() != null) {

            handleReviews(cur.getReviews());
        }
        //Name
        if (cur.getName() != null) {
            name.setText(cur.getName());
        }

        cur.getCurrentOpeningHours().getWeekdayText().forEach(s -> Log.i("Weekday", s));
        for (Review review : cur.getReviews()) {
            Log.i("Review", review.getRating().toString());
        }
        // Rating
        if (cur.getRating() != null) {
            Log.i("Place1", String.valueOf(cur.getRating()));
            rating.setText(String.valueOf(cur.getRating()));
            rating1.setText(String.valueOf(cur.getRating()));
            ratingBar.setRating(cur.getRating().floatValue());
        }

        //Total User Ratings
        if (cur.getUserRatingsTotal() != null) {
            Log.i("Place2", String.valueOf(cur.getUserRatingsTotal()));
            totalRating.setText("(" + String.valueOf(cur.getUserRatingsTotal()) + ")");
        }

        //Price Level
        if (cur.getPriceLevel() != null) {
            Log.i("Place3", String.valueOf(cur.getPriceLevel()));

            if (cur.getPriceLevel() == 0) price.setText("Price Level: Lowest Price");
            else if (cur.getPriceLevel() == 1) price.setText("Price Level: Inexpensive");
            else if (cur.getPriceLevel() == 2) price.setText("Price Level: Moderate");
            else if (cur.getPriceLevel() == 3) price.setText("Price Level: Expensive");
            else if (cur.getPriceLevel() == 4) price.setText("Price Level: Very Expensive");
            else price.setText("Price Level: Unknown");
        } else {
            price.setText("Price Level: Unknown");
        }

        Log.i("OpeningHours11", " " + "haha");

        //Phone
        if (cur.getPhoneNumber() != null) {
            phoneNumber.setText(cur.getPhoneNumber());
            phoneNumber.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:" + cur.getPhoneNumber()));
                    context.startActivity(intent, null);
                }
            });
            call.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:" + cur.getPhoneNumber()));
                    context.startActivity(intent, null);
                }
            });
        } else {
            phoneLayout.setVisibility(View.GONE);
            phoneLine.setVisibility(View.GONE);
        }
        Log.i("OpeningHours12", " " + "haha");

        // Access website
        //Log.i("Websitexass: ", cur.getWebsiteUri().toString());
        if (cur.getWebsiteUri() != null) {
            website.setText(cur.getWebsiteUri().toString());
            website.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, cur.getWebsiteUri());
                    context.startActivity(intent, null);
                }
            });
            accessWebsite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, cur.getWebsiteUri());
                    context.startActivity(intent, null);
                }
            });
        } else {
            websiteLayout.setVisibility(View.GONE);
            websiteLine.setVisibility(View.GONE);
        }

        //Share
//        if (cur.getWebsiteUri() != null) {
//            Log.i("OpeningHours121", " " + "haha");
//
//            share.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Log.i("OpeningHours1211", " " + "1");
//
//                    Intent intent = new Intent(Intent.ACTION_SEND);
//                    Log.i("OpeningHours1212", " " + "2");
//
//                    intent.setType("text/plain");
//                    intent.putExtra(Intent.EXTRA_TEXT, cur.getWebsiteUri().toString());
//                    Log.i("OpeningHours1213", " " + "3");
//
//                    context.startActivity(Intent.createChooser(intent, "Share1"), null);
//                }
//            });
//            Log.i("OpeningHours122", " " + "haha");
//
//        } else {
//            share.setVisibility(View.GONE);
//        }


        //Opening Hours
        Log.i("OpeningHours13", " " + "haha");
        addition.setText("");
        status.setText("Closed ");
        status.setTextColor(context.getResources().getColor(R.color.red));

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

            handlerTime(week, day);
        }

        // Fetch Photos
        Log.i("Lengthphoto", "haha");

        photoList.removeAllViews();
        if (placeInfo.photos != null) {

            Log.i("loadImage115", " " + placeInfo.photos.length);
            Log.i("PhotoAdapter", " " + placeInfo.photos.length);
            loadImage(placeInfo);
        }

        dayOfWeek.removeAllViews();
        if (cur.getCurrentOpeningHours() != null) {

            String today = getCurrentDayOfWeek();
            Log.i("Today is", today);

            cur.getCurrentOpeningHours().getWeekdayText();
            List<Period> weekDay = cur.getCurrentOpeningHours().getPeriods();
            for (int i = 0; i < weekDay.size(); i++) {
                Log.i("WeekDay22", "Day" + weekDay.get(i).getOpen().getDay().toString());
                Log.i("WeekDay22", "Time" + weekDay.get(i).getOpen().getTime().getHours());
//                Log.i("WeekDay22", "Time" + timeOpen);
//                Log.i("WeekDay22", "Time" + timeClose);

                String day = weekDay.get(i).getOpen().getDay().toString();
                day = convertToNoun(day);

                String timeOpen = assembleTime(weekDay.get(i).getOpen().getTime().getHours(), weekDay.get(i).getOpen().getTime().getMinutes());
                String timeClose = assembleTime(weekDay.get(i).getClose().getTime().getHours(), weekDay.get(i).getClose().getTime().getMinutes());

                final View singleFrame = LayoutInflater.from(context).inflate(R.layout.day_time, null);
                TextView dayName = (TextView) singleFrame.findViewById(R.id.day);
                TextView time = (TextView) singleFrame.findViewById(R.id.time);

                dayName.setText(day);
                time.setText(timeOpen + " - " + timeClose);

                if (day.equals(today)) {
                    dayName.setTextColor(context.getResources().getColor(R.color.green));
                    time.setTextColor(context.getResources().getColor(R.color.green));
                }

                dayOfWeek.addView(singleFrame);

            }
        }

        if (cur.getAddress() != null) {
            address.setText(cur.getAddress());
        } else {
            addressLayout.setVisibility(View.GONE);
            addressLine.setVisibility(View.GONE);
        }

        if (cur.getEditorialSummary() != null) {
            description.setText(cur.getEditorialSummary());
        } else {
            descriptionLayout.setVisibility(View.GONE);
        }


        //status.setText(places[position].getCurrentOpeningHours().toString());
        return;
    }

    private void handleReviews(List<Review> reviews) {


        double[] star = new double[10];
        for (int i = 0; i < reviews.size(); i++) {
            int starRating = roundDouble(reviews.get(i).getRating());
            if (starRating == 0) star[1]++;
            else star[starRating]++;
        }

        progress5star.setProgress(roundDouble(star[5]));
        progress4star.setProgress(roundDouble(star[4]));
        progress3star.setProgress(roundDouble(star[3]));
        progress2star.setProgress(roundDouble(star[2]));
        progress1star.setProgress(roundDouble(star[1]));

        progress5star.setMax(reviews.size());
        progress4star.setMax(reviews.size());
        progress3star.setMax(reviews.size());
        progress2star.setMax(reviews.size());
        progress1star.setMax(reviews.size());

        for (int i = 0; i < reviews.size(); i++) {
            String name = reviews.get(i).getAuthorAttribution().getName();
            double rating = reviews.get(i).getRating();
            String text = reviews.get(i).getText();
            String imageUrl = reviews.get(i).getAuthorAttribution().getPhotoUri();
            String time = reviews.get(i).getPublishTime();
            String att = reviews.get(i).getAttribution();
            Log.i("Review", "Name: " + name);
            Log.i("Review", "Rating: " + rating);
            Log.i("Review", "Text: " + text);
            Log.i("Review", "Image: " + imageUrl);
            Log.i("Review", "Time: " + time);
            Log.i("Review", "Att: " + att);

        }
        Review[] convertArray = reviews.toArray(new Review[0]);
        Log.i("LengthReviews", " " + convertArray.length);
        CustomReviewAdapter customReviewAdapter = new CustomReviewAdapter(context, R.layout.review, convertArray);
        reviewList.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(context));
        reviewList.setAdapter(customReviewAdapter);

    }

    public int roundDouble(double d) {
        return (int) Math.round(d);
    }

    public String assembleTime(int hourInt, int minuteInt) {
        String hour = String.valueOf(hourInt);
        String minute = String.valueOf(minuteInt);

        if (hour.length() == 1) {
            hour = "0" + hour;
        }
        if (minute.length() == 1) {
            minute = "0" + minute;
        }
        return hour + ":" + minute;
    }

    private String convertToNoun(String day) {
        return day = day.substring(0, 1).toUpperCase() + day.substring(1).toLowerCase();
    }

    private String getCurrentDayOfWeek() {
        LocalDate currentDate = LocalDate.now();
        DayOfWeek dayOfWeek = currentDate.getDayOfWeek();
        String today = dayOfWeek.name();
        return convertToNoun(today);
    }

    public void loadImage(PlaceInfo placeInfo) {
        Log.i("loadImage", " " + placeInfo.photos.length);
        for (int i = 0; i < placeInfo.curLoad; i++) {
            final View singleFrame = LayoutInflater.from(context).inflate(R.layout.photo_large_item, null);
            ImageView photo = (ImageView) singleFrame.findViewById(R.id.photo);

            int finalI = i;
            ((MainActivity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    photo.setImageBitmap(placeInfo.photos[finalI]);
                }
            });
            photoList.addView(singleFrame);
        }
    }

    @SuppressLint("ResourceAsColor")
    private void handlerTime(List<String> week, int day) {
        String today = week.get(day);
        if (today.contains("Open 24 hours")) {
            status.setText("Open 24 hours");
            status.setTextColor(context.getResources().getColor(R.color.green));
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
                status.setText("Open ");
                status.setTextColor(context.getResources().getColor(R.color.green));
                addition.setText("Closes " + times[1]);
            } else {
                status.setText("Closed ");
                status.setTextColor(context.getResources().getColor(R.color.red));
                addition.setText("Opens " + times[0]);

            }
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
