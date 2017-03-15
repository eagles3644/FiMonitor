package com.tishcn.fimonitor.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.tishcn.fimonitor.R;
import com.tishcn.fimonitor.sql.DBTask;
import com.tishcn.fimonitor.sql.FiMonitorDbHelper;
import com.tishcn.fimonitor.sql.FiMonitorDbRunnable;
import com.tishcn.fimonitor.util.Constants;

/**
 * Created by leona on 6/29/2016.
 */
public class HistoryFragment extends Fragment {

    private BroadcastReceiver mReceiver;
    private FiMonitorDbHelper mDatabase;
    private Cursor mCursor;
    private ListView mHistList;
    private RelativeLayout mNoHistView;
    private HistoryAdapter mHistAdapter;
    private boolean mReceiverRunning = false;

    public HistoryFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_history, container, false);

        mHistList = (ListView) rootView.findViewById(R.id.histListView);
        mNoHistView = (RelativeLayout) rootView.findViewById(R.id.histNoHistView);

        return rootView;
    }

    @Override
    public void onPause(){
        if(mReceiverRunning){
            getActivity().unregisterReceiver(mReceiver);
            mReceiverRunning = false;
        }
        super.onPause();
    }

    @Override
    public void onDestroy(){
        if(mReceiverRunning){
            getActivity().unregisterReceiver(mReceiver);
            mReceiverRunning = false;
        }
        super.onDestroy();
    }

    @Override
    public void onResume(){
        super.onResume();

        //initialize database helper
        mDatabase = new FiMonitorDbHelper(getContext());

        //Initialize listView
        initializeListView();

    }

    private void initializeListView (){
        //var for history row count
        final int[] histCount = new int[1];

        //get history cursor
        new DBTask().execute(new FiMonitorDbRunnable() {
            @Override
            public void executeDBTask() {
                histCount[0] = mDatabase.getHistoryRowCount();
            }

            @Override
            public void postExecuteDBTask() {
                if (histCount[0] == 0) {
                    showHistoryRecycler(false);
                    registerReceiver();
                } else {
                    new DBTask().execute(new FiMonitorDbRunnable() {
                        @Override
                        public void executeDBTask() {
                            mCursor = mDatabase.getHistoryRows();
                        }

                        @Override
                        public void postExecuteDBTask() {
                            showHistoryRecycler(true);
                            mHistAdapter = new HistoryAdapter(getContext(), mCursor, 0);
                            mHistList.setAdapter(mHistAdapter);
                            mHistList.setClickable(true);
                            mHistList.setLongClickable(false);
                            mHistList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                                    refreshHistory();
                                }
                            });
                            mHistList.setDividerHeight(0);
                            registerReceiver();
                        }
                    });
                }

            }
        });
    }

    private void registerReceiver(){
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.HISTORY_CHANGE_INTENT_ACTION);
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(Constants.HISTORY_CHANGE_INTENT_ACTION)) {
                    refreshHistory();
                }
            }
        };
        getActivity().registerReceiver(mReceiver, intentFilter);
        mReceiverRunning = true;
    }

    private void refreshHistory(){
        final Cursor[] newCursor = new Cursor[1];
        final int[] histCount = new int[1];
        new DBTask().execute(new FiMonitorDbRunnable() {
            @Override
            public void executeDBTask() {
                newCursor[0] = mDatabase.getHistoryRows();
                histCount[0] = mDatabase.getHistoryRowCount();
            }

            @Override
            public void postExecuteDBTask() {
                if (histCount[0] == 0) {
                    showHistoryRecycler(false);
                } else {
                    if(mCursor == null || mHistAdapter == null){
                        mCursor = newCursor[0];
                        mHistAdapter = new HistoryAdapter(getContext(), mCursor, 0);
                        mHistList.setAdapter(mHistAdapter);
                        mHistList.setClickable(true);
                        mHistList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                refreshHistory();
                            }
                        });
                        mHistList.setLongClickable(false);
                        mHistList.setDividerHeight(0);
                    } else {
                        mHistAdapter.swapCursor(newCursor[0]);
                    }
                    showHistoryRecycler(true);
                }
            }
        });
    }

    private void showHistoryRecycler(boolean show) {
        if(show){
            mNoHistView.setVisibility(View.GONE);
            mHistList.setVisibility(View.VISIBLE);
        } else {
            mHistList.setVisibility(View.GONE);
            mNoHistView.setVisibility(View.VISIBLE);
        }
    }

}
