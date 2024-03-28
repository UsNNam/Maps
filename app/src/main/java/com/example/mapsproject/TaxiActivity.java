package com.example.mapsproject;

import android.os.Bundle;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mapsproject.Entity.TaxiDescription;

import java.util.ArrayList;
import java.util.List;

public class TaxiActivity extends AppCompatActivity {

    List<TaxiDescription> taxiDescriptions;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.taxi_price);
        taxiDescriptions = new ArrayList<>();
        taxiDescriptions.add(new TaxiDescription("Taxi 1", "Description 1", "Price 1", "123456789"));
        taxiDescriptions.add(new TaxiDescription("Taxi 2", "Description 2", "Price 2", "123456789"));
        taxiDescriptions.add(new TaxiDescription("Taxi 3", "Description 3", "Price 3", "123456789"));
        taxiDescriptions.add(new TaxiDescription("Taxi 4", "Description 4", "Price 4", "123456789"));
        taxiDescriptions.add(new TaxiDescription("Taxi 5", "Description 5", "Price 5", "123456789"));
        taxiDescriptions.add(new TaxiDescription("Taxi 6", "Description 6", "Price 6", "123456789"));
        taxiDescriptions.add(new TaxiDescription("Taxi 7", "Description 7", "Price 7", "123456789"));

        ListView listView = (ListView) findViewById(R.id.priceListView);
        CustomeAdapterTaxiPrice adapter = new CustomeAdapterTaxiPrice(this, R.layout.custom_row_taxi_price, taxiDescriptions);
        listView.setAdapter(adapter);
    }
}
