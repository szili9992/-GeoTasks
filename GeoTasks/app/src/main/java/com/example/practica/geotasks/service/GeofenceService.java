package com.example.practica.geotasks.service;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.practica.geotasks.R;
import com.example.practica.geotasks.data.TasksDataSource;
import com.example.practica.geotasks.models.Task;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by szili on 2016-08-23.
 */
public class GeofenceService extends IntentService {

    TasksDataSource taskDataSource = new TasksDataSource(this);
    ArrayList<Task> everyTask;
    Task selectedTask;


    public GeofenceService() {
        super("MyGeofenceId");
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        taskDataSource.open();
        everyTask=taskDataSource.getAllTasks();
        GeofencingEvent event = GeofencingEvent.fromIntent(intent);


        if (event.hasError()) {
            Log.d("error", event.toString());
        } else {
            int transition = event.getGeofenceTransition();
            List<Geofence> geofences = event.getTriggeringGeofences();
            Geofence geofence = geofences.get(0);
            String requestId = geofence.getRequestId();
            selectedTask=getTask(requestId,everyTask);


            if (transition == Geofence.GEOFENCE_TRANSITION_ENTER) {
                showNotification();
                Log.e("enter", "you entered");


            } else if (transition == Geofence.GEOFENCE_TRANSITION_EXIT) {
//                showNotification();
                Log.e("leave", "you left");

            }
        }

    }


    public void showNotification() {

        // define sound URI, the sound to be played when there's a notification
        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);

        // intent triggered, you can add other intent for other actions
//        Intent intent = new Intent(MainActivity.this, NotificationReceiver.class);
//        PendingIntent pIntent = PendingIntent.getActivity(MainActivity.this, 0, intent, 0);

        // this is it, we'll build the notification!
        // in the addAction method, if you don't want any icon, just set the first param to 0
        Notification mNotification = new Notification.Builder(this)

                .setContentTitle("GeoTask")
                .setContentText("One of your tasks is nearby: " + selectedTask.getDestinationName())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(largeIcon)
                .setSound(soundUri)
                .build();

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        notificationManager.notify(0, mNotification);
    }

    public Task getTask(String taskName, ArrayList<Task> tasks) {
        for (int i = 0; i < tasks.size(); i++) {
            if (taskName.equals(tasks.get(i).getTaskName()) ) {
                return tasks.get(i);
            }
        }
        return null;
    }
}
