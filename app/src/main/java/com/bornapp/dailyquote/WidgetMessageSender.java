package com.bornapp.dailyquote;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Mehdi on 11/24/2015.
 */
public class WidgetMessageSender {

    public static void Broadcast(String message) {
        Context context = App.getContext();
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        ComponentName thisAppWidget = new ComponentName(context.getPackageName(), WidgetMessageReceiver.class.getName());
        Intent updateIntent = new Intent(context, WidgetMessageReceiver.class);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisAppWidget);
        updateIntent.setAction(message);
        updateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
        context.sendBroadcast(updateIntent);
    }
}
