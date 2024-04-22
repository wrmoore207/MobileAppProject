package com.example.scheduleapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

public class RoleAdapter extends ArrayAdapter<String> {

    private final LayoutInflater mInflater;

    public RoleAdapter(Context context) {
        super(context, android.R.layout.simple_spinner_item);
        mInflater = LayoutInflater.from(context);

        // Load data from array resource
        String[] roleArray = context.getResources().getStringArray(R.array.role_array);
        // Add the data to the adapter
        addAll(roleArray);
    }

    @Override
    public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
        return createItemView(position, convertView, parent);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        return createItemView(position, convertView, parent);
    }

    private View createItemView(int position, View convertView, ViewGroup parent) {
        // Reuse or inflate the view
        RoleAdapter.ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(android.R.layout.simple_spinner_dropdown_item, parent, false);
            holder = new RoleAdapter.ViewHolder();
            holder.textView = convertView.findViewById(android.R.id.text1);
            convertView.setTag(holder);
        } else {
            holder = (RoleAdapter.ViewHolder) convertView.getTag();
        }

        // Set the data to the views
        String time = getItem(position);
        holder.textView.setText(time);

        return convertView;
    }

    private static class ViewHolder {
        TextView textView;
    }
}