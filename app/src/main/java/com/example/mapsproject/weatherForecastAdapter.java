package com.example.mapsproject;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.gson.Gson;

import java.util.List;

public class weatherForecastAdapter extends ArrayAdapter<WeatherData.Hourly> {
    private Context context;
    private WeatherData.Hourly [] timelines;
    Gson gson = new Gson();
    WeatherData weatherData;
    public weatherForecastAdapter(Context context, int layoutToBeInflated, List<WeatherData.Hourly> timelines)
    {
        super(context, layoutToBeInflated, timelines);
        this.context = context;
        this.timelines= new WeatherData.Hourly[timelines.size()];
        timelines.toArray(this.timelines);
    }

    static class ViewHolder
    {
        TextView weatherTemp, weatherInfoTime;
        ImageView weatherInfoImg;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            convertView = inflater.inflate(R.layout.popup_info, null);

            holder = new ViewHolder();
            holder.weatherTemp= (TextView) convertView.findViewById(R.id.weatherTemp);
            holder.weatherInfoImg = (ImageView) convertView.findViewById(R.id.weatherInfoImg);
            holder.weatherInfoTime = (TextView) convertView.findViewById(R.id.weatherInfoTime);
            convertView.setTag(holder);

            WeatherData.Hourly timeline =  timelines[position];
            String time = timeline.getTime();
            WeatherData.Values values = timeline.getValues();
            int weathercode = values.getWeatherCode();
            Double temperature = values.getTemperature();
            int imgsrc = setImg(weathercode);
            holder.weatherInfoImg.setImageResource(imgsrc);
            String hourString = time.substring(11, 13);
            holder.weatherInfoTime.setText(hourString);
            holder.weatherTemp.setText(temperature.toString()+"°");

        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        return convertView;
    }

    public int setImg(int weathercode)
    {
        if (weathercode == 1000)
        {
            return R.drawable.clear_large;
        }
        else if (weathercode == 1100)
        {
            return R.drawable.mostly_clear_large;
        }
        else if (weathercode == 1101)
        {
            return R.drawable.partly_cloudy_large;
        }
        else if (weathercode == 1102)
        {
            return R.drawable.mostly_cloudy_large;
        }
        else if (weathercode == 1001)
        {
            return R.drawable.cloudy_large;
        }
        else if (weathercode == 2100)
        {
            return R.drawable.fog_light_large;
        }
        else if (weathercode == 2000)
        {
            return R.drawable.fog_large;
        }
        else if (weathercode == 4000)
        {
            return R.drawable.drizzle_large;
        }
        else if (weathercode == 4200 || weathercode == 4001)
        {
            return R.drawable.rain_light_large;
        }
        else if (weathercode == 4201)
        {
            return R.drawable.rain_heavy_large;
        }
        return R.drawable.clear_large;
    }
}
