package com.example.mapsproject;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;


import com.example.mapsproject.Entity.TravelMode;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;


public class HttpRequestTask extends AsyncTask<Void, Void, String> {

    Context context;
    EditText startLocationEditText;
    EditText destinationLocationEditText;
    String travelMode;
    double lat1, lng1, lat2, lng2;

    public HttpRequestTask(Context context, TravelMode travelMode){
        this.context = context;
        this.travelMode = travelMode.getString();
    }

    private static final String TAG = "HttpRequestTask";
    TextView summaryTextView;

    @Override
    protected String doInBackground(Void... params) {


        try {
            startLocationEditText = ((MainActivity) context).findViewById(R.id.startLocationEditText);
            destinationLocationEditText = ((MainActivity) context).findViewById(R.id.destinationEditText);

            // Get the latitude and longitude of the start location
            String[] startLocationParts = startLocationEditText.getText().toString().split(",");
            lat1 = Double.parseDouble(startLocationParts[0]);
            lng1 = Double.parseDouble(startLocationParts[1]);

            // Get the latitude and longitude of the destination location
            String[] destinationLocationParts = destinationLocationEditText.getText().toString().split(",");
            lat2 = Double.parseDouble(destinationLocationParts[0]);
            lng2 = Double.parseDouble(destinationLocationParts[1]);

/*            GlobalVariable.myMap.addMarker(new MarkerOptions()
                    .position(new LatLng(lat2, lng2))
                    .title("Destination"));*/

            String urlString = "https://routes.googleapis.com/directions/v2:computeRoutes";

            String postData = String.format("{\"origin\":" +
                    "{\"location\":" +
                    "{\"latLng\":" +
                    "{\"latitude\": %s ,\"longitude\":%s}}}," +
                    "\"destination\":{\"location\":{\"latLng\":" +
                    "{\"latitude\":%s,\"longitude\":%s}}}," +
                    "\"travelMode\": \"%s\", \"extraComputations\": [\"TRAFFIC_ON_POLYLINE\"]," +
                    "\"routingPreference\":\"TRAFFIC_AWARE\"," +
                    "\"departureTime\":\"2024-10-15T15:01:23.045123456Z\"," +
                    "\"computeAlternativeRoutes\":false,\"routeModifiers\":{\"avoidTolls\":false," +
                    "\"avoidHighways\":false,\"avoidFerries\":false},\"languageCode\":\"en-US\"," +
                    "\"units\":\"IMPERIAL\"}", lat1, lng1, lat2, lng2, travelMode);


            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("X-Goog-Api-Key", "AIzaSyAme6iuddzeJcueQi-LQxcMc6N7cb_XkeM");
            conn.setRequestProperty("X-Goog-FieldMask", "routes.duration,routes.distanceMeters," +
                    "routes.polyline.encodedPolyline,routes.legs.steps," +
                    "routes.travelAdvisory,routes.legs.travelAdvisory");
            conn.setDoOutput(true);

            DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
            wr.writeBytes(postData);
            wr.flush();
            wr.close();

            int responseCode = conn.getResponseCode();
            Log.d(TAG, "Response Code: " + responseCode);

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            return response.toString();
        } catch (Exception e) {
            Log.e(TAG, "Error: " + e.getMessage(), e);
            return null;
        }
    }

    public static String metersToKilometers(int meters) {
        double kilometers = meters / 1000.0; // 1 kilometer = 1000 meters
        DecimalFormat df = new DecimalFormat("#.#"); // Format to one decimal place
        return df.format(kilometers);
    }

    public static String convertTime(int seconds) {
        int hours = seconds / 3600;
        int remainingSeconds = seconds % 3600;
        int minutes = remainingSeconds / 60;
        remainingSeconds = remainingSeconds % 60;

        StringBuilder timeString = new StringBuilder();

        if (hours > 0) {
            timeString.append(hours).append(" hour").append(hours > 1 ? "s" : "").append(" ");
        }

        if (minutes > 0 || hours > 0) {
            timeString.append(minutes).append(" minute").append(minutes > 1 ? "s" : "");
        }

        return timeString.toString().trim();
    }

    public static int extractSeconds(String input) {
        try {
            String numericPart = input;
            // Remove non-numeric characters from the string
            if (input == null || input.isEmpty()) {
                return -1; // Return the input string if it's null or empty
            } else {
                // Return a substring of the input string excluding the last character
                 numericPart = numericPart.substring(0, input.length() - 1);
            }

            // Parse the remaining string to get the numeric value
            return Integer.parseInt(numericPart);
        } catch (NumberFormatException e) {
            // If parsing fails, return 0 or handle the exception as needed
            return 0;
        }
    }

    @Override
    protected void onPostExecute(String result) {
        // Process the result here
        if (result != null) {
            Log.d(TAG, "Response: " + result);
            try {
                JSONObject jsonObject = new JSONObject(result);
                JSONArray routesArray = jsonObject.getJSONArray("routes");
                for (int i = 0; i < routesArray.length(); i++) {
                    List<RouteStep> routeSteps = new ArrayList<>();

                    // Get the route object of routesArray
                    JSONObject route = routesArray.getJSONObject(i);
                    // Take duration from the route object
                    String duration = route.getString("duration");
                    //Do the same for distanceMeters and polyline.encodedPolyline
                    String distanceMeters = route.getString("distanceMeters");
                    String encodedPolyline = route.getJSONObject("polyline")
                            .getString("encodedPolyline");
                    JSONArray steps = route.getJSONArray("legs").getJSONObject(0)
                            .getJSONArray("steps");
                    JSONArray trafficArray = route.getJSONArray("legs").getJSONObject(0)
                            .getJSONObject("travelAdvisory")
                            .getJSONArray("speedReadingIntervals");
                    for (int j = 0; j < steps.length(); j++) {
                        JSONObject step = steps.getJSONObject(j);
                        String instruction = step.getJSONObject("navigationInstruction").
                                getString("instructions");
                        String distance = step.getString("distanceMeters");
                        routeSteps.add(new RouteStep(distance, instruction, R.drawable.start_point));
                    }



                    CustomAdapterRouteInfo customAdapterRouteInfo = new CustomAdapterRouteInfo(context, R.layout.custom_row_route, routeSteps);
                    ListView routeInfoListView = ((MainActivity) context).findViewById(R.id.listViewRouteInfo);
                    routeInfoListView.setAdapter(customAdapterRouteInfo);

                    LinearLayout bottomSheet = ((MainActivity) context).findViewById(R.id.bottomSheet);
                    bottomSheet.setVisibility(View.VISIBLE);

                    TextView summaryTextView = ((MainActivity) context).findViewById(R.id.summaryTextView);
                    summaryTextView.setText(metersToKilometers(Integer.parseInt(distanceMeters)) +
                            " km (" + convertTime(extractSeconds(duration)) + ")");
                    // Log all data collected from the route object
                    Log.d(TAG, "Distance: " + distanceMeters);
                    Log.d(TAG, "Polyline: " + encodedPolyline);
                    Log.d(TAG, "Duration: " + duration);

                    drawPolyline(encodedPolyline, distanceMeters, duration, trafficArray);

                    LatLng startLocation = new LatLng(lat1, lng1);
                    GlobalVariable.myMap.moveCamera(CameraUpdateFactory.newLatLng(startLocation));
                    // Zoom in the Google Map, people don't need to zoom in manually
                    GlobalVariable.myMap.animateCamera(CameraUpdateFactory.zoomTo(15));
                }
            } catch (Exception e) {
                Log.d(TAG,"Error: " + e.getMessage());
            }
        } else {
            Log.e(TAG, "Response is null");
        }
    }

    //Draw the polyline on the map
    public void drawPolyline(String encodedPolyline, String distanceMeters, String durationSeconds,
                             JSONArray trafficArray) {
        try{
            //Decode the encodedPolyline
            List<LatLng> decodedPolyline = PolyUtil.decode(encodedPolyline);



            PolylineOptions polylineOptions = new PolylineOptions();
/*            for(int i = 0; i < trafficArray.length(); i++){
                JSONObject traffic = trafficArray.getJSONObject(i);
                int start = traffic.getInt("startPolylinePointIndex");
                int end = traffic.getInt("endPolylinePointIndex");
                String speed = traffic.getString("speed");
                int color;
                if(speed.equals("SLOW")){
                    color = Color.YELLOW;
                } else if(speed.equals("TRAFFIC_JAM")){
                    color = Color.RED;
                } else {
                    color = Color.BLUE;
                }

                // Add polyline segment with corresponding color
                for (int j = start; j < end; j++) {
                    polylineOptions.add(decodedPolyline.get(j));
                }
                polylineOptions.color(color);
            }*/
            for (LatLng point : decodedPolyline) {
                polylineOptions.add(point);
            }

            for(int i = 0; i < trafficArray.length(); i++){
                JSONObject traffic = trafficArray.getJSONObject(i);
                int start = traffic.getInt("startPolylinePointIndex");
                int end = traffic.getInt("endPolylinePointIndex");
                String speed = traffic.getString("speed");
                if(speed.equals("SLOW")){
                    polylineOptions.add(decodedPolyline.get(start), decodedPolyline.get(end))
                            .color(Color.YELLOW);
                }
                else if(speed.equals("TRAFFIC_JAM")){
                    polylineOptions.add(decodedPolyline.get(start), decodedPolyline.get(end))
                            .color(Color.RED);
                }
                else {
                    polylineOptions.add(decodedPolyline.get(start), decodedPolyline.get(end))
                            .color(Color.BLUE);
                }

                Log.d("Traffic", "Start: " + start + " | End: "+ end + " | Speed: " + speed);
            }

            // Set properties for the polyline (e.g., color, width)
            //polylineOptions.width(10).color(Color.BLUE);

            if(GlobalVariable.polyline != null){
                GlobalVariable.polyline.remove();
            }

            GlobalVariable.polyline = GlobalVariable.myMap.addPolyline(polylineOptions);
            GlobalVariable.polyline.setClickable(true);


            GlobalVariable.myMap.setOnPolylineClickListener(new GoogleMap.OnPolylineClickListener() {

                @Override
                public void onPolylineClick(com.google.android.gms.maps.model.Polyline polyline) {

                    try{

                        TextView summaryTextView = ((MainActivity) context).findViewById(R.id.summaryTextView);

                        summaryTextView.setText("Duration: " + durationSeconds  +" seconds | " +
                                "Distance: " + distanceMeters + " meters");

                    }catch (Exception e){
                        Log.d("Error polyline: ", e.getMessage());
                    }
                }
            });
        }catch (Exception e){
            Log.d("Error draw polyline: ", e.getMessage());
        }

    }



}
