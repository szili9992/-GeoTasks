package com.example.practica.geotasks.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by practica16 on 8/16/2016.
 */
public class TaskSQLiteHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "tasks.db";
    private static final int DATABASE_VERSION = 2;

    public static final String TABLE_TASKS = "tasks";
    public static final String COLUMN_ID = "_id", COLUMN_TASK_NAME = "task_name", COLUMN_DESTINATION_NAME = "destination_name", COLUMN_DESTINATION_LONGITUDE = "destination_longitude", COLUMN_DESTINATION_LATITUDE = "location_latitude", COLUMN_INTERVAL_START = "interval_start", COLUMN_INTERVAL_END = "interval_end", COLUMN_GEOFENCE_RADIUS = "geofence_radius", COLUMN_WEATHER = "weather";

    public static final String DATABASE_CREATE = "create table "
            + TABLE_TASKS + "( " + COLUMN_ID
            + " integer primary key autoincrement, "
            + COLUMN_TASK_NAME + " text not null, "
            + COLUMN_DESTINATION_NAME + " text, "
            + COLUMN_DESTINATION_LONGITUDE + " double, "
            + COLUMN_DESTINATION_LATITUDE + " double, "
            + COLUMN_INTERVAL_START + " long, "
            + COLUMN_INTERVAL_END + " long, "
            + COLUMN_GEOFENCE_RADIUS + " int, "
            + COLUMN_WEATHER + " double);";

    public TaskSQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(TaskSQLiteHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TASKS);
        onCreate(db);
    }
}
