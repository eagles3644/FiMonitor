package com.tishcn.fimonitor.receiver;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationManager;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.tishcn.fimonitor.notification.FiMonitorNotifManager;
import com.tishcn.fimonitor.sql.FiMonitorContract;
import com.tishcn.fimonitor.sql.FiMonitorDbHelper;
import com.tishcn.fimonitor.util.Constants;
import com.tishcn.fimonitor.util.DateFormat;
import com.tishcn.fimonitor.util.FiMonitor;

/**
 * Created by leona on 7/7/2016.
 */
public class NetworkChangeReceiver extends BroadcastReceiver {

    GoogleApiClient mGoogleApiClient;
    GoogleApiClient.ConnectionCallbacks mGoogleApiConnectionCallbacks;
    GoogleApiClient.OnConnectionFailedListener mGoogleApiConnectionFailedListener;

    public NetworkChangeReceiver() {
    }

    @Override
    public void onReceive(final Context context, Intent intent) {

        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        if (intent.getAction().equals(Constants.NETWORK_STATE_CHANGE_INTENT_ACTION)) {

            boolean logging = prefs.getBoolean(Constants.PREF_CELL_HIST_LOGGING, true);
            FiMonitor fiMonitor = new FiMonitor(context);
            final String mccmnc = fiMonitor.getNetworkOperator();
            final FiMonitorDbHelper dbHelper = new FiMonitorDbHelper(context);
            String cellConnState = prefs.getString(Constants.PREF_CELL_SERVICE_STATE
                    , Constants.PREF_CELL_SERVICE_STATE_UNKNOWN);
            final String prevCellOperatorName = prefs.getString(Constants.PREF_CELL_LAST_OPERATOR
                    , Constants.PREF_CELL_LAST_OPERATOR_UNKNOWN);
            final String histType = Constants.HIST_TYPE_CELL;
            String histAction = Constants.HIST_ACTION_CONNECTED;

            if (!cellConnState.equals(Constants.PREF_CELL_SERVICE_STATE_TRUE)) {
                histAction = Constants.HIST_ACTION_DISCONNECTED;
            }

            final String finalHistAction = histAction;

            int maxRowId = dbHelper.getMaxHistRowIdByType(Constants.HIST_TYPE_CELL);
            int maxStatRowId = dbHelper.getMaxStatsRowId();
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
            if (finalHistAction.equals(Constants.HIST_ACTION_DISCONNECTED)) {
                if (logging) {
                    long rowId =
                            dbHelper.insertHistoryRow(
                                    histType
                                    , finalHistAction
                                    , curTimeMillis
                                    , ""
                                    , prevCellOperatorName
                            );
                    rowCount = rowCount + 1;
                    getLocation(context, dbHelper, rowId);
                }
                dbHelper.updateStatsRow(maxStatRowId);
                dbHelper.insertStatRow(Constants.HIST_ACTION_DISCONNECTED);
            } else if (finalHistAction.equals(Constants.HIST_ACTION_CONNECTED)) {
                if (logging) {
                    if (!full && !newRow) {
                        dbHelper.updateHistoryRow(maxRowId
                                , Constants.HIST_ACTION_DISCONNECT_CONNECT
                                , System.currentTimeMillis()
                                , ""
                                , mccmnc);
                    } else {
                        long rowId = dbHelper.insertHistoryRowToOnly(
                                histType
                                , finalHistAction
                                , System.currentTimeMillis()
                                , ""
                                , mccmnc
                        );
                        rowCount = rowCount + 1;
                        getLocation(context, dbHelper, rowId);
                    }
                }
                dbHelper.updateStatsRow(maxStatRowId);
                dbHelper.insertStatRow(mccmnc);
            }
            if (rowCount > rowLimit) {
                dbHelper.deleteHistoryRowsByLimit(rowLimit);
            }
            if (prefs.getBoolean(Constants.PREF_NOTIF_ENABLED, true)) {
                maxRowId = dbHelper.getMaxHistRowIdByType(Constants.HIST_TYPE_CELL);
                if (maxRowId > 0) {
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

    private void getLocation(final Context context, final FiMonitorDbHelper dbHelper, final long rowId) {
        int twoMins = 1000 * 60 * 2;
        int minAccuracy = 20;
        boolean gpsEnabled = false;
        boolean networkEnabled = false;
        boolean locationEnabled = false;
        long gpsTimeDelta;
        long networkTimeDelta;
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        Location location = null;
        Location gpsLocation = null;
        Location networkLocation = null;
        boolean gpsOlderTwoMins;
        boolean networkOlderTwoMins;
        boolean gpsHasAccuracy;
        boolean networkHasAccuracy;
        boolean gpsNewer = false;
        boolean gpsMoreAccurate = false;
        String locationTypeUsed = "";
        if(ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                gpsEnabled = true;
                gpsLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            }
            if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                networkEnabled = true;
                networkLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }
            boolean gpsNull = gpsLocation == null;
            boolean networkNull = networkLocation == null;
            if(gpsEnabled && networkEnabled && !networkNull && !gpsNull) {
                    gpsNewer = gpsLocation.getTime() > networkLocation.getTime();
                    gpsMoreAccurate = gpsLocation.getAccuracy() > networkLocation.getAccuracy();
                    gpsTimeDelta = System.currentTimeMillis() - gpsLocation.getTime();
                    networkTimeDelta = System.currentTimeMillis() - networkLocation.getTime();
                    gpsOlderTwoMins = false;//gpsTimeDelta > twoMins;
                    networkOlderTwoMins = false;//networkTimeDelta > twoMins;
                    gpsHasAccuracy = gpsLocation.getAccuracy() > minAccuracy;
                    networkHasAccuracy = networkLocation.getAccuracy() > minAccuracy;
                    if (gpsNewer && gpsMoreAccurate && !gpsOlderTwoMins && gpsHasAccuracy) {
                        locationEnabled = true;
                        location = gpsLocation;
                        locationTypeUsed = "GPS";
                    } else if (!networkOlderTwoMins && networkHasAccuracy) {
                        locationEnabled = true;
                        location = gpsLocation;
                        locationTypeUsed = "Network";
                    }
            } else if(gpsEnabled && !gpsNull){
                gpsTimeDelta = System.currentTimeMillis() - gpsLocation.getTime();
                gpsOlderTwoMins = false;//gpsTimeDelta > twoMins;
                gpsHasAccuracy = gpsLocation.getAccuracy() > minAccuracy;
                if(!gpsOlderTwoMins && gpsHasAccuracy) {
                    locationEnabled = true;
                    location = gpsLocation;
                    locationTypeUsed = "GPS";
                }
            } else if(networkEnabled && !networkNull){
                networkTimeDelta = System.currentTimeMillis() - networkLocation.getTime();
                networkOlderTwoMins = false;//networkTimeDelta > twoMins;
                networkHasAccuracy = networkLocation.getAccuracy() > minAccuracy;
                if(!networkOlderTwoMins && networkHasAccuracy) {
                    locationEnabled = true;
                    location = networkLocation;
                    locationTypeUsed = "Network";
                }
            }
            if (locationEnabled && location.getLatitude() != 0.0 && location.getLongitude() != 0.0) {
                dbHelper.updateHistoryRowLocation(rowId, String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()));
                Log.d("NetChangeRecLoc", "LocationType=" + locationTypeUsed
                        + "Lat/Lng " + String.valueOf(location.getLatitude() + "/"
                        + location.getLongitude()) + " gpsNewer=" + gpsNewer + " gpsMoreAccurate="
                        + gpsMoreAccurate + " Updated=" + DateFormat.formatDateTime(location.getTime()));
            }
        }
    }
}