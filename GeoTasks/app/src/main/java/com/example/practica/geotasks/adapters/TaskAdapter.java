package com.example.practica.geotasks.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.practica.geotasks.R;
import com.example.practica.geotasks.models.Task;
import com.example.practica.geotasks.weather.WeatherInfo;
import com.squareup.picasso.Picasso;

import java.util.List;


/**
 * Created by szili on 2016-08-09.
 */
public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder> {

    private List<Task> taskList;
    private Task task;
    private WeatherInfo weatherInfo;
    private Context context;
    private List<WeatherInfo> weatherInfoList;
    private String base = "file:///android_asset/md-weather-iconset";


    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView taskName, destination, currentWeather;

        private ImageView weatherImg;

        public ViewHolder(View view) {
            super(view);
            taskName = (TextView) view.findViewById(R.id.task);
            destination = (TextView) view.findViewById(R.id.date);
            currentWeather = (TextView) view.findViewById(R.id.weather);
            weatherImg = (ImageView) view.findViewById(R.id.weather_icon);
        }
    }

    public TaskAdapter(List<Task> taskList, List<WeatherInfo> weatherInfoList) {
        this.taskList = taskList;
        this.weatherInfoList = weatherInfoList;
    }

    public void add(int position, Task item) {
        taskList.add(position, item);
        notifyItemInserted(position);
    }

    public void remove(Task item) {
        int position = taskList.indexOf(item);
        taskList.remove(position);
        notifyItemRemoved(position);
    }

    public Task getTask(int position) {
        return taskList.get(position);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_rv, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        task = taskList.get(position);

        holder.taskName.setText(task.getTaskName());
        holder.destination.setText(task.getDestinationName());

        try {
            weatherInfo = weatherInfoList.get(position);
            holder.currentWeather.setText(String.valueOf(weatherInfo.getMain().getTemp()) + "\u2103");
            getWeatherIcon(holder);

        } catch (Exception e) {
            Log.d("out of bound","out of bound");
            holder.currentWeather.setText("N/A");
            Picasso.with(context).load(base + "/weather-none-available.png").into(holder.weatherImg);
        }
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }


    /**
     * Display the correct weather icon from assets based on the data returned from the Retrofit query.
     *
     * @param viewHolder
     */
    private void getWeatherIcon(ViewHolder viewHolder) {


        switch (weatherInfo.getWeather().get(0).getIcon()) {
            case "01d":
                Picasso.with(context).load(base + "/weather-clear.png").into(viewHolder.weatherImg);
                break;
            case "02d":
                Picasso.with(context).load(base + "/weather-few-clouds.png").into(viewHolder.weatherImg);
                break;
            case "03d":
                Picasso.with(context).load(base + "/weather-clouds.png").into(viewHolder.weatherImg);
                break;
            case "04d":
                Picasso.with(context).load(base + "/weather-clouds.png").into(viewHolder.weatherImg);
                break;
            case "09d":
                Picasso.with(context).load(base + "/weather-showers-day.png").into(viewHolder.weatherImg);
                break;
            case "10d":
                Picasso.with(context).load(base + "/weather-rain-day.png").into(viewHolder.weatherImg);
                break;
            case "11d":
                Picasso.with(context).load(base + "/weather-storm-day.png").into(viewHolder.weatherImg);
                break;
            case "13d":
                Picasso.with(context).load(base + "/weather-snow.png").into(viewHolder.weatherImg);
                break;
            case "50d":
                Picasso.with(context).load(base + "/weather-mist.png").into(viewHolder.weatherImg);
                break;
            case "01n":
                Picasso.with(context).load(base + "/weather-clear-night.png").into(viewHolder.weatherImg);
                break;
            case "02n":
                Picasso.with(context).load(base + "/weather-few-clouds-night.png").into(viewHolder.weatherImg);
                break;
            case "03n":
                Picasso.with(context).load(base + "/weather-clouds-night.png").into(viewHolder.weatherImg);
                break;
            case "04n":
                Picasso.with(context).load(base + "/weather-clouds-night.png").into(viewHolder.weatherImg);
                break;
            case "09n":
                Picasso.with(context).load(base + "/weather-showers-night.png").into(viewHolder.weatherImg);
                break;
            case "10n":
                Picasso.with(context).load(base + "/weather-rain-night.png").into(viewHolder.weatherImg);
                break;
            case "11n":
                Picasso.with(context).load(base + "/weather-storm-night.png").into(viewHolder.weatherImg);
                break;
            case "13n":
                Picasso.with(context).load(base + "/weather-snow.png").into(viewHolder.weatherImg);
                break;
            case "50n":
                Picasso.with(context).load(base + "/weather-mist.png").into(viewHolder.weatherImg);
                break;
        }
    }
}
