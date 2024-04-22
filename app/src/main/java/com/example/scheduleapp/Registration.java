package com.example.scheduleapp;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.auth.FirebaseUser;

import java.util.HashMap;
import java.util.Map;

public class Registration extends AppCompatActivity {

    // UI References
    TextInputEditText editTextEmail, editTextPassword, editTextName, editTextPhone;
    Button buttonReg;
    FirebaseAuth mAuth;
    ProgressBar progressBar;
    TextView textView;
    FirebaseFirestore fStore;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registration);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize Firebase Auth and Firestore.
        mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        // Initialize UI components.
        textView = findViewById(R.id.LoginNow);
        progressBar = findViewById(R.id.progressBar);
        editTextName = findViewById(R.id.fullName);
        editTextPhone = findViewById(R.id.PhoneNumber);
        editTextEmail = findViewById(R.id.email);
        editTextPassword = findViewById(R.id.password);
        buttonReg = findViewById(R.id.register_button);

        // Listner to redirect user to Login activity
        textView.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
            finish();
        });

        // Listener to submit new user information
        buttonReg.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);

            // Retrieve text from input fields.
            String email = String.valueOf(editTextEmail.getText());
            String password = String.valueOf(editTextPassword.getText());
            String phone = String.valueOf(editTextPhone.getText());
            String name = String.valueOf(editTextName.getText());

            // Input validation for each field.
            if(TextUtils.isEmpty(email)){
                Toast.makeText(Registration.this, "Enter Email", Toast.LENGTH_LONG).show();
                return;
            }

            if(TextUtils.isEmpty(password)){
                Toast.makeText(Registration.this, "Enter Password", Toast.LENGTH_LONG).show();
                return;
            }

            if(TextUtils.isEmpty(name)){
                Toast.makeText(Registration.this, "Enter Your Name", Toast.LENGTH_LONG).show();
                return;
            }

            if(TextUtils.isEmpty(phone)){
                Toast.makeText(Registration.this, "Enter Your Phone Number", Toast.LENGTH_LONG).show();
                return;
            }

            if(TextUtils.isEmpty(email)){
                Toast.makeText(Registration.this, "Enter Email", Toast.LENGTH_LONG).show();
                return;
            }

            // Create user account with email and password.
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Hide the progress bar and log success.
                            progressBar.setVisibility(View.GONE);
                            Log.d(TAG, "createUserWithEmail:success");
                            Toast.makeText(Registration.this, "Account Created.",
                                    Toast.LENGTH_SHORT).show();

                            // Add the other information to the Firestore
                            user = mAuth.getCurrentUser();
                            // Create a document reference
                            DocumentReference df = fStore.collection("Users").document(user.getUid());
                            // Create a Map to pass information to database
                            Map<String, Object> userInfo = new HashMap<>();
                            userInfo.put("FullName", name);
                            userInfo.put("Email", email);
                            userInfo.put("PhoneNumber", phone);
                            // Specify access info (default is always "N")
                            userInfo.put("isAdmin", "N");
                            // Pass the Map to the db reference to set the information in the new document
                            df.set(userInfo);
                            // Redirect to login page after successful registration.
                            Intent intent = new Intent(getApplicationContext(), Login.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(Registration.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        });

    }

}