package com.example.weatherapp.api;

import com.example.weatherapp.model.WeatherResponseModel;
import com.example.weatherapp.model.dailyWeather.DailyWeatherResponseModel;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiInterface {

    @GET("weather")
    Call<WeatherResponseModel> getWeather(@Query("lat") String lat, @Query("lon")
            String lon,@Query("units") String unit, @Query("appid") String appId);
    @GET("onecall")
    Call<DailyWeatherResponseModel> getFutureWeather(@Query("lat") String lat, @Query("lon")
            String lon, @Query("units") String unit,@Query("exclude") String exclude, @Query("appid") String appId);

}
