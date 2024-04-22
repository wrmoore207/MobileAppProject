package com.example.scheduleapp;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class ShiftsAdapter extends RecyclerView.Adapter<ShiftsAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(ShiftModel shift);
    }

    private List<ShiftModel> shiftModels;
    private OnItemClickListener listener;

    public ShiftsAdapter(List<ShiftModel> shiftModels, Context context) {
        this.shiftModels = shiftModels;
    }

    public ShiftsAdapter(List<ShiftModel> shiftModels) {
        this.shiftModels = shiftModels;
    }

    public void setData(List<ShiftModel> data) {
        this.shiftModels = data;
        notifyDataSetChanged();
    }

    // Setter method for the listener
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.shift_item, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ShiftModel shiftModel = shiftModels.get(position);
        holder.shiftNameTextView.setText(shiftModel.getEmployee());
        holder.shiftRoleTextView.setText(shiftModel.getRole());
        Log.d("Timestamp", String.valueOf(shiftModel.getStartTimeStamp()));
        // Format the timestamp into a readable date format
        if (shiftModel.getStartTimeStamp() != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy hh:mm a", Locale.getDefault());
            String formattedDate = dateFormat.format(shiftModel.getStartTimeStamp().toDate());
            holder.shiftStartTimeTextView.setText(formattedDate);
        } else {
//            holder.shiftStartTimeTextView.setText(holder.itemView.getContext().getString(R.string.no_start_time));
        }

        if (shiftModel.isOpen()) {
            holder.itemView.setBackgroundColor(Color.GREEN);
        } else {
            holder.itemView.setBackgroundColor(Color.WHITE);
        }

        holder.employeeHeadshotImageView.setImageResource(R.drawable.baseline_person_24);
    }



    @Override
    public int getItemCount() {
        return shiftModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView shiftNameTextView;
        public final TextView shiftRoleTextView;
        public final TextView shiftStartTimeTextView;
        public final ImageView employeeHeadshotImageView;

        public ViewHolder(View itemView) {
            super(itemView);
            shiftNameTextView = itemView.findViewById(R.id.shiftName);
            shiftRoleTextView = itemView.findViewById(R.id.shiftRole);
            shiftStartTimeTextView = itemView.findViewById(R.id.shiftStartTime);
            employeeHeadshotImageView = itemView.findViewById(R.id.employeeHeadshotImageView);

            // Set click listener
            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onItemClick(shiftModels.get(position));
                    }
                });
            }
        }
    }