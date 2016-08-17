package com.example.practica.geotasks;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    public static RecycledViewAdapter viewAdapter;
    private JSONObject response, profile_pic_data, profile_pic_url;
    private CircularImageView facebookProfilePicture;
    private String fbJsonData;
    private TasksDataSource dataSource;
    private MaterialDialog.Builder builder;
    private ArrayList<Task> tasks;
    private int taskId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        dataSource = new TasksDataSource(this);
        dataSource.open();
        tasks = dataSource.getAllTaks();


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View hView = navigationView.inflateHeaderView(R.layout.nav_header_main);
        facebookProfilePicture = (CircularImageView) hView.findViewById(R.id.fbProfilePicture);


        getUserInfo();

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                setUserProfile(fbJsonData);
            }
        }, 500);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        viewAdapter = new RecycledViewAdapter(tasks);


        // use a linear layout manager
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        // specify an adapter (see also next example)
        recyclerView.setAdapter(viewAdapter);

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(this, recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Intent intent = new Intent(MainActivity.this, CreateTaskActivity.class);
                Task task = viewAdapter.getItem(position);
                intent.putExtra("id",task.get_id());
                startActivity(intent);

                //Toast.makeText(MainActivity.this, "task" + task.toString(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onLongClick(View view, final int position) {
                Task task = viewAdapter.getItem(position);
                taskId=task.get_id();
                alertDialogShow(taskId);
                Toast.makeText(MainActivity.this, "onLongclick position: " + position, Toast.LENGTH_SHORT).show();
            }
        }));


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();


        navigationView.setNavigationItemSelectedListener(this);
    }

//    @Override
//    public void onBackPressed() {
//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        if (drawer.isDrawerOpen(GravityCompat.START)) {
//            drawer.closeDrawer(GravityCompat.START);
//        } else {
//            super.onBackPressed();
//        }
//    }

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

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

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
    }

    /**
     * Request user data from Facebook. Result is a JSON object that is stored in fbJsonData String which is then passed to setUserProfile();
     */
    private void getUserInfo() {
        GraphRequest data_request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject json_object, GraphResponse response) {
                fbJsonData = json_object.toString();
            }
        });
        Bundle permission_param = new Bundle();
        permission_param.putString("fields", "id,name,email,picture.width(200).height(200)");
        data_request.setParameters(permission_param);
        data_request.executeAsync();
    }

    /**
     * Display current user data using the JSON object from getUserInfo()
     *
     * @param jsonData
     */
    public void setUserProfile(String jsonData) {
        try {
            response = new JSONObject(jsonData);
            //user_email.setText(response.get("email").toString());
            //user_name.setText(response.get("name").toString());
            profile_pic_data = new JSONObject(response.get("picture").toString());
            profile_pic_url = new JSONObject(profile_pic_data.getString("data"));
            Picasso.with(this).load(profile_pic_url.getString("url")).into(facebookProfilePicture);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void alertDialogShow(final int position){

        builder = new MaterialDialog.Builder(MainActivity.this)
                .content(R.string.alert_dialog_content)
                .positiveText(R.string.alert_dialog_positive)
                .negativeText(R.string.alert_dialog_negative)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        viewAdapter.remove(getTask(position,tasks));
                        dataSource.deleteTask(getTask(position,tasks));

                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                });
        MaterialDialog dialog = builder.build();
        dialog.show();
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
