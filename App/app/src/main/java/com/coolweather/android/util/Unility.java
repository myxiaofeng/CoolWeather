package com.coolweather.android.util;

import android.text.TextUtils;
import android.util.Log;
import android.widget.LinearLayout;

import com.coolweather.android.db.City;
import com.coolweather.android.db.County;
import com.coolweather.android.db.Province;
import com.coolweather.android.gson.AQI;
import com.coolweather.android.gson.Basic;
import com.coolweather.android.gson.Forecast;
import com.coolweather.android.gson.Now;
import com.coolweather.android.gson.Suggestion;
import com.coolweather.android.gson.Update;
import com.coolweather.android.gson.Weather;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.function.LongFunction;

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
    private static void parseWeather(Weather weather){
        Basic basic = weather.basic;
        Update update = weather.update;
        String status = weather.status;
        Now now = weather.now;
        List<Forecast> forecastList = weather.forecastList;
        AQI aqi = weather.aqi;
        Suggestion suggestion = weather.suggestion;
        String msg = weather.msg;
        {
            Log.d("ZGF", basic.cid);
            Log.d("ZGF", basic.location);
            Log.d("ZGF", basic.parent_city);
            Log.d("ZGF", basic.admin_area);
            Log.d("ZGF", basic.cnty);
            Log.d("ZGF", basic.lat);
            Log.d("ZGF", basic.lon);
            Log.d("ZGF", basic.tz);
            Log.d("ZGF", basic.cityName);
            Log.d("ZGF", basic.weatherId);
            Log.d("ZGF", basic.update.locTime);
            Log.d("ZGF", basic.update.utcTime);
        }

        {
            Log.d("ZGF", update.locTime);
            Log.d("ZGF", update.utcTime);
        }
        {
            Log.d("ZGF", status);
        }

        {
            Log.d("ZGF", now.cloud);
            Log.d("ZGF", now.cond_code);
            Log.d("ZGF", now.cond_txt);
            Log.d("ZGF", now.fl);
            Log.d("ZGF", now.hum);
            Log.d("ZGF", now.pcpn);
            Log.d("ZGF", now.pres);
            Log.d("ZGF", now.temperature);
            Log.d("ZGF", now.vis);
            Log.d("ZGF", now.wind_deg);
            Log.d("ZGF", now.wind_dir);
            Log.d("ZGF", now.wind_sc);
            Log.d("ZGF", now.wind_spd);
            Log.d("ZGF", now.cond.code);
            Log.d("ZGF", now.cond.txt);
        }


        {
            for(int i = 0; i < forecastList.size(); i++){
                Log.d("ZGF", forecastList.get(i).date);
                Log.d("ZGF", forecastList.get(i).cond.txt_d);
                Log.d("ZGF", forecastList.get(i).temperature.max);
                Log.d("ZGF", forecastList.get(i).temperature.min);
            }

        }

        {
            Log.d("ZGF", aqi.city.aqi);
            Log.d("ZGF", aqi.city.pm25);
            Log.d("ZGF", aqi.city.qlty);
        }

        {
            Log.d("ZGF", suggestion.comfort.type);
            Log.d("ZGF", suggestion.comfort.brf);
            Log.d("ZGF", suggestion.comfort.txt);

            Log.d("ZGF", suggestion.sport.type);
            Log.d("ZGF", suggestion.sport.brf);
            Log.d("ZGF", suggestion.sport.txt);

            Log.d("ZGF", suggestion.carWash.type);
            Log.d("ZGF", suggestion.carWash.brf);
            Log.d("ZGF", suggestion.carWash.txt);
        }

        {
            Log.d("ZGF", msg);
        }
    };

    public static Weather handleWeatherResponse(String response) {

        try{

            JSONObject jsonObject = new JSONObject(response);
            Log.d("ZGF", jsonObject.toString());
            JSONArray jsonArray = jsonObject.getJSONArray("HeWeather");
            String weatherContent = jsonArray.getJSONObject(0).toString();
            Log.d("ZGF", weatherContent);
            Weather weather = new Gson().fromJson(weatherContent, Weather.class);
            parseWeather(weather);
            return weather;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
};
