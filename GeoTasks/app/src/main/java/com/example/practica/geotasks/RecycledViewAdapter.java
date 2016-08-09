package com.example.practica.geotasks;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by szili on 2016-08-09.
 */
public class RecycledViewAdapter extends RecyclerView.Adapter<RecycledViewAdapter.ViewHolder> {
    private ArrayList<String> dataSet;

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView txtTask;
        public TextView txtDate;
        public ImageView weatherImg;

        public ViewHolder(View view){
            super(view);
            txtTask=(TextView) view.findViewById(R.id.task);
            txtDate=(TextView) view.findViewById(R.id.date);
            weatherImg=(ImageView) view.findViewById(R.id.weather);
        }
    }
    public void add(int position, String item){
        dataSet.add(position,item);
        notifyItemInserted(position);
    }

    public void remove(String item){
        int position=dataSet.indexOf(item);
        dataSet.remove(position);
        notifyItemRemoved(position);
    }

    public RecycledViewAdapter(ArrayList<String> myDataSet) {
        this.dataSet = myDataSet;
    }

    @Override
    public RecycledViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.row_rv,parent,false);
        ViewHolder viewHolder=new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.txtTask.setText(dataSet.get(position));
        holder.txtDate.setText(dataSet.get(position));
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }
}
