package com.example.financetracker;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;

public class ForgotPasswordChangeActivity extends AppCompatActivity {

    private EditText new_password, confirm_password;
    private Button change_password_button;
    private FirebaseAuth firebaseAuth;
    private String oobCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password_change);

        firebaseAuth = FirebaseAuth.getInstance();
        new_password = findViewById(R.id.new_password);
        confirm_password = findViewById(R.id.confirm_password);
        change_password_button = findViewById(R.id.change_password_button);

        // Handle the dynamic link
        FirebaseDynamicLinks.getInstance()
                .getDynamicLink(getIntent())
                .addOnSuccessListener(this, pendingDynamicLinkData -> {
                    Uri deepLink = null;
                    if (pendingDynamicLinkData != null) {
                        deepLink = pendingDynamicLinkData.getLink();
                    }

                    if (deepLink != null) {
                        oobCode = deepLink.getQueryParameter("oobCode");
                    }
                })
                .addOnFailureListener(this, e -> Toast.makeText(ForgotPasswordChangeActivity.this, "Failed to load reset link.", Toast.LENGTH_SHORT).show());

        change_password_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePassword();
            }
        });
    }

    public void changePassword() {
        String newPassword = new_password.getText().toString().trim();
        String confirmPassword = confirm_password.getText().toString().trim();

        if (TextUtils.isEmpty(newPassword)) {
            new_password.setError("New Password is required");
            new_password.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(confirmPassword)) {
            confirm_password.setError("Confirm Password is required");
            confirm_password.requestFocus();
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            confirm_password.setError("Passwords do not match");
            confirm_password.requestFocus();
            return;
        }

        if (oobCode != null) {
            // Use the oobCode to verify and reset the password
            firebaseAuth.confirmPasswordReset(oobCode, newPassword)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(ForgotPasswordChangeActivity.this, "Password has been reset successfully.", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(ForgotPasswordChangeActivity.this, MainActivity.class));
                                finish();
                            } else {
                                Toast.makeText(ForgotPasswordChangeActivity.this, "Failed to reset password.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } else {
            Toast.makeText(this, "Invalid reset link.", Toast.LENGTH_SHORT).show();
        }
    }
}
