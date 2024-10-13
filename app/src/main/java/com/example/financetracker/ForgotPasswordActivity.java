package com.example.financetracker;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.SignInMethodQueryResult;

public class ForgotPasswordActivity extends AppCompatActivity {

    // Input field for the user to enter their email address.
    private EditText email_forgot_password;
    // Button to initiate the password reset process.
    private Button reset_password_button;
    // TextView to navigate back to the login screen.
    private TextView back_to_login_text;
    // Firebase Authentication sending password reset emails.
    private FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        firebaseAuth = FirebaseAuth.getInstance();
        email_forgot_password = findViewById(R.id.email_forgot_password);
        reset_password_button = findViewById(R.id.reset_password_button);
        back_to_login_text = findViewById(R.id.back_to_login_text);

        reset_password_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = email_forgot_password.getText().toString().trim();

                if (email.isEmpty()){
                    email_forgot_password.setError("Email is required");
                    email_forgot_password.requestFocus();
                }else {
                    // Email exists, send the reset password email
                    firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                // Password reset email sent
                                Toast.makeText(ForgotPasswordActivity.this, "Reset password email sent", Toast.LENGTH_SHORT).show();
                                // redirect to login page
                                Intent intent = new Intent(ForgotPasswordActivity.this, MainActivity.class);
                                startActivity(intent);
                            } else {
                                // Error sending password reset email
                                Toast.makeText(ForgotPasswordActivity.this, "Error sending reset password email", Toast.LENGTH_SHORT).show();
                            }
                        }

                    });
                }
            }
        });
        back_to_login_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Go back to the login screen
                Intent intent = new Intent(ForgotPasswordActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });
    }
}