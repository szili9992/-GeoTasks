package com.example.practica.geotasks.weather;

import java.io.IOException;

import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by practica16 on 8/19/2016.
 */
public class WeatherInfoService {

    private WeatherApi weatherApi;

    public WeatherInfoService(){
        Retrofit retrofit=new Retrofit.Builder()
                .baseUrl("http://api.openweathermap.org")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        weatherApi=retrofit.create(WeatherApi.class);
    }

    public void getWeatherData(double lat, double lon,String appid,String units, Callback callback) throws IOException {
        weatherApi.getWeatherData(lat, lon,appid,units).enqueue(callback);
    }

}
