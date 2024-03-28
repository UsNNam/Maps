package com.example.mapsproject;

import static androidx.core.content.ContextCompat.startActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.mapsproject.Entity.TaxiDescription;

import java.util.ArrayList;
import java.util.List;

public class CustomeAdapterTaxiPrice extends ArrayAdapter<TaxiDescription> {
    Context context;
    List<TaxiDescription> taxiDescriptions;

    public CustomeAdapterTaxiPrice(Context context, int layoutToBeInflated, List<TaxiDescription> taxiDescriptions) {
        super(context,layoutToBeInflated, taxiDescriptions);

        this.context = context;
        this.taxiDescriptions = taxiDescriptions;

    }

    static class ViewHolder {
        TextView taxiName;
        TextView description;
        TextView price;
        ImageView call;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            convertView = inflater.inflate(R.layout.custom_row_taxi_price, null);

            holder = new ViewHolder();
            holder.taxiName= (TextView) convertView.findViewById(R.id.taxiNameTextView);
            holder.description = (TextView) convertView.findViewById(R.id.taxiDescriptionTextView);
            holder.price = (TextView) convertView.findViewById(R.id.taxiPriceTextView);
            holder.call = (ImageView) convertView.findViewById(R.id.callTaxi);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.taxiName.setText(taxiDescriptions.get(position).getName());
        holder.description.setText(taxiDescriptions.get(position).getDescription());
        holder.price.setText(taxiDescriptions.get(position).getPrice());
        //Phone
        if (taxiDescriptions.get(position).getPhoneNumber() != null) {
            holder.call.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:" + taxiDescriptions.get(position).getPhoneNumber()));
                    startActivity(context, intent, null);
                }
            });
        } else {
            holder.call.setVisibility(View.GONE);
        }

        return convertView;
    }
}
