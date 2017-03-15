package com.tishcn.fimonitor.util;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.telephony.SignalStrength;

/**
 * Created by leona on 7/7/2016.
 */
public class MyPhoneStateListener extends PhoneStateListener {

    private Context mContext;

    public MyPhoneStateListener(Context context){
        mContext = context;
    }

    @Override
    public void onSignalStrengthsChanged(SignalStrength signalStrength){

        Intent intent = new Intent(Constants.NETWORK_SIGNAL_STRENGTH_CHANGE_INTENT_ACTION);
        mContext.sendBroadcast(intent);

    }

    @Override
    public void onServiceStateChanged(ServiceState serviceState){

        int intServiceState = serviceState.getState();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor editor = prefs.edit();
        String prefServiceState = prefs.getString(Constants.PREF_CELL_SERVICE_STATE
                , Constants.PREF_CELL_SERVICE_STATE_UNKNOWN);
        boolean sendIntent = false;

        if(intServiceState == ServiceState.STATE_IN_SERVICE
                && !prefServiceState.equals(Constants.PREF_CELL_SERVICE_STATE_TRUE)
                && !prefServiceState.equals(Constants.PREF_CELL_SERVICE_STATE_UNKNOWN)){

            sendIntent = true;
            editor.putString(Constants.PREF_CELL_SERVICE_STATE
                    , Constants.PREF_CELL_SERVICE_STATE_TRUE);
            editor.apply();

        } else if(intServiceState == ServiceState.STATE_OUT_OF_SERVICE
                && !prefServiceState.equals(Constants.PREF_CELL_SERVICE_STATE_FALSE)
                && !prefServiceState.equals(Constants.PREF_CELL_SERVICE_STATE_UNKNOWN)){

            sendIntent = true;
            editor.putString(Constants.PREF_CELL_SERVICE_STATE
                    , Constants.PREF_CELL_SERVICE_STATE_FALSE);
            editor.apply();

        } else if(prefServiceState.equals(Constants.PREF_CELL_SERVICE_STATE_UNKNOWN)){

            if(intServiceState == ServiceState.STATE_IN_SERVICE){

                editor.putString(Constants.PREF_CELL_SERVICE_STATE
                        , Constants.PREF_CELL_SERVICE_STATE_TRUE);
                editor.apply();

            } else if(intServiceState ==  ServiceState.STATE_OUT_OF_SERVICE){

                editor.putString(Constants.PREF_CELL_SERVICE_STATE
                        , Constants.PREF_CELL_SERVICE_STATE_FALSE);
                editor.apply();

            }

        }

        if(sendIntent){

            Intent intent = new Intent(Constants.NETWORK_STATE_CHANGE_INTENT_ACTION);
            mContext.sendBroadcast(intent);

        }

    }

}
