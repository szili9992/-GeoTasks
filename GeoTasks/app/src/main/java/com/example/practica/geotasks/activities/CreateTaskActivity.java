package com.example.practica.geotasks.activities;

import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.practica.geotasks.utilities.GeofenceBuilder;
import com.example.practica.geotasks.R;
import com.example.practica.geotasks.models.Task;
import com.example.practica.geotasks.data.TasksDataSource;
import com.example.practica.geotasks.weather.WeatherInfo;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

import java.util.ArrayList;
import java.util.Calendar;

public class CreateTaskActivity extends AppCompatActivity {

    private static final int PLACE_PICKER_FLAG = 1;
    private EditText taskName;
    private TextView longitude, latitude, destination, intervalStart, intervalEnd, geofenceRadius;
    private TasksDataSource taskDataSource;
    private Place place;
    private Task selectedTask, createTask;
    private boolean update = false;
    private Intent editIntent;
    private ArrayList<Task> allTasks;
    private DiscreteSeekBar seekBar;
    private GoogleApiClient googleApiClient;
    private GeofenceBuilder geofenceBuilder;

    private double createTaskLatitude, createTaskLongitude;
    private String createTaskDestination, createTaskName, createTaskIntervalStart, createTaskIntervalEnd;
    private int createTaskGeofenceRadius, createTaskSeekBarValue;

    private String[] PERMISSIONS = {android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_task);
        editIntent = getIntent();
        geofenceBuilder = new GeofenceBuilder();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        taskName = (EditText) findViewById(R.id.task_name);
        longitude = (TextView) findViewById(R.id.longitude_value);
        latitude = (TextView) findViewById(R.id.latitude_value);
        destination = (TextView) findViewById(R.id.destination_value);
        intervalStart = (TextView) findViewById(R.id.interval_start);
        intervalEnd = (TextView) findViewById(R.id.interval_end);
        geofenceRadius = (TextView) findViewById(R.id.geofence_radius_value);
        seekBar = (DiscreteSeekBar) findViewById(R.id.radius_seek_bar);

        createTask = new Task();

        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(@Nullable Bundle bundle) {
                        Log.d("Connection success", "1");
                    }

                    @Override
                    public void onConnectionSuspended(int i) {
                        Log.d("Connection suspended", "2");
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


        seekBar.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {
            @Override
            public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {
                createTaskSeekBarValue = value;
            }

            @Override
            public void onStartTrackingTouch(DiscreteSeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(DiscreteSeekBar seekBar) {
                geofenceRadius.setText(String.valueOf(createTaskSeekBarValue) + " meters");
            }
        });


        taskDataSource = new TasksDataSource(this);
        taskDataSource.open();

        allTasks = taskDataSource.getAllTasks();

        if (editIntent.getExtras() != null) {
            new editTaskAsync().execute();
        }

        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, 1);
        }
    }

    /**
     * Add/edit task in the recyclerView depending on the value of the update boolean variable.
     *
     * @param view
     */
    public void addTask(View view) {
        Intent addTaskIntent = new Intent(CreateTaskActivity.this, MainActivity.class);


        if (update) {
            try {
                geofenceBuilder.stopGeofenceMonitoring(googleApiClient, createTaskName);
            } catch (Exception e) {
                Log.e("Geofence", "there are no Geofences with this id");
            }


            createTask.setTaskName(taskName.getText().toString());
            createTask.setDestinationLatitude(createTaskLatitude);
            createTask.setDestinationLongitude(createTaskLongitude);
            createTask.setDestinationName(createTaskDestination);
            createTask.setIntervalStart(createTaskIntervalStart);
            createTask.setIntervalEnd(createTaskIntervalEnd);
            createTask.setGeofenceRadius(createTaskSeekBarValue);


            createTask.set_id(selectedTask.get_id());
            taskDataSource.editTask(createTask);
            if (!Double.isNaN(createTaskLatitude) && !Double.isNaN(createTaskLongitude)) {
                geofenceBuilder.startGeofenceMonitoring(this, googleApiClient, createTaskName, createTaskLatitude, createTaskLongitude, createTaskSeekBarValue);
                Log.d("update geofence", "updating geofence " + createTaskName + " was a success");
            }
        } else {

            createTask.setTaskName(taskName.getText().toString());
            createTask.setDestinationName(createTaskDestination);
            createTask.setDestinationLongitude(createTaskLongitude);
            createTask.setDestinationLatitude(createTaskLatitude);
            createTask.setIntervalStart(createTaskIntervalStart);
            createTask.setIntervalEnd(createTaskIntervalEnd);
            createTask.setGeofenceRadius(createTaskSeekBarValue);

            taskDataSource.creatTask(createTask);
            if (place.getLatLng() != null) {
                geofenceBuilder.startGeofenceMonitoring(this, googleApiClient, taskName.getText().toString(), createTaskLatitude, createTaskLongitude, createTaskSeekBarValue);
                geofenceBuilder.startLocationMonitoring(googleApiClient);
            }
        }
        Log.e("Task", "Created/updated: " + createTask);
        startActivity(addTaskIntent);
    }


    /**
     * Chose destination with Google Places API Place picker
     *
     * @param view
     */
    public void chooseDestination(View view) {
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
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

    /**
     * Check if current android version is M or higher. If true request location permission.
     *
     * @param context
     * @param permissions
     * @return
     */
    private static boolean hasPermissions(Context context, String[] permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PLACE_PICKER_FLAG:
                    place = PlacePicker.getPlace(this, data);
                    createTaskLongitude = place.getLatLng().longitude;
                    createTaskLatitude = place.getLatLng().latitude;
                    createTaskDestination = place.getName().toString();

                    destination.setText(createTaskDestination);
                    latitude.setText(String.valueOf(createTaskLatitude));
                    longitude.setText(String.valueOf(createTaskLongitude));
                    break;
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        googleApiClient.reconnect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        googleApiClient.disconnect();
    }


    /**
     * Back button to main activity.
     *
     * @param item
     * @return
     */
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

    /**
     * Compare if the task id from recycled view matches the one stored in the arrayList. If it matches then it returns that specific task.
     *
     * @param taskId
     * @param tasks
     * @return
     */
    private Task getTask(int taskId, ArrayList<Task> tasks) {
        for (int i = 0; i < tasks.size(); i++) {
            if (taskId == tasks.get(i).get_id()) {
                return tasks.get(i);
            }
        }
        return null;
    }

    public void startTime(View view) {
        Calendar calendar = Calendar.getInstance();
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                createTaskIntervalStart = String.format("%02d:%02d", hour, minute);
                intervalStart.setText(createTaskIntervalStart);
            }
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
        timePickerDialog.show();


    }

    public void endTime(View view) {
        Calendar calendar = Calendar.getInstance();
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                createTaskIntervalEnd = String.format("%02d:%02d", hour, minute);
                intervalEnd.setText(createTaskIntervalEnd);
            }
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
        timePickerDialog.show();

    }

    /**
     * If editIntent returns and extra value then issue an Async task to edit the selected task.
     */
    private class editTaskAsync extends AsyncTask<Void, Void, Void> {
        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            dialog = ProgressDialog.show(CreateTaskActivity.this, "Loading...", "Getting task data.");
        }

        @Override
        protected Void doInBackground(Void... voids) {
            int taskId = editIntent.getExtras().getInt("id");
            selectedTask = getTask(taskId, allTasks);

            createTaskName = selectedTask.getTaskName();
            createTaskLatitude = selectedTask.getDestinationLatitude();
            createTaskLongitude = selectedTask.getDestinationLongitude();
            createTaskDestination = selectedTask.getDestinationName();
            createTaskIntervalStart = selectedTask.getIntervalStart();
            createTaskIntervalEnd = selectedTask.getIntervalEnd();
            createTaskGeofenceRadius = selectedTask.getGeofenceRadius();

            update = true;
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            taskName.setText(createTaskName);
            latitude.setText(String.valueOf(createTaskLatitude));
            longitude.setText(String.valueOf(createTaskLongitude));
            destination.setText(createTaskDestination);
            intervalStart.setText(createTaskIntervalStart);
            intervalEnd.setText(createTaskIntervalEnd);
            geofenceRadius.setText(String.valueOf(createTaskGeofenceRadius) + " meters");
            seekBar.setProgress(createTaskGeofenceRadius);
            dialog.dismiss();
        }
    }


}
