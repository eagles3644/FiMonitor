package com.tishcn.fimonitor.ui;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.tishcn.fimonitor.R;
import com.tishcn.fimonitor.notification.FiMonitorNotifManager;
import com.tishcn.fimonitor.service.FiMonitorService;
import com.tishcn.fimonitor.sql.DBTask;
import com.tishcn.fimonitor.sql.FiMonitorContract;
import com.tishcn.fimonitor.sql.FiMonitorDbHelper;
import com.tishcn.fimonitor.sql.FiMonitorDbRunnable;
import com.tishcn.fimonitor.util.Constants;
import com.tishcn.fimonitor.util.FiMonitor;
import com.tishcn.fimonitor.util.MyPhoneStateListener;

import java.lang.reflect.Method;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private BroadcastReceiver mReceiver;
    private TelephonyManager mTelephonyManager;
    private MyPhoneStateListener mPhoneStateListener;
    private String mSSID =  Constants.PLEASE_WAIT;
    private String mRSSI;
    private String mLinkSpeed;
    private String mNetworkOperatorName = Constants.PLEASE_WAIT;
    private String mNetworkOperator;
    private String mNetworkTypeName;
    private String mNetworkSignalStrength;
    private boolean mReceiverRunning = false;
    private boolean mVarsInitialized;
    private SharedPreferences mPrefs;
    private ActionBar actionBar;
    private Snackbar mSnackbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //Create local vars
        Fragment fragment = null;
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        actionBar = getSupportActionBar();

        //Start Service
        Intent serviceIntent = new Intent(getApplicationContext(), FiMonitorService.class);
        startService(serviceIntent);

        mPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        mVarsInitialized = mPrefs.getBoolean(Constants.PREF_VARS_INITIALIZED, false);

        //Set action bar title
        assert actionBar != null;

        fragment = null;

        //change fragment
        String homeFrag = mPrefs.getString(Constants.PREF_HOME_FRAGMENT
                , Constants.DIALER_CODES_FRAG_TITLE);
        if(savedInstanceState == null){
            switch (homeFrag) {
                case Constants.HISTORY_FRAG_TITLE:
                    fragment = new HistoryFragment();
                    navigationView.setCheckedItem(R.id.nav_history);
                    actionBar.setTitle(Constants.HISTORY_FRAG_TITLE);
                    break;
                case Constants.DIALER_CODES_FRAG_TITLE:
                    fragment = new DialerCodesFragment();
                    navigationView.setCheckedItem(R.id.nav_dialer_codes);
                    actionBar.setTitle(Constants.DIALER_CODES_FRAG_TITLE);
                    break;
                case Constants.STATISTICS_FRAG_TITLE:
                    fragment = new StatisticsFragment();
                    navigationView.setCheckedItem(R.id.nav_statistics);
                    actionBar.setTitle(Constants.STATISTICS_FRAG_TITLE);
                    break;
            }
        } else {
            String existingFrag = savedInstanceState.getString(Constants.EXTRA_ATTACHED_FRAGMENT
                    , homeFrag);
            FragmentManager fm = getSupportFragmentManager();
            for(int i = 0; i < fm.getBackStackEntryCount(); ++i) {
                fm.popBackStack();
            }
            switch (existingFrag) {
                case Constants.HISTORY_FRAG_TITLE:
                    fragment = new HistoryFragment();
                    navigationView.setCheckedItem(R.id.nav_history);
                    actionBar.setTitle(Constants.HISTORY_FRAG_TITLE);
                    break;
                case Constants.DIALER_CODES_FRAG_TITLE:
                    fragment = new DialerCodesFragment();
                    navigationView.setCheckedItem(R.id.nav_dialer_codes);
                    actionBar.setTitle(Constants.DIALER_CODES_FRAG_TITLE);
                    break;
                case Constants.NOTIFICATIONS_FRAG_TITLE:
                    fragment = new NotificationsFragment();
                    navigationView.setCheckedItem(R.id.nav_notifications);
                    actionBar.setTitle(Constants.NOTIFICATIONS_FRAG_TITLE);
                    break;
                case Constants.STATISTICS_FRAG_TITLE:
                    fragment = new StatisticsFragment();
                    navigationView.setCheckedItem(R.id.nav_statistics);
                    actionBar.setTitle(Constants.STATISTICS_FRAG_TITLE);
                    break;
                case Constants.HISTORY_MAP_FRAG_TITLE:
                    fragment = new HistoryMapFragment();
                    navigationView.setCheckedItem(R.id.nav_history_map);
                    actionBar.setTitle(Constants.HISTORY_MAP_FRAG_TITLE);
                    break;
            }
        }

        ft.replace(R.id.mainFrame, fragment);
        ft.commit();

        boolean appRated = mPrefs.getBoolean(Constants.PREF_APP_RATED, false);
        int appOpenCount = mPrefs.getInt(Constants.PREF_APP_OPEN_COUNT, 0);
        appOpenCount =  appOpenCount + 1;
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.putInt(Constants.PREF_APP_OPEN_COUNT, appOpenCount);
        editor.apply();
        if(!appRated){
            switch (appOpenCount){
                case Constants.PREF_APP_OPEN_COUNT_TRIG1:
                case Constants.PREF_APP_OPEN_COUNT_TRIG2:
                case Constants.PREF_APP_OPEN_COUNT_TRIG3:
                    showSnackBar();
                    break;
                default:
                    break;
            }
        }
    }

    private void showSnackBar() {
        final Snackbar snackbar = Snackbar.make(findViewById(R.id.mainFrame),
                Constants.RATE_APP_SNACK_TEXT, Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction(Constants.YES, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        snackbar.dismiss();
                        rateApp();
                    }
                });
        snackbar.show();
    }

    public Snackbar showNoLoggingSnackBar(String snackMsg) {
        mSnackbar = Snackbar.make(findViewById(R.id.mainFrame), snackMsg
                , Snackbar.LENGTH_INDEFINITE);
        mSnackbar.setAction(Constants.FIX, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mSnackbar.dismiss();
                        startSettingsActivity();
                    }
                });
        mSnackbar.show();
        return mSnackbar;
    }

    private void rateApp() {
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.putBoolean(Constants.PREF_APP_RATED, true);
        editor.apply();
        startActivity(new Intent(Intent.ACTION_VIEW,
                Uri.parse("http://play.google.com/store/" + "apps/details?id="
                        + getApplicationContext().getPackageName())));
    }

    @Override
    public void onSaveInstanceState(Bundle outState){
        outState.putString(Constants.EXTRA_ATTACHED_FRAGMENT, String.valueOf(actionBar.getTitle()));
        super.onSaveInstanceState(outState);
    }

    private void setNetworkHeaderText() {
        TextView networkName = (TextView) findViewById(R.id.mainHeadNetworkName);
        TextView networkInfo = (TextView) findViewById(R.id.mainHeadNetworkInfo);

        if(mNetworkOperatorName.equals(Constants.NOT_CONNECTED)
                || mNetworkOperatorName.equals(Constants.PLEASE_WAIT)) {
            networkInfo.setVisibility(View.GONE);
            networkName.setText(mNetworkOperatorName);
        } else {
            networkName.setText(mNetworkOperatorName);
            if(mNetworkSignalStrength.equals(Constants.NETWORK_SIGNAL_STRENGTH_UNKNOWN.concat(" ")
                    .concat(Constants.SIGNAL_STRENGTH_UNITS))
                    || mNetworkTypeName.equals(Constants.NETWORK_TYPE_NAME_UNKNOWN)
                    || mNetworkSignalStrength.equals(Constants.NETWORK_SIGNAL_STRENGTH_UNKNOWN)){
                networkInfo.setVisibility(View.GONE);
            } else {
                networkInfo.setText(mNetworkTypeName.concat(" ").concat(mNetworkSignalStrength));
                networkInfo.setVisibility(View.VISIBLE);
            }
        }
    }

    public static String get(Context context, String key) {
        String ret = "";

        try {
            ClassLoader cl = context.getClassLoader();
            @SuppressWarnings("rawtypes")
            Class SystemProperties = cl.loadClass("android.os.SystemProperties");

            //Parameters Types
            @SuppressWarnings("rawtypes")
            Class[] paramTypes= new Class[1];
            paramTypes[0]= String.class;

            Method get = SystemProperties.getMethod("get", paramTypes);

            //Parameters
            Object[] params = new Object[1];
            params[0] = key;

            ret = (String) get.invoke(SystemProperties, params);
        } catch(Exception e) {
            ret = "";
            //TODO : Error handling
        }

        return ret;
    }

    private void setWifiHeaderText() {
        TextView tvSSID = (TextView) findViewById(R.id.mainHeadWifiSsid);
        TextView tvWifiInfo = (TextView) findViewById(R.id.mainHeadWifiInfo);
        if (mSSID.equals(Constants.WIFI_UNKNOWN_SSID)
                || mSSID.equals(Constants.CONNECTING)
                || mSSID.equals(Constants.PLEASE_WAIT)){
            if(mSSID.equals(Constants.WIFI_UNKNOWN_SSID)){
                tvSSID.setText(Constants.NOT_CONNECTED);
            } else {
                tvSSID.setText(mSSID);
            }
            tvWifiInfo.setVisibility(View.GONE);
        } else {
            tvSSID.setText(mSSID);
            tvWifiInfo.setVisibility(View.VISIBLE);
            tvWifiInfo.setText(
                    mLinkSpeed.concat(" ".concat(mRSSI)));
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if(id == R.id.action_settings){
            startSettingsActivity();
        } else if(id == R.id.action_fake_notif){
            FiMonitorNotifManager fiMonitorNotifManager = new FiMonitorNotifManager(getApplicationContext());
            fiMonitorNotifManager.createTriggerNotification("test","test",System.currentTimeMillis()
                    ,"test", "test",System.currentTimeMillis(),"test","test");
        } else if(id == R.id.action_rate_app){
            rateApp();
        }

        return super.onOptionsItemSelected(item);
    }

    private void startSettingsActivity() {
        Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        //Create local vars
        Fragment fragment = null;
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;

        if (id == R.id.nav_history) {
            fragment = new HistoryFragment();
            actionBar.setTitle(Constants.HISTORY_FRAG_TITLE);
        } else if(id == R.id.nav_dialer_codes){
            fragment = new DialerCodesFragment();
            actionBar.setTitle(Constants.DIALER_CODES_FRAG_TITLE);
        } else if(id == R.id.nav_notifications){
            fragment = new NotificationsFragment();
            actionBar.setTitle(Constants.NOTIFICATIONS_FRAG_TITLE);
        } else if(id == R.id.nav_statistics){
            fragment = new StatisticsFragment();
            actionBar.setTitle(Constants.STATISTICS_FRAG_TITLE);
        } else if (id == R.id.nav_history_map) {
            fragment = new HistoryMapFragment();
            actionBar.setTitle(Constants.HISTORY_MAP_FRAG_TITLE);
        }

        if(mSnackbar != null && mSnackbar.isShown()){
            mSnackbar.dismiss();
        }

        ft.replace(R.id.mainFrame, fragment);
        ft.commit();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    @Override
    public void onPause(){
        if(mReceiverRunning) {
            unregisterReceiver(mReceiver);
            mReceiverRunning = false;
        }
        mTelephonyManager.listen(mPhoneStateListener, PhoneStateListener.LISTEN_NONE);
        super.onPause();
    }

    @Override
    public void onResume(){
        super.onResume();

        FiMonitor fiMonitor = new FiMonitor(getApplicationContext());
        mSSID = fiMonitor.getWifiSsid();
        mRSSI = fiMonitor.getWifiSignalStrength();
        mLinkSpeed = fiMonitor.getWifiLinkSpeed();
        mNetworkOperator = fiMonitor.getNetworkOperator();
        mNetworkOperatorName = fiMonitor.getNetworkOperatorName(mNetworkOperator);
        mNetworkTypeName = fiMonitor.getNetworkTypeName();
        mNetworkSignalStrength = fiMonitor.getNetworkSignalStrength();

        boolean statsInitialized = mPrefs.getBoolean(Constants.PREF_STATS_EXIST, false);
        if(!statsInitialized){
            new DBTask().execute(new FiMonitorDbRunnable() {
                @Override
                public void executeDBTask() {
                    FiMonitorDbHelper dbHelper = new FiMonitorDbHelper(getApplicationContext());
                    int statsRowCount = dbHelper.getStatsRowCount();
                    if(statsRowCount == 0) {
                        dbHelper.insertStatRow(mNetworkOperator);
                    }
                }

                @Override
                public void postExecuteDBTask() {
                    SharedPreferences.Editor editor = mPrefs.edit();
                    editor.putBoolean(Constants.PREF_STATS_EXIST, true);
                    editor.apply();
                }
            });
        }

        setWifiHeaderText();
        setNetworkHeaderText();

        if(!mVarsInitialized){
            SharedPreferences.Editor editor = mPrefs.edit();
            editor.putString(Constants.PREF_CELL_LAST_OPERATOR, mNetworkOperator);
            editor.putString(Constants.PREF_LAST_NETWORK_TYPE, mNetworkTypeName);
            editor.putString(Constants.PREF_WIFI_LAST_SSID, mSSID);
            editor.putInt(Constants.PREF_DB_VERSION_NUM, FiMonitorContract.DATABASE_VERSION);
            editor.putBoolean(Constants.PREF_VARS_INITIALIZED, true);
            if(mSSID.equals(Constants.WIFI_UNKNOWN_SSID)){
                editor.putString(Constants.PREF_WIFI_CONN_STATE, Constants.PREF_WIFI_CONN_FALSE);
            } else {
                editor.putString(Constants.PREF_WIFI_CONN_STATE, Constants.PREF_WIFI_CONN_TRUE);
            }
            editor.apply();
        }

        WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
        wifiManager.startScan();

        mPhoneStateListener = new MyPhoneStateListener(getApplicationContext());
        mTelephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        mTelephonyManager.listen(mPhoneStateListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);

        registerMyReceiver();

        int googleApiErrorCode = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(
                getApplicationContext());

        if(googleApiErrorCode != ConnectionResult.SUCCESS){
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(this,googleApiErrorCode,1);
            if(dialog != null){
                dialog.show();
            }
        }
}

    private void registerMyReceiver() {

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.RSSI_CHANGED_ACTION);
        intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        intentFilter.addAction(Constants.NETWORK_SIGNAL_STRENGTH_CHANGE_INTENT_ACTION);
        intentFilter.addAction(Constants.NETWORK_STATE_CHANGE_INTENT_ACTION);

        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(!isInitialStickyBroadcast()) {
                    if (intent.getAction().equals(WifiManager.RSSI_CHANGED_ACTION)) {
                        mRSSI = String.valueOf(intent.getIntExtra(WifiManager.EXTRA_NEW_RSSI, 0))
                                .concat(" ").concat(Constants.SIGNAL_STRENGTH_UNITS);
                        setWifiHeaderText();
                    } else if (intent.getAction().equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
                        NetworkInfo networkInfo = intent.getParcelableExtra(
                                WifiManager.EXTRA_NETWORK_INFO);
                        if (networkInfo.getState().equals(NetworkInfo.State.CONNECTED)) {
                            WifiInfo wifiInfo = intent.getParcelableExtra(WifiManager.EXTRA_WIFI_INFO);
                            mSSID = wifiInfo.getSSID();
                            mSSID = mSSID.substring(1, mSSID.length() - 1);
                            mRSSI = String.valueOf(wifiInfo.getRssi()).concat(" ")
                                    .concat(Constants.SIGNAL_STRENGTH_UNITS);
                            mLinkSpeed = String.valueOf(wifiInfo.getLinkSpeed()).concat(" ")
                                    .concat(WifiInfo.LINK_SPEED_UNITS);
                        } else if (networkInfo.getState().equals(NetworkInfo.State.CONNECTING)) {
                            mSSID = Constants.CONNECTING;
                        } else {
                            mSSID = Constants.WIFI_UNKNOWN_SSID;
                        }
                        setWifiHeaderText();
                    } else if (intent.getAction().equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
                        FiMonitor fiMonitor = new FiMonitor(context);
                        if (fiMonitor.wifiConnected()) {
                            mSSID = fiMonitor.getWifiSsid();
                            mRSSI = fiMonitor.getWifiSignalStrength();
                            mLinkSpeed = fiMonitor.getWifiLinkSpeed();
                        } else {
                            mSSID = Constants.WIFI_UNKNOWN_SSID;
                        }
                        setWifiHeaderText();
                    } else if (intent.getAction().equals(Constants.NETWORK_SIGNAL_STRENGTH_CHANGE_INTENT_ACTION)){
                        FiMonitor fiMonitor = new FiMonitor(context);
                        mNetworkSignalStrength = fiMonitor.getNetworkSignalStrength();
                        mNetworkTypeName = fiMonitor.getNetworkTypeName();
                        setNetworkHeaderText();
                    } else if (intent.getAction().equals(Constants.NETWORK_STATE_CHANGE_INTENT_ACTION)) {
                        Log.d("MainActivity:", "Received network state change intent.");
                        FiMonitor fiMonitor = new FiMonitor(context);
                        mNetworkOperator = fiMonitor.getNetworkOperator();
                        mNetworkOperatorName = fiMonitor.getNetworkOperatorName(mNetworkOperator);
                        mNetworkTypeName = fiMonitor.getNetworkTypeName();
                        mNetworkSignalStrength = fiMonitor.getNetworkSignalStrength();
                        setNetworkHeaderText();
                    }
                }
            }
        };
        registerReceiver(mReceiver, intentFilter);
        mReceiverRunning = true;
    }
}
