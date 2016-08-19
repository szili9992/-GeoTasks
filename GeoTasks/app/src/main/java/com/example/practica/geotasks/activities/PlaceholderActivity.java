package com.example.practica.geotasks.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.example.practica.geotasks.R;
import com.example.practica.geotasks.weather.WeatherApi;
import com.example.practica.geotasks.weather.WeatherInfo;
import com.example.practica.geotasks.weather.WeatherInfoService;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PlaceholderActivity extends AppCompatActivity {
    private WeatherInfoService service;
    private TextView name,temperaturePlace;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_placeholder);
        name=(TextView)findViewById(R.id.name_place);
        temperaturePlace=(TextView)findViewById(R.id.temperature);

        service=new WeatherInfoService();

        getWeatherForCoords(35,139,"8617b30a6fc114ad2ad929c111b76edf","metric");
    }


    private void getWeatherForCoords(double lat, double lon,String appid,String units) {
        try {
            service.getWeatherData(lat, lon,appid,units, new Callback() {
                @Override
                public void onResponse(Call call, Response response) {
                    WeatherInfo weatherInfo = (WeatherInfo) response.body();
                    String place=weatherInfo.getName();
                    String temperature=String.valueOf(weatherInfo.getMain().getTemp());

                    name.setText("Place: "+place);
                    temperaturePlace.setText("Weather: "+temperature+" \u2109");

                }

                @Override
                public void onFailure(Call call, Throwable t) {

                }

            });
        } catch (IOException e) {
            Toast.makeText(PlaceholderActivity.this, "igy jartal faszi", Toast.LENGTH_SHORT).show();
        }
    }
}
