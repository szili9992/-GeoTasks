package com.example.practica.geotasks.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.example.practica.geotasks.models.Task;

import java.util.ArrayList;

/**
 * Created by szili on 2016-08-16.
 */
public class TasksDataSource {

    private SQLiteDatabase database;
    private TaskSQLiteHelper dbHelper;
    private String[] allColumns = {TaskSQLiteHelper.COLUMN_ID, TaskSQLiteHelper.COLUMN_TASK_NAME,
            TaskSQLiteHelper.COLUMN_DESTINATION_NAME, TaskSQLiteHelper.COLUMN_DESTINATION_LONGITUDE,
            TaskSQLiteHelper.COLUMN_DESTINATION_LATITUDE, TaskSQLiteHelper.COLUMN_INTERVAL_START,
            TaskSQLiteHelper.COLUMN_INTERVAL_END, TaskSQLiteHelper.COLUMN_GEOFENCE_RADIUS};

    public TasksDataSource(Context context) {
        dbHelper = new TaskSQLiteHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public Task creatTask(Task task) {

        ContentValues values = new ContentValues();
        values.put(TaskSQLiteHelper.COLUMN_TASK_NAME, task.getTaskName());
        values.put(TaskSQLiteHelper.COLUMN_DESTINATION_NAME, task.getDestinationName());
        values.put(TaskSQLiteHelper.COLUMN_DESTINATION_LONGITUDE, task.getDestinationLongitude());
        values.put(TaskSQLiteHelper.COLUMN_DESTINATION_LATITUDE, task.getDestinationLatitude());
        values.put(TaskSQLiteHelper.COLUMN_INTERVAL_START, task.getIntervalStart());
        values.put(TaskSQLiteHelper.COLUMN_INTERVAL_END, task.getIntervalEnd());
        values.put(TaskSQLiteHelper.COLUMN_GEOFENCE_RADIUS, task.getGeofenceRadius());
        long insertId = database.insert(TaskSQLiteHelper.TABLE_TASKS, null, values);
        Cursor cursor = database.query(TaskSQLiteHelper.TABLE_TASKS, allColumns, TaskSQLiteHelper.COLUMN_ID + " = " + insertId, null, null, null, null);
        cursor.moveToFirst();
        Task newTask = cursorToTask(cursor);
        cursor.close();
        return newTask;
    }

    private Task cursorToTask(Cursor cursor) {
        Task task = new Task(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getDouble(3), cursor.getDouble(4), cursor.getString(5), cursor.getString(6), cursor.getInt(7));
        return task;
    }

    /**
     * Get every task from the database
     *
     * @return
     */
    public ArrayList<Task> getAllTasks() {

        ArrayList<Task> tasks = new ArrayList<>();
        Cursor cursor = database.query(TaskSQLiteHelper.TABLE_TASKS, allColumns, null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Task task = cursorToTask(cursor);
            tasks.add(task);
            cursor.moveToNext();
        }
        cursor.close();
        return tasks;
    }

    /**
     * Edit a created task from the recyclerView
     *
     * @param task
     */
    public void editTask(Task task) {

        ContentValues values = new ContentValues();
        values.put(TaskSQLiteHelper.COLUMN_TASK_NAME, task.getTaskName());
        values.put(TaskSQLiteHelper.COLUMN_DESTINATION_NAME, task.getDestinationName());
        values.put(TaskSQLiteHelper.COLUMN_DESTINATION_LONGITUDE, task.getDestinationLongitude());
        values.put(TaskSQLiteHelper.COLUMN_DESTINATION_LATITUDE, task.getDestinationLatitude());
        values.put(TaskSQLiteHelper.COLUMN_INTERVAL_START, task.getIntervalStart());
        values.put(TaskSQLiteHelper.COLUMN_INTERVAL_END, task.getIntervalEnd());
        values.put(TaskSQLiteHelper.COLUMN_GEOFENCE_RADIUS, task.getGeofenceRadius());


        database.update(TaskSQLiteHelper.TABLE_TASKS, values, "_id = " + task.get_id(), null);
    }

    public void deleteTask(Task task) {
        int id = task.get_id();
        System.out.println("Task deleted with id: " + id);
        database.delete(TaskSQLiteHelper.TABLE_TASKS, TaskSQLiteHelper.COLUMN_ID + " = " + id, null);
    }
}
