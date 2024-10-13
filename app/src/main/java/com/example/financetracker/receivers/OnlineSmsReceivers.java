package com.example.financetracker.receivers;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import com.example.financetracker.model.Data;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class OnlineSmsReceivers extends BroadcastReceiver {


    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    @Override
    public void onReceive(Context context, Intent intent) {

        Bundle bundle = intent.getExtras();
        Object[] smsObject = (Object[]) bundle.get("pdus");

        assert smsObject != null;

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user == null) return;

        String uidString = user.getUid();

        for (Object sms : smsObject) {

            SmsMessage smsMessage;
            String format = bundle.getString("format");

            smsMessage = SmsMessage.createFromPdu((byte[]) sms, format);
            String sender = smsMessage.getDisplayOriginatingAddress();
            String message = smsMessage.getDisplayMessageBody();

            DatabaseReference databaseReference;


            // Message from the HDFC
            if (message.toUpperCase().contains("HDFC")){
                if (message.toLowerCase().contains("credited to a/c")) {

                    // ================= Extract specific information Income HDFC Bank =================
                    String HDFCIncomeAmount = extractHDFCIncomeAmount(message);
                    String HDFCIncomeDate = extractHDFCIncomeDate(message);
                    String HDFCIncomeTo = extractHDFCIncomeTo(message);

                    databaseReference = FirebaseDatabase.getInstance().getReference("IncomeData").child(uidString);

                    if (!HDFCIncomeAmount.isEmpty() && !HDFCIncomeDate.isEmpty() && !HDFCIncomeTo.isEmpty()) {
                        String messageId = databaseReference.push().getKey();
                        if (messageId != null) {

                            Data smsModel = new Data.Builder()
                                    .setId(messageId)
                                    .setAmount(Double.valueOf(HDFCIncomeAmount))
                                    .setDate(HDFCIncomeDate)
                                    .setNote(HDFCIncomeTo)
                                    .setType("Online")
                                    .build();

                            try {
                                databaseReference.child(messageId).setValue(smsModel);
                                // Log extracted values for income
                                Log.d("HdfcIncomeAmount", "Extracted HDFC Income Amount: " + HDFCIncomeAmount);
                                Log.d("HdfcIncomeDate", "Extracted HDFC Income Date: " + HDFCIncomeDate);
                                Log.d("HdfcIncomeTo", "Extracted HDFC Income To: " + HDFCIncomeTo);
                                // log for database status
                                Log.d("DatabaseStatus", "Income data successfully stored.");
                            } catch (Exception e) {
                                Log.e("DatabaseError", "Error storing income data: " + e.getMessage());
                            }
                        }
                    } else {
                        Log.d("Error", "Income data missing for HDFC Bank necessary fields.");
                    }
                }
                // Expense message from the HDFC bank
                else if (message.toLowerCase().contains("amt sent") &&
                        message.toLowerCase().contains("to")) {

                    // =============== Extract specific information Expense HDFC Bank ==================
                    String HDFCExpenseAmount = extractHDFCExpenseAmount(message);
                    String HDFCExpenseDate = extractHDFCExpenseDate(message);
                    String HDFCExpenseTo = extractHDFCExpenseTo(message);

                    databaseReference = FirebaseDatabase.getInstance().getReference("ExpenseData").child(uidString);

                    if (!HDFCExpenseAmount.isEmpty() && !HDFCExpenseDate.isEmpty() && !HDFCExpenseTo.isEmpty()) {
                        String messageId = databaseReference.push().getKey();
                        if (messageId != null) {

                            Data smsModel = new Data.Builder()
                                    .setId(messageId)
                                    .setAmount(Double.valueOf(HDFCExpenseAmount))
                                    .setDate(HDFCExpenseDate)
                                    .setNote(HDFCExpenseTo)
                                    .setType("Online")
                                    .build();

                            try {
                                databaseReference.child(messageId).setValue(smsModel);
                                // Log extracted values for expense
                                Log.d("HDFCExpenseAmount", "Extracted HDFC expense Amount: " + HDFCExpenseAmount);
                                Log.d("HDFCExpenseDate", "Extracted HDFC expense Date: " + HDFCExpenseDate);
                                Log.d("HDFCExpenseTo", "Extracted HDFC expense To: " + HDFCExpenseTo);
                                // log for database status
                                Log.d("DatabaseStatus", "Expense data successfully stored.");
                            } catch (Exception e) {
                                Log.e("DatabaseError", "Error storing expense data: " + e.getMessage());
                            }
                        }
                    } else {
                        Log.d("Error", "Expense data missing for HDFC Bank necessary fields.");
                    }
                }
            }
            // ================== END Extract specific information HDFC Bank =======================

            // Message from the IDBI
            if (message.toLowerCase().contains("idbi bank")) {
                // Expense message from the IDBI bank
                if (message.toLowerCase().contains("debited for")
                        && message.toLowerCase().contains("upi")) {

                    // ================ Extract specific information Expense IDBI Bank =================
                    String IDBIExpenseAmount = extractIDBIExpenseAmount(message);
                    String IDBIExpenseDate = extractIDBIExpenseDate(message);
                    String IDBICreditor = extractIDBICreditor(message);

                    databaseReference = FirebaseDatabase.getInstance().getReference("ExpenseData").child(uidString);

                    if (!IDBIExpenseAmount.isEmpty() && !IDBIExpenseDate.isEmpty() && !IDBICreditor.isEmpty()) {
                        String messageId = databaseReference.push().getKey();
                        if (messageId != null) {

                            Data smsModel = new Data.Builder()
                                    .setId(messageId)
                                    .setAmount(Double.valueOf(IDBIExpenseAmount))
                                    .setDate(IDBIExpenseDate)
                                    .setNote(IDBICreditor)
                                    .setType("Online")
                                    .build();

                            try {
                                databaseReference.child(messageId).setValue(smsModel);
                                // Log the extracted values for debugging
                                Log.d("IDBIExpenseAmount", "Extracted Expense Amount: " + IDBIExpenseAmount);
                                Log.d("IDBIExpenseDate", "Extracted Expense Date: " + IDBIExpenseDate);
                                Log.d("IDBICreditor", "Extracted Creditor: " + IDBICreditor);

                                // Log for database status
                                Log.d("DatabaseStatus", "Expense data successfully stored.");
                            } catch (Exception e) {
                                Log.e("DatabaseError", "Error storing expense data: " + e.getMessage());
                            }
                        }
                    } else {
                        Log.d("Error", "Expense data from IDBI bank missing necessary fields.");
                    }
                }
                // Income message from the IDBI bank
                else if (message.toLowerCase().contains("dear customer,")
                            && message.toLowerCase().contains("credited")) {

                    // ================ Extract specific information Income IDBI Bank ==================
                    String IDBIIncomeAmount = extractIDBIIncomeAmount(message);
                    String IDBIIncomeDate = extractIDBIIncomeDate(message);
                    String IDBIIncomeTo = extractIDBIIncomeTo(message);

                    databaseReference = FirebaseDatabase.getInstance().getReference("IncomeData").child(uidString);

                    if (!IDBIIncomeAmount.isEmpty() && !IDBIIncomeDate.isEmpty() && !IDBIIncomeTo.isEmpty()) {
                        String messageId = databaseReference.push().getKey();
                        if (messageId != null) {

                            Data smsModel = new Data.Builder()
                                    .setId(messageId)
                                    .setAmount(Double.valueOf(IDBIIncomeAmount))
                                    .setDate(IDBIIncomeDate)
                                    .setNote(IDBIIncomeTo)
                                    .setType("Online")
                                    .build();

                            try {
                                databaseReference.child(messageId).setValue(smsModel);
                                // Log extracted values for income
                                Log.d("IDBIIncomeAmount", "Extracted IDBI Bank Income Amount: " + IDBIIncomeAmount);
                                Log.d("IDBIIncomeDate", "Extracted IDBI Bank Income Date: " + IDBIIncomeDate);
                                Log.d("IDBIIncomeTo", "Extracted IDBI Bank Income To: " + IDBIIncomeTo);
                                // Log for database status
                                Log.d("DatabaseStatus", "Income data successfully stored.");
                            } catch (Exception e) {
                                Log.e("DatabaseError", "Error storing income data: " + e.getMessage());
                            }
                        }
                    } else {
                        Log.d("Error", "Income data missing for IDBI Bank necessary fields.");
                    }
                }
            }

            // ================== END Extract specific information IDBI Bank =======================

            // Message from the AU Bank
            else if (message.toLowerCase().contains("au bank")){
                // income message from the AU Small bank
                if (message.toLowerCase().contains("credited inr")) {
                    // ================ Extract specific information Income AU Bank ========================
                    String AUSmallIncomeAmount = extractAUIncomeAmount(message);
                    String AUSmallIncomeDate = extractAUIncomeDate(message);
                    String AUSmallIncomeTo = extractAUIncomeTo(message);

                    databaseReference = FirebaseDatabase.getInstance().getReference("IncomeData").child(uidString);

                    if (!AUSmallIncomeAmount.isEmpty() && !AUSmallIncomeDate.isEmpty() && !AUSmallIncomeTo.isEmpty()) {
                        String messageId = databaseReference.push().getKey();
                        if (messageId != null) {

                            Data smsModel = new Data.Builder()
                                    .setId(messageId)
                                    .setAmount(Double.valueOf(AUSmallIncomeAmount))
                                    .setDate(AUSmallIncomeDate)
                                    .setNote(AUSmallIncomeTo)
                                    .setType("Online")
                                    .build();

                            try {
                                databaseReference.child(messageId).setValue(smsModel);
                                // Log extracted values for income
                                Log.d("AUSmallIncomeAmount", "Extracted AUSmall Income Amount: " + AUSmallIncomeAmount);
                                Log.d("AUSmallIncomeDate", "Extracted AUSmall Income Date: " + AUSmallIncomeDate);
                                Log.d("AUSmallIncomeTo", "Extracted AU Small Income To: " + AUSmallIncomeTo);
                                // log for database status
                                Log.d("DatabaseStatus", "Income data successfully stored.");
                            } catch (Exception e) {
                                Log.e("DatabaseError", "Error storing income data: " + e.getMessage());
                            }
                        }
                    } else {
                        Log.d("Error", "Income data missing for AU Small Finance Bank necessary fields.");
                    }

                }
                // Expense message from the AU Small bank
                else if (message.toLowerCase().contains("debited inr")) {

                    // ================ Extract specific information Expense AU Bank ===================
                    String AUSmallExpenseAmount = extractAUExpenseAmount(message);
                    String AUSmallExpenseDate = extractAUExpenseDate(message);
                    String AUSmallCreditor = extractAUExpenseTo(message);

                    databaseReference = FirebaseDatabase.getInstance().getReference("ExpenseData").child(uidString);

                    if (!AUSmallExpenseAmount.isEmpty() && !AUSmallExpenseDate.isEmpty() && !AUSmallCreditor.isEmpty()) {
                        String messageId = databaseReference.push().getKey();
                        if (messageId != null) {

                            Data smsModel = new Data.Builder()
                                    .setId(messageId)
                                    .setAmount(Double.valueOf(AUSmallExpenseAmount))
                                    .setDate(AUSmallExpenseDate)
                                    .setNote(AUSmallCreditor)
                                    .setType("Online")
                                    .build();

                            try {
                                databaseReference.child(messageId).setValue(smsModel);
                                // Log extracted values for expense
                                Log.d("AUSmallExpenseAmount", "Extracted AUSmall Expense Amount: " + AUSmallExpenseAmount);
                                Log.d("AUSmallExpenseDate", "Extracted AUSmall Expense Date: " + AUSmallExpenseDate);
                                Log.d("AUSmallCreditor", "Extracted AU Small Expense To: " + AUSmallCreditor);
                                // log for database status
                                Log.d("DatabaseStatus", "Expense data successfully stored.");
                            } catch (Exception e) {
                                Log.e("DatabaseError", "Error storing expense data: " + e.getMessage());
                            }
                        }
                    } else {
                        Log.d("Error", "Expense data missing for AU Small bank necessary fields.");
                    }
                }
            }
            // ================== END Extract specific information AU Bank =========================

            // Message from the Godhra URBAN Bank
            else if (message.toUpperCase().contains("GODHRA URBAN BANK") || message.toUpperCase().contains("GODHRA URBAN COOP BANK")){
                // income message from the Urban Bank
                if (message.toLowerCase().contains("cr by rs")) {

                    // ================= Extract specific information Income URBAN Bank ================
                    String URBANIncomeAmount = extractUrbanBankIncomeAmount(message);
                    String URBANIncomeDate = extractUrbanBankIncomeDate(message);
                    String URBANIncomeTo = extractUrbanBankIncomeTo(message);

                    databaseReference = FirebaseDatabase.getInstance().getReference("IncomeData").child(uidString);

                    if (!URBANIncomeAmount.isEmpty() && !URBANIncomeDate.isEmpty() && !URBANIncomeTo.isEmpty()) {
                        String messageId = databaseReference.push().getKey();
                        if (messageId != null) {

                            Data smsModel = new Data.Builder()
                                    .setId(messageId)
                                    .setAmount(Double.valueOf(URBANIncomeAmount))
                                    .setDate(URBANIncomeDate)
                                    .setNote(URBANIncomeTo)
                                    .setType("Online")
                                    .build();

                            try {
                                databaseReference.child(messageId).setValue(smsModel);
                                // Log extracted values for income
                                Log.d("URBANIncomeAmount", "Extracted URBAN Income Amount: " + URBANIncomeAmount);
                                Log.d("URBANIncomeDate", "Extracted URBAN Income Date: " + URBANIncomeDate);
                                Log.d("URBANIncomeTo", "Extracted URBAN Income To: " + URBANIncomeTo);
                                // log for database status
                                Log.d("DatabaseStatus", "Income data successfully stored.");
                            } catch (Exception e) {
                                Log.e("DatabaseError", "Error storing income data: " + e.getMessage());
                            }
                        }
                    } else {
                        Log.d("Error", "Income data missing for URBAN Bank necessary fields.");
                    }

                }
                // expense message from Urban Bank
                else if (message.toLowerCase().contains("debited for") &&
                        message.toLowerCase().contains("towards linked")) {

                    // =============== Extract specific information Expense URBAN Bank =================
                    String URBANExpenseAmount = extractUrbanBankExpenseAmount(message);
                    String URBANExpenseDate = extractUrbanBankExpenseDate(message);
                    String URBANExpenseTo = extractUrbanBankExpenseTo(message);

                    databaseReference = FirebaseDatabase.getInstance().getReference("ExpenseData").child(uidString);

                    if (!URBANExpenseAmount.isEmpty() && !URBANExpenseDate.isEmpty() && !URBANExpenseTo.isEmpty()) {
                        String messageId = databaseReference.push().getKey();
                        if (messageId != null) {

                            Data smsModel = new Data.Builder()
                                    .setId(messageId)
                                    .setAmount(Double.valueOf(URBANExpenseAmount))
                                    .setDate(URBANExpenseDate)
                                    .setNote(URBANExpenseTo)
                                    .setType("Online")
                                    .build();

                            try {
                                databaseReference.child(messageId).setValue(smsModel);
                                // Log extracted values for expense
                                Log.d("URBANExpenseAmount", "Extracted URBAN bank Expense Amount: " + URBANExpenseAmount);
                                Log.d("URBANExpenseDate", "Extracted URBAN bank Expense Date: " + URBANExpenseDate);
                                Log.d("URBANExpenseTo", "Extracted URBAN bank Expense To: " + URBANExpenseTo);
                                // log for database status
                                Log.d("DatabaseStatus", "Expense data successfully stored.");
                            } catch (Exception e) {
                                Log.e("DatabaseError", "Error storing expense data: " + e.getMessage());
                            }
                        }
                    } else {
                        Log.d("Error", "Expense data missing for URBAN bank necessary fields.");
                    }
                }
            }
            // ================== END Extract specific information URBAN Bank ======================

            // Message from the Kotak Bank
            else if (message.toLowerCase().contains("kotak bank")) {
                // income message from the Kotak Bank
                if (message.toLowerCase().contains("received rs.")) {

                    // ================= Extract specific information Income Kotak Bank ================
                    String kotakIncomeAmount = extractKotakBankIncomeAmount(message);
                    String kotakIncomeDate = extractKotakBankIncomeDate(message);
                    String kotakIncomeFrom = extractKotakBankIncomeFrom(message);

                    databaseReference = FirebaseDatabase.getInstance().getReference("IncomeData").child(uidString);

                    if (!kotakIncomeAmount.isEmpty() && !kotakIncomeDate.isEmpty() && !kotakIncomeFrom.isEmpty()) {
                        String messageId = databaseReference.push().getKey();
                        if (messageId != null) {

                            Data smsModel = new Data.Builder()
                                    .setId(messageId)
                                    .setAmount(Double.valueOf(kotakIncomeAmount))
                                    .setDate(kotakIncomeDate)
                                    .setNote(kotakIncomeFrom)
                                    .setType("Online")
                                    .build();

                            try {
                                databaseReference.child(messageId).setValue(smsModel);
                                // Log extracted values for income
                                Log.d("KotakIncomeAmount", "Extracted Kotak Income Amount: " + kotakIncomeAmount);
                                Log.d("KotakIncomeDate", "Extracted Kotak Income Date: " + kotakIncomeDate);
                                Log.d("KotakIncomeFrom", "Extracted Kotak Income From: " + kotakIncomeFrom);
                                // log for database status
                                Log.d("DatabaseStatus", "Income data successfully stored.");
                            } catch (Exception e) {
                                Log.e("DatabaseError", "Error storing income data: " + e.getMessage());
                            }
                        }
                    } else {
                        Log.d("Error", "Income data missing for Kotak Bank necessary fields.");
                    }

                }
                // expense message from Kotak Bank
                else if (message.toLowerCase().contains("sent rs.")) {

                    // =============== Extract specific information Expense URBAN Bank =================
                    String kotakExpenseAmount = extractKotakExpenseAmount(message);
                    String kotakExpenseDate = extractKotakExpenseDate(message);
                    String kotakExpenseTo = extractKotakExpenseTo(message);

                    databaseReference = FirebaseDatabase.getInstance().getReference("ExpenseData").child(uidString);

                    if (!kotakExpenseAmount.isEmpty() && !kotakExpenseDate.isEmpty() && !kotakExpenseTo.isEmpty()) {
                        String messageId = databaseReference.push().getKey();
                        if (messageId != null) {

                            Data smsModel = new Data.Builder()
                                    .setId(messageId)
                                    .setAmount(Double.valueOf(kotakExpenseAmount))
                                    .setDate(kotakExpenseDate)
                                    .setNote(kotakExpenseTo)
                                    .setType("Online")
                                    .build();

                            try {
                                databaseReference.child(messageId).setValue(smsModel);
                                // Log extracted values for expense
                                Log.d("KotakExpenseAmount", "Extracted Kotak Expense Amount: " + kotakExpenseAmount);
                                Log.d("KotakExpenseDate", "Extracted Kotak Expense Date: " + kotakExpenseDate);
                                Log.d("KotakExpenseTo", "Extracted Kotak Expense To: " + kotakExpenseTo);
                                // log for database status
                                Log.d("DatabaseStatus", "Expense data successfully stored.");
                            } catch (Exception e) {
                                Log.e("DatabaseError", "Error storing expense data: " + e.getMessage());
                            }
                        }
                    } else {
                        Log.d("Error", "Expense data missing for Kotak Bank necessary fields.");
                    }
                }
            }
            // ================== END Extract specific information Kotak Bank ======================

            // Message from the ICICI Bank
            else if (message.toLowerCase().contains("icici bank")){
                // Income message from ICICI Bank
                if (message.toLowerCase().contains("credited with rs")) {

                    // ================= Extract specific information Income ICICI Bank ================
                    String iciciIncomeAmount = extractICICIIncomeAmount(message);
                    String iciciIncomeDate = extractICICIIncomeDate(message);
                    String iciciIncomeFrom = extractICICIIncomeFrom(message);

                    databaseReference = FirebaseDatabase.getInstance().getReference("IncomeData").child(uidString);

                    if (!iciciIncomeAmount.isEmpty() && !iciciIncomeDate.isEmpty() && !iciciIncomeFrom.isEmpty()) {
                        String messageId = databaseReference.push().getKey();
                        if (messageId != null) {

                            Data smsModel = new Data.Builder()
                                    .setId(messageId)
                                    .setAmount(Double.valueOf(iciciIncomeAmount))
                                    .setDate(iciciIncomeDate)
                                    .setNote(iciciIncomeFrom)
                                    .setType("Online")
                                    .build();

                            try {
                                databaseReference.child(messageId).setValue(smsModel);
                                // Log extracted values for income
                                Log.d("ICICIIncomeAmount", "Extracted ICICI Income Amount: " + iciciIncomeAmount);
                                Log.d("ICICIIncomeDate", "Extracted ICICI Income Date: " + iciciIncomeDate);
                                Log.d("ICICIIncomeFrom", "Extracted ICICI Income From: " + iciciIncomeFrom);
                                // log for database status
                                Log.d("DatabaseStatus", "Income data successfully stored.");
                            } catch (Exception e) {
                                Log.e("DatabaseError", "Error storing income data: " + e.getMessage());
                            }
                        }
                    } else {
                        Log.d("Error", "Income data missing for ICICI Bank necessary fields.");
                    }
                }
                // Expense message from ICICI Bank
                else if (message.toLowerCase().contains("debited for rs")) {

                    // ================= Extract specific information for ICICI Bank Expense ================
                    String iciciExpenseAmount = extractICICIExpenseAmount(message);
                    String iciciExpenseDate = extractICICIExpenseDate(message);
                    String iciciExpenseTo = extractICICIExpenseTo(message);

                    databaseReference = FirebaseDatabase.getInstance().getReference("ExpenseData").child(uidString);

                    if (!iciciExpenseAmount.isEmpty() && !iciciExpenseDate.isEmpty() && !iciciExpenseTo.isEmpty()) {
                        String messageId = databaseReference.push().getKey();
                        if (messageId != null) {

                            Data smsModel = new Data.Builder()
                                    .setId(messageId)
                                    .setAmount(Double.valueOf(iciciExpenseAmount))
                                    .setDate(iciciExpenseDate)
                                    .setNote(iciciExpenseTo)
                                    .setType("Online")
                                    .build();

                            try {
                                databaseReference.child(messageId).setValue(smsModel);
                                // Log extracted values for expense
                                Log.d("ICICIExpenseAmount", "Extracted ICICI Expense Amount: " + iciciExpenseAmount);
                                Log.d("ICICIExpenseDate", "Extracted ICICI Expense Date: " + iciciExpenseDate);
                                Log.d("ICICIExpenseTo", "Extracted ICICI Expense To: " + iciciExpenseTo);
                                // log for database status
                                Log.d("DatabaseStatus", "Expense data successfully stored.");
                            } catch (Exception e) {
                                Log.e("DatabaseError", "Error storing expense data: " + e.getMessage());
                            }
                        }
                    } else {
                        Log.d("Error", "Expense data missing for ICICI Bank necessary fields.");
                    }
                }
            }
            // ================== END Extract specific information Kotak Bank ======================

            // Message from the BOB Bank
            else if (message.toLowerCase().contains("bank of baroda")){
                // Income message from Bank of Baroda
                if (message.toLowerCase().contains("credited to a/c")){
                    // ================= Extract specific information for Income from Bank of Baroda ================
                    String bobIncomeAmount = extractBOBIncomeAmount(message);
                    String bobIncomeDate = extractBOBIncomeDate(message);
                    String bobIncomeFrom = extractBOBIncomeFrom(message);

                    databaseReference = FirebaseDatabase.getInstance().getReference("IncomeData").child(uidString);

                    if (!bobIncomeAmount.isEmpty() && !bobIncomeDate.isEmpty() && !bobIncomeFrom.isEmpty()) {
                        String messageId = databaseReference.push().getKey();
                        if (messageId != null) {

                            Data smsModel = new Data.Builder()
                                    .setId(messageId)
                                    .setAmount(Double.valueOf(bobIncomeAmount))
                                    .setDate(bobIncomeDate)
                                    .setNote(bobIncomeFrom)
                                    .setType("Online")
                                    .build();

                            try {
                                databaseReference.child(messageId).setValue(smsModel);
                                // Log extracted values for income
                                Log.d("BOBIncomeAmount", "Extracted BOB Income Amount: " + bobIncomeAmount);
                                Log.d("BOBIncomeDate", "Extracted BOB Income Date: " + bobIncomeDate);
                                Log.d("BOBIncomeFrom", "Extracted BOB Income From: " + bobIncomeFrom);
                                // log for database status
                                Log.d("DatabaseStatus", "Income data successfully stored.");
                            } catch (Exception e) {
                                Log.e("DatabaseError", "Error storing income data: " + e.getMessage());
                            }
                        }
                    } else {
                        Log.d("Error", "Income data missing for Bank of Baroda necessary fields.");
                    }
                    }
                // Expense message from Bank of Baroda
                else if (message.toLowerCase().contains("transferred from a/c")) {

                    // ================= Extract specific information for Expense from Bank of Baroda ================
                    String bobExpenseAmount = extractBOBExpenseAmount(message);
                    String bobExpenseDate = extractBOBExpenseDate(message);
                    String bobExpenseTo = extractBOBExpenseTo(message);

                    databaseReference = FirebaseDatabase.getInstance().getReference("ExpenseData").child(uidString);

                    if (!bobExpenseAmount.isEmpty() && !bobExpenseDate.isEmpty() && !bobExpenseTo.isEmpty()) {
                        String messageId = databaseReference.push().getKey();
                        if (messageId != null) {

                            Data smsModel = new Data.Builder()
                                    .setId(messageId)
                                    .setAmount(Double.valueOf(bobExpenseAmount))
                                    .setDate(bobExpenseDate)
                                    .setNote(bobExpenseTo)
                                    .setType("Online")
                                    .build();

                            try {
                                databaseReference.child(messageId).setValue(smsModel);
                                // Log extracted values for expense
                                Log.d("BOBExpenseAmount", "Extracted BOB Expense Amount: " + bobExpenseAmount);
                                Log.d("BOBExpenseDate", "Extracted BOB Expense Date: " + bobExpenseDate);
                                Log.d("BOBExpenseTo", "Extracted BOB Expense To: " + bobExpenseTo);
                                // log for database status
                                Log.d("DatabaseStatus", "Expense data successfully stored.");
                            } catch (Exception e) {
                                Log.e("DatabaseError", "Error storing expense data: " + e.getMessage());
                            }
                        }
                    } else {
                        Log.d("Error", "Expense data missing for Bank of Baroda necessary fields.");
                    }
                }
            }
            // ================== END Extract specific information Kotak Bank ======================
        }
    }

    // Extra date format methode

    private String formatDateString(String dateStr) {
        try {
            // Define the input and output date formats
            SimpleDateFormat inputFormat = new SimpleDateFormat("dd-MMM-yy", Locale.ENGLISH);  // Input is "27-Sep-24"
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MM-yy", Locale.ENGLISH);  // Desired format is "27-09-24"

            // Parse the date string into a Date object
            Date date = inputFormat.parse(dateStr);

            // Return the formatted date string
            return outputFormat.format(date);
        } catch (ParseException e) {
            Log.e("DateError", "Error parsing date: " + e.getMessage());
            return dateStr; // Return the original string if parsing fails
        }
    }


    private String formatExpenseDate(String dateStr) {
        // Get the current year as a two-digit string
        SimpleDateFormat yearFormat = new SimpleDateFormat("yy");
        String currentYear = yearFormat.format(new Date());

        // Append the current year to the date string
        return dateStr + "-" + currentYear;
    }

   // Example date formatting function
    private String formatBOBDate(String fullDate) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("dd-MM-yyyy");
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MM-yy");
            Date date = inputFormat.parse(fullDate);
            return outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return fullDate;
        }
    }

    // ========================== Start Kotak Bank Regex for income and expense ====================
    // ========================== Income Methods ==========================
    private String extractKotakBankIncomeAmount(String message) {
        Pattern pattern = Pattern.compile("Received Rs\\.(\\d+\\.\\d{2})");
        Matcher matcher = pattern.matcher(message);
        return matcher.find() ? matcher.group(1) : "";
    }

    // Method to extract the date from the Kotak Bank income message
    private String extractKotakBankIncomeDate(String message) {
        Pattern pattern = Pattern.compile("on\\s*(\\d{2}-\\d{2}-\\d{2})");
        Matcher matcher = pattern.matcher(message);
        if (matcher.find()) {
            String date = matcher.group(1);
            // Assuming you have a method to format the date as needed
            return formatDateString(date);  // Use your existing date formatting method
        }
        return "";
    }

    // Method to extract the "from" field from the Kotak Bank income message
    private String extractKotakBankIncomeFrom(String message) {
        Pattern pattern = Pattern.compile("from\\s([\\w.-]+@[\\w.-]+)");
        Matcher matcher = pattern.matcher(message);
        return matcher.find() ? matcher.group(1) : "";
    }

    // ========================== Expense Methods ==========================
    private String extractKotakExpenseAmount(String message) {
        Pattern pattern = Pattern.compile("Sent Rs\\.\\s?([\\d,]+\\.\\d+)");
        Matcher matcher = pattern.matcher(message);
        return matcher.find() ? matcher.group(1) : "";
    }

    private String extractKotakExpenseDate(String message) {
        Pattern pattern = Pattern.compile("on\\s*(\\d{2}-\\d{2}-\\d{2})");
        Matcher matcher = pattern.matcher(message);
        if (matcher.find()) {
            String date = matcher.group(1);
            return formatDateString(date);  // Use your existing date formatting method
        }
        return "";
    }

    private String extractKotakExpenseTo(String message) {
        Pattern pattern = Pattern.compile("to\\s*([^\\s]+@[\\w.]+)");
        Matcher matcher = pattern.matcher(message);
        return matcher.find() ? matcher.group(1).trim() : "";
    }
    // ========================== END Kotak Bank Regex for income and expense ======================

    // ========================== Start URBAN Bank Regex for income and expense ====================
    // ========================== Income Methods ==========================
    private String extractUrbanBankIncomeAmount(String message) {
        Pattern pattern = Pattern.compile("Cr by Rs\\.\\s?(\\d+\\.\\d+)");
        Matcher matcher = pattern.matcher(message);
        return matcher.find() ? matcher.group(1) : "";
    }

    private String extractUrbanBankIncomeDate(String message) {
        Pattern pattern = Pattern.compile("ON\\s*(\\d{2}-[A-Z]{3}-\\d{4})");
        Matcher matcher = pattern.matcher(message);
        if (matcher.find()) {
            String date = matcher.group(1);
            // Format the extracted date
            return formatDateString(date);  // Use your existing date formatting method
        }
        return "";
    }

    private String extractUrbanBankIncomeTo(String message) {
        // Since the source is always "UPI", we can return it directly
        return "UPI";
    }
    // ========================== Expense Methods ==========================
    private String extractUrbanBankExpenseAmount(String message) {
        Pattern pattern = Pattern.compile("debited for Rs\\.\\s?([\\d,]+\\.\\d+)");
        Matcher matcher = pattern.matcher(message);
        if (matcher.find()) {
            String amount = matcher.group(1);
            Log.d("Debug", "Extracted Amount: " + amount); // Log amount
            return amount;
        }
        return "";
    }

    private String extractUrbanBankExpenseDate(String message) {
        Pattern pattern = Pattern.compile("on\\s*(\\d{2}-\\w{3}-\\d{2})");
        Matcher matcher = pattern.matcher(message);
        if (matcher.find()) {
            String date = matcher.group(1);
            Log.d("Debug", "Extracted Date: " + date); // Log date
            return formatDateString(date); // Ensure formatDateString works correctly
        }
        return "";
    }

    private String extractUrbanBankExpenseTo(String message) {
        Pattern pattern = Pattern.compile("towards linked\\s+([^\\s]+@[a-zA-Z0-9.-]+)");
        Matcher matcher = pattern.matcher(message);
        if (matcher.find()) {
            String to = matcher.group(1).trim();
            Log.d("Debug", "Extracted To: " + to); // Log "to" value
            return to;
        }
        return "";
    }
    // ========================== END URBAN Bank Regex for income and expense ======================

    // ========================== Start Au Bank Regex for income and expense =======================
    // ========================== Income Methods ==========================
    private String extractAUIncomeAmount(String message) {
        Pattern pattern = Pattern.compile("Credited INR\\s?(\\d+\\.\\d+)");
        Matcher matcher = pattern.matcher(message);
        return matcher.find() ? matcher.group(1) : "";
    }

    private String extractAUIncomeDate(String message) {
        Pattern pattern = Pattern.compile("on\\s*(\\d{2}-\\w{3}-\\d{4})");
        Matcher matcher = pattern.matcher(message);
        if (matcher.find()) {
            String date = matcher.group(1);
            // Format the extracted date
            return formatDateString(date);  // Use your existing date formatting method
        }
        return "";
    }

    private String extractAUIncomeTo(String message) {
        Pattern pattern = Pattern.compile("Ref\\s+UPI/CR/\\S+/([A-Za-z\\s]+)\\.");
        Matcher matcher = pattern.matcher(message);
        return matcher.find() ? matcher.group(1).trim() : "";
    }
    // ========================== Expense Methods ==========================
    private String extractAUExpenseAmount(String message) {
        Pattern pattern = Pattern.compile("Debited INR\\s?([\\d,]+\\.\\d+)");
        Matcher matcher = pattern.matcher(message);
        return matcher.find() ? matcher.group(1) : "";
    }

    private String extractAUExpenseDate(String message) {
        Pattern pattern = Pattern.compile("on\\s*(\\d{2}-\\w{3}-\\d{4})");
        Matcher matcher = pattern.matcher(message);
        if (matcher.find()) {
            String date = matcher.group(1);
            return formatDateString(date);  // Use your existing date formatting method
        }
        return "";
    }

    private String extractAUExpenseTo(String message) {
        Pattern pattern = Pattern.compile("from A/c [^\\n]+ on \\d{2}-\\w{3}-\\d{4}\\s+UPI/[^\\n]+/([^\\n]+)");
        Matcher matcher = pattern.matcher(message);
        return matcher.find() ? matcher.group(1).trim() : "";
    }
    // ========================== END AU Bank Regex for income and expense =========================

    // ========================== Start HDFC Bank Regex for income and expense =====================
    // ========================== Income Methods ==========================
    private String extractHDFCIncomeAmount(String message) {
        return message.replaceAll(".*Rs\\.\\s?(\\d+\\.\\d+).*", "$1");

    }

    private String extractHDFCIncomeDate(String message) {
        return message.replaceAll(".*on\\s*(\\d{2}-\\d{2}-\\d{2}).*", "$1");
    }

    private String extractHDFCIncomeTo(String message) {
        return message.replaceAll(".*by a/c linked to VPA\\s*([^\\s\\(]+).*", "$1").trim();
    }

    // ========================== Expense Methods ==========================
    private String extractHDFCExpenseAmount(String message) {
        String amount = "";
        Pattern pattern = Pattern.compile("Amt Sent Rs\\.\\s?(\\d+\\.\\d+)");
        Matcher matcher = pattern.matcher(message);
        if (matcher.find()) {
            amount = matcher.group(1);
        }
        return amount;
    }

    private String extractHDFCExpenseDate(String message) {
        String date = "";
        Pattern pattern = Pattern.compile("On\\s*(\\d{2}-\\d{2})");
        Matcher matcher = pattern.matcher(message);
        if (matcher.find()) {
            date = matcher.group(1);
            // Format the extracted date to include the current year
            return formatExpenseDate(date);
        }
        return date;
    }

    private String extractHDFCExpenseTo(String message) {
        String to = "";
        Pattern pattern = Pattern.compile("To\\s+([\\w\\s]+)\\s+On");
        Matcher matcher = pattern.matcher(message);
        if (matcher.find()) {
            to = matcher.group(1).trim();
        }
        return to;
    }
    // ========================== END HDFC Bank Regex for income and expense =======================

    // ========================== Start IDBI Bank Regex for income and expense =====================
    // ========================== Income Methode ==========================
    private String extractIDBIIncomeAmount(String message) {
        Pattern pattern = Pattern.compile("Rs\\s?(\\d+\\.\\d+)");
        Matcher matcher = pattern.matcher(message);
        return matcher.find() ? matcher.group(1) : "";
    }

    private String extractIDBIIncomeDate(String message) {
        String date = "";
        Pattern pattern = Pattern.compile("on\\s*(\\d{2}-\\w{3}-\\d{2})");
        Matcher matcher = pattern.matcher(message);
        if (matcher.find()) {
            date = matcher.group(1);
            // Format the extracted date
            return formatDateString(date);
        }
        return date;
    }

    private String extractIDBIIncomeTo(String message) {
        Pattern pattern = Pattern.compile("from\\s+([^\\s]+\\s+[^\\s]+)");
        Matcher matcher = pattern.matcher(message);
        return matcher.find() ? matcher.group(1).trim() : "";
    }

    // ========================== Expense Methode ==========================
    private String extractIDBIExpenseAmount(String message) {
        Pattern pattern = Pattern.compile("Rs (\\d+\\.?\\d*)");
        Matcher matcher = pattern.matcher(message);
        return matcher.find() ? matcher.group(1) : "";
    }

    private String extractIDBIExpenseDate(String message) {
        String date = "";
        Pattern pattern = Pattern.compile("(\\d{2}-\\w{3}-\\d{2})");
        Matcher matcher = pattern.matcher(message);
        if (matcher.find()) {
            date = matcher.group(1);
            return formatDateString(date);  // Ensure this converts 'dd-MMM-yy' to 'dd-MM-yy'
        }
        return date;
    }

    private String extractIDBICreditor(String message) {
        Pattern pattern = Pattern.compile("debited for.*?\\s+([A-Za-z]+\\s+[A-Za-z]+)\\s+credited");
        Matcher matcher = pattern.matcher(message);
        return matcher.find() ? matcher.group(1).trim() : "";
    }


    // ========================== END IDBI Bank Regex for income and expense =======================

    // ========================== Start ICICI Bank Regex for income and expense ====================
    // ========================== Income Methode ==========================
    private String extractICICIIncomeAmount(String message) {
        Pattern pattern = Pattern.compile("credited with Rs\\s?(\\d+\\.\\d{2})");
        Matcher matcher = pattern.matcher(message);
        return matcher.find() ? matcher.group(1) : "";
    }

    private String extractICICIIncomeDate(String message) {
        Pattern pattern = Pattern.compile("on\\s*(\\d{2}-[A-Za-z]{3}-\\d{2})");
        Matcher matcher = pattern.matcher(message);
        if (matcher.find()) {
            String date = matcher.group(1);
            return formatDateString(date);  // Assuming your date formatting method is already implemented
        }
        return "";
    }

    private String extractICICIIncomeFrom(String message) {
        Pattern pattern = Pattern.compile("from\\s([A-Za-z\\s]+)\\.");
        Matcher matcher = pattern.matcher(message);
        return matcher.find() ? matcher.group(1).trim() : "";
    }

    // ========================== Expense Methode ==========================
    private String extractICICIExpenseAmount(String message) {
        String amount = "";
        Pattern pattern = Pattern.compile("debited for Rs\\s?(\\d+\\.\\d+)");
        Matcher matcher = pattern.matcher(message);
        if (matcher.find()) {
            amount = matcher.group(1);
        }
        return amount;
    }

    private String extractICICIExpenseDate(String message) {
        String date = "";
        Pattern pattern = Pattern.compile("on\\s*(\\d{2}-[A-Za-z]{3}-\\d{2})");
        Matcher matcher = pattern.matcher(message);
        if (matcher.find()) {
            date = matcher.group(1);
            // Assuming you have a method to format the date as needed
            return formatDateString(date);  // Use your existing date formatting method
        }
        return date;
    }

    private String extractICICIExpenseTo(String message) {
        String to = "";
        Pattern pattern = Pattern.compile(";\\s([\\w\\s]+)\\scredited");
        Matcher matcher = pattern.matcher(message);
        if (matcher.find()) {
            to = matcher.group(1).trim();
        }
        return to;
    }

    // ========================== END ICICI Bank Regex for income and expense ======================

    // ========================== Start ICICI Bank Regex for income and expense =====================
    // ========================== Income Methode ==========================
    private String extractBOBIncomeAmount(String message) {
        Pattern pattern = Pattern.compile("Rs\\.(\\d+)(?=\\s+Credited)");
        Matcher matcher = pattern.matcher(message);
        return matcher.find() ? matcher.group(1) : "";
    }

    private String extractBOBIncomeDate(String message) {
        Pattern pattern = Pattern.compile("\\((\\d{2}-\\d{2}-\\d{4})"); // Matches "28-09-2024"
        Matcher matcher = pattern.matcher(message);
        if (matcher.find()) {
            String fullDate = matcher.group(1);
            return formatBOBDate(fullDate);  // Format it to "dd-MM-yy"
        }
        return "";
    }

    private String extractBOBIncomeFrom(String message) {
        Pattern pattern = Pattern.compile("by\\s([\\w.-]+)\\."); // Matches "jamalia240-1_ok"
        Matcher matcher = pattern.matcher(message);
        return matcher.find() ? matcher.group(1).trim() : "";
    }

    // ========================== Expense Methode ==========================
    private String extractBOBExpenseAmount(String message) {
        Pattern pattern = Pattern.compile("Rs\\.(\\d+)\\s+transferred");
        Matcher matcher = pattern.matcher(message);
        return matcher.find() ? matcher.group(1) : "";
    }

    private String extractBOBExpenseDate(String message) {
        Pattern pattern = Pattern.compile("\\((\\d{2}-\\d{2}-\\d{4})"); // Extracts full date like 28-09-2024
        Matcher matcher = pattern.matcher(message);
        if (matcher.find()) {
            String fullDate = matcher.group(1);  // This captures the full date as "28-09-2024"
            return formatBOBDate(fullDate);  // Reformat to "28-09-24"
        }
        return "";
    }

    private String extractBOBExpenseTo(String message) {

        return "UPI";
    }
    // ========================== END ICICI Bank Regex for income and expense =======================


}
