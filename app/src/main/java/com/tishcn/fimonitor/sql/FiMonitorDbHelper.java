package com.tishcn.fimonitor.sql;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by leona on 6/30/2016.
 */
public class FiMonitorDbHelper extends SQLiteOpenHelper {

    public FiMonitorDbHelper(Context context) {
        super(context, FiMonitorContract.DATABASE_NAME, null, FiMonitorContract.DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(FiMonitorContract.SQL_CREATE_HISTORY);
        db.execSQL(FiMonitorContract.SQL_CREATE_STATS);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if(oldVersion < 3) {
            db.execSQL(FiMonitorContract.SQL_DROP_HISTORY_TABLE);
            db.execSQL(FiMonitorContract.SQL_DROP_STATS_TABLE);
            onCreate(db);
        } else if(oldVersion == 4) {
            db.execSQL(FiMonitorContract.SQL_ALTER_HISTORY_ADD_FROM_LAT);
            db.execSQL(FiMonitorContract.SQL_ALTER_HISTORY_ADD_FROM_LONG);
            db.execSQL(FiMonitorContract.SQL_ALTER_HISTORY_ADD_TO_LAT);
            db.execSQL(FiMonitorContract.SQL_ALTER_HISTORY_ADD_TO_LONG);
        }
    }

    public long insertHistoryRow(String type, String action, long fromDTM, String fromMSG, String fromMCCMNC){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        long time = System.currentTimeMillis();
        values.put(FiMonitorContract.HistoryTable.COLUMN_NAME_HIST_TYPE, type);
        values.put(FiMonitorContract.HistoryTable.COLUMN_NAME_HIST_ACTION, action);
        values.put(FiMonitorContract.HistoryTable.COLUMN_NAME_HIST_FROM_DTM, fromDTM);
        values.put(FiMonitorContract.HistoryTable.COLUMN_NAME_HIST_FROM_MSG, fromMSG);
        values.put(FiMonitorContract.HistoryTable.COLUMN_NAME_HIST_FROM_MCCMNC, fromMCCMNC);
        values.put(FiMonitorContract.HistoryTable.COLUMN_NAME_HIST_LAST_UPDATE_DTM, time);
        values.put(FiMonitorContract.HistoryTable.COLUMN_NAME_HIST_ROW_CLOSED, "N");
        long intInsertId = db.insert(FiMonitorContract.HistoryTable.TABLE_NAME, null, values);
        db.close();
        return intInsertId;
    }

    public long insertHistoryRowToOnly(String type, String action, long toDTM, String toMSG, String toMCCMNC){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        long time = System.currentTimeMillis();
        values.put(FiMonitorContract.HistoryTable.COLUMN_NAME_HIST_TYPE, type);
        values.put(FiMonitorContract.HistoryTable.COLUMN_NAME_HIST_ACTION, action);
        values.put(FiMonitorContract.HistoryTable.COLUMN_NAME_HIST_TO_DTM, toDTM);
        values.put(FiMonitorContract.HistoryTable.COLUMN_NAME_HIST_TO_MSG, toMSG);
        values.put(FiMonitorContract.HistoryTable.COLUMN_NAME_HIST_TO_MCCMNC, toMCCMNC);
        values.put(FiMonitorContract.HistoryTable.COLUMN_NAME_HIST_LAST_UPDATE_DTM, time);
        values.put(FiMonitorContract.HistoryTable.COLUMN_NAME_HIST_ROW_CLOSED, "N");
        long intInsertId = db.insert(FiMonitorContract.HistoryTable.TABLE_NAME, null, values);
        db.close();
        return intInsertId;
    }

    public void updateHistoryRow(int id, String action, long toDTM, String toMSG, String toMCCMNC){
        SQLiteDatabase db = getWritableDatabase();
        String[] selectionArgs = {String.valueOf(id)};
        String selection = FiMonitorContract.HistoryTable.COLUMN_NAME_HIST_ID.concat("=?");
        long time = System.currentTimeMillis();
        ContentValues values = new ContentValues();
        values.put(FiMonitorContract.HistoryTable.COLUMN_NAME_HIST_ACTION, action);
        values.put(FiMonitorContract.HistoryTable.COLUMN_NAME_HIST_TO_DTM, toDTM);
        values.put(FiMonitorContract.HistoryTable.COLUMN_NAME_HIST_TO_MSG, toMSG);
        values.put(FiMonitorContract.HistoryTable.COLUMN_NAME_HIST_TO_MCCMNC, toMCCMNC);
        values.put(FiMonitorContract.HistoryTable.COLUMN_NAME_HIST_LAST_UPDATE_DTM, time);
        values.put(FiMonitorContract.HistoryTable.COLUMN_NAME_HIST_ROW_CLOSED, "Y");
        db.update(FiMonitorContract.HistoryTable.TABLE_NAME, values, selection, selectionArgs);
        db.close();
    }

    public void updateHistoryRowLocation(long id, String latitude, String longitude){
        SQLiteDatabase db = getWritableDatabase();
        String[] selectionArgs = {String.valueOf(id)};
        String selection = FiMonitorContract.HistoryTable.COLUMN_NAME_HIST_ID.concat("=?");
        ContentValues values = new ContentValues();
        values.put(FiMonitorContract.HistoryTable.COLUMN_NAME_HIST_FROM_LATITUDE, latitude);
        values.put(FiMonitorContract.HistoryTable.COLUMN_NAME_HIST_FROM_LONGITUDE, longitude);
        values.put(FiMonitorContract.HistoryTable.COLUMN_NAME_HIST_TO_LATITUDE, latitude);
        values.put(FiMonitorContract.HistoryTable.COLUMN_NAME_HIST_TO_LONGITUDE, longitude);
        db.update(FiMonitorContract.HistoryTable.TABLE_NAME, values, selection, selectionArgs);
        db.close();
    }

    public Cursor getHistoryRowById(int id) {
        SQLiteDatabase db = getReadableDatabase();
        String[] selectionArgs = {String.valueOf(id)};
        String selection = FiMonitorContract.HistoryTable.COLUMN_NAME_HIST_ID.concat("=?");
        Cursor mCursor = db.query(
                FiMonitorContract.HistoryTable.TABLE_NAME,
                FiMonitorContract.HistoryTable.ALL_COLUMN_NAMES,
                selection,
                selectionArgs,
                null,
                null,
                null
        );
        mCursor.moveToFirst();
        db.close();
        return  mCursor;
    }


    public int getMaxHistRowIdByType(String type){
        SQLiteDatabase db = getReadableDatabase();
        String[] selectionArgs = {type};
        String selection = FiMonitorContract.HistoryTable.COLUMN_NAME_HIST_TYPE.concat("=?");
        int id = 0;
        Cursor mCursor = db.query(FiMonitorContract.HistoryTable.TABLE_NAME
                , FiMonitorContract.HistoryTable.ALL_COLUMN_NAMES
                , selection
                , selectionArgs
                , null
                , null
                , FiMonitorContract.HistoryTable.COLUMN_NAME_HIST_ID.concat(" DESC"));
        mCursor.moveToFirst();
        if(mCursor.getCount() > 0) {
            id = mCursor.getInt(
                    mCursor.getColumnIndex(FiMonitorContract.HistoryTable.COLUMN_NAME_HIST_ID));
        }
        mCursor.close();
        db.close();
        return id;
    }

    public boolean isHistoryRowFull(int id){
        boolean full = false;
        Cursor mCursor = getHistoryRowById(id);
        if(mCursor.getString(
                mCursor.getColumnIndex(
                        FiMonitorContract.HistoryTable.COLUMN_NAME_HIST_ROW_CLOSED)).equals("Y")){
            full = true;
        }
        mCursor.close();
        return full;
    }

    public Cursor getHistoryRows() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor mCursor = db.query(FiMonitorContract.HistoryTable.TABLE_NAME
                , FiMonitorContract.HistoryTable.ALL_COLUMN_NAMES
                , null
                , null
                , null
                , null
                , FiMonitorContract.HistoryTable.COLUMN_NAME_HIST_LAST_UPDATE_DTM.concat(" DESC"));
        mCursor.moveToFirst();
        db.close();
        return  mCursor;
    }

    public Cursor getHistoryRowsWithLocation(){
        SQLiteDatabase db = getReadableDatabase();
        Cursor mCursor = db.query(FiMonitorContract.HistoryTable.TABLE_NAME
                , FiMonitorContract.HistoryTable.ALL_COLUMN_NAMES
                , FiMonitorContract.HistoryTable.COLUMN_NAME_HIST_TO_LATITUDE + " IS NOT NULL AND "
                        + FiMonitorContract.HistoryTable.COLUMN_NAME_HIST_TO_LONGITUDE
                        + " IS NOT NULL"
                , null
                , null
                , null
                , FiMonitorContract.HistoryTable.COLUMN_NAME_HIST_LAST_UPDATE_DTM.concat(" DESC"));
        mCursor.moveToFirst();
        db.close();
        return mCursor;
    }

    public int getHistoryRowCount(){
        SQLiteDatabase db = getReadableDatabase();
        Cursor mCursor = db.query(FiMonitorContract.HistoryTable.TABLE_NAME
                , FiMonitorContract.HistoryTable.ALL_COLUMN_NAMES
                , null
                , null
                , null
                , null
                , FiMonitorContract.HistoryTable.COLUMN_NAME_HIST_LAST_UPDATE_DTM.concat(" DESC"));
        mCursor.moveToFirst();
        int count = mCursor.getCount();
        db.close();
        mCursor.close();
        return count;
    }

    public int getHistoryRowsWithLocationCount(){
        SQLiteDatabase db = getReadableDatabase();
        Cursor mCursor = db.query(FiMonitorContract.HistoryTable.TABLE_NAME
                , FiMonitorContract.HistoryTable.ALL_COLUMN_NAMES
                , FiMonitorContract.HistoryTable.COLUMN_NAME_HIST_TO_LATITUDE + " IS NOT NULL AND "
                        + FiMonitorContract.HistoryTable.COLUMN_NAME_HIST_TO_LONGITUDE
                        + " IS NOT NULL"
                , null
                , null
                , null
                , FiMonitorContract.HistoryTable.COLUMN_NAME_HIST_LAST_UPDATE_DTM.concat(" DESC"));
        mCursor.moveToFirst();
        int count = mCursor.getCount();
        db.close();
        mCursor.close();
        return count;
    }

    public void deleteHistoryRowsByLimit(int logLimit){
        SQLiteDatabase db = getWritableDatabase();
        db.delete(FiMonitorContract.HistoryTable.TABLE_NAME
                , FiMonitorContract.HistoryTable.COLUMN_NAME_HIST_ID + " NOT IN ( SELECT "
                        + FiMonitorContract.HistoryTable.COLUMN_NAME_HIST_ID + " FROM "
                        + FiMonitorContract.HistoryTable.TABLE_NAME + " ORDER BY "
                        + FiMonitorContract.HistoryTable.COLUMN_NAME_HIST_LAST_UPDATE_DTM
                        + " DESC LIMIT 0," + logLimit + " )"
                , null);
        db.close();
    }

    public void deleteAllHistoryRows(){
        SQLiteDatabase db = getWritableDatabase();
        db.delete(FiMonitorContract.HistoryTable.TABLE_NAME, null, null);
        db.close();
    }

    public long insertStatRow(String mccmnc){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(FiMonitorContract.StatsTable.COLUMN_NAME_STAT_MCCMNC, mccmnc);
        values.put(FiMonitorContract.StatsTable.COLUMN_NAME_STAT_START_TIME, System.currentTimeMillis());
        long intInsertId = db.insert(FiMonitorContract.StatsTable.TABLE_NAME, null, values);
        db.close();
        return intInsertId;
    }

    public void updateStatsRow(int id){
        SQLiteDatabase db = getWritableDatabase();
        String[] selectionArgs = {String.valueOf(id)};
        String selection = FiMonitorContract.StatsTable.COLUMN_NAME_STAT_ID.concat("=?");
        ContentValues values = new ContentValues();
        values.put(FiMonitorContract.StatsTable.COLUMN_NAME_STAT_END_TIME, System.currentTimeMillis());
        db.update(FiMonitorContract.StatsTable.TABLE_NAME, values, selection, selectionArgs);
        db.close();
    }

    public Cursor getStatsRows() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor mCursor = db.query(FiMonitorContract.StatsTable.TABLE_NAME
                , FiMonitorContract.StatsTable.ALL_COLUMN_NAMES
                , null
                , null
                , null
                , null
                , FiMonitorContract.StatsTable.COLUMN_NAME_STAT_ID.concat(" ASC"));
        mCursor.moveToFirst();
        db.close();
        return  mCursor;
    }

    public int getMaxStatsRowId(){
        SQLiteDatabase db = getReadableDatabase();
        int id = 0;
        Cursor mCursor = db.query(FiMonitorContract.StatsTable.TABLE_NAME
                , FiMonitorContract.StatsTable.ALL_COLUMN_NAMES
                , null
                , null
                , null
                , null
                , FiMonitorContract.StatsTable.COLUMN_NAME_STAT_ID.concat(" DESC"));
        mCursor.moveToFirst();
        if(mCursor.getCount() > 0) {
            id = mCursor.getInt(
                    mCursor.getColumnIndex(FiMonitorContract.StatsTable.COLUMN_NAME_STAT_ID));
        }
        mCursor.close();
        db.close();
        return id;
    }

    public int getStatsRowCount(){
        SQLiteDatabase db = getReadableDatabase();
        Cursor mCursor = db.query(FiMonitorContract.StatsTable.TABLE_NAME
                , FiMonitorContract.StatsTable.ALL_COLUMN_NAMES
                , null
                , null
                , null
                , null
                , FiMonitorContract.StatsTable.COLUMN_NAME_STAT_ID.concat(" DESC"));
        mCursor.moveToFirst();
        int count = mCursor.getCount();
        db.close();
        mCursor.close();
        return count;
    }

    public void deleteAllStatRows(){
        SQLiteDatabase db = getWritableDatabase();
        db.delete(FiMonitorContract.StatsTable.TABLE_NAME, null, null);
        db.close();
    }
}
