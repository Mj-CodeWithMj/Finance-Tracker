package com.example.financetracker;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangePasswordFragment extends Fragment {

    private View view;
    private EditText edit_current_password, edit_new_password, edit_confirm_new_password;
    private Button btn_change_password;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_change_password, container, false);

        // initialize firebase auth
        firebaseAuth = FirebaseAuth.getInstance();

        edit_current_password = view.findViewById(R.id.edit_current_password);
        edit_new_password = view.findViewById(R.id.edit_new_password);
        edit_confirm_new_password = view.findViewById(R.id.edit_confirm_new_password);
        btn_change_password = view.findViewById(R.id.btn_change_password);
        progressDialog = new ProgressDialog(getActivity());


        btn_change_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changePassword();
            }
        });
        return view;
    }

    private void changePassword() {
        String oldPassword = edit_current_password.getText().toString().trim();
        String newPassword = edit_new_password.getText().toString().trim();
        String confirmPassword = edit_confirm_new_password.getText().toString().trim();

        if (TextUtils.isEmpty(newPassword) || TextUtils.isEmpty(confirmPassword)) {
            Toast.makeText(getContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            Toast.makeText(getContext(), "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUser user = firebaseAuth.getCurrentUser();

        if (user != null) {

            // Show ProgressDialog while re-authenticating
            progressDialog.setMessage("Changing password...");
            progressDialog.setCancelable(false); // Prevent dismissal by tapping outside
            progressDialog.show(); // Show the dialog

            // ReAuthenticate the user first
            AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), oldPassword);
            user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        // If reauthentication is successful, update the password
                        user.updatePassword(newPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                // Dismiss the dialog
                                if (progressDialog.isShowing()) {
                                    progressDialog.dismiss();
                                }
                                if (task.isSuccessful()) {
                                    Toast.makeText(getContext(), "Password updated successfully", Toast.LENGTH_SHORT).show();

                                    // Sign out the user
                                    firebaseAuth.signOut();

                                    // Redirect to MainActivity (login screen)
                                    Intent intent = new Intent(getActivity(), MainActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);  // Start MainActivity
                                    getActivity().finish();  // Finish the current activity

                                } else {
                                    Toast.makeText(getContext(), "Failed to update password", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else {
                        // Dismiss the dialog
                        if (progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                        Toast.makeText(getContext(), "ReAuthentication failed. Check your current password.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            Toast.makeText(getContext(), "User not authenticated", Toast.LENGTH_SHORT).show();
        }
    }
}
