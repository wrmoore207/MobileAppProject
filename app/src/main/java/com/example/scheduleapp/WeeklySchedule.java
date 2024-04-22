package com.example.scheduleapp;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class WeeklySchedule extends Fragment {

    private String fullName;
    private ShiftsAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_schedule, container, false);

        // Initialize ViewModel
        SharedViewModel sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        sharedViewModel.getUserEmail().observe(getViewLifecycleOwner(), email -> {
            // Query the User collection to get the user's FullName
            FirebaseFirestore.getInstance().collection("Users")
                    .whereEqualTo("Email", email)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                fullName = document.getString("FullName");
                                if (fullName != null) {
                                    Log.d("User FullName", fullName);
                                    // Once fullName is obtained, initiate Firestore query
                                    initiateFirestoreQuery();
                                } else {
                                    Log.d("User FullName", "No FullName found for the user with email: " + email);
                                    // If fullName is null, handle the case accordingly
                                }
                            }
                        } else {
                            Log.d("User FullName", "Error getting documents: ", task.getException());
                            // Handle error
                        }
                    });
        });

        // Initialize RecyclerView
        RecyclerView recyclerView = rootView.findViewById(R.id.recyclerView);
        adapter = new ShiftsAdapter(new ArrayList<>(), requireContext());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Set item click listener
        adapter.setOnItemClickListener(this::showShiftOptionsDialog);

        return rootView;
    }

    private void showShiftOptionsDialog(ShiftModel shift) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Shift Options")
                .setItems(new CharSequence[]{"Make Available", "Swap"}, (dialog, which) -> {
                    switch (which) {
                        case 0:
                            makeShiftAvailable(shift);
                            break;
                        case 1:
                            // Implement swap logic here
                            break;
                    }
                });
        builder.create().show();
    }

    private void makeShiftAvailable(ShiftModel shift) {
        // Update Firestore document to mark shift as available
        FirebaseFirestore.getInstance().collection("Shifts")
                .document(shift.getId())
                .update("Open", true)
                .addOnSuccessListener(aVoid -> {
                    // Update UI if necessary
                    // For example, update RecyclerView item
                })
                .addOnFailureListener(e -> {
                    // Handle error
                });
    }

    // Method to initiate Firestore query
    private void initiateFirestoreQuery() {
        // Initialize Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference shiftsCollection = db.collection("Shifts");

        // Define start and end of the week
        Timestamp Monday = getMondayDate();
        Timestamp Sunday = getSundayDate(Monday);

        // Check if fullName is not null before executing Firestore query
        if (fullName != null) {
            // Create query conditions
            Query query = shiftsCollection
                    .whereGreaterThanOrEqualTo("StartTime", Monday)
                    .whereLessThanOrEqualTo("StartTime", Sunday)
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

    private List<ShiftModel> getDefaultShifts() {
        List<ShiftModel> defaultShifts = new ArrayList<>();
        // Add default shift model with empty or default values
        defaultShifts.add(new ShiftModel("Employee Name", "Employee Role"));
        // Add more default items as needed
        return defaultShifts;
    }

private List<ShiftModel> getShiftsFromFirestore(QuerySnapshot querySnapshot) {
    List<ShiftModel> shiftModels = new ArrayList<>();
    for (DocumentSnapshot document : querySnapshot.getDocuments()) {
        ShiftModel shiftModel = document.toObject(ShiftModel.class);
        if (shiftModel != null) {
            shiftModels.add(shiftModel);
            Log.d("Firestore Data", "Fetched Shift with Start Time: " + (shiftModel.getStartTimeStamp() != null ? shiftModel.getStartTimeStamp().toDate().toString() : "null"));
        }
    }
    return shiftModels;
}

    public static Timestamp getMondayDate() {
        // Get current date
        Calendar currentDate = Calendar.getInstance();

        // Calculate the difference between the current day of the week and Monday (Calendar.MONDAY)
        int daysUntilMonday = (currentDate.get(Calendar.DAY_OF_WEEK) + 7 - Calendar.MONDAY) % 7;

        // Subtract the difference to get Monday's date
        currentDate.add(Calendar.DAY_OF_YEAR, -daysUntilMonday);

        return new Timestamp(currentDate.getTime());
    }

    public static Timestamp getSundayDate(Timestamp mondayDate) {
        // Convert Monday's Timestamp to Calendar object
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(mondayDate.toDate());

        // Add 6 days to get to Sunday
        calendar.add(Calendar.DAY_OF_YEAR, 6);

        // Convert back to Timestamp
        return new Timestamp(calendar.getTime());
    }
}