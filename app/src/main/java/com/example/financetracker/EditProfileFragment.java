package com.example.financetracker;

import static android.content.Context.MODE_PRIVATE;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class EditProfileFragment extends Fragment {

    private View view;
    private EditText edit_firstname, edit_lastname, edit_username, edit_birth_date;
    private Button save_button;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    private ProgressDialog progressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_edit_profile, container, false);

        // Initialize Firestore and FirebaseAuth
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        currentUser = firebaseAuth.getCurrentUser();

        edit_firstname = view.findViewById(R.id.edit_firstname);
        edit_lastname = view.findViewById(R.id.edit_lastname);
        edit_username = view.findViewById(R.id.edit_username);
        edit_birth_date = view.findViewById(R.id.edit_birth_date);
        save_button = view.findViewById(R.id.save_button);
        progressDialog = new ProgressDialog(getActivity());


        // Load existing user data from Firestore
        loadUserProfile();

        save_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateUserProfile();
            }
        });

        edit_birth_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog();
            }
        });
        return view;
    }

    private void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), (view, selectedYear, selectedMonth, selectedDay) -> {
            String formattedDate = selectedDay + "-" + (selectedMonth + 1) + "-" + selectedYear;
            edit_birth_date.setText(formattedDate);
        }, year, month, day);

        datePickerDialog.show();
    }

    private void loadUserProfile() {
        String userId = currentUser.getUid();
        DocumentReference documentReference = firebaseFirestore.collection("users").document(userId);

        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    edit_firstname.setText(documentSnapshot.getString("firstName"));
                    edit_lastname.setText(documentSnapshot.getString("lastName"));
                    edit_username.setText(documentSnapshot.getString("userName"));

                    // Handle the birthDate field
                    Object birthDateObj = documentSnapshot.get("birthDate");
                    if (birthDateObj != null) {
                        if (birthDateObj instanceof String) {
                            // If it's a string, use it as is
                            edit_birth_date.setText((String) birthDateObj);
                        } else if (birthDateObj instanceof Timestamp) {
                            // If it's a Timestamp, format it to a readable date string
                            Timestamp timestamp = (Timestamp) birthDateObj;
                            Date date = timestamp.toDate();
                            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                            String formattedDate = sdf.format(date);
                            edit_birth_date.setText(formattedDate);
                        } else {
                            Toast.makeText(getActivity(), "Invalid birthDate format", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    // Handle the case where the user document doesn't exist
                    Toast.makeText(getActivity(), "No profile data found", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(), "Failed to load profile: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUserProfile() {
        String userId = currentUser.getUid();
        String firstName = edit_firstname.getText().toString().trim();
        String lastName = edit_lastname.getText().toString().trim();
        String userName = edit_username.getText().toString().trim();
        String birthDate = edit_birth_date.getText().toString().trim(); // Get birth date

        if (TextUtils.isEmpty(firstName) && TextUtils.isEmpty(lastName) && TextUtils.isEmpty(userName) && TextUtils.isEmpty(birthDate)) {
            Toast.makeText(getActivity(), "Please enter at least one field to update", Toast.LENGTH_SHORT).show();
            return;
        }

        // Prepare updated data
        Map<String, Object> updatedData = new HashMap<>();
        if (!TextUtils.isEmpty(firstName)) {
            updatedData.put("firstName", firstName);
        }
        if (!TextUtils.isEmpty(lastName)) {
            updatedData.put("lastName", lastName);
        }
        if (!TextUtils.isEmpty(userName)) {
            updatedData.put("userName", userName);
        }
        if (!TextUtils.isEmpty(birthDate)) {
            updatedData.put("birthDate", birthDate);
        }

        progressDialog.setMessage("Updating profile...");
        progressDialog.setCancelable(false); // Optional: prevent dismissal when touching outside
        progressDialog.show(); // Show the dialog

        // Update Firestore
        firebaseFirestore.collection("users").document(userId).update(updatedData).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                // Dismiss the dialog
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }

                // Update SharedPreferences if userName was changed
                if (!TextUtils.isEmpty(userName)) {
                    SharedPreferences sharedPreferences = getActivity().getSharedPreferences("user_data", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("userName", userName); // Update cached username
                    editor.apply();
                }
                Toast.makeText(getActivity(), "Profile updated successfully", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Dismiss the dialog
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                Toast.makeText(getActivity(), "Failed to update profile: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
