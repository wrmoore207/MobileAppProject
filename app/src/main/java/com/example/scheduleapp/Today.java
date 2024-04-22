package com.example.scheduleapp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class Today extends Fragment {

    private FirebaseFirestore db;
    private RecyclerView recyclerView;
    private ShiftsAdapter adapter;
    private Calendar currentCalendar = Calendar.getInstance();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_today, container, false);
        initializeUI(rootView);
        // Get today's shifts
        fetchShifts(currentCalendar);
        return rootView;
    }

    private void initializeUI(View rootView) {
        recyclerView = rootView.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ShiftsAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);
        db = FirebaseFirestore.getInstance();

        rootView.findViewById(R.id.buttonToday).setOnClickListener(v -> fetchShifts(Calendar.getInstance()));
        rootView.findViewById(R.id.buttonPrevDay).setOnClickListener(v -> {
            adjustCalendar(-1);
            fetchShifts(currentCalendar);
        });
        rootView.findViewById(R.id.buttonNextDay).setOnClickListener(v -> {
            adjustCalendar(1);
            fetchShifts(currentCalendar);
        });
    }

    private void adjustCalendar(int days) {
        currentCalendar.add(Calendar.DATE, days);
    }

    private void fetchShifts(Calendar calendar) {
        Timestamp startOfDay = getStartOfDay(calendar);
        Timestamp endOfDay = getEndOfDay(calendar);
        Query query = db.collection("Shifts")
                .whereGreaterThanOrEqualTo("StartTime", startOfDay)
                .whereLessThanOrEqualTo("StartTime", endOfDay);

        query.get().addOnSuccessListener(queryDocumentSnapshots -> {
            List<ShiftModel> shifts = getShiftsFromFirestore(queryDocumentSnapshots);
            if (shifts.isEmpty()) {
                adapter.setData(getDefaultShifts());
            } else {
                adapter.setData(shifts);
            }
        }).addOnFailureListener(e -> {
            Log.d("Firestore Error", "Error fetching shifts", e);
        });
    }

    // Method to retrieve default shifts
    private List<ShiftModel> getDefaultShifts() {
        List<ShiftModel> defaultShifts = new ArrayList<>();
        // Add default shift model with empty or default values
        defaultShifts.add(new ShiftModel("Employee Name", "Employee Role"));
        // Add more default items as needed
        return defaultShifts;
    }

    private List<ShiftModel> getShiftsFromFirestore(QuerySnapshot querySnapshot) {
        Log.d("DEBUGGING", "Inside getShiftsFromFirestore");
        List<ShiftModel> shiftModels = new ArrayList<>();
        for (DocumentSnapshot document : querySnapshot.getDocuments()) {
            Log.d("DEBUGGING", "Inside getShiftsFromFirestore for loop");

            ShiftModel shiftModel = document.toObject(ShiftModel.class);
            // Log the timestamp format
            assert shiftModel != null;
            Timestamp startTimeStamp = shiftModel.getStartTimeStamp();
            Timestamp endTimeStamp = shiftModel.getEndTimeStamp();

            // Check if the Timestamp objects are not null before invoking toDate()
            if (startTimeStamp != null && endTimeStamp != null) {
                Log.d("Firestore Timestamp Format", "Start Timestamp: " + startTimeStamp.toDate() + ", End Timestamp: " + endTimeStamp.toDate());
            } else {
                Log.d("Firestore Timestamp Format", "Start or End Timestamp is null");
            }

            shiftModels.add(shiftModel);
        }
        Log.d("DEBUGGING", "Size of shiftModel List: "+shiftModels.size());
        return shiftModels;
    }

    private Timestamp getStartOfDay(Calendar calendar) {
        Calendar start = (Calendar) calendar.clone();
        start.set(Calendar.HOUR_OF_DAY, 0);
        start.set(Calendar.MINUTE, 0);
        start.set(Calendar.SECOND, 0);
        start.set(Calendar.MILLISECOND, 0);
        return new Timestamp(start.getTime());
    }

    private Timestamp getEndOfDay(Calendar calendar) {
        Calendar end = (Calendar) calendar.clone();
        end.set(Calendar.HOUR_OF_DAY, 23);
        end.set(Calendar.MINUTE, 59);
        end.set(Calendar.SECOND, 59);
        end.set(Calendar.MILLISECOND, 999);
        return new Timestamp(end.getTime());
    }
}
