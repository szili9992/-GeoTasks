package com.example.practica.geotasks.models;

import com.example.practica.geotasks.weather.WeatherInfo;

/**
 * Created by Practica on 8/9/2016.
 */
public class Task {
    private int _id;
    private String taskName, destinationName,intervalStart, intervalEnd;;
    private double destinationLongitude, destinationLatitude, weather;
    private int geofenceRadius;




    public Task() {
    }

    public Task(int _id, String taskName, String destinationName, double destinationLongitude, double destinationLatitude, String intervalStart, String intervalEnd, int geofenceRadius, double weather) {

        this._id = _id;
        this.taskName = taskName;
        this.destinationName = destinationName;
        this.destinationLongitude = destinationLongitude;
        this.destinationLatitude = destinationLatitude;
        this.intervalStart = intervalStart;
        this.intervalEnd = intervalEnd;
        this.geofenceRadius = geofenceRadius;
        this.weather = weather;


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

    public String getIntervalStart() {
        return intervalStart;
    }

    public void setIntervalStart(String intervalStart) {
        this.intervalStart = intervalStart;
    }

    public String getIntervalEnd() {
        return intervalEnd;
    }

    public void setIntervalEnd(String intervalEnd) {
        this.intervalEnd = intervalEnd;
    }

    public int getGeofenceRadius() {
        return geofenceRadius;
    }

    public void setGeofenceRadius(int geofenceRadius) {
        this.geofenceRadius = geofenceRadius;
    }

    public double getWeather() {
        return weather;
    }

    public void setWeather(double weather) {
        this.weather = weather;
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
                '}';
    }
}
