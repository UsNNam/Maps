package com.example.mapsproject;




import java.util.ArrayList;
import java.util.List;
import java.util.ArrayList;
import java.util.List;

public class WeatherDataV2 {
    public Current getCurrent() {
        return current;
    }

    public void setCurrent(Current current) {
        this.current = current;
    }

    private Current current;
    public Forecast getForecast() {
        return forecast;
    }

    public void setForecast(Forecast forecast) {
        this.forecast = forecast;
    }

    private Forecast forecast;
    public static class Current
    {
        private Condition condition;

        public int getIs_day() {
            return is_day;
        }

        public void setIs_day(int is_day) {
            this.is_day = is_day;
        }

        private int is_day;

        public String getLast_updated() {
            return last_updated;
        }

        public void setLast_updated(String last_updated) {
            this.last_updated = last_updated;
        }

        public double getTemp_c() {
            return temp_c;
        }

        public void setTemp_c(double temp_c) {
            this.temp_c = temp_c;
        }

        private String last_updated;
        private double temp_c;

        public Condition getCondition() {
            return condition;
        }

        public void setCondition(Condition condition) {
            this.condition = condition;
        }
    }

    public static class Forecast{
        public List<Forecastday> getForecastday() {
            return forecastday;
        }

        public void setForecastday(List<Forecastday> forecastday) {
            this.forecastday = forecastday;
        }

        private List<Forecastday> forecastday;
    }
    public static class Forecastday
    {
        public List<Hour> getHour() {
            return hour;
        }

        public void setHour(List<Hour> hour) {
            this.hour = hour;
        }

        private List<Hour> hour;
    }
    public static class Hour {
        private long time_epoch;
        private String time;
        private double temp_c;
        private double temp_f;
        private int is_day;
        private Condition condition;

        public long getTime_epoch() {
            return time_epoch;
        }

        public void setTime_epoch(long time_epoch) {
            this.time_epoch = time_epoch;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public double getTemp_c() {
            return temp_c;
        }

        public void setTemp_c(double temp_c) {
            this.temp_c = temp_c;
        }

        public double getTemp_f() {
            return temp_f;
        }

        public void setTemp_f(double temp_f) {
            this.temp_f = temp_f;
        }

        public int getIs_day() {
            return is_day;
        }

        public void setIs_day(int is_day) {
            this.is_day = is_day;
        }

        public Condition getCondition() {
            return condition;
        }

        public void setCondition(Condition condition) {
            this.condition = condition;
        }
    }

    public static class Condition {
        private String text;
        private String icon;
        private int code;

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public String getIcon() {
            return icon;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

    }


}
