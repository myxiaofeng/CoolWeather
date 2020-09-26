package com.coolweather.android.gson;

import com.google.gson.annotations.SerializedName;

import org.litepal.tablemanager.AssociationUpdater;

public class Basic {

    public String cid;
    public String location;
    public String parent_city;
    public String admin_area;
    public String cnty;
    public String lat;
    public String lon;
    public String tz;
    @SerializedName("city")
    public String cityName;
    @SerializedName("id")
    public String weatherId;


    public Update update;


}
