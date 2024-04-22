package com.example.scheduleapp;

import static android.content.ContentValues.TAG;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Requests extends Fragment {

    private FirebaseFirestore db;
    private String fullName;
    private ShiftsAdapter adapter;

    private AlertDialog alertDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_requests, container, false);

        // Initialize FirebaseFirestore
        db = FirebaseFirestore.getInstance();

        // Initialize ViewModel
        SharedViewModel sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        sharedViewModel.getUserEmail().observe(getViewLifecycleOwner(), email -> {
            // Query the User collection to get the user's FullName
            db.collection("Users")
                    .whereEqualTo("Email", email)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                fullName = document.getString("FullName");
                                if (fullName != null) {
                                    initiateFirestoreQuery();
                                }  // Handle case where FullName is null

                            }
                        }  // Handle error getting documents

                    });
        });

        // Initialize FloatingActionButton
        FloatingActionButton fab = rootView.findViewById(R.id.requestFAB);
        fab.setOnClickListener(view -> openNewRequestDialog());

        // Initialize RecyclerView
        RecyclerView recyclerView = rootView.findViewById(R.id.requestRecyclerView);
        adapter = new ShiftsAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        return rootView;
    }

    // Method to initiate Firestore query
    private void initiateFirestoreQuery() {
        // Initialize Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference shiftsCollection = db.collection("Shifts");

        // Check if fullName is not null before executing Firestore query
        if (fullName != null) {
            // Create query conditions
            Query query = shiftsCollection
                    .whereEqualTo("Role", "OFF")
                    .whereEqualTo("Employee", fullName);

            // Execute Firestore query
            query.get().addOnSuccessListener(queryDocumentSnapshots -> {
                List<ShiftModel> shifts = getShiftsFromFirestore(queryDocumentSnapshots);
                if (shifts.isEmpty()) {
                    Log.d("Error", "No shifts found");
                    adapter.setData(getDefaultShifts());
                } else {
                    Log.d("Success", "Shifts Found");
                    adapter.setData(shifts);

                    // Log FullName field compared to fullName variable
                    for (ShiftModel shift : shifts) {
                        String shiftEmployee = shift.getEmployee();
                        if (shiftEmployee != null) {
                            if (shiftEmployee.equals(fullName)) {
                                Log.d("Comparison", "FullName matched: " + fullName);
                            } else {
                                Log.d("Comparison", "FullName didn't match. Shift Employee: " + shiftEmployee + ", Variable fullName: " + fullName);
                            }
                        } else {
                            Log.d("Comparison", "Employee field in shift document is null");
                        }
                    }
                }
            }).addOnFailureListener(e -> {
                // Handle failures here
                Log.e("Firestore Query", "Failed to fetch shifts: " + e.getMessage());
            });
        } else {
            // Handle case where fullName is null
            Log.d("Firestore Query", "fullName is null. Cannot execute Firestore query.");
            // You may set a default value for fullName or handle the case according to your app logic
        }
    }

    private List<ShiftModel> getShiftsFromFirestore(QuerySnapshot querySnapshot) {
        Log.d("Debugging", "Inside getShiftsFromFirestore");

        List<ShiftModel> shiftModels = new ArrayList<>();
        for (DocumentSnapshot document : querySnapshot.getDocuments()) {
            Log.d("Debugging", "Inside get shifts for loop");
            ShiftModel shiftModel = document.toObject(ShiftModel.class);
            // Log timestamp format for reference
            assert shiftModel != null;
            shiftModels.add(shiftModel);
        }
        Log.d("DEBUGGING", "Size of shiftModel List: " + shiftModels.size());
        return shiftModels;
    }

    private List<ShiftModel> getDefaultShifts() {
        List<ShiftModel> defaultShifts = new ArrayList<>();
        // Add default shift model with empty or default values
        defaultShifts.add(new ShiftModel("Employee Name", "Employee Role"));
        // Add more default items as needed
        return defaultShifts;
    }

    private void openNewRequestDialog() {
        // Create AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_new_request, null);
        builder.setView(dialogView);

        // Title
        builder.setTitle("New Request");

        // Date Picker
        DatePicker datePicker = dialogView.findViewById(R.id.datePicker);

        // Submit Button
        Button submitButton = dialogView.findViewById(R.id.submitButton);
        submitButton.setOnClickListener(v -> {
            // Handle submission
            String selectedDate = getDateFromDatePicker(datePicker);

            if (!TextUtils.isEmpty(selectedDate)) {
                // Use selectedDate to create Timestamps
                Timestamp startTime = getTimestampForDate(selectedDate, true);
                Timestamp endTime = getTimestampForDate(selectedDate, false);
                // Submit the request
                createShift(fullName, startTime, endTime);
                Log.d("OpenNewRequestDialog", "Success");
                // Dismiss the dialog after submission
                alertDialog.dismiss(); // Dismiss the stored AlertDialog
            } else {
                Log.d("OpenNewRequestDialog", "Error Making Request");
            }
        });

        // Store AlertDialog
        alertDialog = builder.create();

        // Show AlertDialog
        alertDialog.show();
    }

    private void createShift(String employee, Timestamp startTimeStamp, Timestamp endTimeStamp) {
        Map<String, Object> shiftData = new HashMap<>();
        shiftData.put("Employee", employee);
        shiftData.put("StartTime", startTimeStamp);
        shiftData.put("EndTime", endTimeStamp);
        shiftData.put("Role", "OFF");

        db.collection("Shifts")
                .add(shiftData)
                .addOnSuccessListener(documentReference -> {
                    Log.d(TAG, "Shift document added with ID: " + documentReference.getId());
                    // Shift added successfully, perform any additional actions if needed
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Error adding shift document", e);
                    // Handle errors
                });
    }


    private String getDateFromDatePicker(DatePicker datePicker) {
        int day = datePicker.getDayOfMonth();
        int month = datePicker.getMonth() + 1;
        int year = datePicker.getYear();
        return day + "/" + month + "/" + year;
    }

    private Timestamp getTimestampForDate(String date, boolean isStartTime) {
        String[] dateComponents = date.split("/");
        int day = Integer.parseInt(dateComponents[0]);
        int month = Integer.parseInt(dateComponents[1]) - 1; // Subtract 1 since Calendar months are zero-based
        int year = Integer.parseInt(dateComponents[2]);

        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);

        if (isStartTime) {
            // Set the time to the start of the day
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
        } else {
            // Set the time to the end of the day
            calendar.set(Calendar.HOUR_OF_DAY, 23);
            calendar.set(Calendar.MINUTE, 59);
            calendar.set(Calendar.SECOND, 59);
            calendar.set(Calendar.MILLISECOND, 999);
        }

        return new Timestamp(calendar.getTime());
    }
}