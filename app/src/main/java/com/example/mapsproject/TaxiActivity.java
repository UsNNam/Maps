package com.example.mapsproject;

import android.icu.text.DecimalFormat;
import android.os.Bundle;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mapsproject.Entity.TaxiDescription;

import java.util.ArrayList;
import java.util.List;

public class TaxiActivity extends AppCompatActivity {

    List<TaxiDescription> taxiDescriptions;
    double distance;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.taxi_price);
        distance = getIntent().getDoubleExtra("distance", 0);

        double price1, price2, price3, price4, price5, price6;

        DecimalFormat df = new DecimalFormat("#.#");

        if(distance <= 1) {
            price1 = 5000;
        } else if(distance <= 25) {
            price1 = 5000 + (distance-0.8)*13900;
        } else {
            price1 = 5000 + (distance-0.8)*13900 + (distance-25)*11600;
        }

        if(distance <= 1) {
            price2 = 11000;
        } else if(distance <= 30) {
            price2 = 11000 + (distance-1)*15800;
        } else {
            price2 = 11000 + (distance-1)*15800 + (distance-30)*13600;
        }

        if(distance <= 1) {
            price3 = 11000;
        } else if(distance <= 30) {
            price3 = 11000 + (distance-1)*14500;
        } else {
            price3 = 11000 + (distance-1)*14500 + (distance-30)*11600;
        }

        if(distance <= 1) {
            price4 = 11000;
        } else if(distance <= 30) {
            price4 = 11000 + (distance-1)*15500;
        } else {
            price4 = 11000 + (distance-1)*15500 + (distance-30)*13600;
        }

        if(distance <= 1) {
            price5 = 8000;
        } else if(distance <= 30) {
            price5 = 8000 + (distance-1)*14500;
        } else {
            price5 = 8000 + (distance-1)*14500 + (distance-30)*11000;
        }

        if(distance <= 1) {
            price6 = 9000;
        } else if(distance <= 30) {
            price6 = 9000 + (distance-1)*16000;
        } else {
            price6 = 9000 + (distance-1)*15500 + (distance-30)*12000;
        }



        taxiDescriptions = new ArrayList<>();
        taxiDescriptions.add(new TaxiDescription("Mai Linh Taxi", "4-seater", df.format(price1) , "02838298888"));
        taxiDescriptions.add(new TaxiDescription("Mai Linh Taxi", "7-seater", df.format(price2), "02838298888"));
        taxiDescriptions.add(new TaxiDescription("Vinasun Taxi", "4-seater", df.format(price3), "02838272727"));
        taxiDescriptions.add(new TaxiDescription("Vinasun Taxi", "7-seater", df.format(price4), "02838272727"));
        taxiDescriptions.add(new TaxiDescription("Vina Taxi", "4-seater", df.format(price5), "02838111111"));
        taxiDescriptions.add(new TaxiDescription("Vina Taxi", "7-seater", df.format(price6), "02838111111"));

        ListView listView = (ListView) findViewById(R.id.priceListView);
        CustomeAdapterTaxiPrice adapter = new CustomeAdapterTaxiPrice(this, R.layout.custom_row_taxi_price, taxiDescriptions);
        listView.setAdapter(adapter);
    }
}
