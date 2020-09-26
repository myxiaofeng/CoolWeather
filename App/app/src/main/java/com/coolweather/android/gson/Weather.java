package com.coolweather.android.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Weather {
    @SerializedName("basic")
    public Basic basic;
    @SerializedName("update")
    public Update update;
    @SerializedName("status")
    public String status;
    @SerializedName("now")
    public Now now;
    @SerializedName("daily_forecast")
    public List<Forecast> forecastList;
    @SerializedName("aqi")
    public AQI aqi;
    @SerializedName("suggestion")
    public Suggestion suggestion;
    @SerializedName("msg")
    public String msg;

}
