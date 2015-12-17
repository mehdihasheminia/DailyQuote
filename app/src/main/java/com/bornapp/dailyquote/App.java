package com.bornapp.dailyquote;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.SystemClock;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

class Sequence {
    private List<String> stringList;
    private int iteratorIndex;

    public boolean IsInitialized(){
        try
        {
            return !stringList.isEmpty();
        }
        catch(Exception e)
        {
            return false;
        }
    }

    public void Init() {
        //Retrieve strings from resources
        String[] resources = App.get().getResources().getStringArray(R.array.My_String_Array);
        //Initialize a random sequence of strings
        stringList = new ArrayList<>(Arrays.asList(resources));
        Collections.shuffle(stringList);
    }

    public String CurrentString() {
        if( !IsInitialized() )
           return "";

        return stringList.get(iteratorIndex);
    }

    public String NextString() {
        if( !IsInitialized() )
             return "";

        if( iteratorIndex < stringList.size()-1 )
            iteratorIndex++;
        else
            iteratorIndex = 0;

        return stringList.get(iteratorIndex);
    }

    public String PreviousString() {
        if( !IsInitialized() )
             return "";

        if( iteratorIndex > 0 )
            iteratorIndex--;
        else
            iteratorIndex = stringList.size()-1;

        return stringList.get(iteratorIndex);
    }
}

class Alarm {
    private int     alarmUpdateRate = 86400000;
    private boolean isAlarmStarted  = false;
    private boolean alarmSetRequest = false;

    public void UpdateAlarm() {

        AlarmManager alarms = (AlarmManager) App.get().getSystemService(Context.ALARM_SERVICE);

        if( alarmSetRequest ) {
            if (isAlarmStarted)
                return;
            //As we just want to have one alarm in app,
            // we do not send appWidgetId for each appWidget instance
            PendingIntent pending = makeControlPendingIntent("update", 1 );
            alarms.setRepeating(AlarmManager.ELAPSED_REALTIME,
                              SystemClock.elapsedRealtime(), alarmUpdateRate, pending);

            isAlarmStarted = true;
        }
        else
        {
            if(!isAlarmStarted )
                return;
            //As we just want to have one alarm in app,
            // we do not send appWidgetId for each appWidget instance
            PendingIntent pending = makeControlPendingIntent("update", 1);
            alarms.cancel(pending);

            isAlarmStarted = false;
        }
    }

    private PendingIntent makeControlPendingIntent( String command, int appWidgetId) {
        Intent intent = new Intent(App.get(), ClockService.class);
        intent.setAction(command);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        // The Uri data is to make the PendingIntent unique
        Uri uri = Uri.withAppendedPath( Uri.parse("bornapAppWidget://widget/id/#"+command+appWidgetId),
                                        String.valueOf(appWidgetId) );
        intent.setData(uri);
        return (PendingIntent.getService(App.get(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT));
    }

    public void RequestStart()
    {
        alarmSetRequest = true;
    }

    public void RequestStop()
    {
        alarmSetRequest = false;
    }

    public void RequestUpdateRate(int newUpdateRate) {
        int updateRate_Low = App.get().getResources().getInteger(R.integer.int_UpdateRate_Low);
        int updateRate_Med = App.get().getResources().getInteger(R.integer.int_UpdateRate_Med);
        int updateRate_hi = App.get().getResources().getInteger(R.integer.int_UpdateRate_Hi);

        if( newUpdateRate <= updateRate_hi)
            alarmUpdateRate = updateRate_hi;
        else if( newUpdateRate <= updateRate_Med)
            alarmUpdateRate = updateRate_Med;
        else
            alarmUpdateRate = updateRate_Low;
    }

    public int  Get_UpdateRate(){
        return alarmUpdateRate;
    }
}

class SharedPrefs {
    // Writes preference item to the SharedPreferences file for this widget
    //Like : SavePrefItem(context, mAppWidgetId, "UserName", Value1);
    public static void SavePrefItem(Context context, int appWidgetId, String key, String value) {
        String prefName = context.getString(R.string.txt_pref_name);
        String prefprefix = context.getString(R.string.txt_pref_prefix);
        SharedPreferences.Editor prefs = context.getSharedPreferences(prefName, 0).edit();
        prefs.putString(prefprefix + appWidgetId + key, value);
        prefs.commit();
    }

    // Read preference item from the SharedPreferences file for this widget.
    // If there is no preference saved, returns a default from a resource
    public static String LoadPrefItem(Context context, int appWidgetId, String key) {
        String prefName = context.getString(R.string.txt_pref_name);
        String prefprefix = context.getString(R.string.txt_pref_prefix);
        SharedPreferences prefs = context.getSharedPreferences(prefName, 0);
        String value = prefs.getString(prefprefix + appWidgetId + key, null);
        if (value != null) {
            return value;
        }
        else {
            return context.getString(R.string.txt_pref_Default);
        }
    }
}

public class App extends Application {

    private static App instance;
    public static App get() { return instance; }

    public  Sequence sequence;
    public  Alarm    alarm;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        if(sequence == null)
            sequence = new Sequence();
        if( alarm == null )
            alarm = new Alarm();
    }

}
