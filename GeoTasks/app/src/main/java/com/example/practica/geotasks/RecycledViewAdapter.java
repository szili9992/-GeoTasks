package com.example.practica.geotasks;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by szili on 2016-08-09.
 */
public class RecycledViewAdapter extends RecyclerView.Adapter<RecycledViewAdapter.ViewHolder> {
    private List<Task> taskList;

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView txtTask;
        public TextView txtDate;
        public ImageView weatherImg;

        public ViewHolder(View view) {
            super(view);
            txtTask = (TextView) view.findViewById(R.id.task);
            txtDate = (TextView) view.findViewById(R.id.date);
            weatherImg = (ImageView) view.findViewById(R.id.weather);
        }
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

    public RecycledViewAdapter(List<Task> taskList) {
        this.taskList = taskList;
    }

    @Override
    public RecycledViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_rv, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Task task = taskList.get(position);

        holder.txtTask.setText(task.getTaskName());
        holder.txtDate.setText(task.getTime());
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }
}
