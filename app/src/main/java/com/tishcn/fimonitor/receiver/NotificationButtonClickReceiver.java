package com.tishcn.fimonitor.receiver;

import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.tishcn.fimonitor.util.Constants;

public class NotificationButtonClickReceiver extends BroadcastReceiver {
    public NotificationButtonClickReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(Constants.NOTIFICATION_BUTTON_CLICK_INTENT_ACTION)){

            Intent it = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
            context.sendBroadcast(it);

            String strCode = intent.getStringExtra(Constants.NOTIFICATION_BUTTON_EXTRA_DIALER_CODE);

            Intent newIntent = new Intent(Intent.ACTION_DIAL);
            newIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

            ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText(strCode, strCode);
            clipboard.setPrimaryClip(clip);
            Toast.makeText(context, "The dialer code has been copied to your clipboard. " +
                    "Simply paste into the dialer and your request will be " +
                    "processed.", Toast.LENGTH_SHORT).show();

            context.startActivity(newIntent);
        }
    }
}
