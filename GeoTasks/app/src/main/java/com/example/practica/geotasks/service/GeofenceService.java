package com.example.practica.geotasks.service;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.practica.geotasks.R;
import com.example.practica.geotasks.activities.MainActivity;
import com.example.practica.geotasks.data.TasksDataSource;
import com.example.practica.geotasks.models.Task;
import com.example.practica.geotasks.weather.Main;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.text.DateFormat;
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

    private TasksDataSource taskDataSource = new TasksDataSource(this);
    private ArrayList<Task> everyTask;
    private Task selectedTask;
    private Context context;

    public GeofenceService() {
        super("GeofenceService");
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        taskDataSource.open();
        everyTask = taskDataSource.getAllTasks();
        GeofencingEvent event = GeofencingEvent.fromIntent(intent);
        context = getApplicationContext();


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

    /**
     * When the user enters the geofence area and the compareTime method returns true a heads-up notification will be issued.
     */
    private void showNotification() {

        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);

        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(context, 0, intent, 0);

        Notification mNotification = new Notification.Builder(this)

                .setContentTitle("GeoTask")
                .setContentText("One of your tasks is nearby: " + selectedTask.getDestinationName())
                .setSmallIcon(R.drawable.ic_location_on)
                .setPriority(Notification.PRIORITY_MAX)
                .setContentIntent(pIntent)
                .setDefaults(Notification.DEFAULT_ALL)
                .setLargeIcon(largeIcon)
                .setSound(soundUri)
                .build();

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        notificationManager.notify(0, mNotification);
    }

    /**
     * Compare the geofence received with the ones stored in the database to get the task.
     *
     * @param taskName
     * @param tasks
     * @return
     */
    private Task getTask(String taskName, ArrayList<Task> tasks) {
        for (int i = 0; i < tasks.size(); i++) {
            if (taskName.equals(tasks.get(i).getTaskName())) {
                return tasks.get(i);
            }
        }
        return null;
    }

    /**
     * Compare the start and end time. If the user is in time range then it will return true else false.
     *
     * @return
     */
    private boolean compareTime() {
        Date start_time, end_time, current_time;
        Calendar calendar_start, calendar_end, calendar_current;
        String currentTime;

        calendar_current = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        currentTime = sdf.format(calendar_current.getTime());
        try {
            start_time = new SimpleDateFormat("HH:mm").parse(selectedTask.getIntervalStart());
            calendar_start = Calendar.getInstance();
            calendar_start.setTime(start_time);

            end_time = new SimpleDateFormat("HH:mm").parse(selectedTask.getIntervalEnd());
            calendar_end = Calendar.getInstance();
            calendar_end.setTime(end_time);

            current_time = new SimpleDateFormat("HH:mm").parse(currentTime);
            calendar_current.setTime(current_time);

            //
            if (current_time.compareTo(end_time) < 0) {
                calendar_current.add(Calendar.DATE, 1);
                current_time = calendar_current.getTime();
            }

            if (start_time.compareTo(end_time) < 0) {
                calendar_start.add(Calendar.DATE, 1);
                start_time = calendar_start.getTime();
            }
            //
            if (current_time.before(start_time)) {

                System.out.println(" Time is Lesser ");

                return false;
            } else {

                if (current_time.after(end_time)) {
                    calendar_end.add(Calendar.DATE, 1);
                    end_time = calendar_end.getTime();
                }

                if (current_time.before(end_time)) {
                    System.out.println("RESULT, true");
                    return true;
                } else {
                    System.out.println("RESULT, false");
                    return false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}





