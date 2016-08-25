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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
        everyTask = taskDataSource.getAllTasks();
        GeofencingEvent event = GeofencingEvent.fromIntent(intent);


        if (event.hasError()) {
            Log.d("error", event.toString());
        } else {
            int transition = event.getGeofenceTransition();
            List<Geofence> geofences = event.getTriggeringGeofences();
            Geofence geofence = geofences.get(0);
            String requestId = geofence.getRequestId();
            selectedTask = getTask(requestId, everyTask);

            Log.e("compare time", String.valueOf(compareTime()));
            if (transition == Geofence.GEOFENCE_TRANSITION_ENTER && compareTime()) {
                showNotification();
                Log.e("enter", "you entered");


            } else if (transition == Geofence.GEOFENCE_TRANSITION_EXIT) {
//                showNotification();
                Log.e("leave", "you left");

            }
        }

    }


    public void showNotification() {

        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);

        // intent triggered, you can add other intent for other actions
//        Intent intent = new Intent(MainActivity.this, NotificationReceiver.class);
//        PendingIntent pIntent = PendingIntent.getActivity(MainActivity.this, 0, intent, 0);

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
            if (taskName.equals(tasks.get(i).getTaskName())) {
                return tasks.get(i);
            }
        }
        return null;
    }

    public boolean compareTime() {
        Date start_time, end_time, current_time;
        Calendar calendar_start, calendar_end, calendar_current;
        int start_time_hour, start_time_minute, end_time_hour, end_time_minute, current_time_hour, current_time_minute;

        try {
            start_time = new SimpleDateFormat("hh:mm").parse(selectedTask.getIntervalStart());
            end_time = new SimpleDateFormat("hh:mm").parse(selectedTask.getIntervalEnd());

            calendar_start = Calendar.getInstance();
            calendar_start.setTime(start_time);
            start_time_hour = calendar_start.get(Calendar.HOUR_OF_DAY);
            start_time_minute = calendar_start.get(Calendar.MINUTE);

            calendar_end = Calendar.getInstance();
            calendar_end.setTime(end_time);
            end_time_hour = calendar_end.get(Calendar.HOUR_OF_DAY);
            end_time_minute = calendar_end.get(Calendar.MINUTE);

            calendar_current = Calendar.getInstance();
            current_time_hour = calendar_current.get(Calendar.HOUR_OF_DAY);
            current_time_minute = calendar_current.get(Calendar.MINUTE);

            current_time = calendar_current.getTime();

            Log.e("time", "current time:" + String.valueOf(current_time) + " |start time:" + String.valueOf(calendar_start.getTime()) + " |end time:" + String.valueOf(calendar_end.getTime()));

            if (current_time_hour >= start_time_hour && current_time_hour <= end_time_hour) {
                if (current_time_minute >= start_time_minute && current_time_minute <= end_time_minute)
                    return true;
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }
}
