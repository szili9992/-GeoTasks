package com.example.practica.geotasks;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by practica16 on 8/16/2016.
 */
public class TaskSQLiteHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "tasks.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_TASKS = "tasks";
    public static final String COLUMN_ID = "_id", COLUMN_TASK_NAME = "task_name", COLUMN_LOCATION_NAME = "location_name", COLUMN_LOCATION_LONGITUDE = "location_longitude", COLUMN_LOCATION_LATITUDE = "location_latitude", COLUMN_INTERVAL_START = "interval_start", COLUMN_INTERVAL_END = "interval_end";

    public static final String DATABASE_CREATE = "create table "
            + TABLE_TASKS +"( "+COLUMN_ID
            +" integer primary key autoincrement, "
            +COLUMN_TASK_NAME+" text not null, "
            +COLUMN_LOCATION_NAME+" text, "
            +COLUMN_LOCATION_LONGITUDE+" double, "
            +COLUMN_LOCATION_LATITUDE+" double, "
            +COLUMN_INTERVAL_START +" long, "
            +COLUMN_INTERVAL_END+"long);";

    public TaskSQLiteHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
