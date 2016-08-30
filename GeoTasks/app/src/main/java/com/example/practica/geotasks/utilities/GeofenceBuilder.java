package com.example.practica.geotasks.utilities;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.MenuItem;

import com.example.practica.geotasks.activities.CreateTaskActivity;
import com.example.practica.geotasks.service.GeofenceService;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;

/**
 * Created by szili on 2016-08-24.
 */
public class GeofenceBuilder {


    public void startLocationMonitoring(GoogleApiClient googleApiClient) {
        try {
            LocationRequest locationRequest = LocationRequest.create()
                    .setInterval(180000)
                    .setFastestInterval(5000)
                    .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    Log.d("location changed", "lat/long update" + location.getLatitude() + " " + location.getLongitude());
                }
            });
        } catch (SecurityException e) {
            Log.d("error", "Security exception" + e.getMessage());
        }
    }

    public void startGeofenceMonitoring(Context context, GoogleApiClient googleApiClient, String requestId, double latitude, double longitude, float radius) {
        try {
            Geofence geofence = new Geofence.Builder()
                    .setRequestId(requestId)
                    .setCircularRegion(latitude, longitude, radius)
                    .setExpirationDuration(Geofence.NEVER_EXPIRE)
                    .setNotificationResponsiveness(1000)
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                    .build();
            GeofencingRequest geofencingRequest = new GeofencingRequest.Builder()
                    .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
                    .addGeofence(geofence)
                    .build();

            Intent intent = new Intent(context, GeofenceService.class);
            PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);


            if (!googleApiClient.isConnected()) {
                Log.d("api client", "not connected");
            } else {
                LocationServices.GeofencingApi.addGeofences(googleApiClient, geofencingRequest, pendingIntent)
                        .setResultCallback(new ResultCallback<Status>() {
                            @Override
                            public void onResult(@NonNull Status status) {
                                if (status.isSuccess()) {
                                    Log.d("succes", "succesfully added geofence");
                                } else {
                                    Log.d("failed", "failed to add geofence" + status.getStatus());
                                }
                            }
                        });
            }
        } catch (SecurityException e) {
            Log.d("security", e.getMessage());
        }
    }

    public void stopGeofenceMonitoring(GoogleApiClient googleApiClient, String requestId) {
        ArrayList<String> geofenceIds = new ArrayList<>();
        geofenceIds.add(requestId);
        LocationServices.GeofencingApi.removeGeofences(googleApiClient, geofenceIds);
        Log.e("Geofence", "Geofence monitorin has stoped: " + requestId);
    }


    public void stopLocationUpdates(GoogleApiClient mGoogleApiClient, Context context) {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, (LocationListener) context);
    }


}
