package com.tishcn.fimonitor.ui;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.tishcn.fimonitor.R;
import com.tishcn.fimonitor.sql.DBTask;
import com.tishcn.fimonitor.sql.FiMonitorContract;
import com.tishcn.fimonitor.sql.FiMonitorDbHelper;
import com.tishcn.fimonitor.sql.FiMonitorDbRunnable;
import com.tishcn.fimonitor.util.Constants;

public class SplashActivity extends AppCompatActivity {

    private SharedPreferences mPrefs;
    private boolean mFiAppInstalled;
    private TextView mTextView;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);

        mPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        mTextView = (TextView) findViewById(R.id.splashTextView);
        mProgressBar = (ProgressBar) findViewById(R.id.splashProgressBar);

    }

    @Override
    public void onResume(){

        mFiAppInstalled = isPackageInstalled(Constants.GOOGLE_FI_APP_PACKAGE);

        if (hasAllPermissions()) {
            goToMainActivity();
        } else {
            final AppCompatActivity activity = this;
            mTextView.setText(R.string.no_location_permission);
            String snackText = "";
            String[] permString = new String[0];
            if(!hasLocationPermission() && !hasPhonePermission()){
                snackText = Constants.PHONE_AND_LOCATION_PERMISSION;
                permString = new String[] {Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.READ_PHONE_STATE};
            } else if(!hasLocationPermission()){
                snackText = Constants.LOCATION_PERMISSION;
                permString = new String[] {Manifest.permission.ACCESS_FINE_LOCATION};
            } else if(!hasPhonePermission()){
                snackText = Constants.PHONE_PERMISSION;
                permString = new String[] {Manifest.permission.READ_PHONE_STATE};
            }
            final String[] finalPermString = permString;
            Snackbar.make(findViewById(android.R.id.content),
                    snackText, Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.give_permission, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ActivityCompat.requestPermissions(activity, finalPermString, 5);
                        }
                    })
                    .show();
        }

        super.onResume();
    }

    private boolean isUpgrading() {

        boolean upgrading = false;
        int curDbVersion = FiMonitorContract.DATABASE_VERSION;
        int lastDbVersion = mPrefs.getInt(Constants.PREF_DB_VERSION_NUM, 0);

        if(curDbVersion > lastDbVersion){
            upgrading = true;
        }

        return upgrading;
    }

    private boolean hasLocationPermission(){
        return ActivityCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private boolean hasPhonePermission(){
        return ActivityCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED;
    }

    private boolean hasAllPermissions() {
        return ActivityCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED;
    }

    private void goToMainActivity(){
        if(mFiAppInstalled){

            SharedPreferences.Editor editor = mPrefs.edit();

            if(isUpgrading() && mPrefs.getBoolean(Constants.PREF_VARS_INITIALIZED, false)){
                mProgressBar.setVisibility(View.VISIBLE);
                mTextView.setText(Constants.UPGRADING);

                editor.putInt(Constants.PREF_DB_VERSION_NUM, FiMonitorContract.DATABASE_VERSION);
                editor.remove(Constants.PREF_VARS_INITIALIZED);
                editor.apply();

                final FiMonitorDbHelper dbHelper = new FiMonitorDbHelper(getApplicationContext());
                new DBTask().execute(new FiMonitorDbRunnable() {
                    @Override
                    public void executeDBTask() {
                        int count = dbHelper.getHistoryRowCount();
                    }

                    @Override
                    public void postExecuteDBTask() {
                        delayedStartNextActivity();
                    }
                });

            } else {
                if (isUpgrading()) {
                    editor.putInt(Constants.PREF_DB_VERSION_NUM, FiMonitorContract.DATABASE_VERSION);
                    editor.apply();
                }
                delayedStartNextActivity();
            }

        } else {
            mTextView.setText(R.string.fi_app_not_installed);
            Snackbar.make(findViewById(android.R.id.content),
                    Constants.INSTALL_FI_APP, Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.install, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            try {
                                startActivity(new Intent(Intent.ACTION_VIEW,
                                        Uri.parse("market://details?id=".concat(
                                                Constants.GOOGLE_FI_APP_PACKAGE))));
                            } catch (android.content.ActivityNotFoundException anfe) {
                                startActivity(new Intent(Intent.ACTION_VIEW,
                                        Uri.parse("https://play.google.com/store/apps/details?id="
                                                .concat(Constants.GOOGLE_FI_APP_PACKAGE))));
                            }
                        }
                    })
                    .show();
        }
    }

    private void delayedStartNextActivity() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        }, Constants.SPLASH_DELAY_MILIS);
    }

    private void delayedFinish() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, Constants.SPLASH_DELAY_MILIS);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[]
            , @NonNull int[] grantResults) {
        switch (requestCode) {
            case 4: {
                SharedPreferences.Editor editor = mPrefs.edit();
                if (grantResults.length > 0 && grantResults[0]
                        == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(), "Thanks for permission!"
                            , Toast.LENGTH_SHORT).show();
                    editor.putBoolean(Constants.PREF_LOCATION_PERMISSION, true);
                    editor.apply();
                    goToMainActivity();
                } else {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this
                            , Manifest.permission.ACCESS_FINE_LOCATION)) {
                        editor.putBoolean(Constants.PREF_LOCATION_PERMISSION, false);
                        editor.apply();
                        Toast.makeText(getApplicationContext()
                                , "You can't use this app without granting " +
                                        "location permissions."
                                , Toast.LENGTH_LONG).show();
                    } else if(ActivityCompat.shouldShowRequestPermissionRationale(this
                            , Manifest.permission.READ_PHONE_STATE)) {
                        Toast.makeText(getApplicationContext()
                                , "You can't use this app without granting " +
                                        "access to read the phone state."
                                , Toast.LENGTH_LONG).show();
                    } else {
                        goToSettings();
                    }
                }
            }
        }
    }

    private void goToSettings() {
        Intent myAppSettings = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + getPackageName()));
        myAppSettings.addCategory(Intent.CATEGORY_DEFAULT);
        myAppSettings.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivityForResult(myAppSettings, 5);
    }

    private boolean isPackageInstalled(String uri) {
        PackageManager pm = getPackageManager();
        boolean appInstalled;
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            appInstalled = true;
        }
        catch (PackageManager.NameNotFoundException e) {
            appInstalled = false;
        }
        return appInstalled;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 5) {
            if (hasAllPermissions()) {
                Toast.makeText(getApplicationContext(), "Thanks for permission!"
                        , Toast.LENGTH_SHORT).show();
                goToMainActivity();
            } else {
                SharedPreferences.Editor editor = mPrefs.edit();
                String toastMsg = "";
                if(!hasLocationPermission() && !hasPhonePermission()){
                    editor.putBoolean(Constants.PREF_LOCATION_PERMISSION, false);
                    editor.putBoolean(Constants.PREF_PHONE_PERMISSION, false);
                    editor.apply();
                    toastMsg = "You can't use this app without granting location and phone permissions.";
                } else if(!hasLocationPermission()){
                    editor.putBoolean(Constants.PREF_LOCATION_PERMISSION, false);
                    editor.apply();
                    toastMsg = "You can't use this app without granting location permissions.";
                } else if(!hasPhonePermission()){
                    editor.putBoolean(Constants.PREF_PHONE_PERMISSION, false);
                    editor.apply();
                    toastMsg = "You can't use this app without granting phone permissions.";
                }
                Toast.makeText(getApplicationContext()
                        , toastMsg
                        , Toast.LENGTH_LONG).show();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
