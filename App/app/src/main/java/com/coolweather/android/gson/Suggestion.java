package com.coolweather.android.gson;

import com.google.gson.annotations.SerializedName;

public class Suggestion {
    @SerializedName("comf")
    public Comfort comfort;

    public Sport sport;

    @SerializedName("cw")
    public Cw carWash;
    public class Comfort{
        public String type;
        public String brf;
        public String txt;
    }

    public class CarWash{
        @SerializedName("txt")
        public String info;
    }
    public class Sport{
        public String type;
        public String brf;
        public String txt;
    }

    public class Cw{
        public String type;
        public String brf;
        public String txt;
    }
}
