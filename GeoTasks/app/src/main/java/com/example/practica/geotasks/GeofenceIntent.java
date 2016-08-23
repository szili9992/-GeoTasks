package com.example.practica.geotasks;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.List;

/**
 * Created by szili on 2016-08-23.
 */
public class GeofenceIntent extends IntentService {

    public GeofenceIntent() {
        super("MyGeofenceId");
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        GeofencingEvent event=GeofencingEvent.fromIntent(intent);
        if(event.hasError()){
            Log.d("error",event.toString());
        }
        else{
            int transition=event.getGeofenceTransition();
            List<Geofence> geofences=event.getTriggeringGeofences();
            Geofence geofence=geofences.get(0);
            String requestId=geofence.getRequestId();

            if (transition==Geofence.GEOFENCE_TRANSITION_ENTER){
//                NotificationCompat.Builder builder=new NotificationCompat.Builder(this)
//                        .setContentTitle("GeoTask")
//                        .setContentText("You're entering the geofence area");
//
//                Notification notification=builder.build();
//                NotificationManager nm=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);
//                nm.notify(1,notification);
                Log.e("enter","you entered");


            }
            else if(transition==Geofence.GEOFENCE_TRANSITION_EXIT){
//                NotificationCompat.Builder builder=new NotificationCompat.Builder(this)
//                        .setContentTitle("GeoTask")
//                        .setContentText("You're leaving the geofence area");
//
//
//                Notification notification2=builder.build();
//                NotificationManager nm2=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);
//                nm2.notify(1,notification2);

                Log.e("leave","you left");

            }
        }

    }
}
