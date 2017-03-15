package com.tishcn.fimonitor.ui;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.tishcn.fimonitor.R;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.settingsFrame, new SettingsFragment());
        ft.commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if(item.getItemId() ==  android.R.id.home){
            onBackPressed();
            return true;
        }
        return false;
    }
}
