package com.example.mapsproject;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class CustomAdapterRouteInfo extends ArrayAdapter<RouteStep> {
    Context context;
    List<RouteStep> routeSteps;

    public CustomAdapterRouteInfo(Context context, int layoutToBeInflated, List<RouteStep> routeSteps) {
        super(context,layoutToBeInflated, routeSteps);
        this.context = context;
        this.routeSteps = routeSteps;
    }

    static class ViewHolder {
        ImageView icon;
        TextView instruction;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            convertView = inflater.inflate(R.layout.custom_row_route, null);

            holder = new ViewHolder();
            holder.instruction = (TextView) convertView.findViewById(R.id.instruction);
            holder.icon = (ImageView) convertView.findViewById(R.id.icon);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }


        holder.instruction.setText(routeSteps.get(position).instruction);
        holder.icon.setImageResource(routeSteps.get(position).thumbnail);

        return convertView;
    }


}