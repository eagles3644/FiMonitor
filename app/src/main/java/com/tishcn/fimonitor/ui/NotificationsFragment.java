package com.tishcn.fimonitor.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.MultiSelectListPreference;
import android.preference.Preference;
import android.preference.PreferenceGroup;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.util.Log;

import com.tishcn.fimonitor.R;
import com.tishcn.fimonitor.notification.FiMonitorNotifManager;
import com.tishcn.fimonitor.util.Constants;

import java.util.Set;

/**
 * Created by leona on 8/13/2016.
 */
public class NotificationsFragment extends MyPreferenceFragment implements
        SharedPreferences.OnSharedPreferenceChangeListener {

    private Snackbar mNoLoggingSnackBar;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.notifications);
        Preference pNotifTrigger = findPreference(Constants.PREF_NOTIF_TRIGGER);
        MultiSelectListPreference triggerMultiPref = (MultiSelectListPreference) pNotifTrigger;
        Preference showCellPref = findPreference(Constants.PREF_NOTIF_SHOW_CELLULAR);
        Preference showWiFiPref = findPreference(Constants.PREF_NOTIF_SHOW_WIFI);
        if(pNotifTrigger.getSharedPreferences().getString(Constants.PREF_NOTIF_TYPE, Constants.NOTIFICATION_ON_TRIGGER)
                .equals(Constants.NOTIFICATION_ON_TRIGGER)){
            pNotifTrigger.setEnabled(true);
            Set<String> triggerSelections = triggerMultiPref.getValues();
            int selectionSize = triggerSelections.size();
            boolean showCell = false;
            boolean showWifi = false;
            if(selectionSize > 0) {
                String[] selectionsArray = triggerSelections.toArray(new String[selectionSize]);
                for (int i = 0; i < selectionSize; i++) {
                    String value = selectionsArray[i];
                    if(value.equals(Constants.NOTIFICATIONS_TRIGGER_CELLULAR)){
                        showCell = true;
                    }
                    if(value.equals(Constants.NOTIFICATIONS_TRIGGER_WIFI)) {
                        showWifi = true;
                    }
                }
            }
            showCellPref.setEnabled(showCell);
            showWiFiPref.setEnabled(showWifi);
        } else {
            pNotifTrigger.setEnabled(false);
            showCellPref.setEnabled(true);
            showWiFiPref.setEnabled(false);
        }
        initSummary(getPreferenceScreen());
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
        dismissSnackIfNeeded();
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
        showSnackIfNeeded();
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    private void initSummary(Preference p) {
        if (p instanceof PreferenceGroup) {
            PreferenceGroup pGrp = (PreferenceGroup) p;
            for (int i = 0; i < pGrp.getPreferenceCount(); i++) {
                initSummary(pGrp.getPreference(i));
            }
        } else {
            updatePrefSummary(p);
        }
    }

    private void updatePrefSummary(Preference p) {
        if (p instanceof ListPreference) {
            ListPreference listPref = (ListPreference) p;
            p.setSummary(listPref.getEntry());
        }
        if (p instanceof EditTextPreference) {
            EditTextPreference editTextPref = (EditTextPreference) p;
            if (p.getTitle().toString().toLowerCase().contains("password"))
            {
                p.setSummary("******");
            } else {
                p.setSummary(editTextPref.getText());
            }
        }
        if (p instanceof MultiSelectListPreference) {
            MultiSelectListPreference multiSelectListPreference = (MultiSelectListPreference) p;
            String summary = "Nothing Selected";
            if(multiSelectListPreference.getKey().equals(Constants.PREF_NOTIF_TRIGGER)){
                summary = "No Triggers Selected";
            }
            Set<String> selections = multiSelectListPreference.getValues();
            int selectionSize = selections.size();
            if(selectionSize > 0) {
                summary = "";
                String[] selectionsArray = selections.toArray(new String[selections.size()]);
                boolean showCell = false;
                boolean showWifi = false;
                for (int i = 0; i < selectionSize; i++) {
                    String value = selectionsArray[i];
                    summary = summary.concat(", ").concat(value);
                    if(multiSelectListPreference.getKey().equals(Constants.PREF_NOTIF_TRIGGER)){
                        Preference showCellPref = findPreference(Constants.PREF_NOTIF_SHOW_CELLULAR);
                        Preference showWiFiPref = findPreference(Constants.PREF_NOTIF_SHOW_WIFI);
                        if(value.equals(Constants.NOTIFICATIONS_TRIGGER_CELLULAR)){
                            showCell = true;
                        }
                        if(value.equals(Constants.NOTIFICATIONS_TRIGGER_WIFI)) {
                            showWifi = true;
                        }
                        showCellPref.setEnabled(showCell);
                        showWiFiPref.setEnabled(showWifi);
                        showSnackIfNeeded();
                    }
                }
                summary = summary.substring(2);
            } else {
                if(multiSelectListPreference.getKey().equals(Constants.PREF_NOTIF_TRIGGER)){
                    Preference showCellPref = findPreference(Constants.PREF_NOTIF_SHOW_CELLULAR);
                    Preference showWiFiPref = findPreference(Constants.PREF_NOTIF_SHOW_WIFI);
                    if(multiSelectListPreference.getSharedPreferences()
                            .getString(Constants.PREF_NOTIF_TYPE, Constants.NOTIFICATION_ON_TRIGGER).equals(Constants.NOTIFICATION_PERSISTENT)){
                        showCellPref.setEnabled(true);
                        showWiFiPref.setEnabled(true);
                    } else {
                        showCellPref.setEnabled(false);
                        showWiFiPref.setEnabled(false);
                    }
                }
            }
            p.setSummary(summary);
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        initSummary(getPreferenceScreen());
        if(key.equals(Constants.PREF_NOTIF_TYPE)){
            Preference triggerPref = findPreference(Constants.PREF_NOTIF_TRIGGER);
            Preference showCellPref = findPreference(Constants.PREF_NOTIF_SHOW_CELLULAR);
            Preference showWiFiPref = findPreference(Constants.PREF_NOTIF_SHOW_WIFI);
            Preference playSoundPref = findPreference(Constants.PREF_NOTIF_PLAY_SOUND);
            Preference vibratePref = findPreference(Constants.PREF_NOTIF_VIBRATE);
            Preference ledPref = findPreference(Constants.PREF_NOTIF_LED);
            if(sharedPreferences.getString(key, Constants.NOTIFICATION_ON_TRIGGER)
                    .equals(Constants.NOTIFICATION_ON_TRIGGER)){
                triggerPref.setEnabled(true);
                MultiSelectListPreference triggerMultiPref = (MultiSelectListPreference) triggerPref;
                Set<String> triggerSelections = triggerMultiPref.getValues();
                int selectionSize = triggerSelections.size();
                boolean showCell = false;
                boolean showWifi = false;
                if(selectionSize > 0) {
                    String[] selectionsArray = triggerSelections.toArray(new String[selectionSize]);
                    for (int i = 0; i < selectionSize; i++) {
                        String value = selectionsArray[i];
                        if(value.equals(Constants.NOTIFICATIONS_TRIGGER_CELLULAR)){
                            showCell = true;
                        } else if(value.equals(Constants.NOTIFICATIONS_TRIGGER_WIFI)) {
                            showWifi = true;
                        }
                    }
                }
                showCellPref.setEnabled(showCell);
                showWiFiPref.setEnabled(showWifi);
                playSoundPref.setEnabled(true);
                vibratePref.setEnabled(true);
                ledPref.setEnabled(true);
            } else {
                triggerPref.setEnabled(false);
                showCellPref.setEnabled(true);
                showWiFiPref.setEnabled(true);
                playSoundPref.setEnabled(false);
                vibratePref.setEnabled(false);
                ledPref.setEnabled(false);
            }
            initSummary(getPreferenceScreen());
        } else if(key.equals(Constants.PREF_NOTIF_ENABLED)){
            FiMonitorNotifManager fiMonitorNotifManager;
            if(sharedPreferences.getBoolean(Constants.PREF_NOTIF_ENABLED, true)){
                if(sharedPreferences.getString(Constants.PREF_NOTIF_TYPE
                        , Constants.NOTIFICATION_ON_TRIGGER)
                        .equals(Constants.NOTIFICATION_PERSISTENT)) {
                    fiMonitorNotifManager = new FiMonitorNotifManager(getContext());
                    //TODO: Start Persistent Notification
                }
                showSnackIfNeeded();
            } else {
                fiMonitorNotifManager = new FiMonitorNotifManager(getContext());
                fiMonitorNotifManager.cancelAllNotifications();
                dismissSnackIfNeeded();
            }
        }
    }

    private void showSnackIfNeeded() {
        Preference pNotifEnabled = findPreference(Constants.PREF_NOTIF_ENABLED);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        boolean notifEnabled = pNotifEnabled.isEnabled();
        boolean wifiLoggingEnabled = prefs.getBoolean(Constants.PREF_WIFI_HIST_LOGGING, true);
        boolean cellLoggingEnabled = prefs.getBoolean(Constants.PREF_CELL_HIST_LOGGING, true);
        Preference pNotifTrigger = findPreference(Constants.PREF_NOTIF_TRIGGER);
        MultiSelectListPreference triggerMultiPref = (MultiSelectListPreference) pNotifTrigger;
        Set<String> triggerSelections = triggerMultiPref.getValues();
        int selectionSize = triggerSelections.size();
        boolean showCell = false;
        boolean showWifi = false;
        if(selectionSize > 0) {
            String[] selectionsArray = triggerSelections.toArray(new String[selectionSize]);
            for (int i = 0; i < selectionSize; i++) {
                String value = selectionsArray[i];
                if(value.equals(Constants.NOTIFICATIONS_TRIGGER_CELLULAR)){
                    showCell = true;
                }
                if(value.equals(Constants.NOTIFICATIONS_TRIGGER_WIFI)) {
                    showWifi = true;
                }
            }
        }
        if(notifEnabled){
            if(!wifiLoggingEnabled || !cellLoggingEnabled){
                String neither = "You have event logging disabled, you won't receive " +
                        "event notifications.";
                String noCell = "You have cellular event logging disabled, you " +
                        "won't receive cellular event notifications.";
                String noWifi = "You have wifi event logging disabled, you " +
                        "won't receive wifi event notifications.";
                if(showWifi && !wifiLoggingEnabled && showCell && !cellLoggingEnabled){
                    MainActivity mainActivity = (MainActivity) getActivity();
                    mNoLoggingSnackBar = mainActivity.showNoLoggingSnackBar(neither);
                } else if(showWifi && !wifiLoggingEnabled){
                    MainActivity mainActivity = (MainActivity) getActivity();
                    mNoLoggingSnackBar = mainActivity.showNoLoggingSnackBar(noWifi);
                } else if(showCell && !cellLoggingEnabled){
                    MainActivity mainActivity = (MainActivity) getActivity();
                    mNoLoggingSnackBar = mainActivity.showNoLoggingSnackBar(noCell);
                } else {
                    dismissSnackIfNeeded();
                }
            } else {
                dismissSnackIfNeeded();
            }
        } else {
            dismissSnackIfNeeded();
        }
    }

    private void dismissSnackIfNeeded() {
        if(mNoLoggingSnackBar != null && mNoLoggingSnackBar.isShown()){
            mNoLoggingSnackBar.dismiss();
        }
    }

    Preference.OnPreferenceClickListener  mPrefClickListener = new
            Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    if (preference.getKey().equals(Constants.PREF_NOTIF_ENABLED)) {
                        Log.d("NotifFragPrefClick", "Clicked ".concat(Constants.PREF_NOTIF_ENABLED));
                    }
                    return true;
                }
            };
}
