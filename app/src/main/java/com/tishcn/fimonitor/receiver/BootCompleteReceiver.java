package com.tishcn.fimonitor.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.tishcn.fimonitor.service.FiMonitorService;

public class BootCompleteReceiver extends BroadcastReceiver {
    public BootCompleteReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)){
            Intent serviceIntent = new Intent(context, FiMonitorService.class);
            context.startService(serviceIntent);
        }
    }
}
