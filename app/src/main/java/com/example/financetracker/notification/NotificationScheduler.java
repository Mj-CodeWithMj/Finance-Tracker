package com.example.financetracker.notification;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Calendar;

public class NotificationScheduler {

    public static void scheduleDailyNotifications(Context context, String income, String expense, String balance) {
        // Schedule for 1:10 PM
        scheduleNotification(context, 13, 10, 1, income, expense, balance);

        // Schedule for 2:30 PM
        scheduleNotification(context, 14, 30, 2, income, expense, balance);

        // Schedule for 9:30 PM
        scheduleNotification(context, 21, 30, 3, income, expense, balance);
    }

    private static void scheduleNotification(Context context, int hour, int minute, int requestCode, String income, String expense, String balance) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, NotificationReceiver.class);

        // Pass the income, expense, and balance from Firebase
        intent.putExtra("income", income);
        intent.putExtra("expense", expense);
        intent.putExtra("balance", balance);
        intent.putExtra("type", "daily");

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        // Set the time for the notification
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        // If the time is in the past, move to the next day
        if (calendar.getTimeInMillis() < System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }

        // Schedule a repeating alarm
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);

        Log.d("NotificationScheduler", "Scheduled notification for " + hour + ":" + minute);
    }

    public static void cancelDailyNotifications(Context context) {
        Intent intent = new Intent(context, NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_NO_CREATE | PendingIntent.FLAG_IMMUTABLE);

        if (pendingIntent != null) {
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            if (alarmManager != null) {
                alarmManager.cancel(pendingIntent);
            }
            pendingIntent.cancel(); // Clean up the pending intent
        }
    }
}
