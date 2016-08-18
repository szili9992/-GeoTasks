package com.example.practica.geotasks.adapters;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.practica.geotasks.R;
import com.example.practica.geotasks.models.Task;

import java.util.List;

/**
 * Created by szili on 2016-08-09.
 */
public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder> {

    private List<Task> taskList;

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView taskName;
        public TextView destination;
        public ImageView weatherImg;

        public ViewHolder(View view) {
            super(view);
            taskName = (TextView) view.findViewById(R.id.task);
            destination = (TextView) view.findViewById(R.id.date);
            //weatherImg = (ImageView) view.findViewById(R.id.weather);
        }
    }

    public TaskAdapter(List<Task> taskList) {
        this.taskList = taskList;
    }

    public void add(int position, Task item) {
        taskList.add(position, item);
        notifyItemInserted(position);
    }

    public void remove(Task item) {
        int position = taskList.indexOf(item);
        taskList.remove(position);
        notifyItemRemoved(position);
    }

    public Task getTask(int position){
        return taskList.get(position);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_rv, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Task task = taskList.get(position);

        holder.taskName.setText(task.getTaskName());
        holder.destination.setText(task.getDestinationName());

    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }
}
