package com.coolweather.android.util;

import android.text.TextUtils;
import android.util.Log;

import com.coolweather.android.db.City;
import com.coolweather.android.db.County;
import com.coolweather.android.db.Province;
import com.coolweather.android.gson.Weather;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Unility {
    /*解析和处理Province*/
    public static boolean handleProvinceResponse(String response){
        Log.d("ZGF", "in handleProvinceResponse");
        if(!TextUtils.isEmpty(response)){
            try{
                Log.d("ZGF", "response is empty");
                JSONArray allProvinces = new JSONArray(response);
                for(int i= 0; i < allProvinces.length(); i++){
                    JSONObject provinceObject = allProvinces.getJSONObject(i);
                    Province province = new Province();
                    province.setProvinceName(provinceObject.getString("name"));
                    province.setProvinceCode(provinceObject.getInt("id"));
                    province.save();
                }
                return true;
            }catch (JSONException e){
                e.printStackTrace();
            }
        }
        return false;
    };
    /*解析和处理city*/
    public static boolean handleCityResponse(String response, int provinceId){
        Log.d("ZGF", "in handleCityResponse");
        if(!TextUtils.isEmpty(response)){
            try{
                Log.d("ZGF", "in handleCityResponse try");
                JSONArray allCities = new JSONArray(response);
                for(int i= 0; i < allCities.length(); i++){
                    JSONObject cityObject = allCities.getJSONObject(i);
                    City city = new City();
                    city.setCityName(cityObject.getString("name"));
                    city.setCityCode(cityObject.getInt("id"));
                    city.setProvinceId(provinceId);
                    Log.d("ZGF", "in handleCityResponse and test save");
                    city.save();
                }
                return true;
            }catch (JSONException e){
                Log.d("ZGF", "in handleCityResponse");
                e.printStackTrace();
            }
        }
        return false;
    };

    /*解析和处理country*/
    public static boolean handleCountyResponse(String response, int cityId){
        Log.d("ZGF", "in handleCountyResponse");
        if(!TextUtils.isEmpty(response)){
            try{
                JSONArray allCounties = new JSONArray(response);
                for(int i= 0; i < allCounties.length(); i++){
                    JSONObject countyObject = allCounties.getJSONObject(i);
                    County county = new County();
                    county.setCountyName(countyObject.getString("name"));
                    county.setWeatherId(countyObject.getString("weather_id"));
                    county.setCityId(cityId);
                    county.save();
                }
                return true;
            }catch (JSONException e){
                e.printStackTrace();
            }
        }
        return false;
    };
    /*将返回的JSON数据解析成weather实体类*/


    public static Weather handleWeatherResponse(String response) {
        try{
            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArray = jsonObject.getJSONArray("HeWeather");
            String weatherContent = jsonArray.getJSONArray(0).toString();
            return new Gson().fromJson(weatherContent, Weather.class);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
};
