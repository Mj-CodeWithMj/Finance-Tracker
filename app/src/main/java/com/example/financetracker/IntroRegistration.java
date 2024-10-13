package com.example.financetracker;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.financetracker.model.UserData;
import com.example.financetracker.utils.FirebaseUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class IntroRegistration extends AppCompatActivity {

    EditText first_name_reg, last_name_reg, user_name_reg,user_birth_date;
    Button next_button_reg;
    Calendar calendar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro_registrion);

        first_name_reg = findViewById(R.id.first_name_reg);
        last_name_reg = findViewById(R.id.last_name_reg);
        user_name_reg = findViewById(R.id.user_name_reg);
        user_birth_date = findViewById(R.id.user_birth_date);
        next_button_reg = findViewById(R.id.next_button_reg);

        calendar = Calendar.getInstance();



        // Set OnClickListener on birthdate field to open DatePicker
        user_birth_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDatePicker();
            }
        });

        next_button_reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setDetails();
            }
        });
    }

    private void openDatePicker(){

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {
                        // Set selected date in EditText in the format dd-MM-yyyy
                        calendar.set(selectedYear, selectedMonth, selectedDay);
                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                        user_birth_date.setText(dateFormat.format(calendar.getTime()));
                    }
                },
                year, month, day);

        // Show the DatePickerDialog
        datePickerDialog.show();
    }


    public void setDetails() {

        String firstName = first_name_reg.getText().toString().trim();
        String lastName = last_name_reg.getText().toString().trim();
        String userName = user_name_reg.getText().toString().trim();
        String birthDateS = user_birth_date.getText().toString().trim();

        // Validate input
        if (firstName.isEmpty()) {
            first_name_reg.setError("Required First Name");
            first_name_reg.requestFocus();
            return;
        }

        if (lastName.isEmpty()) {
            last_name_reg.setError("Required Last Name");
            last_name_reg.requestFocus();
            return;
        }

        if (userName.isEmpty()) {
            user_name_reg.setError("Required User Name");
            user_name_reg.requestFocus();
            return;
        }

        if (userName.length() < 3) {
            user_name_reg.setError("User Name must be at least 3 characters long");
            user_name_reg.requestFocus();
            return;
        }

        // Validate birth date
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        Date birthDate = null;
        try {
            birthDate = dateFormat.parse(birthDateS);
        } catch (ParseException e) {
            user_birth_date.setError("Invalid birth date. Use dd-MM-yyyy format.");
            user_birth_date.requestFocus();
            return;
        }

        // Create UserData object
        UserData userData = new UserData(firstName, lastName, userName, birthDate);


        // Store the data in Firebase Firestore
        DocumentReference userRef = FirebaseUtils.currentUserDetails();
        userRef.set(userData).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    // Data saved successfully, move to the RegistrationActivity
                    Toast.makeText(IntroRegistration.this, "Registration Successful....", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(IntroRegistration.this, HomeActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    // Handle the error
                    Exception e = task.getException();
                    if (e != null) {
                        // Log the exception message
                        e.printStackTrace();
                    }
                    first_name_reg.setError("Failed to save data");
                    last_name_reg.setError("Failed to save data");
                    user_name_reg.setError("Failed to save data");
                    user_birth_date.setError("Failed to save data");
                }
            }
        });
    }

}

