package com.example.weatherapp.activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.weatherapp.R;
import com.example.weatherapp.api.ApiClient;
import com.example.weatherapp.api.ApiInterface;
import com.example.weatherapp.model.WeatherResponseModel;
import com.example.weatherapp.model.dailyWeather.DailyWeatherResponseModel;
import com.example.weatherapp.utils.GlideApp;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_LOCATION = 1;
    String latitude = "12.9762", longitude = "77.6033";
    private ProgressDialog progressDialog;

    private ImageView weather_image_main, weekday_weather_status_ImageView, weekday_weather_status_ImageView1,
            weekday_weather_status_ImageView2;
    private TextView weather_status, weather_location, temperature, temp_min_max,
            weekday_name, weekday_name1, weekday_name2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        latitude = intent.getStringExtra("latitude");
        longitude = intent.getStringExtra("longitude");

        weather_image_main = findViewById(R.id.weather_image_main);
        weekday_weather_status_ImageView = findViewById(R.id.weekday_weather_status_ImageView);
        weekday_weather_status_ImageView1 = findViewById(R.id.weekday_weather_status_ImageView1);
        weekday_weather_status_ImageView2 = findViewById(R.id.weekday_weather_status_ImageView2);

        weather_status = findViewById(R.id.weather_status);
        weather_location = findViewById(R.id.weather_location);
        temperature = findViewById(R.id.temperature);
        temp_min_max = findViewById(R.id.atmospheric_pressure);
        weekday_name = findViewById(R.id.weekday_name);
        weekday_name1 = findViewById(R.id.weekday_name1);
        weekday_name2 = findViewById(R.id.weekday_name2);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading..");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);
        progressDialog.setProgress(0);
        progressDialog.show();


        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                REQUEST_LOCATION);

        try {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 101);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        apiCall();
    }

    private void apiCall() {
        final ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        String exclude = "current,minutely,hourly";

        Log.i("location", longitude + latitude);
        Call<WeatherResponseModel> call = apiService.getWeather(latitude, longitude, "metric", ApiClient.API_KEY);
        Call<DailyWeatherResponseModel> call1 = apiService.getFutureWeather(latitude, longitude, "metric", exclude, ApiClient.API_KEY);

        call.enqueue(new Callback<WeatherResponseModel>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onResponse(Call<WeatherResponseModel> call, Response<WeatherResponseModel> response) {
                try {
                    assert response.body() != null;
                    String icon = response.body().getWeather().get(0).getIcon();
                    String iconUrl = "http://openweathermap.org/img/w/" + icon + ".png";

                    double maxTemp = response.body().getMain().getTempMax();
                    int tempMax1 = (int) maxTemp;

                    double minTemp = response.body().getMain().getTempMin();
                    int tempMin1 = (int) minTemp;

                    String tempMinMax = "H " + tempMax1 + "   L " + tempMin1;

                    double temp = response.body().getMain().getTemp();
                    int temp1 = (int) temp;
                    String temp2 = temp1 + "°";

                    weather_location.setText(response.body().getName());
                    GlideApp.with(MainActivity.this).load(iconUrl).into(weather_image_main);
                    weather_status.setText(response.body().getWeather().get(0).getMain());
                    temperature.setText(temp2);
                    temp_min_max.setText(tempMinMax);

                    progressDialog.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, "Having some issues, Please restart the app", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<WeatherResponseModel> call, Throwable t) {
                Log.e("Error: ", t.getMessage());
                Toast.makeText(MainActivity.this, t.toString(), Toast.LENGTH_LONG).show();
                progressDialog.dismiss();
            }
        });

        call1.enqueue(new Callback<DailyWeatherResponseModel>() {
            @Override
            public void onResponse(Call<DailyWeatherResponseModel> call, Response<DailyWeatherResponseModel> response) {
                ArrayList<String> weekDays = new ArrayList<>();
                weekDays.add("Mon");
                weekDays.add("Tue");
                weekDays.add("Wed");
                weekDays.add("Thu");
                weekDays.add("Fri");
                weekDays.add("Sat");
                weekDays.add("Sun");

                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
                    Date d = new Date();
                    String dayOfTheWeek = sdf.format(d);

                    String dayOfTheWeek2 = null;
                    String dayOfTheWeek3 = null;
                    String day = dayOfTheWeek.substring(0, 3);

                    int i;
                    for (i = 0; i < weekDays.size() - 1; i++) {
                        if (day.equals(weekDays.get(i)) && !day.equals("Fri") && !day.equals("Sat") && !day.equals("Sun")) {
                            dayOfTheWeek = weekDays.get(i + 1);
                            dayOfTheWeek2 = weekDays.get(i + 2);
                            dayOfTheWeek3 = weekDays.get(i + 3);
                        } else if (day.equals("Fri")) {
                            dayOfTheWeek = weekDays.get(5);
                            dayOfTheWeek2 = weekDays.get(6);
                            dayOfTheWeek3 = weekDays.get(0);
                        } else if (day.equals("Sat")) {
                            dayOfTheWeek = weekDays.get(6);
                            dayOfTheWeek2 = weekDays.get(0);
                            dayOfTheWeek3 = weekDays.get(1);
                        } else if (day.equals("Sun")) {
                            dayOfTheWeek = weekDays.get(0);
                            dayOfTheWeek2 = weekDays.get(1);
                            dayOfTheWeek3 = weekDays.get(2);
                        }
                    }

                    assert response.body() != null;
                    String weather_icon = response.body().getDaily().get(1).getWeather().get(0).getIcon();
                    String iconUrl = "http://openweathermap.org/img/w/" + weather_icon + ".png";
                    GlideApp.with(MainActivity.this).
                            load(iconUrl).
                            into(weekday_weather_status_ImageView);
                    weekday_name.setText(dayOfTheWeek);

                    String weather_icon1 = response.body().getDaily().get(2).getWeather().get(0).getIcon();
                    String iconUrl1 = "http://openweathermap.org/img/w/" + weather_icon1 + ".png";
                    Log.i("Check icon UIrl i", iconUrl1);
                    GlideApp.with(MainActivity.this).
                            load(iconUrl1).
                            into(weekday_weather_status_ImageView1);
                    weekday_name1.setText(dayOfTheWeek2);

                    String weather_icon2 = response.body().getDaily().get(3).getWeather().get(0).getIcon();
                    String iconUrl2 = "http://openweathermap.org/img/w/" + weather_icon2 + ".png";
                    GlideApp.with(MainActivity.this).
                            load(iconUrl2).
                            into(weekday_weather_status_ImageView2);
                    weekday_name2.setText(dayOfTheWeek3);

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, e.toString(), Toast.LENGTH_LONG).show();

                }

            }

            @Override
            public void onFailure(Call<DailyWeatherResponseModel> call, Throwable t) {
                Log.e("Error: ", t.getMessage());
                Toast.makeText(MainActivity.this, t.toString(), Toast.LENGTH_LONG).show();
            }
        });
    }

}