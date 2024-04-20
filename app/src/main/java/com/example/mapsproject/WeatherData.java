package com.example.mapsproject;

import java.util.List;

public class WeatherData {
    public Timelines getTimelines() {
        return timelines;
    }

    public void setTimelines(Timelines timelines) {
        this.timelines = timelines;
    }

    private Timelines timelines;
    // Getter và Setter

    public static class Timelines {
        public List<Hourly> getHourly() {
            return hourly;
        }

        public void setHourly(List<Hourly> hourly) {
            this.hourly = hourly;
        }

        private List<Hourly> hourly;
        // Getter và Setter
    }

    public static class Hourly {
        private String time;

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public Values getValues() {
            return values;
        }

        public void setValues(Values values) {
            this.values = values;
        }

        private Values values;
        // Getter và Setter
    }

    public static class Values {
        public Integer getWeatherCode() {
            return weatherCode;
        }

        public void setWeatherCode(Integer weatherCode) {
            this.weatherCode = weatherCode;
        }

        public Double getTemperature() {
            return temperature;
        }

        public void setTemperature(Double temperature) {
            this.temperature = temperature;
        }

        private Integer weatherCode;
        private Double temperature;
        // Getter và Setter
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
}
