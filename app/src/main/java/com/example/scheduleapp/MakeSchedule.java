package com.example.scheduleapp;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MakeSchedule extends Fragment {

    private FirebaseFirestore db;
    private Spinner spinnerEmployees;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_make_schedule, container, false);

        // Initialize FirebaseFirestore
        db = FirebaseFirestore.getInstance();

        // Initialize UI Elements and set Listeners
        DatePicker datePicker = rootView.findViewById(R.id.datePicker);
        Spinner spinnerStartTime = rootView.findViewById(R.id.spinnerStartTime);
        Spinner spinnerEndTime = rootView.findViewById(R.id.spinnerEndTime);
        Spinner spinnerRole = rootView.findViewById(R.id.spinnerRole);
        spinnerEmployees = rootView.findViewById(R.id.spinnerEmployees);
        Button submitButton = rootView.findViewById(R.id.btnSubmit);

        // Create and set the adapter for start time spinner
        TimeSpinnerAdapter startTimeAdapter = new TimeSpinnerAdapter(requireContext());
        spinnerStartTime.setAdapter(startTimeAdapter);

        // Create and set the adapter for end time spinner
        TimeSpinnerAdapter endTimeAdapter = new TimeSpinnerAdapter(requireContext());
        spinnerEndTime.setAdapter(endTimeAdapter);

        // Create and set the adapter for the Role Spinner
        RoleAdapter roleAdapter = new RoleAdapter(requireContext());
        spinnerRole.setAdapter(roleAdapter);

        submitButton.setOnClickListener(v -> {
            // Get selected date from DatePicker
            String selectedDate = getDateFromDatePicker(datePicker);

            // Get selected start time from Spinner
            String startTime = spinnerStartTime.getSelectedItem().toString();

            // Get selected end time from Spinner
            String endTime = spinnerEndTime.getSelectedItem().toString();

            // Get selected role from Spinner
            String role = spinnerRole.getSelectedItem().toString();

            // Get selected employee from Spinner
            String employee = spinnerEmployees.getSelectedItem().toString();

            // Convert selected date and time strings to Timestamp objects
            Timestamp startTimeStamp = getTimestamp(selectedDate, startTime);
            Timestamp endTimeStamp = getTimestamp(selectedDate, endTime);

            // Create and add the shift to the database
            createShift(employee, startTimeStamp, endTimeStamp, role);
        });

        fetchUserNames(); // Fetch user names and populate spinner

        return rootView;
    }

    private String getDateFromDatePicker(DatePicker datePicker) {
        int day = datePicker.getDayOfMonth();
        int month = datePicker.getMonth() + 1;
        int year = datePicker.getYear();
        return day + "/" + month + "/" + year;
    }

    private void createShift(String employee, Timestamp startTimeStamp, Timestamp endTimeStamp, String role) {
        Map<String, Object> shiftData = new HashMap<>();
        shiftData.put("Employee", employee);
        shiftData.put("StartTime", startTimeStamp);
        shiftData.put("EndTime", endTimeStamp);
        shiftData.put("Role", role);

        db.collection("Shifts")
                .add(shiftData)
                .addOnSuccessListener(documentReference -> Toast.makeText(requireContext(),"Huzzah! Shift Created", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> {
                    // Handle errors
                });
    }

    private Timestamp getTimestamp(String date, String time) {
        try {
            String[] dateComponents = date.split("/");
            int day = Integer.parseInt(dateComponents[0]);
            int month = Integer.parseInt(dateComponents[1]) - 1; // Subtract 1 since Calendar months are zero-based
            int year = Integer.parseInt(dateComponents[2]);

            // Parse time components
            String[] timeComponents = time.split(":");
            int hour = Integer.parseInt(timeComponents[0]);
            int minute = Integer.parseInt(timeComponents[1].substring(0, 2)); // Extract minutes
            String amPm = timeComponents[1].substring(3); // Extract AM/PM

            // Adjust hour for PM times
            if (amPm.equalsIgnoreCase("PM") && hour != 12) {
                hour += 12;
            } else if (amPm.equalsIgnoreCase("AM") && hour == 12) {
                hour = 0; // Midnight
            }

            // Create Calendar instance and set date and time components
            Calendar calendar = Calendar.getInstance();
            calendar.set(year, month, day, hour, minute, 0); // Set seconds to 0

            // Set milliseconds to 0
            calendar.set(Calendar.MILLISECOND, 0);

            // Create Timestamp from Calendar
            return new Timestamp(calendar.getTime());
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException | NullPointerException e) {
            Log.e("MakeSchedule", "Error", e);
            return null;
        }
    }


    private void fetchUserNames() {
        db.collection("Users")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<String> employeeNames = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String fullName = document.getString("FullName");
                            if (fullName != null) {
                                employeeNames.add(fullName);
                            }
                        }
                        // Populate Spinner with employee names using UserSpinnerAdapter
                        UserSpinnerAdapter adapter = new UserSpinnerAdapter(requireContext(), employeeNames);
                        spinnerEmployees.setAdapter(adapter);
                    }  // Handle errors

                });
    }
}