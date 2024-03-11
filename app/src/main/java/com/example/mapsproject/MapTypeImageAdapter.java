package com.example.mapsproject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mapsproject.MainActivity;
import com.example.mapsproject.R;

public class MapTypeImageAdapter extends BaseAdapter {
    private Context context; // main activity’s context
    Integer[] smallImages; // thumbnail data set
    String[] lables;
    public MapTypeImageAdapter(Context mainActivityContext, Integer[] thumbnails, String[] mapTypes) {
        context = mainActivityContext;
        smallImages = thumbnails;
        lables = mapTypes;
    }

    public int getCount() { return smallImages.length; }

    public Object getItem(int position) { return smallImages[position]; }

    public long getItemId(int position) { return position; }

    public View getView(int position, View convertView, ViewGroup parent) {
        View item;

        if (convertView == null) {
            LayoutInflater inflater = ((MainActivity) context).getLayoutInflater();
            item = inflater.inflate(R.layout.map_type_item, null);
            TextView label = (TextView) item.findViewById(R.id.label);
            ImageView icon = (ImageView) item.findViewById(R.id.image);

            icon.setImageResource(smallImages[position]);
            label.setText(lables[position]);
        }
        else { item = (View) convertView; }

        return item;
    }
}