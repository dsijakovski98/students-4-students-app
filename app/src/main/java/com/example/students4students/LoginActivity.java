package com.example.students4students;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.students4students.registration.RegisterActivity;
import com.example.students4students.user_homepage.DashboardActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

public class LoginActivity extends AppCompatActivity {

    // Inputs
    private EditText emailEditText;
    private EditText passwordEditText;

    // Buttons
    private Button loginBtn;
    private Button signupBtn;

    // Progress bar
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Check if user is already logged in
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if(auth.getCurrentUser() != null) {
            Intent goToDashboard = new Intent(LoginActivity.this, DashboardActivity.class);
            startActivity(goToDashboard);
            finish();
        }
        
        emailEditText = findViewById(R.id.loginEmail);
        passwordEditText = findViewById(R.id.loginPassword);
        
        loginBtn = findViewById(R.id.loginBtn);
        signupBtn = findViewById(R.id.signupBtn);

        progressBar = findViewById(R.id.loginProgressBar);
        
        // Login button click event handler
        loginBtn.setOnClickListener(v -> {
            if(loginIsValid()) {
                loginUser();
            }
        });

        // Sign up button click event handler
        signupBtn.setOnClickListener(v -> {
            Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(registerIntent);
        });
    }

    private void loginUser() {
        // Set progress bar to visible
        progressBar.setVisibility(View.VISIBLE);

        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        // Login user with firebase
        FirebaseAuth auth = FirebaseAuth.getInstance();

        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    // Set progress bar to be invisible
                    progressBar.setVisibility(View.GONE);

                    if(task.isSuccessful()) {
                        Intent goToDashboard = new Intent(LoginActivity.this, DashboardActivity.class);
                        startActivity(goToDashboard);
                        finish();
                    }
                    else {
                        Exception exception = task.getException();
                        if(exception instanceof FirebaseAuthInvalidUserException) {
                            setError(emailEditText, "Email doesn't exist or has been disabled!");
                        }
                        if(exception instanceof FirebaseAuthInvalidCredentialsException) {
                            setError(passwordEditText, "Wrong password!");
                        }
                    }
                });

    }

    private boolean loginIsValid() {
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        // Email validation
        // 1. Empty field
        if(email.trim().isEmpty())
            return setError(emailEditText, "Enter your email!");
        // 2. Invalid format
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches())
            return setError(emailEditText, "Invalid email format!");

        // Password validation
        // 2. Empty field
        if(password.trim().isEmpty())
            return setError(passwordEditText, "Enter your password!");
        // 2. At least 6 characters
        if(password.length() < 6)
            return setError(passwordEditText, "Password must be at least 6 characters!");

        // At this point, login is valid
        return true;
    }

    private boolean setError(EditText inputField, String errorMessage) {
        inputField.setError(errorMessage);
        inputField.requestFocus();
        return false;
    }

}