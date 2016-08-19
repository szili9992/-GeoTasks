package com.example.practica.geotasks.weather;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by practica16 on 8/19/2016.
 */
public interface WeatherApi {
//http://api.openweathermap.org/data/2.5/weather?lat=35&lon=139&appid=8617b30a6fc114ad2ad929c111b76edf
    @GET("/data/2.5/weather")
    Call<WeatherInfo> getWeatherData(@Query("lat")double lat,@Query("lon")double lon,@Query("appid")String appid,@Query("units")String units);
}
