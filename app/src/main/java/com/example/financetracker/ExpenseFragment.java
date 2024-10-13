package com.example.financetracker;

import static android.content.ContentValues.TAG;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.example.financetracker.adapters.ExpenseAdapter;
import com.example.financetracker.model.Data;
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

public class ExpenseFragment extends Fragment {

    private static final String TAG = "ExpenseFragment";


    private RecyclerView recyclerView;
    private ExpenseAdapter expenseAdapter;
    private ArrayList<Data> expenseList;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private Button update_btn,yes_btn_exp,cancel_btn,no_btn_exp,date_selector_button;
    private Spinner type_spinner_update;
    private TextView totalExpenseSum;
    private EditText amount_edittext_update,note_edittext_update,date_edittext_update;
    private View view;
    private ImageView calendar_icon, filter_icon;
    private String selectedDate;


    @Override
    public void onStart() {
        super.onStart();

        expenseList = new ArrayList<>();

        if (databaseReference == null) {
            Log.e(TAG, "Database reference is null, cannot load data");
            return;
        }

        // Load current day's transactions initially
        fetchExpenseDataForDate(getCurrentDate());

        databaseReference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("DefaultLocale")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                expenseList.clear();
                double totalExpenseValue = 0.0;

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Data expense = dataSnapshot.getValue(Data.class);
                    if (expense != null) {
                        expenseList.add(expense);
                        totalExpenseValue += expense.getAmount();
                    }
                }

                // Sort the list by date
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yy", Locale.getDefault());
                Collections.sort(expenseList, (expense1, expense2) -> {
                    try {
                        Date date1 = sdf.parse(expense1.getDate());
                        Date date2 = sdf.parse(expense2.getDate());
                        return date1.compareTo(date2);
                    } catch (ParseException e) {
                        Log.e(TAG, "Date parsing error", e);
                        return 0; // Fallback
                    }
                });

                if (expenseAdapter == null){
                    expenseAdapter = new ExpenseAdapter(expenseList, new ExpenseAdapter.OnItemClickListener() {
                        @Override
                        public void onUpdateClick(Data expense) {
                            showUpdateDialog(expense);
                        }

                        @Override
                        public void onDeleteClick(Data expense) {
                            showDeleteConfirmationDialog(expense);
                        }
                    });
                    recyclerView.setAdapter(expenseAdapter);
                }else {
                    expenseAdapter.notifyDataSetChanged();
                }
                // Round totalIncome to 2 decimal places
                totalExpenseValue = Math.round(totalExpenseValue * 100.0) / 100.0;
                totalExpenseSum.setText(String.format("%.2f", totalExpenseValue));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to load data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_expense, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser expenseUser = firebaseAuth.getCurrentUser();
        String uidString = expenseUser != null ? expenseUser.getUid() : "";

        databaseReference = FirebaseDatabase.getInstance().getReference().child("ExpenseData").child(uidString);

        recyclerView = view.findViewById(R.id.recycler_view_expense);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(expenseAdapter);

        totalExpenseSum = view.findViewById(R.id.expensefragment_amount_result);
        date_selector_button = view.findViewById(R.id.date_selector_exp_button);
        calendar_icon = view.findViewById(R.id.calendar_exp_icon);
        filter_icon = view.findViewById(R.id.filter_exp_icon);

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
            filterExpenseByType(selectedType);
            dialog.dismiss(); // Close dialog after selecting
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }


    private void filterExpenseByType(String type) {
        // Clear the current list before fetching new data
        expenseList.clear();

        // Create a query based on the type
        if (type.equals("All")) {
            if (selectedDate !=null){
                // Show all transactions for the selected date
                databaseReference.orderByChild("date").equalTo(selectedDate)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                double totalExpenseValue = 0.0;
                                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                    Data expense = dataSnapshot.getValue(Data.class);
                                    if (expense !=null){
                                        expenseList.add(expense);
                                        totalExpenseValue += expense.getAmount();
                                    }
                                }
                                updateRecyclerView(totalExpenseValue);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(getContext(), "Failed to load data: " + error.getMessage(), Toast.LENGTH_SHORT).show();

                            }
                        });
            }else {
                // Show all transactions if no date is selected
                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        double totalExpenseValue = 0.0;
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            Data expense = dataSnapshot.getValue(Data.class);
                            if (expense != null) {
                                expenseList.add(expense);
                                totalExpenseValue += expense.getAmount();
                            }
                        }
                        updateRecyclerView(totalExpenseValue);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getContext(), "Failed to load data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        } else {
            // Query for specific type For Cash, Bank, Online options
            if (selectedDate !=null){
                // Fetch transactions of selected type for the selected date
                databaseReference.orderByChild("date").equalTo(selectedDate)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                double totalExpenseValue = 0.0;
                                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                    Data expense = dataSnapshot.getValue(Data.class);
                                    if (expense != null && expense.getType().equals(type)) {
                                        expenseList.add(expense);
                                        totalExpenseValue += expense.getAmount();
                                    }
                                }
                                updateRecyclerView(totalExpenseValue);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(getContext(), "Failed to load data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }else {
                // Fetch all transactions of the selected type if no date is selected
                databaseReference.orderByChild("type").equalTo(type)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                double totalExpenseValue = 0.0;
                                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                    Data expense = dataSnapshot.getValue(Data.class);
                                    if (expense != null) {
                                        expenseList.add(expense);
                                        totalExpenseValue += expense.getAmount();
                                    }
                                }
                                updateRecyclerView(totalExpenseValue);
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
                    fetchExpenseDataForDate(formattedDate);

                    // Update the button text with the selected date
                    date_selector_button.setText(formattedDate);
                },
                year, month, day);
        datePickerDialog.show();

    }


    private void fetchExpenseDataForDate(String date) {
        Log.d(TAG, "Fetching data for date: " + date);

        // Clear the current list to avoid showing previous data
        expenseList.clear();

        // Query the database for expenses on the selected date
        databaseReference.orderByChild("date").equalTo(date)
            .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                double totalExpenseValue = 0.0;
                if (!snapshot.exists()) {
                    Log.d(TAG, "No data found for date: " + date);  // Log if no data is found
                    return;
                }
                // Iterate through the retrieved expenses
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Data expense = dataSnapshot.getValue(Data.class);
                    if (expense != null) {
                        expenseList.add(expense); // Add each expense to the list
                        totalExpenseValue += expense.getAmount(); // Sum the amounts
                        Log.d(TAG, "Fetched income: " + expense.toString()); // Log each fetched income
                    }
                }
                // Update the UI with the filtered data
                updateRecyclerView(totalExpenseValue);
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

    private void updateRecyclerView(double totalExpenseValue) {
        if (expenseAdapter == null) {
            expenseAdapter = new ExpenseAdapter(expenseList, new ExpenseAdapter.OnItemClickListener() {
                @Override
                public void onUpdateClick(Data expense) {
                    showUpdateDialog(expense);
                }

                @Override
                public void onDeleteClick(Data expense) {
                    showDeleteConfirmationDialog(expense);
                }
            });
            recyclerView.setAdapter(expenseAdapter);
        } else {
            expenseAdapter.notifyDataSetChanged();
        }
        totalExpenseSum.setText(String.valueOf(totalExpenseValue));
    }


    private void showUpdateDialog(Data expense) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_update_expense, null);
        builder.setView(dialogView);

        // Initialize dialog variables
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

        // Set current data
        amount_edittext_update.setText(String.valueOf(expense.getAmount()));
        note_edittext_update.setText(expense.getNote());
        date_edittext_update.setText(expense.getDate());
        type_spinner_update.setSelection(getTypePosition(expense.getType()));

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

        // Handle button clicks
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
                    if (expense.getId() == null || expense.getId().isEmpty()) {
                        Toast.makeText(getContext(), "Invalid transaction ID", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    DatabaseReference expenseRef = databaseReference.child(expense.getId());
                    // Update the fields
                    expenseRef.child("amount").setValue(amount);
                    expenseRef.child("note").setValue(updatedNote);
                    expenseRef.child("date").setValue(updatedDate);
                    expenseRef.child("type").setValue(updatedType);

                    Toast.makeText(getContext(), "Expense updated", Toast.LENGTH_SHORT).show();
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

    private void showDeleteConfirmationDialog(Data expense) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.delete_expense_dialog_box, null);
        builder.setView(dialogView);

        yes_btn_exp = dialogView.findViewById(R.id.yes_btn_exp);
        no_btn_exp = dialogView.findViewById(R.id.no_btn_exp);

        AlertDialog dialog = builder.create();

        yes_btn_exp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                databaseReference.child(expense.getId()).removeValue()
                        .addOnSuccessListener(unused -> {
                            Toast.makeText(getContext(), "Expense deleted", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(getContext(), "Failed to delete expense: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        });
            }
        });
        no_btn_exp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }
}
