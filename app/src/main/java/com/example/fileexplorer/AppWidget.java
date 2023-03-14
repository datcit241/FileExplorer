package com.example.fileexplorer;

import android.app.ActivityManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.fileexplorer.Fragment.CategorizedFragment;
import com.example.fileexplorer.Fragment.HomeFragment;

/**
 * Implementation of App Widget functionality.
 */
public class AppWidget extends AppWidgetProvider {


    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

//        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
//        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
//        activityManager.getMemoryInfo(memoryInfo);
//
//        double totalMemory = (double) memoryInfo.totalMem;
//        double freeMemory = (double) memoryInfo.availMem;
//        double usedMemory = totalMemory - freeMemory;

        double usedMemory = getTotalInternalMemorySizeInGB() - getAvailableInternalMemorySizeInGB();
        int usedMemoryRounded = (int) Math.round(usedMemory * 100) / 100;
        double percentageUsed = usedMemory / getTotalInternalMemorySizeInGB() * 100.0;

        CharSequence widgetText = Math.round(percentageUsed*100)/100+"";

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.app_widget);
        views.setTextViewText(R.id.progress_text, widgetText);

        Intent intentImg1 = new Intent(context, AppWidget.class);
        intentImg1.setAction("ACTION_HOME");
        PendingIntent pendingIntentImg1 = PendingIntent.getBroadcast(context, 0, intentImg1, 0);

        Intent intentImg2 = new Intent(context, AppWidget.class);
        intentImg2.setAction("ACTION_STORAGE");
        PendingIntent pendingIntentImg2 = PendingIntent.getBroadcast(context, 0, intentImg2, 0);

        Intent intentImg4 = new Intent(context, AppWidget.class);
        intentImg4.setAction("ACTION_MEMORY");
        PendingIntent pendingIntentImg4 = PendingIntent.getBroadcast(context, 0, intentImg4, 0);

        views.setOnClickPendingIntent(R.id.img_Home, pendingIntentImg1);
        views.setOnClickPendingIntent(R.id.img_Storage, pendingIntentImg2);
        views.setOnClickPendingIntent(R.id.progress_text, pendingIntentImg4);

        views.setTextViewText(R.id.progress_text, widgetText);
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        String action = intent.getAction();

        if ("ACTION_HOME".equals(action)) {
            Intent myIntent = new Intent(context.getApplicationContext(), MainActivity.class);
            myIntent.putExtra("fragment_name", "HomeFragment");myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(myIntent);
        }

        else if ("ACTION_STORAGE".equals(action)) {
            Intent myIntent = new Intent(context.getApplicationContext(), MainActivity.class);
            myIntent.putExtra("fragment_name", "InternalCard");
            myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(myIntent);

        } else if ("ACTION_MEMORY".equals(action)) {
            Intent launchAppIntent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
            context.startActivity(launchAppIntent);
        }
    }

    public static float getAvailableInternalMemorySizeInGB() {
        StatFs statFs = new StatFs(Environment.getDataDirectory().getPath());
        long blockSize = statFs.getBlockSizeLong();
        long availableBlocks = statFs.getAvailableBlocksLong();
        long bytesAvailable = availableBlocks * blockSize;
        float gbAvailable = (float)bytesAvailable / 1073741824;
        return gbAvailable;
    }
    public static float getTotalInternalMemorySizeInGB() {
        StatFs statFs = new StatFs(Environment.getDataDirectory().getPath());
        long blockSize = statFs.getBlockSizeLong();
        long totalBlocks = statFs.getBlockCountLong();
        return ((float) (totalBlocks * blockSize) / 1073741824);
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}