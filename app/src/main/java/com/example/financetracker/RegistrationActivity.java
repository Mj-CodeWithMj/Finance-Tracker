package com.example.financetracker;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegistrationActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "UserPrefs"; // SharedPreferences name
    private TextView signInTextReg;
    private EditText emailReg, passwordReg;
    private Button signUpButtonReg;
    private ProgressDialog progressDialog;

    // Firebase
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrion);

        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        setupRegistration();

        handleBackPress(); // Handle back press with custom behavior
    }

    private void handleBackPress() {
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                firebaseAuth.signOut(); // Sign out the user on back press
                finish(); // Close the current activity
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);
    }

    private void setupRegistration() {
        signInTextReg = findViewById(R.id.sign_in_text_reg);
        emailReg = findViewById(R.id.email_reg);
        passwordReg = findViewById(R.id.password_reg);
        signUpButtonReg = findViewById(R.id.sign_up_button_reg);

        signUpButtonReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emailReg.getText().toString().trim();
                String password = passwordReg.getText().toString().trim();

                if (!isValidInput(email, password)) return;

                registerUser(email, password);
            }
        });

        signInTextReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegistrationActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private boolean isValidInput(String email, String password) {
        if (TextUtils.isEmpty(email)) {
            emailReg.setError("Email is required.");
            return false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailReg.setError("Enter a valid email.");
            return false;
        }
        if (TextUtils.isEmpty(password)) {
            passwordReg.setError("Password is required.");
            return false;
        }
        if (password.length() < 6) {
            passwordReg.setError("Password must be at least 6 characters.");
            return false;
        }
        return true;
    }

    private void registerUser(String email, String password) {

        progressDialog.setMessage("Creating your account...");
        progressDialog.show();

        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            progressDialog.dismiss();

                            resetLoginFlagForNewUser();
                            sendVerificationEmail();
                        } else {
                            progressDialog.dismiss();
                            handleRegistrationFailure(task.getException().getMessage());
                        }
                    }
                });
    }

    private void sendVerificationEmail() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(RegistrationActivity.this,"Verification email sent. Please check your inbox.", Toast.LENGTH_LONG).show();

                        firebaseAuth.signOut(); // Sign out the user
                        redirectToEmailVerification();
                    } else {
                        Toast.makeText(RegistrationActivity.this, "Failed to send verification email.", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    // Reset the isFirstLogin flag for new users
    private void resetLoginFlagForNewUser(){
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isFirstLogin", true);
        editor.apply();
    }

    private void handleRegistrationFailure(String errorMessage) {
        if (errorMessage.contains("email address is already in use")) {
            Toast.makeText(RegistrationActivity.this, "User already exists. Please log in instead.", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(RegistrationActivity.this, "Registration failed: " + errorMessage, Toast.LENGTH_LONG).show();
        }
    }

    private void redirectToEmailVerification() {
        Intent intent = new Intent(RegistrationActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
