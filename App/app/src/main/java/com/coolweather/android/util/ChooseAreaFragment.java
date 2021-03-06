package com.coolweather.android.util;

import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.coolweather.android.MainActivity;
import com.coolweather.android.R;
import com.coolweather.android.WeatherActivity;
import com.coolweather.android.db.City;
import com.coolweather.android.db.County;
import com.coolweather.android.db.Province;
import com.coolweather.android.gson.Weather;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.ListResourceBundle;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ChooseAreaFragment extends Fragment {
    public static final int LEVEL_PROVINCE = 0;
    public static final int LEVEL_CITY = 1;
    public static final int LEVEL_COUNTY = 2;
    private ProgressDialog progressDialog;
    private TextView titleText;
    private Button backButton;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private List<String> dataList = new ArrayList<>();

    /*省列表*/
    private List<Province> provinceList;
    /*市列表*/
    private List<City> cityList;
    /*县列表*/
    private List<County> countyList;

    /*选中的省份*/
    private Province selectedProvince;

    /*选中的城市*/
    private City selectedCity;

    /*当前选中的级别*/
    private int currentLevel;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.choose_area, container, false);
        titleText = (TextView)view.findViewById(R.id.title_text);
        backButton = (Button)view.findViewById(R.id.back_button);
        listView = (ListView)view.findViewById(R.id.list_view);
        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, dataList);
        listView.setAdapter(adapter);
        return view;
    }

    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(currentLevel == LEVEL_PROVINCE){
                    Log.d("ZGF", "ask for cities");
                    selectedProvince = provinceList.get(position);
                    Log.d("ZGF", selectedProvince.getProvinceName());
                    queryCities();
                }
                else if(currentLevel == LEVEL_CITY){
                    Log.d("ZGF", "ask for counties");
                    selectedCity = cityList.get(position);
                    queryCounties();
                }
                else if(currentLevel == LEVEL_COUNTY){
                    String weatherId = countyList.get(position).getWeatherId();
                    if(getActivity() instanceof MainActivity){
                        Intent intent = new Intent(getActivity(), WeatherActivity.class);
                        intent.putExtra("weather_id", weatherId);
                        startActivity(intent);
                        getActivity().finish();
                    }
                    else if(getActivity() instanceof WeatherActivity){
                        WeatherActivity activity = (WeatherActivity)getActivity();
                        activity.drawerLayout.closeDrawers();
                        activity.swipeRefresh.setRefreshing(true);
                        activity.requestWeather(weatherId);
                    }
                }
            }
        });
        backButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                if(currentLevel == LEVEL_COUNTY){
                    queryCities();
                }
                else if(currentLevel == LEVEL_CITY){
                    queryProvinces();
                }
            }
        });
        queryProvinces();
    }
    /*查询全国所有的省，有限从数据库查询，如果没有查询到再去服务器上查询*/
    private void queryProvinces(){
        titleText.setText("中国");
        backButton.setVisibility(View.GONE);
        provinceList = DataSupport.findAll(Province.class);
        if(provinceList.size() > 0){
            dataList.clear();
            for(Province province : provinceList){
                dataList.add(province.getProvinceName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_PROVINCE;
        }
        else{
            Log.d("ZGF", "queryProvinces, network");
            String address = "http://guolin.tech/api/china";
            queryFromServer(address, "province");
        }
    }
    private void queryCities(){
        Log.d("ZGF", "In queryCities");
        titleText.setText(selectedProvince.getProvinceName());
        backButton.setVisibility(View.VISIBLE);
        cityList = DataSupport.where("provinceid = ?", String.valueOf(selectedProvince.getId())).find(City.class);
        if(cityList.size() > 0){
            Log.d("ZGF", "cityList is not empty");
            dataList.clear();
            for(City city : cityList){
                dataList.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_CITY;
        }
        else{
            Log.d("ZGF", "cityList is empty and get from network");
            int provinceCode = selectedProvince.getProvinceCode();
            Log.d("ZGF", "provinceCode: "+provinceCode);
            String address = "http://guolin.tech/api/china/" + provinceCode;
            queryFromServer(address, "city");
        }
    }
    /*查询全国所有的市，有限从数据库查询，如果没有查询到再去服务器上查询*/
    private void queryCounties(){
        titleText.setText(selectedCity.getCityName());
        backButton.setVisibility(View.VISIBLE);
        countyList = DataSupport.where("cityid = ?", String.valueOf(selectedCity.getId())).find(County.class);
        if(countyList.size() > 0){
            dataList.clear();
            for(County county : countyList){
                dataList.add(county.getCountyName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_COUNTY;
        }
        else{
            int provinceCode = selectedProvince.getProvinceCode();
            int cityCode = selectedCity.getCityCode();
            String address = "http://guolin.tech/api/china/" + provinceCode + '/' + cityCode;

            queryFromServer(address, "county");
        }
    }

    private static boolean ret = true;
    /*根据传入的地址和类型从服务器上查询省市县数据*/
    private boolean queryFromServer(String address, final String type){

        Log.d("ZGF", "in queryFromServer");
        showProgressDailog();
        ret = true;
        HttpUtil.sendOkHttpRequest(address, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("ZGF", "in onFailure");
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDailog();
                        Toast.makeText(getContext(), "加载失败", Toast.LENGTH_SHORT).show();
                    }
                });
                ret = false;
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d("ZGF", "in onResponse");
                String responseText = response.body().string();
                boolean result = false;
                if("province".equals(type)){
                    result = Unility.handleProvinceResponse(responseText);
                }
                else if("city".equals(type)){
                    result = Unility.handleCityResponse(responseText, selectedProvince.getId());
                }
                else if("county".equals(type)){
                    result = Unility.handleCountyResponse(responseText, selectedCity.getId());
                }
                if(result){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDailog();
                            if("province".equals(type)){
                                queryProvinces();
                            }
                            else if("city".equals(type)){
                                queryCities();
                            }
                            else if("county".equals(type)){
                                queryCounties();
                            }
                        }
                    });
                }
            }
        });
        return ret;
    }
    /*显示进度对话框*/
    private void showProgressDailog(){
        if(progressDialog == null){
            Log.d("ZGF", "In showProgressDailog");
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("正在加载");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        Log.d("ZGF", "In showProgressDailog");
        progressDialog.show();
    }
    private void closeProgressDailog(){
        if(progressDialog != null){
            progressDialog.dismiss();
        }
    }
}

