package com.example.practica.geotasks.service;

import android.app.Service;

import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;



import com.example.practica.geotasks.utilities.GeofenceBuilder;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;



/**
 * Created by szili on 2016-08-30.
 */
public class LocationService extends Service {

    private GoogleApiClient googleApiClient;
    private GeofenceBuilder geofenceBuilder;




    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        geofenceBuilder = new GeofenceBuilder();
        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(@Nullable Bundle bundle) {
                        Log.d("Connection success", "location service api client");
                        geofenceBuilder.startLocationMonitoring(googleApiClient);
                    }

                    @Override
                    public void onConnectionSuspended(int i) {
                        Log.d("Connection suspended", "location service api client suspended");
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
    }



    @Override
    public void onDestroy() {
        googleApiClient.disconnect();
        super.onDestroy();
    }
}

