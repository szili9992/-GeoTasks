package com.example.practica.geotasks;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;

import java.io.Serializable;
import java.util.ArrayList;

public class CreateTaskActivity extends AppCompatActivity {

    private PlacePicker.IntentBuilder builder;
    private static final int PLACE_PICKER_FLAG = 1;
    private EditText taskName, taskDate;
    private TextView longitude, latitude, destination, intervalStart, intervalEnd, geofenceRadius;
    private TasksDataSource dataSource;
    private Place place;
    private int taskId;
    private Task selectedTask;
    private boolean update = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_task);
        Intent editIntent = getIntent();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        taskName = (EditText) findViewById(R.id.taskName);
        longitude = (TextView) findViewById(R.id.longitude);
        latitude = (TextView) findViewById(R.id.latitude);
        destination = (TextView) findViewById(R.id.destinationName);
        intervalStart = (TextView) findViewById(R.id.intervalStart);
        intervalEnd = (TextView) findViewById(R.id.intervalEnd);
        geofenceRadius = (TextView) findViewById(R.id.geofenceRadius);

        dataSource = new TasksDataSource(this);
        dataSource.open();

        ArrayList<Task> allTasks = dataSource.getAllTaks();


        if (editIntent.getExtras() != null) {
            taskId = editIntent.getExtras().getInt("id");
            selectedTask = getTask(taskId, allTasks);

            taskName.setText(selectedTask.getTaskName());
            longitude.setText(String.valueOf(selectedTask.getDestinationLongitude()));
            latitude.setText(String.valueOf(selectedTask.getDestinationLatitude()));
            destination.setText(selectedTask.getDestinationName());
            intervalStart.setText(String.valueOf(selectedTask.getIntervalStart()));
            intervalEnd.setText(String.valueOf(selectedTask.getIntervalEnd()));
            geofenceRadius.setText(String.valueOf(selectedTask.getGeofenceRadius()));

            update = true;
        }


    }

    public void addTask(View view) {
        Intent intent = new Intent(CreateTaskActivity.this, MainActivity.class);
        Task task = new Task();
        task.setTaskName(taskName.getText().toString());
        task.setDestinationName(place.getName().toString());
        task.setDestinationLongitude(place.getLatLng().longitude);
        task.setDestinationLatitude(place.getLatLng().latitude);
        task.setIntervalStart(11);
        task.setIntervalEnd(34);
        Log.d("TASK AT ADD", "task: " + task.toString());

        if (update) {
            task.set_id(selectedTask.get_id());
            dataSource.editTask(task);
        } else {
            dataSource.creatTask(task);
        }
        startActivity(intent);
    }

    public void chooseDestination(View view) {
        builder = new PlacePicker.IntentBuilder();
        try {
            Intent intent = builder.build(CreateTaskActivity.this);
            startActivityForResult(intent, PLACE_PICKER_FLAG);
        } catch (GooglePlayServicesRepairableException e) {
            GooglePlayServicesUtil
                    .getErrorDialog(e.getConnectionStatusCode(), CreateTaskActivity.this, 0);
        } catch (GooglePlayServicesNotAvailableException e) {
            Toast.makeText(CreateTaskActivity.this, "Google Play Services is not available.",
                    Toast.LENGTH_LONG)
                    .show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PLACE_PICKER_FLAG:
                    place = PlacePicker.getPlace(data, this);
                    longitude.setText(String.valueOf(place.getLatLng().longitude));
                    latitude.setText(String.valueOf(place.getLatLng().latitude));
                    destination.setText(place.getName());
                    break;
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public Task getTask(int taskId, ArrayList<Task> tasks) {
        for (int i = 0; i < tasks.size(); i++) {
            if (taskId == tasks.get(i).get_id()) {
                return tasks.get(i);
            }
        }
        return null;
    }


}
