package com.bornapp.dailyquote;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.Toast;

public class DQAppWidgetProvider extends AppWidgetProvider{

    //region Provider Methods
    @Override
    public void onUpdate(Context _context, AppWidgetManager _appWidgetManager, int[] _appWidgetIds) {

        //1st time initialization
        if(!App.get().sequence.IsInitialized()) {
            App.get().sequence.Init();
        }

        for (int currentWidgetId : _appWidgetIds) {
            //access remote view
            RemoteViews views = new RemoteViews(_context.getPackageName(), R.layout.dqwidgetlayout);
            Intent intent = new Intent(_context, DQAppWidgetConfigure.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, currentWidgetId);
            PendingIntent pendingIntent = PendingIntent.getActivity(_context, 0,
                    intent, PendingIntent.FLAG_UPDATE_CURRENT);
            views.setOnClickPendingIntent(R.id.textView, pendingIntent);
            //update Text
            views.setTextViewText(R.id.textView, App.get().sequence.CurrentString());
            //Update Alarm state and rate
            App.get().alarm.UpdateAlarm();
            //Update AppWidget
            _appWidgetManager.updateAppWidget(currentWidgetId, views);
        }
    }

    @Override
    public void onDisabled(Context _context) {
        App.get().alarm.RequestStop();
        App.get().alarm.UpdateAlarm();
        App.get().stopService(new Intent(App.get(), ClockService.class));
        super.onDisabled(_context);
    }

    @Override
    public void onDeleted(Context _context, int[] _appWidgetIds) {
        super.onDeleted(_context, _appWidgetIds);
    }

    @Override
    public void onReceive( Context  _context, Intent _intent) {

        if(_intent.getAction().equals("com.bornapp.appwidget.action.APPWIDGET_PAUSE")) {
            App.get().alarm.RequestStop();
            App.get().alarm.UpdateAlarm();
        }
        else if(_intent.getAction().equals("android.appwidget.action.APPWIDGET_UPDATE")) {
            App.get().alarm.RequestStart();
        }
        super.onReceive(_context, _intent);
    }
    //endregion
}
