package com.tishcn.fimonitor.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.preference.PreferenceManager;

import com.tishcn.fimonitor.notification.FiMonitorNotifManager;
import com.tishcn.fimonitor.sql.FiMonitorContract;
import com.tishcn.fimonitor.sql.FiMonitorDbHelper;
import com.tishcn.fimonitor.util.Constants;

public class WifiChangeReceiver extends BroadcastReceiver {
    public WifiChangeReceiver() {
    }

    @Override
    public void onReceive(final Context context, Intent intent) {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        if (intent.getAction().equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)){

            SharedPreferences.Editor editor = prefs.edit();
            NetworkInfo networkInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
            String wifiConnResult = prefs.getString(Constants.PREF_WIFI_CONN_STATE,
                    Constants.PREF_WIFI_CONN_UNKNOWN);
            boolean insertHist = false;
            String prevSSID = prefs.getString(Constants.PREF_WIFI_LAST_SSID, "");
            String mSSID = null;
            boolean bolWifiHistLogging = prefs.getBoolean(Constants.PREF_WIFI_HIST_LOGGING, true);
            final FiMonitorDbHelper dbHelper = new FiMonitorDbHelper(context);

            if(networkInfo.getState().equals(NetworkInfo.State.CONNECTED)
                    && !wifiConnResult.equals(Constants.PREF_WIFI_CONN_TRUE)){
                WifiInfo wifiInfo = intent.getParcelableExtra(WifiManager.EXTRA_WIFI_INFO);
                mSSID = wifiInfo.getSSID();
                mSSID = mSSID.substring(1, mSSID.length()-1);
                editor.putString(Constants.PREF_WIFI_LAST_SSID, mSSID);
                editor.putString(Constants.PREF_WIFI_CONN_STATE, Constants.PREF_WIFI_CONN_TRUE);
                editor.apply();
                insertHist = true;
            } else if(!networkInfo.getState().equals(NetworkInfo.State.CONNECTED)
                    && wifiConnResult.equals(Constants.PREF_WIFI_CONN_TRUE)){
                mSSID = Constants.PREF_WIFI_LAST_SSID_DISCONNECTED;
                editor.putString(Constants.PREF_WIFI_LAST_SSID, mSSID);
                editor.putString(Constants.PREF_WIFI_CONN_STATE, Constants.PREF_WIFI_CONN_FALSE);
                editor.apply();
                insertHist = true;
            } else if(wifiConnResult.equals(Constants.PREF_WIFI_CONN_UNKNOWN)){
                String newConnStatus = Constants.PREF_WIFI_CONN_FALSE;
                if(networkInfo.getState().equals(NetworkInfo.State.CONNECTED)){
                    newConnStatus = Constants.PREF_WIFI_CONN_TRUE;
                }
                editor.putString(Constants.PREF_WIFI_CONN_STATE, newConnStatus);
                editor.apply();
            }

            if(insertHist){
                final String histType = Constants.HIST_TYPE_WIFI;
                String histAction = Constants.HIST_ACTION_CONNECTED;
                String histMessage = mSSID;
                if(!networkInfo.getState().equals(NetworkInfo.State.CONNECTED)){
                    histAction = Constants.HIST_ACTION_DISCONNECTED;
                    histMessage = prevSSID;
                }

                int maxRowId = dbHelper.getMaxHistRowIdByType(histType);
                int rowCount = dbHelper.getHistoryRowCount();
                int rowLimit = Integer.parseInt(prefs.getString(Constants.PREF_HIST_ROW_LIMIT
                        , Constants.PREF_HIST_ROW_LIMIT_DEFAULT_VALUE));
                long curTimeMillis = System.currentTimeMillis();
                boolean full = true;
                boolean newRow = false;
                if (maxRowId > 0) {
                    full = dbHelper.isHistoryRowFull(maxRowId);
                    if (!full) {
                        Cursor cursor = dbHelper.getHistoryRowById(maxRowId);
                        long fromMillis = cursor.getLong(cursor.getColumnIndex(
                                FiMonitorContract.HistoryTable.COLUMN_NAME_HIST_FROM_DTM));
                        if (curTimeMillis - fromMillis
                                > Constants.HIST_NEW_ROW_TIME_DIFFERENCE_MILLIS) {
                            newRow = true;
                        }
                    }
                }
                if (histAction.equals(Constants.HIST_ACTION_DISCONNECTED)) {
                    if(bolWifiHistLogging) {
                        dbHelper.insertHistoryRow(
                                histType
                                , histAction
                                , curTimeMillis
                                , histMessage
                                , ""
                        );
                        rowCount = rowCount + 1;
                    }
                } else if (histAction.equals(Constants.HIST_ACTION_CONNECTED)) {
                    if(bolWifiHistLogging) {
                        if (!full && !newRow) {
                            dbHelper.updateHistoryRow(maxRowId
                                    , Constants.HIST_ACTION_DISCONNECT_CONNECT
                                    , System.currentTimeMillis(), histMessage, "");
                        } else {
                            dbHelper.insertHistoryRowToOnly(
                                    histType
                                    , histAction
                                    , System.currentTimeMillis()
                                    , histMessage
                                    , ""
                            );
                            rowCount = rowCount + 1;
                        }
                    }
                }
                if (rowCount > rowLimit) {
                    dbHelper.deleteHistoryRowsByLimit(rowLimit);
                }
                if (prefs.getBoolean(Constants.PREF_NOTIF_ENABLED, true)) {
                    maxRowId = dbHelper.getMaxHistRowIdByType(Constants.HIST_TYPE_WIFI);
                    if(maxRowId > 0) {
                        Cursor cursor = dbHelper.getHistoryRowById(maxRowId);
                        FiMonitorNotifManager notifManager = new FiMonitorNotifManager(context);
                        notifManager.createTriggerNotification(
                                cursor.getString(cursor.getColumnIndex(FiMonitorContract.HistoryTable.COLUMN_NAME_HIST_TYPE))
                                , cursor.getString(cursor.getColumnIndex(FiMonitorContract.HistoryTable.COLUMN_NAME_HIST_ACTION))
                                , cursor.getLong(cursor.getColumnIndex(FiMonitorContract.HistoryTable.COLUMN_NAME_HIST_FROM_DTM))
                                , cursor.getString(cursor.getColumnIndex(FiMonitorContract.HistoryTable.COLUMN_NAME_HIST_FROM_MSG))
                                , cursor.getString(cursor.getColumnIndex(FiMonitorContract.HistoryTable.COLUMN_NAME_HIST_FROM_MCCMNC))
                                , cursor.getLong(cursor.getColumnIndex(FiMonitorContract.HistoryTable.COLUMN_NAME_HIST_TO_DTM))
                                , cursor.getString(cursor.getColumnIndex(FiMonitorContract.HistoryTable.COLUMN_NAME_HIST_TO_MSG))
                                , cursor.getString(cursor.getColumnIndex(FiMonitorContract.HistoryTable.COLUMN_NAME_HIST_TO_MCCMNC)));
                        cursor.close();
                    }
                }
                Intent mIntent = new Intent(Constants.HISTORY_CHANGE_INTENT_ACTION);
                context.sendBroadcast(mIntent);
            }
        }
    }
}
