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

public class CreateTaskActivity extends AppCompatActivity {

    private PlacePicker.IntentBuilder builder;
    private static final int PLACE_PICKER_FLAG = 1;
    private EditText taskName, taskDate;
    private TextView longitude, latitude;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_task);

        taskName = (EditText) findViewById(R.id.taskName);
        taskDate = (EditText) findViewById(R.id.taskDate);
        longitude = (TextView) findViewById(R.id.longitude);
        latitude = (TextView) findViewById(R.id.latitude);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

    }

    public void addTask(View view) {
        Intent intent = new Intent(CreateTaskActivity.this, MainActivity.class);
        Task task = new Task();
        task.setTaskName(taskName.getText().toString());
        task.setTime(taskDate.getText().toString());
        Log.d("TASK AT ADD", "task: " + task.toString());
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
                    Place place = PlacePicker.getPlace(data, this);
                    longitude.setText(String.valueOf(place.getLatLng().longitude));
                    latitude.setText(String.valueOf(place.getLatLng().latitude));
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
}
