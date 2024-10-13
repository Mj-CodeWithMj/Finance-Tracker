package com.example.financetracker;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class UserEditProfileFragment extends Fragment {

    private Button deleteProfileButton;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;
    private ProgressDialog progressDialog;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_edit_profile, container, false);

        // Initialize Firebase instances
        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        progressDialog = new ProgressDialog(getActivity());



        // Find views by ID
        deleteProfileButton = view.findViewById(R.id.btn_delete_profile);

        // Delete profile on button click
        deleteProfileButton.setOnClickListener(v -> deleteAccountAndData());

        return view;
    }

    // Step 4: Delete account and associated data
    private void deleteAccountAndData() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            // Prompt user for their password
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Enter your password");

            // Set up the input
            final EditText input = new EditText(getContext());
            input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            builder.setView(input);

            // Set up the buttons
            builder.setPositiveButton("OK", (dialog, which) -> {
                String password = input.getText().toString();

                // Show ProgressDialog while re-authenticating
                progressDialog.setMessage("Deleting your account...");
                progressDialog.setCancelable(false); // Optional: prevent dismissal when touching outside
                progressDialog.show(); // Show the dialog

                // Re-authenticate the user
                AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), password);
                user.reauthenticate(credential)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                // Delete user data from Firestore
                                String userId = user.getUid();
                                firestore.collection("users").document(userId).delete()
                                        .addOnCompleteListener(deleteTask -> {
                                            if (deleteTask.isSuccessful()) {
                                                // Delete the user
                                                user.delete().addOnCompleteListener(deleteUserTask -> {
                                                    // Dismiss the dialog
                                                    if (progressDialog.isShowing()) {
                                                        progressDialog.dismiss();
                                                    }
                                                    if (deleteUserTask.isSuccessful()) {
                                                        Toast.makeText(getContext(), "Account deleted", Toast.LENGTH_SHORT).show();

                                                        // Redirect to MainActivity
                                                        Intent intent = new Intent(getContext(), MainActivity.class);
                                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Clear all previous activities
                                                        startActivity(intent);
                                                        getActivity().finish(); // Optional: this will not be necessary as the task will be cleared
                                                    } else {
                                                        Toast.makeText(getContext(), "Failed to delete account: " + deleteUserTask.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            } else {
                                                // Dismiss the dialog
                                                if (progressDialog.isShowing()) {
                                                    progressDialog.dismiss();
                                                }
                                                Toast.makeText(getContext(), "Failed to delete user data: " + deleteTask.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            } else {
                                // Dismiss the dialog
                                if (progressDialog.isShowing()) {
                                    progressDialog.dismiss();
                                }
                                Toast.makeText(getContext(), "Re-authentication failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            });
            builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
            builder.show();
        }
    }
}
