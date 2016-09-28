package com.example.practica.geotasks.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.example.practica.geotasks.service.LocationService;
import com.example.practica.geotasks.utilities.GeofenceBuilder;
import com.example.practica.geotasks.R;
import com.example.practica.geotasks.utilities.RecyclerTouchListener;
import com.example.practica.geotasks.models.Task;
import com.example.practica.geotasks.data.TasksDataSource;
import com.example.practica.geotasks.adapters.TaskAdapter;
import com.example.practica.geotasks.weather.WeatherApi;
import com.example.practica.geotasks.weather.WeatherInfo;
import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import retrofit2.Call;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private TaskAdapter taskAdapter;
    private CircularImageView facebookProfilePicture;
    private TasksDataSource taskDataSource;
    private ArrayList<Task> taskList;
    private ArrayList<WeatherInfo> weatherInfoList;
    private int taskId;
    private TextView userEmail, userName;
    private GeofenceBuilder geofenceBuilder;
    private GoogleApiClient googleApiClient;
    private WeatherInfo weatherInfo;
    private final String API_KEY = "8617b30a6fc114ad2ad929c111b76edf";
    private final String UNITS = "metric";
    private RecyclerView recyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FacebookSdk.sdkInitialize(getApplicationContext());
        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View hView = navigationView.inflateHeaderView(R.layout.nav_header_main);

        facebookProfilePicture = (CircularImageView) hView.findViewById(R.id.fb_profile_picture);
        userEmail = (TextView) hView.findViewById(R.id.user_email);
        userName = (TextView) hView.findViewById(R.id.user_name);


        geofenceBuilder = new GeofenceBuilder();
        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(@Nullable Bundle bundle) {
                        Log.d("Connection success", "main activity apiclient");
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


        taskDataSource = new TasksDataSource(this);
        taskDataSource.open();
        taskList = taskDataSource.getAllTasks();
        weatherInfoList = new ArrayList<>();


        if(taskList==null){
            stopService(new Intent(this, LocationService.class));
        }
        if (checkForConnection()) {
            new getWeatherDataAsync().execute();
            new FacebookAsyncTask().execute();
        }


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        taskAdapter = new TaskAdapter(taskList, weatherInfoList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(taskAdapter);
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(this, recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Intent intent = new Intent(MainActivity.this, CreateTaskActivity.class);
                Task task = taskAdapter.getTask(position);
                intent.putExtra("id", task.get_id());
                startActivity(intent);
            }

            @Override
            public void onLongClick(View view, final int position) {
                Task task = taskAdapter.getTask(position);
                taskId = task.get_id();
                alertDialogShow(taskId);
            }
        }));


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setItemIconTintList(null);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.stop_geofence_monitoring) {
            for (int i = 0; i < taskList.size(); i++) {
                try {
                    geofenceBuilder.stopGeofenceMonitoring(googleApiClient, taskList.get(i).getTaskName());
                } catch (Exception e) {
                    Log.d("not active", "Geofence not active");
                }
            }
        } else if (id == R.id.stop_location_monitoring_service) {
            try {
                stopService(new Intent(this, LocationService.class));
                Log.d("loc monitor","location monitoring has stopped");
            } catch (Exception e) {
                Log.d("not active", "Location monitoring not active");
            }

        } else if (id == R.id.resume_geofence_monitoring) {
            for (int i = 0; i < taskList.size(); i++) {
                try {
                    geofenceBuilder.startGeofenceMonitoring(this, googleApiClient, taskList.get(i).getTaskName(),
                            taskList.get(i).getDestinationLatitude(), taskList.get(i).getDestinationLongitude(), taskList.get(i).getGeofenceRadius());
                    Toast.makeText(this, "Succesfully started and restored Geofences!", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Log.e("reesume geofence mon", "Can't start geofence on boot: " + e.getMessage());
                }
            }

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * End facebook session.
     *
     * @param item
     */
    public void logOutFb(MenuItem item) {
        LoginManager.getInstance().logOut();
        Intent intent = new Intent(MainActivity.this, LogInActivity.class);
        startActivity(intent);
    }

    public void newTask(View view) {
        Intent intent = new Intent(MainActivity.this, CreateTaskActivity.class);
        startActivity(intent);
    }


    @Override
    protected void onResume() {
        super.onResume();
        int response = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this);
        if (response != ConnectionResult.SUCCESS) {
            GoogleApiAvailability.getInstance().getErrorDialog(this, response, 1).show();
        } else {
            Log.d("Play services available", "ok");
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
     * On recyclerview item long press issue a dialog to ask for task and geofence deletion.
     *
     * @param position
     */
    private void alertDialogShow(final int position) {

        MaterialDialog.Builder builder = new MaterialDialog.Builder(MainActivity.this)
                .content(R.string.alert_dialog_content)
                .positiveText(R.string.alert_dialog_positive)
                .negativeText(R.string.alert_dialog_negative)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        Task task = getSelectedTask(position, taskList);
                        taskAdapter.remove(task);
                        taskDataSource.deleteTask(task);
                        try {
                            geofenceBuilder.stopGeofenceMonitoring(googleApiClient, task.getTaskName());
                        } catch (Exception e) {
                            Log.e("Not active", "Geofence not active");
                        }

                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                    }
                });
        MaterialDialog dialog = builder.build();
        dialog.show();
    }


    /**
     * Compare the task selected from the recylerview with the ones stored in the database.
     *
     * @param taskId
     * @param tasks
     * @return
     */
    private Task getSelectedTask(int taskId, ArrayList<Task> tasks) {
        for (int i = 0; i < tasks.size(); i++) {
            if (taskId == tasks.get(i).get_id()) {
                return tasks.get(i);
            }
        }
        return null;
    }

    /**
     * Check if there's and active internet connection.
     *
     * @return
     */
    private boolean checkForConnection() {
        ConnectivityManager cm =
                (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }

    /**
     * Async task to get facebook data and display it in nav header.
     */
    private class FacebookAsyncTask extends AsyncTask<Void, Void, Void> {
        private String fbJsonData;
        private ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            dialog = ProgressDialog.show(MainActivity.this, "Loading...", "Getting Facebook data.");
        }

        @Override
        protected Void doInBackground(Void... voids) {
            GraphRequest data_request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                @Override
                public void onCompleted(JSONObject json_object, GraphResponse response) {
                    fbJsonData = json_object.toString();
                    Log.e("Succes", "itt vagyok");
                }
            });
            Bundle permission_param = new Bundle();
            permission_param.putString("fields", "id,name,email,picture.width(200).height(200)");
            data_request.setParameters(permission_param);
            data_request.executeAndWait();

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            try {
                JSONObject response = new JSONObject(fbJsonData);
                userEmail.setText(response.get("email").toString());
                userName.setText(response.get("name").toString());
                JSONObject profile_pic_data = new JSONObject(response.get("picture").toString());
                JSONObject profile_pic_url = new JSONObject(profile_pic_data.getString("data"));
                Picasso.with(MainActivity.this).load(profile_pic_url.getString("url")).into(facebookProfilePicture);
            } catch (Exception e) {
                e.printStackTrace();
            }
            dialog.dismiss();
        }
    }

    /**
     * Get weather data with async task.
     */
    private class getWeatherDataAsync extends AsyncTask<Void, Void, Void> {


        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(MainActivity.this, "Loading...", "Getting weather");
        }

        @Override
        protected Void doInBackground(Void... vHolders) {
            for (int i = 0; i < taskList.size(); i++) {
                try {
                    WeatherApi weatherApi = WeatherApi.retrofit.create(WeatherApi.class);
                    Call<WeatherInfo> call = weatherApi.getWeatherData(taskList.get(i).getDestinationLatitude(), taskList.get(i).getDestinationLongitude(), API_KEY, UNITS);
                    weatherInfo = call.execute().body();
                    weatherInfoList.add(weatherInfo);
                    //Log.e("size of weatherinfolist", weatherInfoList.get(i).getCoord().getLat().toString());
                } catch (IOException e) {
                    Log.e("get weather coordinates", "something went wrong: " + e.getMessage());
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            taskAdapter.notifyDataSetChanged();
            progressDialog.dismiss();

        }

    }
}
