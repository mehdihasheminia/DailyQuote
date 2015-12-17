package com.bornapp.dailyquote;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.ClipData;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import android.net.Uri;

public class DQAppWidgetConfigure extends Activity {

    //region Activity methods
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Find the widget id from the intent.
        // And stops activity from running if there is no valid calling widget
        int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt( AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }
        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
        }

        // Set our layout for the Configure Activity
        setContentView(R.layout.dqconfigurelayout);

        //Update configuration UI
        UpdateText( App.get().sequence.CurrentString() );

        // Bind the action for the buttons.
        findViewById(R.id.nextBtn).setOnClickListener(mOnClickNextBtn);
        findViewById(R.id.PreviousBtn).setOnClickListener(mOnClickPrevBtn);
        findViewById(R.id.copyBtn).setOnClickListener(mOnClickCopyBtn);
        findViewById(R.id.shareBtn).setOnClickListener(mOnClickShareBtn);
        findViewById(R.id.supportBtn).setOnClickListener(mOnClickSupportBtn);

        // Bind the action for the SeekBar.
        SeekBar tmpSeekBar1 = (SeekBar)findViewById(R.id.rateSeekBar);
        tmpSeekBar1.setOnSeekBarChangeListener(mOnRateSeekbarChanged);
        UpdateSeekBar();

        //deactivate appWidget
        BroadcastPauseWidget();
    }

    @Override
    public void onDestroy(){
        BroadcastResumeWidget();
        super.onDestroy();
    }

    @Override
    public void onPause(){
        super.onPause();
    }

    @Override
    public void onStop(){
        BroadcastResumeWidget();
        super.onStop();
    }
    //endregion

    //region Broadcasting methods
    private void BroadcastMessage(String _message){
        Context context = DQAppWidgetConfigure.this;
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        ComponentName thisAppWidget = new ComponentName(context.getPackageName(), DQAppWidgetProvider.class.getName());
        Intent updateIntent = new Intent(context, DQAppWidgetProvider.class);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisAppWidget);
        updateIntent.setAction(_message);
        updateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
        context.sendBroadcast(updateIntent);
    }

    private void BroadcastPauseWidget()
    {
        BroadcastMessage("com.bornapp.appwidget.action.APPWIDGET_PAUSE");
    }

    private void BroadcastResumeWidget()
    {
        BroadcastMessage("android.appwidget.action.APPWIDGET_UPDATE");
    }
    //endregion

    //region User Interface
    private void UpdateText(String txt)
    {
        TextView tmpTxtView = (TextView)findViewById(R.id.quoteTxtView2);
        tmpTxtView.setText(txt);
    }

    View.OnClickListener mOnClickNextBtn = new View.OnClickListener() {
        public void onClick(View v) {
            UpdateText(App.get().sequence.NextString());
        }
    };

    View.OnClickListener mOnClickPrevBtn = new View.OnClickListener() {
        public void onClick(View v) {
           UpdateText(App.get().sequence.PreviousString());
        }
    };

    View.OnClickListener mOnClickShareBtn = new View.OnClickListener() {
        public void onClick(View v) {
           Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
           sharingIntent.setType("text/plain");
           String shareBody = App.get().sequence.CurrentString();
           sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, R.string.app_name);
           sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
           startActivity(Intent.createChooser(sharingIntent, "Share via"));
        }
    };

    View.OnClickListener mOnClickCopyBtn = new View.OnClickListener() {
        public void onClick(View v) {

            //Copy text to clipboard
            String txt = App.get().sequence.CurrentString();
            if(txt.length() > 0) {
                if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB) {
                    android.text.ClipboardManager clipboardMgr = (android.text.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    clipboardMgr.setText(txt);
                } else {
                    // this api requires SDK version 11 and above, so suppress warning for now
                    android.content.ClipboardManager clipboardMgr = (android.content.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("Copied text", txt);
                    clipboardMgr.setPrimaryClip(clip);
                }}

            //Let user know
            Context context = DQAppWidgetConfigure.this;
            Toast.makeText(context, context.getString(R.string.txt_copy_message), Toast.LENGTH_SHORT).show();
        }
    };

    View.OnClickListener mOnClickSupportBtn = new View.OnClickListener() {
        public void onClick(View v) {
            Context context = DQAppWidgetConfigure.this;
            //Prepare Email
            Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
            emailIntent.setData(Uri.parse("mailto:"));
            emailIntent.setType("text/plain");
            String toList[] = { context.getString(R.string.txt_email_Address) };
            emailIntent.putExtra(Intent.EXTRA_EMAIL, toList);
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.txt_email_subject));
            emailIntent.putExtra(Intent.EXTRA_TEXT, context.getString(R.string.txt_email_message));
            //Send Email
            try {
                startActivity(Intent.createChooser(emailIntent, context.getString(R.string.txt_email_chooser)));
                finish();
            }
            catch (android.content.ActivityNotFoundException ex) {
                Toast.makeText(context,context.getString(R.string.txt_email_error), Toast.LENGTH_SHORT).show();
            }
        }
    };

    OnSeekBarChangeListener mOnRateSeekbarChanged = new OnSeekBarChangeListener() {
        int seekbarProgress = 0;
        @Override
        public void onProgressChanged(SeekBar seekBar, int progresValue, boolean fromUser) {
            seekbarProgress = progresValue;
            UpdateSeekBarText(seekbarProgress);
        }
        @Override
        public void onStartTrackingTouch(SeekBar seekBar) { }
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            SaveSeekBarValue(seekbarProgress);
            UpdateSeekBar();
        }
    };

    private void UpdateSeekBarText(int value)
    {
        Context context = DQAppWidgetConfigure.this;
        String txt;

        if(value <35)
            txt = context.getResources().getString(R.string.txt_UpdateRate_Low);
        else if(value <67)
            txt = context.getResources().getString(R.string.txt_UpdateRate_Med);
        else
            txt = context.getResources().getString(R.string.txt_UpdateRate_Hi);

        TextView tmpTxtView = (TextView)findViewById(R.id.rateValueTxtView);
        tmpTxtView.setText(txt);
    }

    private void SaveSeekBarValue(int value)
    {
        Context context = DQAppWidgetConfigure.this;
        int updateRate;

        if(value <35)
            updateRate = context.getResources().getInteger(R.integer.int_UpdateRate_Low);
        else if(value <67)
            updateRate = context.getResources().getInteger(R.integer.int_UpdateRate_Med);
        else
            updateRate = context.getResources().getInteger(R.integer.int_UpdateRate_Hi);

        App.get().alarm.RequestUpdateRate(updateRate);
    }

    private void UpdateSeekBar()
    {
        Context context = DQAppWidgetConfigure.this;
        int updateRate = App.get().alarm.Get_UpdateRate();
        SeekBar tmpSeekBar1 = (SeekBar)findViewById(R.id.rateSeekBar);

        if(updateRate == context.getResources().getInteger(R.integer.int_UpdateRate_Hi))
            tmpSeekBar1.setProgress(100);
        else if(updateRate == context.getResources().getInteger(R.integer.int_UpdateRate_Med))
            tmpSeekBar1.setProgress(50);
        else
            tmpSeekBar1.setProgress(0);
    }

    //endregion
}