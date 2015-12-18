package com.bornapp.dailyquote;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.SystemClock;

public class Timer {

    private static Timer instance = null;
    private boolean isTimerRunning = false;
    private int elapsedMinutes = 0;

    //ctor
    private Timer() {
        instance = this;
    }

    //public methods
    public static boolean isInitialized() {
        return (instance != null);
    }

    public static void init() {
        // Singleton private instantiation
        new Timer();
    }

    public static void run() {
        run(true);
    }

    public static void pause() {
        run(false);
    }

    public static void stop() {
        Context context = App.getContext();
        context.stopService(new Intent(context, UpdateService.class));
    }

    public static void Update() {
        instance.elapsedMinutes++;
    }

    public static int getElapsedMinutes() {
        return instance.elapsedMinutes;
    }

    public static void reset() {
        instance.elapsedMinutes = 0;
    }

    //private methods
    private static PendingIntent makeControlPendingIntent(String command, int appWidgetId) {
        Context context = App.getContext();
        Intent intent = new Intent(context, UpdateService.class);
        intent.setAction(command);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        // The Uri data is to make the PendingIntent unique
        Uri uri = Uri.withAppendedPath(Uri.parse("bornaapAppWidget://widget/id/#" + command + appWidgetId),
                String.valueOf(appWidgetId));
        intent.setData(uri);
        return (PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT));
    }

    private static void run(boolean status) {

        AlarmManager alarms = (AlarmManager) App.getContext().getSystemService(Context.ALARM_SERVICE);

        //sets a repeating alarm on every minute
        if (status) {
            if (instance.isTimerRunning)
                return;
            int interval = 60000;
            // As we just want to have one alarm in app,
            // we do not send appWidgetId for each appWidget instance
            PendingIntent pendingIntent = makeControlPendingIntent("update", 1);
            alarms.setRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime(), interval, pendingIntent);

            instance.isTimerRunning = true;
        }
        //stop alarm
        else {
            if (!instance.isTimerRunning)
                return;
            //As we just want to have one alarm in app,
            // we do not send appWidgetId for each appWidget instance
            PendingIntent pendingIntent = makeControlPendingIntent("update", 1);
            alarms.cancel(pendingIntent);

            instance.isTimerRunning = false;
        }
    }
}
