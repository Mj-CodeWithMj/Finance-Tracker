package com.example.financetracker;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private static final int SMS_PERMISSION_CODE = 1;
    private static final int NOTIFICATION_PERMISSION_CODE = 2;

    private EditText email_login, password_login;
    private Button login_button;
    private TextView forgot_password_login, do_not_have_account_login;

    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Handle theme preference
        SharedPreferences sharedPreferences = getSharedPreferences("night_mode_prefs", MODE_PRIVATE);
        boolean isNightMode = sharedPreferences.getBoolean("night_mode", false);
        if (isNightMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        if (currentUser != null && currentUser.isEmailVerified()) {
            // If logged in and verified, redirect to HomeActivity
            startActivity(new Intent(MainActivity.this, HomeActivity.class));
            finish();
        } else {
            // If not logged in, set the content view for login
            setContentView(R.layout.activity_main);
            login();
        }

        checkAndRequestSmsPermissions();
        checkAndRequestNotificationPermission();
        progressDialog = new ProgressDialog(this);
    }

    private void checkAndRequestSmsPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECEIVE_SMS, Manifest.permission.READ_SMS},
                    SMS_PERMISSION_CODE);
        }
    }

    private void checkAndRequestNotificationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.POST_NOTIFICATIONS},
                    NOTIFICATION_PERMISSION_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == SMS_PERMISSION_CODE) {
            if (grantResults.length > 0) {
                boolean receiveSmsGranted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                boolean readSmsGranted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                if (receiveSmsGranted && readSmsGranted) {
                    Toast.makeText(this, "SMS permissions granted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "SMS permissions denied", Toast.LENGTH_SHORT).show();
                }
            }
            checkAndRequestNotificationPermission();
        } else if (requestCode == NOTIFICATION_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Notification permission granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Notification permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void login() {
        email_login = findViewById(R.id.email_login);
        password_login = findViewById(R.id.password_login);
        login_button = findViewById(R.id.login_button);
        forgot_password_login = findViewById(R.id.forgot_password_login);
        do_not_have_account_login = findViewById(R.id.do_not_have_account_login);

        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = email_login.getText().toString().trim();
                String password = password_login.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    email_login.setError("Email is required");
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    password_login.setError("Password is required");
                    return;
                }

                progressDialog.setMessage("Logging in...");
                progressDialog.show();

                firebaseAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressDialog.dismiss();
                                if (task.isSuccessful()) {
                                    FirebaseUser user = firebaseAuth.getCurrentUser();
                                    if (user != null && user.isEmailVerified()) {
                                        handleLoginRedirect();
                                    } else {
                                        Toast.makeText(MainActivity.this, "Please verify your email before logging in.", Toast.LENGTH_LONG).show();
                                        firebaseAuth.signOut(); // Log out the user if not verified
                                    }
                                } else {
                                    Toast.makeText(MainActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

        do_not_have_account_login.setOnClickListener(view -> {
            startActivity(new Intent(MainActivity.this, RegistrationActivity.class));
            finish();
        });

        forgot_password_login.setOnClickListener(view -> {
            startActivity(new Intent(MainActivity.this, ForgotPasswordActivity.class));
            finish();
        });
    }

    private void handleLoginRedirect() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        boolean isFirstLogin = sharedPreferences.getBoolean("isFirstLogin", true);

        if (isFirstLogin) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("isFirstLogin", false);
            editor.apply();

            startActivity(new Intent(MainActivity.this, IntroRegistration.class));
        } else {
            startActivity(new Intent(MainActivity.this, HomeActivity.class));
        }
        finish();
    }
}
