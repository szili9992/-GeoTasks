package com.example.practica.geotasks;

/**
 * Created by Practica on 8/9/2016.
 */
public class Task {
    private int id;
    private String taskName;
    private String locationName;
    private String longitude;
    private String latitude;
    private String time;

    public Task(int id, String taskName, String locationName, String longitude, String latitude, String time) {
        this.id = id;
        this.taskName = taskName;
        this.locationName = locationName;
        this.longitude = longitude;
        this.latitude = latitude;
        this.time = time;
    }

    public Task() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", taskName='" + taskName + '\'' +
                ", locationName='" + locationName + '\'' +
                ", longitude='" + longitude + '\'' +
                ", latitude='" + latitude + '\'' +
                ", time='" + time + '\'' +
                '}';
    }
}
