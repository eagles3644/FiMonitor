package com.tishcn.fimonitor.notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.service.notification.StatusBarNotification;
import android.support.v4.app.NotificationCompat.Action;
import android.support.v4.app.NotificationCompat.MessagingStyle;
import android.support.v4.app.NotificationCompat.MessagingStyle.Message;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.NotificationCompat;

import com.tishcn.fimonitor.R;
import com.tishcn.fimonitor.ui.MainActivity;
import com.tishcn.fimonitor.util.Constants;
import com.tishcn.fimonitor.util.DateFormat;
import com.tishcn.fimonitor.util.FiMonitor;

import java.util.Set;

/**
 * Created by leona on 8/13/2016.
 */
public class FiMonitorNotifManager {

    private Context mContext;
    private NotificationManager mNotifManager;
    private String mGroupKey = "FiMonitor-Notification-Group-Key";
    private int mNotifId = 3644;
    private SharedPreferences mPrefs;

    private boolean mNotifEnabled;
    private String mNotifType;
    private boolean mNotifTriggerCell;
    private boolean mNotifTriggerWifi;
    private String mNotifPriority;
    private boolean mNotifShowonLockScreen;
    private boolean mNotifPlaySound;
    private boolean mNotifVibrate;
    private boolean mNotifLed;
    private boolean mNotifShowCellInfo;
    private boolean mNotifShowWifiInfo;
    private boolean mNotifShowButtons;
    private String mNotifButton1;
    private String mNotifButton2;
    private String mNotifButton3;

    public FiMonitorNotifManager(Context context){
        this.mContext = context;
        mNotifManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        mPrefs = PreferenceManager.getDefaultSharedPreferences(mContext);
    }

    public void cancelAllNotifications(){
        mNotifManager.cancelAll();
    }

    public void createTriggerNotification(String type, String action, long fromMillis
            , String fromMsg, String fromMCCMNC, long toMillis, String toMsg, String toMCCMNC) {

        refreshNotifPrefs();

        boolean process = false;

        if(mNotifTriggerCell && type.equals(Constants.HIST_TYPE_CELL)){
            process = true;
        } else if(mNotifTriggerWifi && type.equals(Constants.HIST_TYPE_WIFI)){
            process = true;
        } else if(action.equals("test")){
            process = true;
        }

        if(mNotifEnabled && mNotifType.equals(Constants.NOTIFICATION_ON_TRIGGER) && process) {

            NotificationCompat.Builder builder;
            NotificationCompat.MessagingStyle msgStyle = new MessagingStyle(Constants.FI_MONITOR);
            FiMonitor fiMonitor = new FiMonitor(mContext);
            Intent intent = new Intent(mContext, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, intent
                    , PendingIntent.FLAG_UPDATE_CURRENT);
            int activeNotifLen = mNotifManager.getActiveNotifications().length;
            String notifMsgText;
            long notifTimeMilli;

            if (fromMCCMNC != null && type.equals(Constants.HIST_TYPE_CELL)) {
                fromMsg = fiMonitor.getNetworkOperatorName(fromMCCMNC);
            }

            if (toMCCMNC != null && type.equals(Constants.HIST_TYPE_CELL)) {
                toMsg = fiMonitor.getNetworkOperatorName(toMCCMNC);
            }

            switch (action) {
                case Constants.HIST_ACTION_DISCONNECT_CONNECT:
                    if (fromMsg.equals(toMsg)) {
                        if (type.equals(Constants.HIST_TYPE_CELL)){
                            notifMsgText = toMsg.concat(" reconnected. ");
                        } else {
                            notifMsgText = type.concat(" reconnected. ");
                        }
                        notifMsgText = notifMsgText.concat("Dropped connection for ")
                                .concat(String.valueOf(((toMillis - fromMillis) / 1000))
                                        .concat(" seconds."));
                    } else {
                        notifMsgText = "Changed from ".concat(fromMsg)
                                .concat(" to ").concat(toMsg).concat(" in ")
                                .concat(String.valueOf(((toMillis - fromMillis) / 1000))
                                        .concat(" seconds."));
                    }
                    notifTimeMilli = toMillis;
                    break;
                case Constants.HIST_ACTION_CONNECTED:
                case Constants.HIST_ACTION_DISCONNECTED:
                    if (action.equals(Constants.HIST_ACTION_CONNECTED)) {
                        notifMsgText = action.concat(" to ").concat(toMsg).concat(".");
                        notifTimeMilli = toMillis;
                    } else {
                        notifMsgText = action.concat(" from ").concat(fromMsg).concat(".");
                        notifTimeMilli = fromMillis;
                    }
                    break;
                default:
                    notifMsgText = Constants.FAKE_NOTIF_TITLE;
                    notifTimeMilli = toMillis;
                    break;
            }

            if (activeNotifLen > 0) {
                for (StatusBarNotification sbn : mNotifManager.getActiveNotifications()) {
                    if (sbn.getGroupKey().contains(mGroupKey)) {
                        msgStyle = NotificationCompat.MessagingStyle.extractMessagingStyleFromNotification(sbn.getNotification());
                        break;
                    }
                }
                msgStyle.setConversationTitle(Constants.NOTIFICATION_GROUP_SUMMARY_MULTIPLE);
            } else {
                msgStyle.setConversationTitle(Constants.NOTIFICATION_GROUP_SUMMARY_SINGLE);
            }

            if(mNotifShowCellInfo){
                if(fiMonitor.getNetworkOperatorName(fiMonitor.getNetworkOperator()).equals(Constants.NOT_CONNECTED)
                        || (action.equals(Constants.HIST_ACTION_DISCONNECTED) && type.equals(Constants.HIST_TYPE_CELL))){
                    notifMsgText = notifMsgText.concat(System.lineSeparator()).concat("Cell: Not Connected");
                } else {
                    notifMsgText = notifMsgText.concat(System.lineSeparator()).concat("Cell: ")
                            .concat(fiMonitor.getNetworkOperatorName(fiMonitor.getNetworkOperator()));
                    String netType = fiMonitor.getNetworkTypeName();
                    String signalStrength = fiMonitor.getNetworkSignalStrength();
                    if(!netType.equals(Constants.NETWORK_TYPE_NAME_UNKNOWN)) {
                        notifMsgText = notifMsgText.concat(" ".concat(netType));
                    }
                    if(!signalStrength.equals(Constants.NETWORK_SIGNAL_STRENGTH_UNKNOWN)) {
                        notifMsgText = notifMsgText.concat(" ".concat(signalStrength));
                    }
                }
            }

            if(mNotifShowWifiInfo){
                if (fiMonitor.wifiConnected() ||
                        (action.equals(Constants.HIST_ACTION_DISCONNECTED) && type.equals(Constants.HIST_TYPE_CELL))) {
                    String ssid = fiMonitor.getWifiSsid();
                    if(!ssid.equals(Constants.WIFI_UNKNOWN_SSID)) {
                        notifMsgText = notifMsgText.concat(System.lineSeparator()).concat("WiFi: ")
                                .concat(ssid);
                        String linkSpeed = fiMonitor.getWifiLinkSpeed();
                        String signalStrength = fiMonitor.getWifiSignalStrength();
                        notifMsgText = notifMsgText.concat(" ".concat(linkSpeed).concat(" "));
                        notifMsgText = notifMsgText.concat("".concat(signalStrength));
                    } else {
                        notifMsgText = notifMsgText.concat(System.lineSeparator()).concat("WiFi: Not Connected");
                    }
                } else {
                    notifMsgText = notifMsgText.concat(System.lineSeparator()).concat("WiFi: Not Connected");
                }
            }

            msgStyle.addMessage(new Message(notifMsgText, notifTimeMilli, DateFormat.formatDateTime(notifTimeMilli)));

            int priority;
            switch(mNotifPriority) {
                case Constants.NOTIFICATION_PRIORTY_MAX:
                    priority = Notification.PRIORITY_MAX;
                    break;
                case Constants.NOTIFICATION_PRIORTY_HIGH:
                    priority = Notification.PRIORITY_HIGH;
                    break;
                case Constants.NOTIFICATION_PRIORTY_LOW:
                    priority = Notification.PRIORITY_LOW;
                    break;
                case Constants.NOTIFICATION_PRIORTY_MIN:
                    priority = Notification.PRIORITY_MIN;
                    break;
                default:
                    priority = Notification.PRIORITY_DEFAULT;
                    break;
            }

            builder = (NotificationCompat.Builder) new NotificationCompat.Builder(mContext)
                    .setSmallIcon(R.drawable.ic_group_notif)
                    .setGroupSummary(true)
                    .setGroup(mGroupKey)
                    .setCategory(Notification.CATEGORY_STATUS)
                    .setSmallIcon(R.drawable.ic_group_notif);
            builder.setContentIntent(pendingIntent);
            builder.setStyle(msgStyle);
            builder.setAutoCancel(true);
            builder.setColor(ContextCompat.getColor(mContext, R.color.colorPrimary));
            builder.setPriority(priority);

            if (mNotifShowButtons){
                Intent intent1 = new Intent(Constants.NOTIFICATION_BUTTON_CLICK_INTENT_ACTION);
                intent1.putExtra(Constants.NOTIFICATION_BUTTON_EXTRA_DIALER_CODE, getDialerCode(mNotifButton1));
                Intent intent2 = new Intent(Constants.NOTIFICATION_BUTTON_CLICK_INTENT_ACTION);
                intent2.putExtra(Constants.NOTIFICATION_BUTTON_EXTRA_DIALER_CODE, getDialerCode(mNotifButton2));
                Intent intent3 = new Intent(Constants.NOTIFICATION_BUTTON_CLICK_INTENT_ACTION);
                intent3.putExtra(Constants.NOTIFICATION_BUTTON_EXTRA_DIALER_CODE, getDialerCode(mNotifButton3));
                PendingIntent pendingIntentBtn1 = PendingIntent.getBroadcast(mContext, 1, intent1, PendingIntent.FLAG_UPDATE_CURRENT);
                PendingIntent pendingIntentBtn2 = PendingIntent.getBroadcast(mContext, 2, intent2, PendingIntent.FLAG_UPDATE_CURRENT);
                PendingIntent pendingIntentBtn3 = PendingIntent.getBroadcast(mContext, 3, intent3, PendingIntent.FLAG_UPDATE_CURRENT);
                Action action1 = new Action(getButtonIcon(mNotifButton1), mNotifButton1, pendingIntentBtn1);
                Action action2 = new Action(getButtonIcon(mNotifButton2), mNotifButton2, pendingIntentBtn2);
                Action action3 = new Action(getButtonIcon(mNotifButton3), mNotifButton3, pendingIntentBtn3);
                builder.addAction(action1);
                builder.addAction(action2);
                builder.addAction(action3);
            }

            Notification notif = builder.build();

            if(!mNotifShowonLockScreen){
                notif.visibility = Notification.VISIBILITY_SECRET;
            }

            if(mNotifPlaySound){
                notif.defaults |= Notification.DEFAULT_SOUND;
            }

            if(mNotifVibrate){
                notif.defaults |= Notification.DEFAULT_VIBRATE;
            }

            if(mNotifLed){
                notif.defaults |= Notification.DEFAULT_LIGHTS;
            }

            mNotifManager.notify(mNotifId, notif);
        }

    }

    private int getButtonIcon(String buttonText) {
        switch (buttonText) {
            case Constants.NETWORK_OPERATOR_NAME_SPRINT:
                 return R.drawable.ic_sprint;
            case Constants.NETWORK_OPERATOR_NAME_TMOBILE:
                return R.drawable.ic_tmobile;
            case Constants.NETWORK_OPERATOR_NAME_US_CELLULAR:
                return R.drawable.ic_us_cellular;
            case Constants.DIALER_CODE_TITLE_NEXT_CARRIER:
                return R.drawable.ic_next;
            case Constants.DIALER_CODE_TITLE_AUTO_SWITCH:
                return R.drawable.ic_swap_horizontal_black_36dp;
            case Constants.DIALER_CODE_TITLE_REPAIR:
                return R.drawable.ic_wrench_black_36dp;
            case Constants.DIALER_CODE_TITLE_INFO:
                return R.drawable.ic_info_outline;
            case Constants.DIALER_CODE_TITLE_TESTING_ACTIVITY:
                return R.drawable.ic_perm_device_information;
            default:
                return R.mipmap.ic_launcher;
        }
    }

    private String getDialerCode(String buttonText) {

        switch (buttonText) {
            case Constants.NETWORK_OPERATOR_NAME_SPRINT:
                return Constants.DIALER_CODE_CODE_SPRINT;
            case Constants.NETWORK_OPERATOR_NAME_TMOBILE:
                return Constants.DIALER_CODE_CODE_TMOBILE;
            case Constants.NETWORK_OPERATOR_NAME_US_CELLULAR:
                return Constants.DIALER_CODE_CODE_US_CELLULAR;
            case Constants.DIALER_CODE_TITLE_NEXT_CARRIER:
                return Constants.DIALER_CODE_CODE_NEXT_CARRIER;
            case Constants.DIALER_CODE_TITLE_AUTO_SWITCH:
                return Constants.DIALER_CODE_CODE_AUTO_SWITCH;
            case Constants.DIALER_CODE_TITLE_REPAIR:
                return Constants.DIALER_CODE_CODE_REPAIR;
            case Constants.DIALER_CODE_TITLE_INFO:
                return Constants.DIALER_CODE_CODE_INFO;
            case Constants.DIALER_CODE_TITLE_TESTING_ACTIVITY:
                return Constants.DIALER_CODE_CODE_TESTING_ACTIVITY;
            default:
                return Constants.FI_MONITOR;
        }
    }

    private void refreshNotifPrefs(){
        mNotifEnabled = mPrefs.getBoolean(Constants.PREF_NOTIF_ENABLED, true);
        mNotifType = mPrefs.getString(Constants.PREF_NOTIF_TYPE, Constants.NOTIFICATION_ON_TRIGGER);
        Set<String> mNotifTriggers = mPrefs.getStringSet(Constants.PREF_NOTIF_TRIGGER, Constants.NOTIFICATION_TRIGGER_DEFAULT_SET);
        mNotifPriority = mPrefs.getString(Constants.PREF_NOTIF_PRIORITY, Constants.NOTIFICATION_DEFAULT_PRIORITY);
        mNotifShowonLockScreen = mPrefs.getBoolean(Constants.PREF_NOTIF_SHOW_ON_LOCK_SCREEN, true);
        mNotifPlaySound = mPrefs.getBoolean(Constants.PREF_NOTIF_PLAY_SOUND, true);
        mNotifVibrate = mPrefs.getBoolean(Constants.PREF_NOTIF_VIBRATE, true);
        mNotifLed = mPrefs.getBoolean(Constants.PREF_NOTIF_LED, true);
        mNotifShowCellInfo = mPrefs.getBoolean(Constants.PREF_NOTIF_SHOW_CELLULAR, true);
        mNotifShowWifiInfo = mPrefs.getBoolean(Constants.PREF_NOTIF_SHOW_WIFI, false);
        mNotifShowButtons = mPrefs.getBoolean(Constants.PREF_NOTIF_SHOW_BUTTONS, true);
        mNotifButton1 = mPrefs.getString(Constants.PREF_NOTIF_BUTTON1, Constants.NETWORK_OPERATOR_NAME_SPRINT);
        mNotifButton2 = mPrefs.getString(Constants.PREF_NOTIF_BUTTON2, Constants.NETWORK_OPERATOR_NAME_TMOBILE);
        mNotifButton3 = mPrefs.getString(Constants.PREF_NOTIF_BUTTON3, Constants.NETWORK_OPERATOR_NAME_US_CELLULAR);

        int selectionSize = mNotifTriggers.size();
        boolean cell = false;
        boolean wifi = false;
        if(selectionSize > 0) {
            String[] selectionsArray = mNotifTriggers.toArray(new String[selectionSize]);
            for (int i = 0; i < selectionSize; i++) {
                String value = selectionsArray[i];
                if(value.equals(Constants.NOTIFICATIONS_TRIGGER_CELLULAR)){
                    cell = true;
                } else if(value.equals(Constants.NOTIFICATIONS_TRIGGER_WIFI)) {
                    wifi = true;
                }
            }
        }
        mNotifTriggerCell = cell;
        mNotifTriggerWifi = wifi;
    }

    private int getCarrierIcon(String carrierName) {
        switch (carrierName) {
            case Constants.NETWORK_OPERATOR_NAME_SPRINT:
                return R.drawable.ic_sprint;
            case Constants.NETWORK_OPERATOR_NAME_TMOBILE:
                return R.drawable.ic_tmobile;
            case Constants.NETWORK_OPERATOR_NAME_US_CELLULAR:
                return R.drawable.ic_us_cellular;
            default:
                return R.drawable.ic_cellular_connection;
        }
    }
}
