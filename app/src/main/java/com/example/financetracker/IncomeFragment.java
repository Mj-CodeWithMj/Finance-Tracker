package com.example.financetracker;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.financetracker.adapters.IncomeAdapter;
import com.example.financetracker.model.Data;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

public class IncomeFragment extends Fragment {

    private static final String TAG = "IncomeFragment";

    private RecyclerView recyclerView;
    private IncomeAdapter incomeAdapter;
    private ArrayList<Data> incomeList;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private Button cancel_btn, update_btn, yes_btn_inc, no_btn_inc, date_selector_button;
    private Spinner type_spinner_update;
    private TextView totalIncomesum;
    private EditText amount_edittext_update, note_edittext_update, date_edittext_update;
    private View view;
    private ImageView calendar_icon, filter_icon;
    private String selectedDate;



    @Override
    public void onStart() {
        super.onStart();

        incomeList = new ArrayList<>();

        if (databaseReference == null) {
            Log.e(TAG, "Database reference is null, cannot load data");
            return;
        }

        // Load current day's transactions initially
        fetchIncomeDataForDate(getCurrentDate());

        databaseReference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("DefaultLocale")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                incomeList.clear();
                double totalIncomeValue = 0.0;

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Data income = dataSnapshot.getValue(Data.class);
                    if (income != null) {
                        incomeList.add(income);
                        totalIncomeValue += income.getAmount();
                    }
                }

                // Sort the list by date
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yy", Locale.getDefault());
                Collections.sort(incomeList, (income1, income2) -> {
                    try {
                        Date date1 = sdf.parse(income1.getDate());
                        Date date2 = sdf.parse(income2.getDate());
                        return date1.compareTo(date2);
                    } catch (ParseException e) {
                        Log.e(TAG, "Date parsing error", e);
                        return 0; // Fallback
                    }
                });

                if (incomeAdapter == null) {
                    incomeAdapter = new IncomeAdapter(incomeList, new IncomeAdapter.OnItemClickListener() {
                        @Override
                        public void onUpdateClick(Data income) {
                            showUpdateDialog(income);
                        }

                        @Override
                        public void onDeleteClick(Data income) {
                            showDeleteConfirmationDialog(income);
                        }
                    });
                    recyclerView.setAdapter(incomeAdapter);
                } else {
                    incomeAdapter.notifyDataSetChanged();
                }
                // Round totalIncome to 2 decimal places
                totalIncomeValue = Math.round(totalIncomeValue * 100.0) / 100.0;
                totalIncomesum.setText(String.format("%.2f", totalIncomeValue));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to load data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_income, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser incomeUser = firebaseAuth.getCurrentUser();
        String uidString = incomeUser != null ? incomeUser.getUid() : "";

        databaseReference = FirebaseDatabase.getInstance().getReference().child("IncomeData").child(uidString);

        recyclerView = view.findViewById(R.id.recycler_view_income);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(incomeAdapter);

        totalIncomesum = view.findViewById(R.id.incomefragment_amount_result);
        date_selector_button = view.findViewById(R.id.date_selector_button);
        calendar_icon = view.findViewById(R.id.calendar_icon);
        filter_icon = view.findViewById(R.id.filter_icon);


        //filter dialog box
        filter_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFilterDialog();
            }
        });
        // select date on button view
        date_selector_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog();
            }
        });

        // select date on calendar icon
        calendar_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog();
            }
        });

        return view;
    }

    private void showFilterDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Select Payment Type");

        // List of options
        String[] types = {"All","Cash", "Bank", "Online"};
        int checkedItem = -1; // No item checked by default

        builder.setSingleChoiceItems(types, checkedItem, (dialog, which) -> {
            String selectedType = types[which];
            filterIncomeByType(selectedType);
            dialog.dismiss(); // Close dialog after selecting
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void filterIncomeByType(String type) {
        // Clear the current list before fetching new data
        incomeList.clear();

        // Create a query based on the type
        if (type.equals("All")) {
            if (selectedDate != null) {
                // Show all transactions for the selected date
                databaseReference.orderByChild("date").equalTo(selectedDate)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                double totalIncomeValue = 0.0;
                                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                    Data income = dataSnapshot.getValue(Data.class);
                                    if (income != null) {
                                        incomeList.add(income);
                                        totalIncomeValue += income.getAmount();
                                    }
                                }
                                updateRecyclerView(totalIncomeValue);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(getContext(), "Failed to load data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            } else {
                // Show all transactions if no date is selected
                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        double totalIncomeValue = 0.0;
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            Data income = dataSnapshot.getValue(Data.class);
                            if (income != null) {
                                incomeList.add(income);
                                totalIncomeValue += income.getAmount();
                            }
                        }
                        updateRecyclerView(totalIncomeValue);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getContext(), "Failed to load data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        } else {
            // Query for specific type For Cash, Bank, Online options
            if (selectedDate != null) {
                // Fetch transactions of selected type for the selected date
                databaseReference.orderByChild("date").equalTo(selectedDate)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                double totalIncomeValue = 0.0;
                                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                    Data income = dataSnapshot.getValue(Data.class);
                                    if (income != null && income.getType().equals(type)) {
                                        incomeList.add(income);
                                        totalIncomeValue += income.getAmount();
                                    }
                                }
                                updateRecyclerView(totalIncomeValue);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(getContext(), "Failed to load data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            } else {
                // Fetch all transactions of the selected type if no date is selected
                databaseReference.orderByChild("type").equalTo(type)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                double totalIncomeValue = 0.0;
                                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                    Data income = dataSnapshot.getValue(Data.class);
                                    if (income != null) {
                                        incomeList.add(income);
                                        totalIncomeValue += income.getAmount();
                                    }
                                }
                                updateRecyclerView(totalIncomeValue);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(getContext(), "Failed to load data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        }
    }

    private void showDatePickerDialog(){
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                getContext(),
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String formattedDate = String.format("%02d-%02d-%02d", selectedDay, (selectedMonth + 1), (selectedYear % 100));
                    Log.d(TAG, "Fetching income data for formatted date: " + formattedDate);
                    selectedDate = formattedDate; // Store the selected date
                    fetchIncomeDataForDate(formattedDate);

                    // Update the button text with the selected date
                    date_selector_button.setText(formattedDate);
                },
                year, month, day);
        datePickerDialog.show();

    }

    private void fetchIncomeDataForDate(String date){
        Log.d(TAG, "Fetching data for date: " + date);

        // Clear the current list to avoid showing previous data
        incomeList.clear();

        // Query the database for expenses on the selected date
        databaseReference.orderByChild("date").equalTo(date)
            .addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    double totalIncomeValue = 0.0;
                    if (!snapshot.exists()) {
                        Log.d(TAG, "No data found for date: " + date);  // Log if no data is found
                    }
                    // Iterate through the retrieved expenses
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Data income = dataSnapshot.getValue(Data.class);
                        if (income != null) {
                            incomeList.add(income);
                            totalIncomeValue += income.getAmount();
                            Log.d(TAG, "Fetched income: " + income.toString()); // Log each fetched income
                        }
                    }
                    // Update the UI with the filtered data
                    updateRecyclerView(totalIncomeValue);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getContext(), "Failed to load data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }

    private String getCurrentDate() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.DAY_OF_MONTH) + "-" + (calendar.get(Calendar.MONTH) + 1) + "-" + calendar.get(Calendar.YEAR);
    }

    private void updateRecyclerView(double totalIncomeValue) {
        if (incomeAdapter == null) {
            incomeAdapter = new IncomeAdapter(incomeList, new IncomeAdapter.OnItemClickListener() {
                @Override
                public void onUpdateClick(Data income) {
                    showUpdateDialog(income);
                }

                @Override
                public void onDeleteClick(Data income) {
                    showDeleteConfirmationDialog(income);
                }
            });
            recyclerView.setAdapter(incomeAdapter);
        } else {
            incomeAdapter.notifyDataSetChanged();
        }
        totalIncomesum.setText(String.valueOf(totalIncomeValue));
    }

    private void showUpdateDialog(Data income) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_update_income, null);
        builder.setView(dialogView);

        // Initialize views from dialog
        amount_edittext_update = dialogView.findViewById(R.id.amount_edittext_update);
        note_edittext_update = dialogView.findViewById(R.id.note_edittext_update);
        date_edittext_update = dialogView.findViewById(R.id.date_edittext_update);
        type_spinner_update = dialogView.findViewById(R.id.type_spinner_update);
        update_btn = dialogView.findViewById(R.id.update_btn);
        cancel_btn = dialogView.findViewById(R.id.cancel_btn);

        // Populate Spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), R.array.transaction_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        type_spinner_update.setAdapter(adapter);

        // Set current data in dialog fields
        amount_edittext_update.setText(String.valueOf(income.getAmount()));
        note_edittext_update.setText(income.getNote());
        date_edittext_update.setText(income.getDate());
        type_spinner_update.setSelection(getTypePosition(income.getType()));

        // DatePicker for date_edittext_update
        date_edittext_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        getContext(),
                        (view, selectedYear, selectedMonth, selectedDay) -> {
                            // Format the date as "dd-MM-yy"
                            String formattedDate = String.format("%02d-%02d-%02d", selectedDay, (selectedMonth + 1), (selectedYear % 100));
                            date_edittext_update.setText(formattedDate);
                        },
                        year, month, day);
                datePickerDialog.show();
            }
        });


        AlertDialog dialog = builder.create();

        // Handle update button click
        update_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String updatedAmount = amount_edittext_update.getText().toString();
                String updatedNote = note_edittext_update.getText().toString();
                String updatedDate = date_edittext_update.getText().toString();
                String updatedType = type_spinner_update.getSelectedItem().toString();

                if (validateInputs(updatedAmount, updatedNote, updatedDate)) {
                    double amount = Double.parseDouble(updatedAmount);

                    // Check for null ID
                    if (income.getId() == null || income.getId().isEmpty()) {
                        Toast.makeText(getContext(), "Invalid transaction ID", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    DatabaseReference incomeRef = databaseReference.child(income.getId());
                    // Update the fields
                    incomeRef.child("amount").setValue(amount);
                    incomeRef.child("note").setValue(updatedNote);
                    incomeRef.child("date").setValue(updatedDate);
                    incomeRef.child("type").setValue(updatedType);

                    Toast.makeText(getContext(), "Income updated", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            }
        });

        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private int getTypePosition(String type) {
        String[] transactionTypes = getResources().getStringArray(R.array.transaction_types);
        return Arrays.asList(transactionTypes).indexOf(type);
    }

    private boolean validateInputs(String amount, String note, String date) {
        if (amount.isEmpty()) {
            Toast.makeText(getContext(), "Amount cannot be empty", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (note.isEmpty()) {
            Toast.makeText(getContext(), "Note cannot be empty", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (date.isEmpty()) {
            Toast.makeText(getContext(), "Date cannot be empty", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void showDeleteConfirmationDialog(Data income) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.delete_income_dialog_box, null);
        builder.setView(dialogView);

        yes_btn_inc = dialogView.findViewById(R.id.yes_btn_inc);
        no_btn_inc = dialogView.findViewById(R.id.no_btn_inc);

        AlertDialog dialog = builder.create();

        yes_btn_inc.setOnClickListener(view -> {
            databaseReference.child(income.getId())
                    .removeValue()
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(getContext(), "Income deleted", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    })
                    .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to delete income: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        });

        no_btn_inc.setOnClickListener(view -> dialog.dismiss());

        dialog.show();
    }

}
