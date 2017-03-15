package com.tishcn.fimonitor.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.MultiSelectListPreference;
import android.preference.Preference;
import android.preference.PreferenceGroup;
import android.widget.Toast;

import com.tishcn.fimonitor.R;
import com.tishcn.fimonitor.sql.DBTask;
import com.tishcn.fimonitor.sql.FiMonitorDbHelper;
import com.tishcn.fimonitor.sql.FiMonitorDbRunnable;
import com.tishcn.fimonitor.util.Constants;

import java.util.Set;

/**
 * Created by leona on 7/10/2016.
 */
public class SettingsFragment extends MyPreferenceFragment implements
        SharedPreferences.OnSharedPreferenceChangeListener {

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
        initSummary(getPreferenceScreen());
        Preference clearAllHistPref = getPreferenceScreen().getPreferenceManager()
                .findPreference(Constants.PREF_CLEAR_ALL_HISTORY);
        clearAllHistPref.setOnPreferenceClickListener(mPrefClickListener);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
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
            Set<String> selections = multiSelectListPreference.getValues();
            int selectionSize = selections.size();
            if(selectionSize > 0) {
                summary = "";
                String[] selectionsArray = selections.toArray(new String[selections.size()]);
                for (int i = 0; i < selectionSize; i++) {
                    summary = summary.concat(", ").concat(selectionsArray[i]);
                }
                summary = summary.substring(2);
            }
            p.setSummary(summary);
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        initSummary(getPreferenceScreen());
        if(key.equals(Constants.PREF_HIST_ROW_LIMIT)){
            final int rowLimit = Integer.parseInt(sharedPreferences.getString(key,
                    Constants.PREF_HIST_ROW_LIMIT_DEFAULT_VALUE));
            final int[] rowCount = new int[1];
            final FiMonitorDbHelper db = new FiMonitorDbHelper(getContext());
            new DBTask().execute(new FiMonitorDbRunnable() {
                @Override
                public void executeDBTask() {
                    rowCount[0] = db.getHistoryRowCount();
                }

                @Override
                public void postExecuteDBTask() {
                    if(rowCount[0] > rowLimit){
                        new DBTask().execute(new FiMonitorDbRunnable() {
                            @Override
                            public void executeDBTask() {
                                db.deleteHistoryRowsByLimit(rowLimit);
                            }

                            @Override
                            public void postExecuteDBTask() {
                                Intent mIntent = new Intent(Constants.HISTORY_CHANGE_INTENT_ACTION);
                                getContext().sendBroadcast(mIntent);
                            }
                        });
                    }
                }
            });
        }
    }


    Preference.OnPreferenceClickListener  mPrefClickListener = new
            Preference.OnPreferenceClickListener() {
        @Override
        public boolean onPreferenceClick(Preference preference) {
            if (preference.getKey().equals(Constants.PREF_CLEAR_ALL_HISTORY)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage(Constants.HIST_CONFIRM_DELETE_ALL);
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        new DBTask().execute(new FiMonitorDbRunnable() {
                            @Override
                            public void executeDBTask() {
                                FiMonitorDbHelper db = new FiMonitorDbHelper(getContext());
                                db.deleteAllHistoryRows();
                            }

                            @Override
                            public void postExecuteDBTask() {
                                Toast.makeText(getContext(), "Cleared Event Log", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(Constants.HISTORY_CHANGE_INTENT_ACTION);
                                getContext().sendBroadcast(intent);
                            }
                        });
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });
                builder.show();
            }
            return true;
        }
    };

}
