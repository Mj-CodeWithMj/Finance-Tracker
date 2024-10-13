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
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BankSmsReceivers extends BroadcastReceiver {

    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    @Override
    public void onReceive(Context context, Intent intent) {

        Bundle bundles = intent.getExtras();
        Object[] smsObjects = (Object[]) bundles.get("pdus");

        assert smsObjects != null;

        FirebaseAuth firebaseAuths = FirebaseAuth.getInstance();
        FirebaseUser users = firebaseAuths.getCurrentUser();
        if (users == null) return;

        String uidString = users.getUid();

        for (Object sms : smsObjects) {

            SmsMessage smsMessages;
            String formats = bundles.getString("format");

            smsMessages = SmsMessage.createFromPdu((byte[]) sms, formats);
            String sender = smsMessages.getDisplayOriginatingAddress();
            String message = smsMessages.getDisplayMessageBody();

            DatabaseReference databaseReferences;

            // HDFC Bank All Transaction
            if (message.toUpperCase().contains("HDFC")) {
                // cash Deposit
                if (message.toLowerCase().contains("cash deposited")) {


                    String HDFCIncomeAmount = extractHDFCIncomeAmount(message);
                    String HDFCIncomeDate = extractHDFCIncomeDate(message);
                    String HDFCIncomeNote = "Cash Deposit";

                    databaseReferences = FirebaseDatabase.getInstance().getReference("IncomeData").child(uidString);

                    if (!HDFCIncomeAmount.isEmpty() && !HDFCIncomeDate.isEmpty()) {
                        String messageId = databaseReferences.push().getKey();
                        if (messageId != null) {

                            Data smsModels = new Data.Builder()
                                    .setId(messageId)
                                    .setAmount(Double.valueOf(HDFCIncomeAmount))
                                    .setDate(HDFCIncomeDate)
                                    .setNote(HDFCIncomeNote)
                                    .setType("Bank")
                                    .build();

                            try {
                                databaseReferences.child(messageId).setValue(smsModels);
                                // Log extracted values for cash deposit
                                Log.d("HdfcIncomeAmount", "Extracted HDFC Income Amount: " + HDFCIncomeAmount);
                                Log.d("HdfcIncomeDate", "Extracted HDFC Income Date: " + HDFCIncomeDate);
                                Log.d("HdfcIncomeNote", "Note: " + HDFCIncomeNote);
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
                // cash withdraw from card
                else if (message.toLowerCase().contains("withdrawn from hdfc bank card")) {

                    // ================= Extract specific information for Card Withdrawal HDFC Bank =================
                    String HDFCExpenseAmount = extractHDFCCardWithdrawAmount(message);  // Extract the amount (e.g., "8000")
                    String HDFCExpenseDate = extractHDFCCardWithdrawDate(message);      // Extract and format the date (e.g., "07-07-24")

                    databaseReferences = FirebaseDatabase.getInstance().getReference("ExpenseData").child(uidString);

                    // Check if required fields are not empty
                    if (!HDFCExpenseAmount.isEmpty() && !HDFCExpenseDate.isEmpty()) {
                        String messageId = databaseReferences.push().getKey();
                        if (messageId != null) {

                            Data smsModels = new Data.Builder()
                                    .setId(messageId)
                                    .setAmount(Double.valueOf(HDFCExpenseAmount))
                                    .setDate(HDFCExpenseDate)
                                    .setNote("Cash Withdrawal from Atm Card")
                                    .setType("Bank")
                                    .build();

                            try {
                                databaseReferences.child(messageId).setValue(smsModels);
                                // Log extracted values for card withdrawal
                                Log.d("HDFCExpenseAmount", "Extracted HDFC Card Withdraw Amount: " + HDFCExpenseAmount);
                                Log.d("HDFCExpenseDate", "Extracted HDFC Card Withdraw Date: " + HDFCExpenseDate);
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
                // chq payment
                else if (message.toLowerCase().contains("received chq")) {

                    // ========== Extract specific information for Cheque Payment HDFC Bank ==========
                    String HDFCChqAmount = extractHDFCChqAmount(message);  // Extract the amount (e.g., "1000.00")
                    String HDFCChqDate = extractHDFCChqDate(message);      // Extract and format the date (e.g., "24-09-24")
                    String HDFCChqNumber = extractHDFCChqNumber(message);  // Extract the cheque number (e.g., "000030")

                    databaseReferences = FirebaseDatabase.getInstance().getReference("ExpenseData").child(uidString);

                    // Check if required fields are not empty
                    if (!HDFCChqAmount.isEmpty() && !HDFCChqDate.isEmpty() && !HDFCChqNumber.isEmpty()) {
                        String messageId = databaseReferences.push().getKey();
                        if (messageId != null) {

                            Data smsModels = new Data.Builder()
                                    .setId(messageId)
                                    .setAmount(Double.valueOf(HDFCChqAmount))
                                    .setDate(HDFCChqDate)
                                    .setNote(HDFCChqNumber)
                                    .setType("Bank")
                                    .build();

                            try {
                                databaseReferences.child(messageId).setValue(smsModels);
                                // Log extracted values for cheque payment
                                Log.d("HDFCChqAmount", "Extracted HDFC Cheque Amount: " + HDFCChqAmount);
                                Log.d("HDFCChqDate", "Extracted HDFC Cheque Date: " + HDFCChqDate);
                                Log.d("HDFCChqNumber", "Extracted HDFC Cheque Number: " + HDFCChqNumber);
                                // log for database status
                                Log.d("DatabaseStatus", "Cheque payment data successfully stored.");
                            } catch (Exception e) {
                                Log.e("DatabaseError", "Error storing cheque payment data: " + e.getMessage());
                            }
                        }
                    } else {
                        Log.d("Error", "Cheque payment data missing for HDFC Bank necessary fields.");
                    }
                }
                // chq deposit
                else if (message.toUpperCase().contains("CHQ DEP") &&
                        message.toLowerCase().contains("deposited in hdfc bank a/c")) {

                    // ========== Extract specific information for Cheque Deposit in HDFC Bank ==========
                    String HDFCChqDepositAmount = extractHDFCChqDepositAmount(message);  // Extract the amount (e.g., "6000.00")
                    String HDFCChqDepositDate = extractHDFCChqDepositDate(message);      // Extract and format the date (e.g., "17-06-24")

                    databaseReferences = FirebaseDatabase.getInstance().getReference("IncomeData").child(uidString);

                    // Check if required fields are not empty
                    if (!HDFCChqDepositAmount.isEmpty() && !HDFCChqDepositDate.isEmpty()) {
                        String messageId = databaseReferences.push().getKey();
                        if (messageId != null) {

                            Data smsModels = new Data.Builder()
                                    .setId(messageId)
                                    .setAmount(Double.valueOf(HDFCChqDepositAmount))
                                    .setDate(HDFCChqDepositDate)
                                    .setNote("Cheque Deposit")
                                    .setType("Bank")
                                    .build();

                            try {
                                databaseReferences.child(messageId).setValue(smsModels);
                                // Log extracted values for cheque deposit
                                Log.d("HDFCChqDepositAmount", "Extracted HDFC Cheque Deposit Amount: " + HDFCChqDepositAmount);
                                Log.d("HDFCChqDepositDate", "Extracted HDFC Cheque Deposit Date: " + HDFCChqDepositDate);
                                // log for database status
                                Log.d("DatabaseStatus", "Cheque deposit data successfully stored.");
                            } catch (Exception e) {
                                Log.e("DatabaseError", "Error storing cheque deposit data: " + e.getMessage());
                            }
                        }
                    } else {
                        Log.d("Error", "Cheque deposit data missing for HDFC Bank necessary fields.");
                    }
                }
            }
            // IDBI Bank All Transaction
            else if (message.toUpperCase().contains("IDBI")) {
                // ATM Withdrawal
                if (message.toLowerCase().contains("debited for")
                        && message.toLowerCase().contains("atm wdl")) {

                    // ========== Extract specific information for ATM Withdrawal from IDBI Bank ==========
                    String IDBIATMWithdrawalAmount = extractIDBIATMWithdrawalAmount(message);  // Extract the amount (e.g., "10000")
                    String IDBIATMWithdrawalDate = extractIDBIATMWithdrawalDate(message);      // Extract and format the date (e.g., "15-06-24")

                    databaseReferences = FirebaseDatabase.getInstance().getReference("ExpenseData").child(uidString);

                    // Check if required fields are not empty
                    if (!IDBIATMWithdrawalAmount.isEmpty() && !IDBIATMWithdrawalDate.isEmpty()) {
                        String messageId = databaseReferences.push().getKey();
                        if (messageId != null) {

                            Data smsModels = new Data.Builder()
                                    .setId(messageId)
                                    .setAmount(Double.valueOf(IDBIATMWithdrawalAmount))
                                    .setDate(IDBIATMWithdrawalDate)
                                    .setNote("ATM Withdrawal")
                                    .setType("Bank")
                                    .build();

                            try {
                                databaseReferences.child(messageId).setValue(smsModels);
                                // Log extracted values for ATM withdrawal
                                Log.d("IDBIATMWithdrawalAmount", "Extracted IDBI ATM Withdrawal Amount: " + IDBIATMWithdrawalAmount);
                                Log.d("IDBIATMWithdrawalDate", "Extracted IDBI ATM Withdrawal Date: " + IDBIATMWithdrawalDate);
                                // log for database status
                                Log.d("DatabaseStatus", "ATM withdrawal data successfully stored.");
                            } catch (Exception e) {
                                Log.e("DatabaseError", "Error storing ATM withdrawal data: " + e.getMessage());
                            }
                        }
                    } else {
                        Log.d("Error", "ATM withdrawal data missing for IDBI Bank necessary fields.");
                    }
                }
                // Cheque Withdrawal
                else if (message.toLowerCase().contains("debited")
                        && message.toLowerCase().contains("chq no")) {
                    // ========== Extract specific information for Cheque Withdrawal from IDBI Bank ==========
                    String IDBIChqWithdrawalAmount = extractIDBIChqWithdrawalAmount(message);  // Extract the amount (e.g., "6000.00")
                    String IDBIChqWithdrawalDate = extractIDBIChqWithdrawalDate(message);      // Extract and format the date (e.g., "15-07-24")
                    String IDBIChqWithdrawalChequeNumber = extractIDBIChqWithdrawalChequeNumber(message); // Extract the cheque number

                    databaseReferences = FirebaseDatabase.getInstance().getReference("ExpenseData").child(uidString);

                    // Check if required fields are not empty
                    if (!IDBIChqWithdrawalAmount.isEmpty() && !IDBIChqWithdrawalDate.isEmpty() && !IDBIChqWithdrawalChequeNumber.isEmpty()) {
                        String messageId = databaseReferences.push().getKey();
                        if (messageId != null) {
                            Data smsModels = new Data.Builder()
                                    .setId(messageId)
                                    .setAmount(Double.valueOf(IDBIChqWithdrawalAmount))
                                    .setDate(IDBIChqWithdrawalDate)
                                    .setNote(IDBIChqWithdrawalChequeNumber) // Include cheque number in note
                                    .setType("Bank")
                                    .build();

                            try {
                                databaseReferences.child(messageId).setValue(smsModels);
                                // Log extracted values for Cheque withdrawal
                                Log.d("IDBIChqWithdrawalAmount", "Extracted IDBI Cheque Withdrawal Amount: " + IDBIChqWithdrawalAmount);
                                Log.d("IDBIChqWithdrawalDate", "Extracted IDBI Cheque Withdrawal Date: " + IDBIChqWithdrawalDate);
                                Log.d("IDBIChqWithdrawalChequeNumber", "Extracted Cheque Number: " + IDBIChqWithdrawalChequeNumber);
                                // log for database status
                                Log.d("DatabaseStatus", "Cheque withdrawal data successfully stored.");
                            } catch (Exception e) {
                                Log.e("DatabaseError", "Error storing Cheque withdrawal data: " + e.getMessage());
                            }
                        }
                    } else {
                        Log.d("Error", "Cheque withdrawal data missing for IDBI Bank necessary fields.");
                    }
                }
                // cash deposit
                else if (message.toLowerCase().contains("det:")
                        && message.toLowerCase().contains("cash receipt. bal")
                            && message.toLowerCase().contains("(incl. of chq in clg)")) {
                    // ========== Extract specific information for Cash Deposit from IDBI Bank ==========
                    String IDBICashDepositAmount = extractIDBICreditedAmount(message);  // Extract the amount (e.g., "10000.00")
                    String IDBICashDepositDate = extractIDBICashDepositDate(message);      // Extract and format the date (e.g., "18JUL")

                    databaseReferences = FirebaseDatabase.getInstance().getReference("IncomeData").child(uidString); // Assuming you're storing cash deposits under IncomeData

                    // Check if required fields are not empty
                    if (!IDBICashDepositAmount.isEmpty() && !IDBICashDepositDate.isEmpty()) {
                        String messageId = databaseReferences.push().getKey();
                        if (messageId != null) {
                            Data smsModels = new Data.Builder()
                                    .setId(messageId)
                                    .setAmount(Double.valueOf(IDBICashDepositAmount))
                                    .setDate(IDBICashDepositDate)
                                    .setNote("Cash Deposit") // Add a note for the deposit
                                    .setType("Bank")
                                    .build();

                            try {
                                databaseReferences.child(messageId).setValue(smsModels);
                                // Log extracted values for Cash deposit
                                Log.d("IDBICashDepositAmount", "Extracted IDBI Cash Deposit Amount: " + IDBICashDepositAmount);
                                Log.d("IDBICashDepositDate", "Extracted IDBI Cash Deposit Date: " + IDBICashDepositDate);
                                // log for database status
                                Log.d("DatabaseStatus", "Cash deposit data successfully stored.");
                            } catch (Exception e) {
                                Log.e("DatabaseError", "Error storing Cash deposit data: " + e.getMessage());
                            }
                        }
                    } else {
                        Log.d("Error", "Cash deposit data missing for IDBI Bank necessary fields.");
                    }
                }
            }
            // Kotak Bank all transaction
            // HDFC Bank All Transaction
            if (message.toLowerCase().contains("kotak mahindra bank")) {
                // cash Deposit
                if (message.toLowerCase().contains("deposited")) {

                    // Extract amount and date from the message
                    String KotakDepAmount = extractKotakCashDepositAmount(message);
                    String KotakDepDate = extractKotakCashDepositDate(message);
                    String KotakDepNote = "Cash Deposit";  // Add any additional note if necessary

                    databaseReferences = FirebaseDatabase.getInstance().getReference("IncomeData").child(uidString);

                    if (!KotakDepAmount.isEmpty() && !KotakDepDate.isEmpty()) {
                        String messageId = databaseReferences.push().getKey();
                        if (messageId != null) {
                            Data smsModels = new Data.Builder()
                                    .setId(messageId)
                                    .setAmount(Double.valueOf(KotakDepAmount))
                                    .setDate(KotakDepDate)
                                    .setNote(KotakDepNote)
                                    .setType("Bank")
                                    .build();

                            try {
                                databaseReferences.child(messageId).setValue(smsModels);
                                // Log extracted values for cash deposit
                                Log.d("KotakAmount", "Extracted Kotak Cash Deposit Amount: " + KotakDepAmount);
                                Log.d("KotakDate", "Extracted Kotak Cash Deposit Date: " + KotakDepDate);
                                Log.d("KotakNote", "Note: " + KotakDepNote);

                                // log for database status
                                Log.d("DatabaseStatus", "Income data successfully stored.");
                            } catch (Exception e) {
                                Log.e("DatabaseError", "Error storing income data: " + e.getMessage());
                            }
                        }
                    } else {
                        Log.d("Error", "Cash deposit data missing for Kotak Bank necessary fields.");
                    }
                }
                // Chq withdraw
                else if (message.toLowerCase().contains("through cheque")) {

                    // ================= Extract specific information for Card Withdrawal HDFC Bank =================
                    // Extract amount and date from the message
                    String KotakChqAmount = extractKotakCashDepositAmount(message);
                    String KotakChqDate = extractKotakCashDepositDate(message);
                    String KotakChqNote = "Chq Withdrawn";  // Add any additional note if necessary


                    databaseReferences = FirebaseDatabase.getInstance().getReference("ExpenseData").child(uidString);

                    // Check if required fields are not empty
                    if (!KotakChqAmount.isEmpty() && !KotakChqDate.isEmpty()) {
                        String messageId = databaseReferences.push().getKey();
                        if (messageId != null) {

                            Data smsModels = new Data.Builder()
                                    .setId(messageId)
                                    .setAmount(Double.valueOf(KotakChqAmount))
                                    .setDate(KotakChqDate)
                                    .setNote(KotakChqNote)
                                    .setType("Bank")
                                    .build();

                            try {
                                databaseReferences.child(messageId).setValue(smsModels);
                                // Log extracted values for cash deposit
                                Log.d("KotakAmount", "Extracted Kotak Chq Withdrawn Amount: " + KotakChqAmount);
                                Log.d("KotakDate", "Extracted Kotak Chq Withdrawn Date: " + KotakChqDate);
                                Log.d("KotakNote", "Note: " + KotakChqNote);
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
                // cash withdraw
                else if (message.toLowerCase().contains("withdrawn")) {

                    // ================= Extract specific information for Card Withdrawal HDFC Bank =================
                    // Extract amount and date from the message
                    String KotakWithAmount = extractKotakCashDepositAmount(message);
                    String KotakWithDate = extractKotakCashDepositDate(message);
                    String KotakWithNote = "Cash Withdrawn";  // Add any additional note if necessary


                    databaseReferences = FirebaseDatabase.getInstance().getReference("ExpenseData").child(uidString);

                    // Check if required fields are not empty
                    if (!KotakWithAmount.isEmpty() && !KotakWithDate.isEmpty()) {
                        String messageId = databaseReferences.push().getKey();
                        if (messageId != null) {

                            Data smsModels = new Data.Builder()
                                    .setId(messageId)
                                    .setAmount(Double.valueOf(KotakWithAmount))
                                    .setDate(KotakWithDate)
                                    .setNote(KotakWithNote)
                                    .setType("Bank")
                                    .build();

                            try {
                                databaseReferences.child(messageId).setValue(smsModels);
                                // Log extracted values for cash deposit
                                Log.d("KotakAmount", "Extracted Kotak Cash Withdrawn Amount: " + KotakWithAmount);
                                Log.d("KotakDate", "Extracted Kotak Cash Withdrawn Date: " + KotakWithDate);
                                Log.d("KotakNote", "Note: " + KotakWithNote);
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
                // Chq Deposit
                else if (message.toLowerCase().contains("cheque deposit")) {

                    // Extract amount and date from the message
                    String KotakChqAmount = extractKotakCashDepositAmount(message);
                    String KotakChqDate = extractKotakCashDepositDate(message);
                    String KotakChqNote = "Chq Deposit";  // Add any additional note if necessary

                    databaseReferences = FirebaseDatabase.getInstance().getReference("IncomeData").child(uidString);

                    if (!KotakChqAmount.isEmpty() && !KotakChqDate.isEmpty()) {
                        String messageId = databaseReferences.push().getKey();
                        if (messageId != null) {
                            Data smsModels = new Data.Builder()
                                    .setId(messageId)
                                    .setAmount(Double.valueOf(KotakChqAmount))
                                    .setDate(KotakChqDate)
                                    .setNote(KotakChqNote)
                                    .setType("Bank")
                                    .build();

                            try {
                                databaseReferences.child(messageId).setValue(smsModels);
                                // Log extracted values for cash deposit
                                Log.d("KotakAmount", "Extracted Kotak Chq Deposit Amount: " + KotakChqAmount);
                                Log.d("KotakDate", "Extracted Kotak Chq Deposit Date: " + KotakChqDate);
                                Log.d("KotakNote", "Note: " + KotakChqNote);

                                // log for database status
                                Log.d("DatabaseStatus", "Income data successfully stored.");
                            } catch (Exception e) {
                                Log.e("DatabaseError", "Error storing income data: " + e.getMessage());
                            }
                        }
                    } else {
                        Log.d("Error", "Cash deposit data missing for Kotak Bank necessary fields.");
                    }
                }

            }
        }
    }


    // date format string
    private String formatDateString(String date, String fromFormat, String toFormat) {
        SimpleDateFormat inputFormat = new SimpleDateFormat(fromFormat);
        SimpleDateFormat outputFormat = new SimpleDateFormat(toFormat);
        try {
            return outputFormat.format(inputFormat.parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }


    private String formatDateStrings(String date) {
        // Validate the input length
        if (date == null || date.length() != 5) {
            return "Invalid date"; // Return an error message if invalid
        }

        // Extract the day and month abbreviation from the input date
        String day = date.substring(0, 2); // Extract "30"
        String monthAbbreviation = date.substring(2, 5).toUpperCase(); // Extract "SEP"

        // Create a mapping of month abbreviations to their respective numbers
        Map<String, String> monthMap = new HashMap<>();
        monthMap.put("JAN", "01");
        monthMap.put("FEB", "02");
        monthMap.put("MAR", "03");
        monthMap.put("APR", "04");
        monthMap.put("MAY", "05");
        monthMap.put("JUN", "06");
        monthMap.put("JUL", "07");
        monthMap.put("AUG", "08");
        monthMap.put("SEP", "09");
        monthMap.put("OCT", "10");
        monthMap.put("NOV", "11");
        monthMap.put("DEC", "12");

        // Get the corresponding month number from the map
        String monthNumber = monthMap.get(monthAbbreviation);

        // Check if the month number was found
        if (monthNumber == null) {
            return "Invalid month"; // Return an error message for invalid month
        }

        // Return the formatted date
        return day + "-" + monthNumber; // Returns "30-09"
    }


    private String formatDateStringWithCurrentYear(String date) {
        // Get the current year as a two-digit string
        SimpleDateFormat yearFormat = new SimpleDateFormat("yy");
        String currentYear = yearFormat.format(new Date());

        // Append the current year to the date string
        return date + "-" + currentYear;
    }



    // ========================= Hdfc bank income Expense all regex  =========================
    // cash deposit
    private String extractHDFCIncomeAmount(String message) {
        Pattern pattern = Pattern.compile("INR\\s?(\\d+,?\\d+\\.\\d{2})");
        Matcher matcher = pattern.matcher(message);
        if (matcher.find()) {
            String amount = matcher.group(1);
            // Remove commas and return only the numeric value before the decimal
            return amount.replace(",", "").split("\\.")[0];
        }
        return "";
    }

    private String extractHDFCIncomeDate(String message) {
        String date = "";
        Pattern pattern = Pattern.compile("on\\s*(\\d{2}/\\d{2}/\\d{2})");
        Matcher matcher = pattern.matcher(message);
        if (matcher.find()) {
            date = matcher.group(1);
            // Format the extracted date from dd/MM/yy to dd-MM-yy
            return formatDateString(date, "dd/MM/yy", "dd-MM-yy");
        }
        return date;
    }
    //cash withdrawn from card
    private String extractHDFCCardWithdrawAmount(String message) {
        Pattern pattern = Pattern.compile("Rs\\.?(\\d+)");
        Matcher matcher = pattern.matcher(message);
        if (matcher.find()) {
            return matcher.group(1);  // Returns the amount without commas (e.g., "8000")
        }
        return "";
    }

    private String extractHDFCCardWithdrawDate(String message) {
        Pattern pattern = Pattern.compile("on\\s*(\\d{4}-\\d{2}-\\d{2})");
        Matcher matcher = pattern.matcher(message);
        if (matcher.find()) {
            String date = matcher.group(1);  // Extracts the date (e.g., "2024-07-07")
            // Format the extracted date from yyyy-MM-dd to dd-MM-yy
            return formatDateString(date, "yyyy-MM-dd", "dd-MM-yy");
        }
        return "";
    }
    //chq payment
    private String extractHDFCChqAmount(String message) {
        Pattern pattern = Pattern.compile("INR\\s?(\\d+,?\\d+\\.\\d{2})");
        Matcher matcher = pattern.matcher(message);
        if (matcher.find()) {
            String amount = matcher.group(1);
            // Remove commas and return the numeric value
            return amount.replace(",", "");
        }
        return "";
    }

    private String extractHDFCChqDate(String message) {
        Pattern pattern = Pattern.compile("on\\s*(\\d{2}-\\w{3}-\\d{2})");
        Matcher matcher = pattern.matcher(message);
        if (matcher.find()) {
            String date = matcher.group(1);  // Extracts the date (e.g., "24-JUL-24")
            // Format the extracted date from dd-MMM-yy to dd-MM-yy
            return formatDateString(date, "dd-MMM-yy", "dd-MM-yy");
        }
        return "";
    }

    private String extractHDFCChqNumber(String message) {
        Pattern pattern = Pattern.compile("Chq#\\s?(\\d+)");
        Matcher matcher = pattern.matcher(message);
        if (matcher.find()) {
            return matcher.group(1);  // Extracts the cheque number (e.g., "000030")
        }
        return "";
    }
    //chq deposit
    private String extractHDFCChqDepositAmount(String message) {
        Pattern pattern = Pattern.compile("INR\\s?(\\d+,?\\d+\\.\\d{2})");
        Matcher matcher = pattern.matcher(message);
        if (matcher.find()) {
            String amount = matcher.group(1);
            // Remove commas and return the numeric value
            return amount.replace(",", "");
        }
        return "";
    }

    private String extractHDFCChqDepositDate(String message) {
        Pattern pattern = Pattern.compile("on\\s*(\\d{2}-\\w{3}-\\d{2})");
        Matcher matcher = pattern.matcher(message);
        if (matcher.find()) {
            String date = matcher.group(1);  // Extracts the date (e.g., "17-JUN-24")
            // Format the extracted date from dd-MMM-yy to dd-MM-yy
            return formatDateString(date, "dd-MMM-yy", "dd-MM-yy");
        }
        return "";
    }

    // ========================= IDBI bank income Expense all regex  =========================
    //atm withdrawal
    private String extractIDBIATMWithdrawalAmount(String message) {
        Pattern pattern = Pattern.compile("INR\\s?(\\d+,?\\d+)");
        Matcher matcher = pattern.matcher(message);
        if (matcher.find()) {
            String amount = matcher.group(1);
            // Remove commas and return the numeric value
            return amount.replace(",", "");
        }
        return "";
    }

    private String extractIDBIATMWithdrawalDate(String message) {
        Pattern pattern = Pattern.compile("as of\\s*(\\d{2}\\w{3})");  // Extract date like "30SEP"
        Matcher matcher = pattern.matcher(message);
        if (matcher.find()) {
            String date = matcher.group(1);  // Extracts the date (e.g., "30SEP")
            // Format the extracted date from ddMMM to dd-MM-yy, assuming the current year
            String dates = formatDateStrings(date);

            return formatDateStringWithCurrentYear(dates);
        }
        return "";
    }
    // chq withdrawal
    private String extractIDBIChqWithdrawalAmount(String message) {
        Pattern pattern = Pattern.compile("INR\\.\\s?(\\d{1,3}(?:,\\d{3})*(?:\\.\\d{2})?)"); // Matches the amount (e.g., "6000.00")
        Matcher matcher = pattern.matcher(message);
        if (matcher.find()) {
            String amount = matcher.group(1);
            // Remove commas and return the numeric value
            return amount.replace(",", "");
        }
        return "";
    }
    private String extractIDBIChqWithdrawalDate(String message) {
        Pattern pattern = Pattern.compile("as of\\s*(\\d{2}\\w{3})"); // Extract date like "15JUL"
        Matcher matcher = pattern.matcher(message);
        if (matcher.find()) {
            String date = matcher.group(1); // Extracts the date (e.g., "15JUL")
            // Format the extracted date from ddMMM to dd-MM-yy, assuming the current year
            String dates = formatDateStrings(date);

            return formatDateStringWithCurrentYear(dates);
        }
        return "";
    }
    private String extractIDBIChqWithdrawalChequeNumber(String message) {
        Pattern pattern = Pattern.compile("Det:([^\\.]+)"); // Extract cheque number after "Det:" up to the first period
        Matcher matcher = pattern.matcher(message);
        if (matcher.find()) {
            return matcher.group(1).trim(); // Return the cheque number
        }
        return "";
    }
    //cash deposit
    private String extractIDBICreditedAmount(String message) {
        // Regex to find the amount formatted after 'INR'
        Pattern pattern = Pattern.compile("INR\\.?(\\s?\\d+(?:,\\d{3})*(?:\\.\\d{2}))");
        Matcher matcher = pattern.matcher(message);
        if (matcher.find()) {
            return matcher.group(1).trim();  // Returns the amount including decimals (e.g., "10000.00")
        }
        return "";
    }



    private String extractIDBICashDepositDate(String message) {
        Pattern pattern = Pattern.compile("as of\\s*(\\d{2}\\w{3})"); // Extract date like "18JUL"
        Matcher matcher = pattern.matcher(message);
        if (matcher.find()) {
            String date = matcher.group(1);
            // Format the extracted date from ddMMM to dd-MM-yy, assuming the current year
            String dates = formatDateStrings(date);

            return formatDateStringWithCurrentYear(dates);
        }
        return "";
    }
    // ========================= Kotak bank income Expense all regex  =========================
    // cash deposit
    private String extractKotakCashDepositAmount(String message) {
        Pattern pattern = Pattern.compile("INR\\s?(\\d{1,3}(?:,\\d{2,3})*)");
        Matcher matcher = pattern.matcher(message);
        if (matcher.find()) {
            String amount = matcher.group(1);
            // Remove commas and return the amount without commas
            return amount.replace(",", "");
        }
        return "";
    }



    private String extractKotakCashDepositDate(String message) {
        Pattern pattern = Pattern.compile("on\\s*(\\d{2}/\\d{2}/\\d{4})");
        Matcher matcher = pattern.matcher(message);
        if (matcher.find()) {
            String date = matcher.group(1);
            // Format the extracted date from dd/MM/yyyy to dd-MM-yy
            return formatDateString(date, "dd/MM/yyyy", "dd-MM-yy");
        }
        return "";
    }



}
