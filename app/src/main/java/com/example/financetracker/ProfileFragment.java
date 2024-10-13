package com.example.financetracker;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import com.example.financetracker.model.Data;
import com.example.financetracker.model.UserData; // Ensure you import your UserData model class
import com.example.financetracker.notification.NotificationScheduler;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import static android.content.Context.MODE_PRIVATE;

import java.util.Map;
import java.util.concurrent.Executor;

public class ProfileFragment extends Fragment {

    private View view;
    private Switch switch_dark_mode, switch_notifications;
    private LinearLayout change_password, edit_profile, user_edit_profile;
    private TextView profile_name;
    private FirebaseUser currentUser;
    private ProgressDialog progressDialog;




    // Firebase references
    private DatabaseReference databaseIncomeReference;
    private DatabaseReference databaseExpenseReference;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;


    private double totalIncome = 0;
    private double totalExpense = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Initialize the views
        switch_dark_mode = view.findViewById(R.id.switch_dark_mode);
        switch_notifications = view.findViewById(R.id.switch_notifications);
        change_password = view.findViewById(R.id.change_password);
        edit_profile = view.findViewById(R.id.edit_profile);
        user_edit_profile = view.findViewById(R.id.user_edit_profile);
        profile_name =view.findViewById(R.id.profile_name);
        progressDialog = new ProgressDialog(getActivity());




        // Handle change to EditProfile Fragment
        edit_profile.setOnClickListener(view -> {
            Intent intent = new Intent(getActivity(), ExtraLayouts.class);
            intent.putExtra("fragment_type", "edit_profile");
            startActivity(intent);
        });

        // Handle change to ChangePassword Fragment
        change_password.setOnClickListener(view -> {
            Intent intent = new Intent(getActivity(), ExtraLayouts.class);
            intent.putExtra("fragment_type", "change_password");
            startActivity(intent);
        });

        // Handle change to user_edit_profile Fragment
        user_edit_profile.setOnClickListener(view -> {
            Intent intent = new Intent(getActivity(), ExtraLayouts.class);
            intent.putExtra("fragment_type", "user_edit_profile");
            startActivity(intent);
        });

        // Check saved theme preference and set the switch state
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("night_mode_prefs", MODE_PRIVATE);
        boolean isNightMode = sharedPreferences.getBoolean("night_mode", false);
        switch_dark_mode.setChecked(isNightMode);

        // Check saved notification preference and set the switch state
        SharedPreferences notificationPreferences = getActivity().getSharedPreferences("notification_prefs", MODE_PRIVATE);
        boolean areNotificationsEnabled = notificationPreferences.getBoolean("notifications_enabled", true);
        switch_notifications.setChecked(areNotificationsEnabled);

        // Set current theme based on saved preference
        AppCompatDelegate.setDefaultNightMode(isNightMode ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);

        // Set up listener for Night Mode switch toggle
        switch_dark_mode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            AppCompatDelegate.setDefaultNightMode(isChecked ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
            saveNightModeState(isChecked);
            Toast.makeText(getContext(), isChecked ? "Night mode enabled" : "Day mode enabled", Toast.LENGTH_SHORT).show();
        });

        // Set up listener for the notification switch toggle
        switch_notifications.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // Schedule notifications
                NotificationScheduler.scheduleDailyNotifications(getContext(), String.valueOf(totalIncome), String.valueOf(totalExpense), String.valueOf(totalIncome - totalExpense));
                saveNotificationState(true);
                Toast.makeText(getContext(), "Notifications enabled", Toast.LENGTH_SHORT).show();
            } else {
                // Cancel notifications
                NotificationScheduler.cancelDailyNotifications(getContext());
                saveNotificationState(false);
                Toast.makeText(getContext(), "Notifications disabled", Toast.LENGTH_SHORT).show();
            }
        });

        // Set up the logout functionality
        view.findViewById(R.id.logout_layout).setOnClickListener(v -> {

            // Show ProgressDialog while logging out
            progressDialog.setMessage("Logging out...");
            progressDialog.setCancelable(false); // Prevent dismissal by tapping outside
            progressDialog.show(); // Show the dialog

            // Clear cached username
            clearCachedUserName();

            // Sign out from Firebase
            FirebaseAuth.getInstance().signOut();

            // Clear night mode preferences
            clearNightModePreferences();

            // Dismiss the ProgressDialog after logout
            progressDialog.dismiss();

            // Show a toast message indicating successful logout
            Toast.makeText(getContext(), "Logged out successfully", Toast.LENGTH_SHORT).show();

            // Redirect to MainActivity
            Intent intent = new Intent(getActivity(), MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            getActivity().finish(); // Finish the current activity
        });

        firebaseAuth =FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        currentUser = firebaseAuth.getCurrentUser();

        // Firebase references
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid(); // Get current user UID
        databaseIncomeReference = FirebaseDatabase.getInstance().getReference().child("IncomeData").child(uid);
        databaseExpenseReference = FirebaseDatabase.getInstance().getReference().child("ExpenseData").child(uid);


//        // Fetch the totals
//        fetchIncomeAndExpenseTotals();

        // Load the username when the fragment is created
        loadUserName();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadUserName(); // Refresh username whenever the fragment is resumed
    }

    private void loadUserName(){
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("user_data", MODE_PRIVATE);
        String cachedUserName = sharedPreferences.getString("userName", null);

        if (cachedUserName != null) {
            profile_name.setText(cachedUserName);
        } else {
            // Fetch from Firestore if not cached
            String userId = currentUser.getUid();
            DocumentReference documentReference = firebaseFirestore.collection("users").document(userId);

            documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.exists()) {
                        String userName = documentSnapshot.getString("userName");
                        profile_name.setText(userName);

                        // Cache the user name
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("userName", userName);
                        editor.apply();
                    } else {
                        Toast.makeText(getActivity(), "No User Name data found", Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getActivity(), "Failed to load User Name: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    // Save night mode state to SharedPreferences
    private void saveNightModeState(boolean nightMode) {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("night_mode_prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("night_mode", nightMode);
        editor.apply();
    }

    // Clear night mode preferences
    private void clearNightModePreferences() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("night_mode_prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("night_mode");
        editor.apply();
    }

    // Save notification state to SharedPreferences
    private void saveNotificationState(boolean notificationsEnabled) {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("notification_prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("notifications_enabled", notificationsEnabled);
        editor.apply();
    }

//    private void fetchIncomeAndExpenseTotals() {
//        // Fetch income total
//        databaseIncomeReference.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                totalIncome = 0;
//                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
//                    Data data = dataSnapshot.getValue(Data.class);
//                    totalIncome += data.getAmount();
//                }
//                fetchExpenseTotal(); // Call to fetch expense after income
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                // Handle error
//                Toast.makeText(getContext(), "Failed to fetch income data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });
//    }

//    private void fetchExpenseTotal() {
//        // Fetch expense total
//        databaseExpenseReference.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                totalExpense = 0;
//                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
//                    Data data = dataSnapshot.getValue(Data.class);
//                    totalExpense += data.getAmount();
//                }
//                double totalBalance = totalIncome - totalExpense;
//
//                // Schedule notifications if enabled
//                if (switch_notifications.isChecked()) {
//                    NotificationScheduler.scheduleDailyNotifications(getContext(), String.valueOf(totalIncome), String.valueOf(totalExpense), String.valueOf(totalBalance));
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                // Handle error
//                Toast.makeText(getContext(), "Failed to fetch expense data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });
//    }

    // Method to clear cached username
    private void clearCachedUserName() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("user_data", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("userName"); // Remove the cached username
        editor.apply();
    }
}
