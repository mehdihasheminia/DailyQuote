package com.bornapp.dailyquote;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

public class ClockService extends Service {

    //region Service methods
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        App.get().sequence.NextString();
        BroadcastUpdateWidget();
        //int passedID = intent.getExtras().getInt(AppWidgetManager.EXTRA_APPWIDGET_ID,-1);
        return START_STICKY;
    }

    @Override
    public void onDestroy() { }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    //endregion

    //region Broadcasting methods
    private void BroadcastUpdateWidget(){

        Context context = getApplicationContext();
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        ComponentName thisAppWidget = new ComponentName(context.getPackageName(), DQAppWidgetProvider.class.getName());
        Intent updateIntent = new Intent(context, DQAppWidgetProvider.class);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisAppWidget);
        updateIntent.setAction("android.appwidget.action.APPWIDGET_UPDATE");
        updateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
        context.sendBroadcast(updateIntent);
    }
    //endregion
}
