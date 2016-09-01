package com.example.practica.geotasks.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;


import com.example.practica.geotasks.data.TasksDataSource;
import com.example.practica.geotasks.models.Task;
import com.example.practica.geotasks.utilities.GeofenceBuilder;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;

/**
 * Created by szili on 2016-08-29.
 */
public class OnBootStartService extends Service {
    private TasksDataSource tasksDataSource;
    private ArrayList<Task> tasks;
    private GoogleApiClient googleApiClient;
    private GeofenceBuilder geofenceBuilder;
    private Context context;
    private final String tag = "on boot service";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        tasksDataSource = new TasksDataSource(this);
        tasksDataSource.open();
        geofenceBuilder = new GeofenceBuilder();
        tasks = tasksDataSource.getAllTasks();

        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(@Nullable Bundle bundle) {
                        Log.d("Connection success", "bootup apiclient");
                    }

                    @Override
                    public void onConnectionSuspended(int i) {
                        Log.d("Connection suspended", "On boot connectrion susspended");
                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Log.d("Connection failed", connectionResult.getErrorMessage());
                    }
                })
                .build();
        googleApiClient.connect();
        googleApiClient.registerConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
            @Override
            public void onConnected(@Nullable Bundle bundle) {
                startGeofenceAndLocationMonitoring(context);
            }

            @Override
            public void onConnectionSuspended(int i) {
                Log.d("api client", "connection suspended");
            }
        });
        tasksDataSource.close();
    }


    /**
     * On successful bootup get tasks from database and start location and geofence monitoring
     *
     * @param context
     */
    private void startGeofenceAndLocationMonitoring(Context context) {

        for (int i = 0; i < tasks.size(); i++) {
            try {
                geofenceBuilder.startLocationMonitoring(googleApiClient);
                geofenceBuilder.startGeofenceMonitoring(context, googleApiClient, tasks.get(i).getTaskName(),
                        tasks.get(i).getDestinationLatitude(), tasks.get(i).getDestinationLongitude(), tasks.get(i).getGeofenceRadius());
                Toast.makeText(OnBootStartService.this, "Succesfully started and restored Geofences!", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Log.e(tag, "Can't start geofence on boot: " + e.getMessage());
            }
        }
    }

    @Override
    public void onDestroy() {
        geofenceBuilder.stopLocationUpdates(googleApiClient,context);
        super.onDestroy();
    }
}
