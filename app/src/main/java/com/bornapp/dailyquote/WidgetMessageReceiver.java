package com.bornapp.dailyquote;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

public class WidgetMessageReceiver extends AppWidgetProvider {

    @Override
    public void onReceive(Context context, Intent intent) {

        //1st time initialization
        if (!RandomString.isInitialized())
            RandomString.init();
        if (!Timer.isInitialized()) {
            Timer.init();
            Timer.run();
        }

        // processing arriving intents
        if (intent.getAction().equals("com.bornaapp.appwidget.action.APPWIDGET_TIMER_TICK")) {

            if (Timer.getElapsedMinutes() >= getWidgetUpdateRateFromPrefs()) {
                RandomString.next();
                Timer.reset();
            }
            Timer.Update();
            Timer.run();
            WidgetMessageSender.Broadcast("android.appwidget.action.APPWIDGET_UPDATE");

        } else if (intent.getAction().equals("com.bornaapp.appwidget.action.ACTIVITY_OPENED")) {

            Timer.pause();

        } else if (intent.getAction().equals("com.bornaapp.appwidget.action.ACTIVITY_CONFIGURED")) {

            Timer.pause();
            Timer.reset();

        } else if (intent.getAction().equals("com.bornaapp.appwidget.action.ACTIVITY_CLOSED")) {

            WidgetMessageSender.Broadcast("android.appwidget.action.APPWIDGET_UPDATE");
            Timer.run();
        }

        //running onUpdate(), onDisabled() & ... operations
        super.onReceive(context, intent);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        UpdateWidget(context, appWidgetManager, appWidgetIds);
    }

    @Override
    public void onDisabled(Context context) {
        Timer.pause();
        Timer.stop();
        super.onDisabled(context);
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
    }

    //private methods
    private int getWidgetUpdateRateFromPrefs() {
        Context context = App.getContext();
        int widgetUpdateRate;
        try {
            widgetUpdateRate = SharedPrefs.LoadPref_Int(context, context.getString(R.string.txt_prefKey_Delay));
        } catch (Exception e) {
            widgetUpdateRate = -1;
        }
        if (widgetUpdateRate < 0) {
            widgetUpdateRate = context.getResources().getInteger(R.integer.int_UpdateFactor_Low);
        }
        return widgetUpdateRate;
    }

    private void UpdateWidget(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        //query each appWidget
        for (int widgetId : appWidgetIds) {
            //access remote view
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widgetlayout);
            Intent intent = new Intent(context, Configure.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
                    intent, PendingIntent.FLAG_UPDATE_CURRENT);
            views.setOnClickPendingIntent(R.id.textView, pendingIntent);
            //update Text fields
            String[] separated = RandomString.current().split("::");
            views.setTextViewText(R.id.textView, separated[0]);
            //Update AppWidget
            appWidgetManager.updateAppWidget(widgetId, views);
        }
    }
}
