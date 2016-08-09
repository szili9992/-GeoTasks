package com.example.practica.geotasks;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class CreateTaskActivity extends AppCompatActivity {

    private EditText taskName;
    private EditText taskDate;
    private Task task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_task);

        taskName=(EditText)findViewById(R.id.taskName);
        taskDate=(EditText)findViewById(R.id.taskDate);

    }

    public void addTask(View view){
        Intent intent=new Intent(CreateTaskActivity.this,MainActivity.class);
        task=new Task();
        task.setTaskName(taskName.getText().toString());
        task.setTime(taskDate.getText().toString());
        startActivity(intent);
    }
}
