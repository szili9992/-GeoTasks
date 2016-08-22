package com.example.practica.geotasks.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.practica.geotasks.R;
import com.example.practica.geotasks.models.Task;
import com.example.practica.geotasks.data.TasksDataSource;
import com.example.practica.geotasks.weather.WeatherInfo;
import com.example.practica.geotasks.weather.WeatherInfoService;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;

import java.io.IOException;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateTaskActivity extends AppCompatActivity {

    private static final int PLACE_PICKER_FLAG = 1;
    private EditText taskName, taskDate;
    private TextView longitude, latitude, destination, intervalStart, intervalEnd, geofenceRadius;
    private TasksDataSource taskDataSource;
    private Place place;
    private Task selectedTask;
    private boolean update = false;
    private WeatherInfoService service;
    private WeatherInfo weatherInfo;
    private double weatherCelsius;


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

        taskDataSource = new TasksDataSource(this);
        taskDataSource.open();

        ArrayList<Task> allTasks = taskDataSource.getAllTasks();


        if (editIntent.getExtras() != null) {
            int taskId = editIntent.getExtras().getInt("id");
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


        service = new WeatherInfoService();


    }

    /**
     * Add/edit task in the recyclerView depending on the value of the update boolean variable.
     *
     * @param view
     */
    public void addTask(View view) {
        Intent addTaskIntent = new Intent(CreateTaskActivity.this, MainActivity.class);
        Task task = new Task();
        task.setTaskName(taskName.getText().toString());
        task.setDestinationName(place.getName().toString());
        task.setDestinationLongitude(place.getLatLng().longitude);
        task.setDestinationLatitude(place.getLatLng().latitude);
        task.setIntervalStart(11);
        task.setIntervalEnd(34);

        getWeatherForCoords(place.getLatLng().longitude, place.getLatLng().latitude, "8617b30a6fc114ad2ad929c111b76edf", "metric");
        Log.e("weatherinfo :", weatherInfo.toString());
        try {
            task.setWeatherInfo(weatherInfo);
        }catch (Exception e){
            Log.e("weatherinfo :", weatherInfo.toString());
        }


        if (update) {
            task.set_id(selectedTask.get_id());
            taskDataSource.editTask(task);
        } else {
            taskDataSource.creatTask(task);
        }
        startActivity(addTaskIntent);
    }

    /**
     * Chose destination with Google Places API Place picket
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


    private void getWeatherForCoords(double lat, double lon, String appid, String units) {
        try {
            service.getWeatherData(lat, lon, appid, units, new Callback() {
                @Override
                public void onResponse(Call call, Response response) {
                    weatherInfo = (WeatherInfo) response.body();
                    //String place = weatherInfo.getName();
                    weatherCelsius = weatherInfo.getMain().getTemp();

//                    name.setText("Place: "+place);
//                    temperaturePlace.setText("Weather: "+temperature+" \u2109");

                }

                @Override
                public void onFailure(Call call, Throwable t) {

                }

            });
        } catch (IOException e) {
//            Toast.makeText(PlaceholderActivity.this, "igy jartal ", Toast.LENGTH_SHORT).show();
        }
    }


}
