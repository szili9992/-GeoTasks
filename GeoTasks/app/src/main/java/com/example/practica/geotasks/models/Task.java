package com.example.practica.geotasks.models;

import com.example.practica.geotasks.weather.WeatherInfo;

/**
 * Created by Practica on 8/9/2016.
 */
public class Task {
    private int _id;
    private String taskName, destinationName;
    private double destinationLongitude, destinationLatitude;
    private long intervalStart, intervalEnd;
    private int geofenceRadius;
    private WeatherInfo weatherInfo;


    public Task() {
    }

    public Task(int _id, String taskName, String destinationName, double destinationLongitude, double destinationLatitude, long intervalStart, long intervalEnd, int geofenceRadius, double weather) {
        this._id = _id;
        this.taskName = taskName;
        this.destinationName = destinationName;
        this.destinationLongitude = destinationLongitude;
        this.destinationLatitude = destinationLatitude;
        this.intervalStart = intervalStart;
        this.intervalEnd = intervalEnd;
        this.geofenceRadius = geofenceRadius;
        this.weatherInfo=new WeatherInfo();
        weatherInfo.getMain().setTemp(weather);



    }



    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getDestinationName() {
        return destinationName;
    }

    public void setDestinationName(String destinationName) {
        this.destinationName = destinationName;
    }

    public double getDestinationLongitude() {
        return destinationLongitude;
    }

    public void setDestinationLongitude(double destinationLongitude) {
        this.destinationLongitude = destinationLongitude;
    }

    public double getDestinationLatitude() {
        return destinationLatitude;
    }

    public void setDestinationLatitude(double destinationLatitude) {
        this.destinationLatitude = destinationLatitude;
    }

    public long getIntervalStart() {
        return intervalStart;
    }

    public void setIntervalStart(long intervalStart) {
        this.intervalStart = intervalStart;
    }

    public long getIntervalEnd() {
        return intervalEnd;
    }

    public void setIntervalEnd(long intervalEnd) {
        this.intervalEnd = intervalEnd;
    }

    public int getGeofenceRadius() {
        return geofenceRadius;
    }

    public void setGeofenceRadius(int geofenceRadius) {
        this.geofenceRadius = geofenceRadius;
    }

    public WeatherInfo getWeatherInfo() {
        return weatherInfo;
    }

    public void setWeatherInfo(WeatherInfo weatherInfo) {
        this.weatherInfo = weatherInfo;
    }

    @Override
    public String toString() {
        return "Task{" +
                "_id=" + _id +
                ", taskName='" + taskName + '\'' +
                ", destinationName='" + destinationName + '\'' +
                ", destinationLongitude=" + destinationLongitude +
                ", destinationLatitude=" + destinationLatitude +
                ", intervalStart=" + intervalStart +
                ", intervalEnd=" + intervalEnd +
                ", geofenceRadius=" + geofenceRadius +
                ", weatherInfo=" + weatherInfo +
                '}';
    }
}
