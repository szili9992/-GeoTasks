package com.example.practica.geotasks;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
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
        GeofencingEvent event = GeofencingEvent.fromIntent(intent);
        if (event.hasError()) {
            Log.d("error", event.toString());
        } else {
            int transition = event.getGeofenceTransition();
            List<Geofence> geofences = event.getTriggeringGeofences();
            Geofence geofence = geofences.get(0);
            String requestId = geofence.getRequestId();

            if (transition == Geofence.GEOFENCE_TRANSITION_ENTER) {
//                NotificationCompat.Builder builder=new NotificationCompat.Builder(this)
//                        .setContentTitle("GeoTask")
//                        .setContentText("You're entering the geofence area");
//
//                Notification notification=builder.build();
//                NotificationManager nm=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);
//                nm.notify(1,notification);
                showNotification();
                Log.e("enter", "you entered");


            } else if (transition == Geofence.GEOFENCE_TRANSITION_EXIT) {
//                NotificationCompat.Builder builder=new NotificationCompat.Builder(this)
//                        .setContentTitle("GeoTask")
//                        .setContentText("You're leaving the geofence area");
//
//
//                Notification notification2=builder.build();
//                NotificationManager nm2=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);
//                nm2.notify(1,notification2);

                Log.e("leave", "you left");

            }
        }

    }


    public void showNotification() {

        // define sound URI, the sound to be played when there's a notification
        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        // intent triggered, you can add other intent for other actions
//        Intent intent = new Intent(MainActivity.this, NotificationReceiver.class);
//        PendingIntent pIntent = PendingIntent.getActivity(MainActivity.this, 0, intent, 0);

        // this is it, we'll build the notification!
        // in the addAction method, if you don't want any icon, just set the first param to 0
        Notification mNotification = new Notification.Builder(this)

                .setContentTitle("New Post!")
                .setContentText("Here's an awesome update for you!")

                .setSound(soundUri)


                .build();

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        notificationManager.notify(0, mNotification);
    }
}
