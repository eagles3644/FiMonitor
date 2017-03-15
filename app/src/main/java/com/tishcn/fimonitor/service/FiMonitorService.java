package com.tishcn.fimonitor.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import com.tishcn.fimonitor.util.MyPhoneStateListener;

public class FiMonitorService extends Service {

    private TelephonyManager telephonyManager;
    private MyPhoneStateListener myPhoneStateListener;

    public FiMonitorService() {
    }

    @Override
    public void onCreate(){
        telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        myPhoneStateListener = new MyPhoneStateListener(getApplicationContext());
        telephonyManager.listen(myPhoneStateListener, PhoneStateListener.LISTEN_SERVICE_STATE);
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy(){
        telephonyManager.listen(myPhoneStateListener, PhoneStateListener.LISTEN_NONE);
        super.onDestroy();
    }
}
