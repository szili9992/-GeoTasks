package com.example.practica.geotasks;

/**
 * Created by Practica on 8/9/2016.
 */
public class Task {
    private int _id,geofenceRadius;
    private String taskName,locationName;
    private double locationLongitude,locationLatitude;
    private long intervalStart,intervalEnd;

    public Task() {
    }

    public Task(int id, int geofenceRadius, String taskName, String locationName, double locationLongitude, double locationLatitude, long intervalStart, long intervalEnd) {
        this._id = id;
        this.geofenceRadius = geofenceRadius;
        this.taskName = taskName;
        this.locationName = locationName;
        this.locationLongitude = locationLongitude;
        this.locationLatitude = locationLatitude;
        this.intervalStart = intervalStart;
        this.intervalEnd = intervalEnd;
    }

    public int getId() {
        return _id;
    }

    public void setId(int id) {
        this._id = id;
    }

    public int getGeofenceRadius() {
        return geofenceRadius;
    }

    public void setGeofenceRadius(int geofenceRadius) {
        this.geofenceRadius = geofenceRadius;
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

    public double getLocationLongitude() {
        return locationLongitude;
    }

    public void setLocationLongitude(double locationLongitude) {
        this.locationLongitude = locationLongitude;
    }

    public double getLocationLatitude() {
        return locationLatitude;
    }

    public void setLocationLatitude(double locationLatitude) {
        this.locationLatitude = locationLatitude;
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
}
