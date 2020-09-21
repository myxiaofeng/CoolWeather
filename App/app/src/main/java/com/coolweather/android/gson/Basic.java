package com.coolweather.android.gson;

import com.google.gson.annotations.SerializedName;

import org.litepal.tablemanager.AssociationUpdater;

public class Basic {
    @SerializedName("city")
    public String cityName;
    @SerializedName("id")
    public String weatherId;

    public Update update;

    public class Update{
      public String updateTime;
    };
}
