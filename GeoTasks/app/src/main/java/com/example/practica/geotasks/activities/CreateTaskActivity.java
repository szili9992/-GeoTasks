package com.example.practica.geotasks.activities;

import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.practica.geotasks.GeofenceBuilder;
import com.example.practica.geotasks.R;
import com.example.practica.geotasks.models.Task;
import com.example.practica.geotasks.data.TasksDataSource;
import com.example.practica.geotasks.service.GeofenceService;
import com.example.practica.geotasks.weather.WeatherInfo;
import com.example.practica.geotasks.weather.WeatherInfoService;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateTaskActivity extends AppCompatActivity {

    private static final int PLACE_PICKER_FLAG = 1;
    private final String API_KEY = "8617b30a6fc114ad2ad929c111b76edf";
    private final String UNITS = "metric";

    private EditText taskName;
    private TextView longitude, latitude, destination, intervalStart, intervalEnd, geofenceRadius;
    private TasksDataSource taskDataSource;
    private Place place;
    private Task selectedTask, createTask;
    private boolean update = false;
    private WeatherInfoService service;
    private WeatherInfo weatherInfo;
    private Intent editIntent;
    private ArrayList<Task> allTasks;
    private DiscreteSeekBar seekBar;
    private int seekBarValue;
    private String intStart,intEnd;
    private GoogleApiClient googleApiClient;
    private GeofenceBuilder geofenceBuilder;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_task);
        editIntent = getIntent();
        service = new WeatherInfoService();
        geofenceBuilder=new GeofenceBuilder();

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
                seekBarValue = value;
            }

            @Override
            public void onStartTrackingTouch(DiscreteSeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(DiscreteSeekBar seekBar) {
                geofenceRadius.setText(String.valueOf(seekBarValue)+" meters");
            }
        });


        taskDataSource = new TasksDataSource(this);
        taskDataSource.open();

        allTasks = taskDataSource.getAllTasks();

        if (editIntent.getExtras() != null) {
            editTask();
        }
    }

    /**
     * Add/edit task in the recyclerView depending on the value of the update boolean variable.
     *
     * @param view
     */
    public void addTask(View view) {
        Intent addTaskIntent = new Intent(CreateTaskActivity.this, MainActivity.class);
        createTask = new Task();
        createTask.setTaskName(taskName.getText().toString());
        createTask.setDestinationName(place.getName().toString());
        createTask.setDestinationLongitude(place.getLatLng().longitude);
        createTask.setDestinationLatitude(place.getLatLng().latitude);
        createTask.setIntervalStart(intStart);
        createTask.setIntervalEnd(intEnd);
        createTask.setGeofenceRadius(seekBarValue);
        createTask.setWeather(weatherInfo.getMain().getTemp());

        if(place.getLatLng()!=null) {
            geofenceBuilder.startGeofenceMonitoring(CreateTaskActivity.this, googleApiClient,
                    taskName.getText().toString(), place.getLatLng().latitude,
                    place.getLatLng().longitude, seekBarValue);
            geofenceBuilder.startLocationMonitoring(googleApiClient);
        }


        if (update) {
            createTask.set_id(selectedTask.get_id());
            taskDataSource.editTask(createTask);
        } else {
            taskDataSource.creatTask(createTask);
        }
        startActivity(addTaskIntent);
    }

    public void editTask() {
        int taskId = editIntent.getExtras().getInt("id");
        selectedTask = getTask(taskId, allTasks);

        taskName.setText(selectedTask.getTaskName());
        longitude.setText(String.valueOf(selectedTask.getDestinationLongitude()));
        latitude.setText(String.valueOf(selectedTask.getDestinationLatitude()));
        destination.setText(selectedTask.getDestinationName());
        intervalStart.setText(String.valueOf(selectedTask.getIntervalStart()));
        intervalEnd.setText(String.valueOf(selectedTask.getIntervalEnd()));
        geofenceRadius.setText(String.valueOf(selectedTask.getGeofenceRadius())+" meters");

        update = true;
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PLACE_PICKER_FLAG:
                    place = PlacePicker.getPlace(this, data);
                    longitude.setText(String.valueOf(place.getLatLng().longitude));
                    latitude.setText(String.valueOf(place.getLatLng().latitude));
                    destination.setText(place.getName());
                    getWeatherForCoords(place.getLatLng().latitude, place.getLatLng().longitude, API_KEY, UNITS);
                    break;
            }
        }
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
    public Task getTask(int taskId, ArrayList<Task> tasks) {
        for (int i = 0; i < tasks.size(); i++) {
            if (taskId == tasks.get(i).get_id()) {
                return tasks.get(i);
            }
        }
        return null;
    }


    /**
     * Get current weather with the coordinates provided by Google Places picker, in metric.
     *
     * @param lat
     * @param lon
     * @param appId
     * @param units
     */
    private void getWeatherForCoords(double lat, double lon, String appId, String units) {
        try {
            service.getWeatherData(lat, lon, appId, units, new Callback() {
                @Override
                public void onResponse(Call call, Response response) {
                    weatherInfo = (WeatherInfo) response.body();
                }

                @Override
                public void onFailure(Call call, Throwable t) {
                    Log.e("something went wrong", t.toString());
                }

            });
        } catch (IOException e) {
            Toast.makeText(CreateTaskActivity.this, "Something went wrong with getting the current weather", Toast.LENGTH_SHORT).show();
        }
    }


    public void startTime(View view) {
        Calendar calendar = Calendar.getInstance();
        TimePickerDialog timePickerDialog=new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                intStart = String.format("%02d:%02d", hour, minute);
                intervalStart.setText(intStart);
            }
        },calendar.get(Calendar.HOUR_OF_DAY),calendar.get(Calendar.MINUTE),true);
        timePickerDialog.show();


    }
    public void endTime(View view) {
        Calendar calendar = Calendar.getInstance();
        TimePickerDialog timePickerDialog=new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                intEnd=String.format("%02d:%02d", hour, minute);
                intervalEnd.setText(intEnd);
            }
        },calendar.get(Calendar.HOUR_OF_DAY),calendar.get(Calendar.MINUTE),true);
        timePickerDialog.show();

    }





}
