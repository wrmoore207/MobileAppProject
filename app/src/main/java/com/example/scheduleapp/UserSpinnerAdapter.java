package com.example.scheduleapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class UserSpinnerAdapter extends ArrayAdapter<String> {

    private final LayoutInflater mInflater;
    private final List<String> mUserNames;

    public UserSpinnerAdapter(@NonNull Context context, @NonNull List<String> userNames) {
        super(context, 0, userNames);
        mInflater = LayoutInflater.from(context);
        mUserNames = userNames;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return createView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return createView(position, convertView, parent);
    }

    private View createView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = mInflater.inflate(android.R.layout.simple_spinner_dropdown_item, parent, false);
        }

        TextView textView = view.findViewById(android.R.id.text1);
        textView.setText(mUserNames.get(position));

        return view;
    }
}