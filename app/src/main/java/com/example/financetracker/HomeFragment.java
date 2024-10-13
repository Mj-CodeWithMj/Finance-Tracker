package com.example.financetracker;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.financetracker.model.Data;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Pattern;

public class HomeFragment extends Fragment {

    //view binding
    View view;


    //floating action button
    private FloatingActionButton income_ft_btn, expense_ft_btn, main_ft_btn;

    //text views
    private TextView income_ft_txt, expense_ft_txt;

    //boolean for floating action button visibility
    private boolean isFabOpen = false;

    //floating action button Animation
    private Animation fade_open, fade_close;

    //firebase database connection
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseIncomeReference, databaseExpenseReference;

    // total income and expense
    private TextView totalIncomeSum,totalExpenseSum,totalBalanceSum;

    // Date pattern for validation
    private static final String DATE_PATTERN = "dd/MM/yyyy";
    private static final Pattern DATE_PATTERN_REGEX = Pattern.compile("\\d{2}/\\d{2}/\\d{4}");

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_home, container, false);

        //connect floating button with layout
        income_ft_btn = view.findViewById(R.id.income_ft_btn);
        expense_ft_btn = view.findViewById(R.id.expense_ft_btn);
        main_ft_btn = view.findViewById(R.id.main_ft_btn);

        //connect text views with layout
        income_ft_txt = view.findViewById(R.id.income_ft_txt);
        expense_ft_txt = view.findViewById(R.id.expense_ft_txt);

        //connect animation view with layout
        fade_open = AnimationUtils.loadAnimation(getContext(), R.anim.fade_open);
        fade_close = AnimationUtils.loadAnimation(getContext(), R.anim.fade_close);

        //total income,expense and balance
        totalIncomeSum = view.findViewById(R.id.income_amount_result);
        totalExpenseSum = view.findViewById(R.id.expense_amount_result);
        totalBalanceSum = view.findViewById(R.id.balance_amount_result);

        // Initialize Firebase Database References
        databaseIncomeReference = FirebaseDatabase.getInstance().getReference("incomes"); // Replace with your correct path
        databaseExpenseReference = FirebaseDatabase.getInstance().getReference("expenses"); // Replace with your correct path

        //firebase connection
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        String uid = user.getUid();

        databaseIncomeReference = FirebaseDatabase.getInstance().getReference().child("IncomeData").child(uid);
        databaseExpenseReference = FirebaseDatabase.getInstance().getReference().child("ExpenseData").child(uid);

        //set listener
        setIncomeListener();
        setExpenseListener();

        main_ft_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //function calling of adddata
                addData();

                if (isFabOpen){

                    //close fab button
                    income_ft_btn.startAnimation(fade_close);
                    expense_ft_btn.startAnimation(fade_close);
                    income_ft_btn.setClickable(false);
                    expense_ft_btn.setClickable(false);

                    //close text views
                    income_ft_txt.startAnimation(fade_close);
                    expense_ft_txt.startAnimation(fade_close);
                    income_ft_txt.setClickable(false);
                    expense_ft_txt.setClickable(false);

                    //set boolean to false
                    isFabOpen = false;

                }else {

                    //open fab button
                    income_ft_btn.startAnimation(fade_open);
                    expense_ft_btn.startAnimation(fade_open);
                    income_ft_btn.setClickable(true);
                    expense_ft_btn.setClickable(true);

                    //open text views
                    income_ft_txt.startAnimation(fade_open);
                    expense_ft_txt.startAnimation(fade_open);
                    income_ft_txt.setClickable(true);
                    expense_ft_txt.setClickable(true);

                    //set boolean to true
                    isFabOpen = true;

                }
            }
        });

        return view;
    }

    private void addData(){

        //fab income button

        income_ft_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                incomeDataInsert();
            }
        });



        // fab expense button

        expense_ft_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                expenseDataInsert();
            }
        });

    }

    // date picker function
    private void setupDatePicker(EditText dateEditText) {
        dateEditText.setOnClickListener(v -> {
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    getContext(),
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        String formattedDate = String.format("%02d/%02d/%d", selectedDay, (selectedMonth + 1), selectedYear);
                        dateEditText.setText(formattedDate);
                    },
                    year, month, day);

            datePickerDialog.show();
        });
    }

    private String formatDate(String inputDate) {
        String formattedDate = "";
        try {
            // Update the input format to match the date picker output
            SimpleDateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MM-yy", Locale.ENGLISH);

            // Parse the input date
            Date date = inputFormat.parse(inputDate);
            if (date != null) {
                formattedDate = outputFormat.format(date);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return formattedDate;
    }

    // income listener function
    public void setIncomeListener() {
        databaseIncomeReference.addValueEventListener(new ValueEventListener() {
            double totalIncome = 0.0;

            @SuppressLint("DefaultLocale")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Data income = dataSnapshot.getValue(Data.class);
                    if (income != null) {
                        totalIncome += income.getAmount();
                    }
                }
                // Round totalIncome to 2 decimal places
                totalIncome = Math.round(totalIncome * 100.0) / 100.0;
                totalIncomeSum.setText(String.format("%.2f", totalIncome));
                updateBalance();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to load data" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Expense listener function
    public void setExpenseListener() {
        databaseExpenseReference.addValueEventListener(new ValueEventListener() {
            double totalExpense = 0.0;

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Data expense = dataSnapshot.getValue(Data.class);
                    if (expense != null) {
                        totalExpense += expense.getAmount();
                    }
                }
                // Round totalExpense to 2 decimal places
                totalExpense = Math.round(totalExpense * 100.0) / 100.0;
                totalExpenseSum.setText(String.format("%.2f", totalExpense));
                updateBalance();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to load data" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void updateBalance() {
        double income = Double.parseDouble(totalIncomeSum.getText().toString());
        double expense = Double.parseDouble(totalExpenseSum.getText().toString());
        double balance = income - expense;
        // Round balance to 2 decimal places
        balance = Math.round(balance * 100.0) / 100.0;
        totalBalanceSum.setText(String.format("%.2f", balance));
    }


    public void incomeDataInsert(){

        AlertDialog.Builder dialogbox = new AlertDialog.Builder(getActivity());
        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        View views = layoutInflater.inflate(R.layout.custom_dialogbox_income,null);
        dialogbox.setView(views);
        AlertDialog dialog = dialogbox.create();

        // income dialog box data
        EditText amount_edittext, note_edittext, date_edittext;
        Spinner type_spinner;
        Button cancel_btn, add_btn;

        //income edit text data
        amount_edittext = views.findViewById(R.id.amount_edittext);
        note_edittext = views.findViewById(R.id.note_edittext);
        date_edittext = views.findViewById(R.id.date_edittext);
        type_spinner = views.findViewById(R.id.type_spinner);

        // income dialog box button
        add_btn = views.findViewById(R.id.add_btn);
        cancel_btn = views.findViewById(R.id.cancel_btn);

        //set up spinner with option
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),R.array.transaction_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        type_spinner.setAdapter(adapter);

        date_edittext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setupDatePicker(date_edittext);
            }
        });

        add_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String amount = amount_edittext.getText().toString().trim();
                String note = note_edittext.getText().toString().trim();
                String date = date_edittext.getText().toString().trim();
                String type = type_spinner.getSelectedItem().toString();

                // Format the date
                String formattedDate = formatDate(date);
                Log.d("IncomeDataInsert", "Formatted Date: " + formattedDate);

                // Inside your button click or data insertion logic
                long dateAsTimestamp;
                try {
                    dateAsTimestamp = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).parse(date).getTime();
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }


                boolean isValid = true;

                if (TextUtils.isEmpty(amount)){
                    amount_edittext.setError("Amount is required....");
                    isValid = false;
                }
                if (TextUtils.isEmpty(note)){
                    note_edittext.setError("Note is required....");
                    isValid = false;
                }
                if (TextUtils.isEmpty(date)){
                    date_edittext.setError("Date cannot be empty....");
                    isValid = false;
                }
                if ("Select Any one".equals(type)) {
                    Toast.makeText(getActivity(), "Please select a valid option.", Toast.LENGTH_SHORT).show();
                    isValid = false;
                }
                if (isValid) {

                    // Convert amount to double
                    double amountValue = Double.parseDouble(amount);

                    // Generate a unique ID for the data
                    String id = databaseIncomeReference.push().getKey();

                    // Create a new Data object
                    Data transaction = new Data.Builder()
                            .setAmount(amountValue)
                            .setType(type)
                            .setNote(note)
                            .setDate(formattedDate)
                            .settimeStamp(dateAsTimestamp)
                            .setId(id)
                            .build();

                    // Save the transaction data to Firebase
                    if (id != null) {
                        databaseIncomeReference.child(id).setValue(transaction).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(getActivity(),"Income data added successfully...",Toast.LENGTH_SHORT).show();
                                // Close the dialog
                                dialog.dismiss();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getActivity(), "Failed to add income. Please try again.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
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

    public void expenseDataInsert(){

        AlertDialog.Builder dialogbox = new AlertDialog.Builder(getActivity());
        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        View views = layoutInflater.inflate(R.layout.custom_dialogbox_expense,null);
        dialogbox.setView(views);
        AlertDialog dialog = dialogbox.create();

        // expense dialog box data
        EditText amount_edittext, note_edittext, date_edittext;
        Spinner type_spinner;
        Button cancel_btn, add_btn;

        //expense edit text data
        amount_edittext = views.findViewById(R.id.amount_edittext);
        note_edittext = views.findViewById(R.id.note_edittext);
        date_edittext = views.findViewById(R.id.date_edittext);
        type_spinner = views.findViewById(R.id.type_spinner);

        // expense dialog box button
        add_btn = views.findViewById(R.id.add_btn);
        cancel_btn = views.findViewById(R.id.cancel_btn);

        //set up spinner with option
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),R.array.transaction_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        type_spinner.setAdapter(adapter);

        date_edittext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setupDatePicker(date_edittext);
            }
        });


        add_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String amount = amount_edittext.getText().toString().trim();
                String note = note_edittext.getText().toString().trim();
                String date = date_edittext.getText().toString().trim();
                String type = type_spinner.getSelectedItem().toString();

                // Format the date
                String formattedDate = formatDate(date);

                // Inside your button click or data insertion logic
                long dateAsTimestamp;
                try {
                    dateAsTimestamp = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).parse(date).getTime();
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }

                boolean isValid = true;

                if (TextUtils.isEmpty(amount)){
                    amount_edittext.setError("Amount is required....");
                    isValid = false;
                }
                if (TextUtils.isEmpty(note)){
                    note_edittext.setError("Note is required....");
                    isValid = false;
                }
                if (TextUtils.isEmpty(date)){
                    date_edittext.setError("Date cannot be empty....");
                    isValid = false;
                }
                if ("Select Any one".equals(type)) {
                    Toast.makeText(getActivity(), "Please select a valid option.", Toast.LENGTH_SHORT).show();
                    isValid = false;
                }
                if (isValid) {

                    // Convert amount to double
                    double amountValue = Double.parseDouble(amount);

                    // Generate a unique ID for the data
                    String id = databaseExpenseReference.push().getKey();

                    // Create a new Data object
                    Data transaction = new Data.Builder()
                            .setAmount(amountValue)
                            .setType(type)
                            .setNote(note)
                            .setDate(formattedDate)
                            .settimeStamp(dateAsTimestamp)
                            .setId(id)
                            .build();

                    // Save the transaction data to Firebase
                    if (id != null) {
                        databaseExpenseReference.child(id).setValue(transaction).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(getActivity(),"Expense data added successfully...",Toast.LENGTH_SHORT).show();
                                // Close the dialog
                                dialog.dismiss();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getActivity(), "Failed to add expense. Please try again...", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
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
}