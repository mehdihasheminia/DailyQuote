package com.bornapp.dailyquote;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;

public class SpinnerItemSelectListener implements OnItemSelectedListener {

    public void onItemSelected(AdapterView parent, View view, int pos, long id) {
        Context context = view.getContext();
        switch (pos) {
            case 0:
                SharedPrefs.SavePrefItem(context, context.getString(R.string.txt_prefKey_Delay), context.getResources().getInteger(R.integer.int_UpdateFactor_Low));
                break;
            case 1:
                SharedPrefs.SavePrefItem(context, context.getString(R.string.txt_prefKey_Delay), context.getResources().getInteger(R.integer.int_UpdateFactor_Med));
                break;
            case 2:
                SharedPrefs.SavePrefItem(context, context.getString(R.string.txt_prefKey_Delay), context.getResources().getInteger(R.integer.int_UpdateFactor_Hi));
                break;
            default:
                SharedPrefs.SavePrefItem(context, context.getString(R.string.txt_prefKey_Delay), context.getResources().getInteger(R.integer.int_UpdateFactor_Low));
        }
    }

    @Override
    public void onNothingSelected(AdapterView arg0) {
    }
}