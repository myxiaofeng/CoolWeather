package com.coolweather.android.gson;

import com.google.gson.annotations.SerializedName;

public class Forecast {
    public String date;
    @SerializedName("cond")
    public Cond cond;

    @SerializedName("tmp")
    public Temperature temperature;
    public class Cond{
      public String txt_d;
    };
    public class Temperature{
        public String max;
        public String min;
    }

    public class More{
        @SerializedName("txt.d")
        public String info;
    }

}
