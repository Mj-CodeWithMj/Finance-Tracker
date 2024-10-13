package com.example.financetracker.notification;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.example.financetracker.MainActivity; // Update to your main activity
import com.example.financetracker.R;

public class NotificationReceiver extends BroadcastReceiver {

    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    @Override
    public void onReceive(Context context, Intent intent) {
        String income = intent.getStringExtra("income");
        String expense = intent.getStringExtra("expense");
        String balance = intent.getStringExtra("balance");

        // Parse balance to double and round it to two decimal places
        if (balance != null) {
            double balanceValue = Double.parseDouble(balance);
            balanceValue = Math.round(balanceValue * 100.0) / 100.0;
            balance = String.format("%.2f", balanceValue);
        }


        createNotification(context, income, expense, balance);

    }

    private void createNotification(Context context, String income, String expense, String balance) {
        String channelId = "finance_tracker_notifications";
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, "Daily Notifications", NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }

        Intent notificationIntent = new Intent(context, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.notification) // Change to your icon
                .setContentTitle("Daily Reminder")
                .setContentText("Remember to log your daily income and expense! Current totals: ₹" + balance)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(contentIntent)
                .setAutoCancel(true);

        notificationManager.notify(1, builder.build());
        Log.d("NotificationReceiver", "Received notification intent");
    }
}
