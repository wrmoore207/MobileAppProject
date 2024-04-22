package com.example.scheduleapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

public class Profile extends Fragment {

    private TextView nameTextView;
    private TextView phoneTextView;
    private TextView emailTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        // Initialize TextView
        nameTextView = rootView.findViewById(R.id.userNameTV);
        phoneTextView = rootView.findViewById(R.id.PhoneNumberTV);
        emailTextView = rootView.findViewById(R.id.emailAddressTV);

        // Initialize ViewModel
        SharedViewModel sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        // Observe LiveData for changes in username
        sharedViewModel.getUserName().observe(getViewLifecycleOwner(), name -> {
            // Update UI with username
            nameTextView.setText(name);
        });

        // Observe LiveData for changes in user phone number
        sharedViewModel.getUserPhoneNumber().observe(getViewLifecycleOwner(), phoneNumber -> {
            // Update UI with phone number
            phoneTextView.setText(phoneNumber);
        });

        // Observe LiveData for changes in user email
        sharedViewModel.getUserEmail().observe(getViewLifecycleOwner(), email -> {
            // Update UI with email address
            emailTextView.setText(email);
        });

        return rootView;
    }
}