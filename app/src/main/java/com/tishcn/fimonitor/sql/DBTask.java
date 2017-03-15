package com.tishcn.fimonitor.sql;

import android.os.AsyncTask;

public class DBTask extends AsyncTask<FiMonitorDbRunnable, Void, FiMonitorDbRunnable> {
    @Override
    protected FiMonitorDbRunnable doInBackground(FiMonitorDbRunnable... runnables){
        runnables[0].executeDBTask();
        return runnables[0];
    }

    @Override
    protected void onPostExecute(FiMonitorDbRunnable runnable){
        runnable.postExecuteDBTask();
    }
}
