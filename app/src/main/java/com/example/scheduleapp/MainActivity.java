package com.example.scheduleapp;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    // Firebase Authentication Variables
    FirebaseAuth auth;
    FirebaseUser user;

    // Firestore Reference
    FirebaseFirestore userDB;

    // Bottom Navigation View Variables
    ViewPager2 viewPager2;
    UserPagerAdapter userPagerAdapter;
    BottomNavigationView bottomNavigationView;

    // View Model
    private SharedViewModel sharedViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize Firebase Authentication
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        // Initialize Firestore
        userDB = FirebaseFirestore.getInstance();

        // Check if user is logged in
        if (user == null) {
            Intent intent = new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
            finish();

        } else {
            Log.d("MainActivity", "user != null");
            // Get UID of the current user
            String uid = user.getUid();
            Log.d("MainActivity Status: User ID",uid);
            // Initialize sharedViewModel
            sharedViewModel = new ViewModelProvider(this).get(SharedViewModel.class);
            Log.d("MainActivity Status: Shared Model View", "Successful");

            // Fetch user's name from Firestore
            userDB.collection("Users").document(uid).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        String fullName = document.getString("FullName");
                        String phoneNumber = document.getString("PhoneNumber");
                        String email = document.getString("Email");
                        sharedViewModel.setUserData(fullName, phoneNumber, email);
                    } else {
                        Log.d("Firestore", "No such document");
                    }
                } else {
                    Log.d("Firestore", "Error getting document: ", task.getException());
                }
            });
        }

        bottomNavigationView = findViewById(R.id.bottom_nav);
        viewPager2 = findViewById(R.id.viewPager);
        userPagerAdapter = new UserPagerAdapter(this);
        viewPager2.setAdapter(userPagerAdapter);

        bottomNavigationView.setOnItemSelectedListener(menuItem -> {
            if (menuItem.getItemId() == R.id.bottom_logout) {
                // Handle logout
                logoutUser();
                return true;
            } else if (menuItem.getItemId() == R.id.bottom_home) {
                viewPager2.setCurrentItem(0);
                return true;
            } else if (menuItem.getItemId() == R.id.bottom_schedule) {
                viewPager2.setCurrentItem(1);
                return true;
            } else if (menuItem.getItemId() == R.id.bottom_requests) {
                viewPager2.setCurrentItem(2);
                return true;
            } else if (menuItem.getItemId() == R.id.bottom_profile) {
                viewPager2.setCurrentItem(3);
                return true;
            }
            return false;
        });
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                switch (position){
                    case 0:
                        bottomNavigationView.getMenu().findItem(R.id.bottom_home).setChecked(true);
                        break;
                    case 1:
                        bottomNavigationView.getMenu().findItem(R.id.bottom_schedule).setChecked(true);
                        break;
                    case 2:
                        bottomNavigationView.getMenu().findItem(R.id.bottom_requests).setChecked(true);
                        break;
                    case 3:
                        bottomNavigationView.getMenu().findItem(R.id.bottom_profile).setChecked(true);
                        break;
                }
                super.onPageSelected(position);
            }
        });
    }

    // Method to logout the user
    private void logoutUser() {
        // Sign out user from Firebase Authentication
        auth.signOut();

        // Redirect to login activity
        Intent intent = new Intent(getApplicationContext(), Login.class);
        startActivity(intent);
        finish();
    }
}