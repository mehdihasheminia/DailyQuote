package com.bornapp.dailyquote;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class UpdateService extends Service {

    //region Service method
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        WidgetMessageSender.Broadcast("com.bornaapp.appwidget.action.APPWIDGET_TIMER_TICK");
        //int passedID = intent.getExtras().getInt(AppWidgetManager.EXTRA_APPWIDGET_ID,-1);

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    //endregion
}
