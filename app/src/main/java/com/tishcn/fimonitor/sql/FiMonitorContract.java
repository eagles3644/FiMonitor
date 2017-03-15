package com.tishcn.fimonitor.sql;

import android.provider.BaseColumns;

/**
 * Created by leona on 6/30/2016.
 */
public final class FiMonitorContract {

    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 5;
    public static final String DATABASE_NAME = "FiMonitor.db";

    private static final String CREATE_TABLE = "CREATE TABLE ";
    private static final String ALTER_TABLE = "ALTER TABLE ";
    private static final String ADD = " ADD ";
    private static final String TEXT_TYPE = " TEXT";
    private static final String INTEGER_TYPE = " INTEGER";
    private static final String INT_PK = " INTEGER PRIMARY KEY";
    private static final String OPEN_PARENS = " (";
    private static final String COMMA_SEP = ",";
    private static final String CLOSE_PARENS = ");";
    private static final String DROP_TABLE = "DROP TABLE IF EXISTS ";

    public static final String SQL_CREATE_HISTORY =
            CREATE_TABLE + HistoryTable.TABLE_NAME +
                    OPEN_PARENS +
                    HistoryTable.COLUMN_NAME_HIST_ID + INT_PK + COMMA_SEP +
                    HistoryTable.COLUMN_NAME_HIST_TYPE + TEXT_TYPE + COMMA_SEP +
                    HistoryTable.COLUMN_NAME_HIST_ACTION + TEXT_TYPE + COMMA_SEP +
                    HistoryTable.COLUMN_NAME_HIST_FROM_DTM + INTEGER_TYPE + COMMA_SEP +
                    HistoryTable.COLUMN_NAME_HIST_FROM_MSG + TEXT_TYPE + COMMA_SEP +
                    HistoryTable.COLUMN_NAME_HIST_FROM_MCCMNC + TEXT_TYPE + COMMA_SEP +
                    HistoryTable.COLUMN_NAME_HIST_FROM_LATITUDE + INTEGER_TYPE + COMMA_SEP +
                    HistoryTable.COLUMN_NAME_HIST_FROM_LONGITUDE+ INTEGER_TYPE + COMMA_SEP +
                    HistoryTable.COLUMN_NAME_HIST_TO_DTM + INTEGER_TYPE + COMMA_SEP +
                    HistoryTable.COLUMN_NAME_HIST_TO_MSG + TEXT_TYPE + COMMA_SEP +
                    HistoryTable.COLUMN_NAME_HIST_TO_MCCMNC + TEXT_TYPE + COMMA_SEP +
                    HistoryTable.COLUMN_NAME_HIST_TO_LATITUDE + TEXT_TYPE + COMMA_SEP +
                    HistoryTable.COLUMN_NAME_HIST_TO_LONGITUDE+ TEXT_TYPE + COMMA_SEP +
                    HistoryTable.COLUMN_NAME_HIST_LAST_UPDATE_DTM + INTEGER_TYPE + COMMA_SEP +
                    HistoryTable.COLUMN_NAME_HIST_ROW_CLOSED + TEXT_TYPE +
                    CLOSE_PARENS;

    public static final String SQL_CREATE_STATS =
            CREATE_TABLE + StatsTable.TABLE_NAME +
                    OPEN_PARENS +
                    StatsTable.COLUMN_NAME_STAT_ID + INT_PK + COMMA_SEP +
                    StatsTable.COLUMN_NAME_STAT_MCCMNC + TEXT_TYPE + COMMA_SEP +
                    StatsTable.COLUMN_NAME_STAT_START_TIME + INTEGER_TYPE + COMMA_SEP +
                    StatsTable.COLUMN_NAME_STAT_END_TIME + INTEGER_TYPE +
                    CLOSE_PARENS;

    public static final String SQL_ALTER_HISTORY_ADD_FROM_LAT = ALTER_TABLE + HistoryTable.TABLE_NAME
            + ADD + HistoryTable.COLUMN_NAME_HIST_FROM_LATITUDE + TEXT_TYPE;
    public static final String SQL_ALTER_HISTORY_ADD_FROM_LONG = ALTER_TABLE + HistoryTable.TABLE_NAME
            + ADD + HistoryTable.COLUMN_NAME_HIST_FROM_LONGITUDE + TEXT_TYPE;
    public static final String SQL_ALTER_HISTORY_ADD_TO_LAT = ALTER_TABLE + HistoryTable.TABLE_NAME
            + ADD + HistoryTable.COLUMN_NAME_HIST_TO_LATITUDE + TEXT_TYPE;
    public static final String SQL_ALTER_HISTORY_ADD_TO_LONG = ALTER_TABLE + HistoryTable.TABLE_NAME
            + ADD + HistoryTable.COLUMN_NAME_HIST_TO_LONGITUDE + TEXT_TYPE;

    public static final String SQL_DROP_HISTORY_TABLE =
            DROP_TABLE + HistoryTable.TABLE_NAME;

    public static final String SQL_DROP_STATS_TABLE =
            DROP_TABLE + StatsTable.TABLE_NAME;

    public FiMonitorContract() {}

    public static abstract class HistoryTable implements BaseColumns {

        public static final String TABLE_NAME = "history";

        public static final String COLUMN_NAME_HIST_ID = "_id";
        public static final String COLUMN_NAME_HIST_TYPE = "hist_type";
        public static final String COLUMN_NAME_HIST_ACTION = "hist_action";
        public static final String COLUMN_NAME_HIST_FROM_DTM = "hist_from_dtm";
        public static final String COLUMN_NAME_HIST_FROM_MSG = "hist_from_msg";
        public static final String COLUMN_NAME_HIST_FROM_MCCMNC = "hist_from_mccmnc";
        public static final String COLUMN_NAME_HIST_FROM_LATITUDE = "hist_from_latitude";
        public static final String COLUMN_NAME_HIST_FROM_LONGITUDE = "hist_from_longitude";
        public static final String COLUMN_NAME_HIST_TO_DTM = "hist_to_dtm";
        public static final String COLUMN_NAME_HIST_TO_MSG = "hist_to_msg";
        public static final String COLUMN_NAME_HIST_TO_MCCMNC = "hist_to_mccmnc";
        public static final String COLUMN_NAME_HIST_TO_LATITUDE = "hist_to_latitude";
        public static final String COLUMN_NAME_HIST_TO_LONGITUDE = "hist_to_longitude";
        public static final String COLUMN_NAME_HIST_LAST_UPDATE_DTM = "hist_last_update_dtm";
        public static final String COLUMN_NAME_HIST_ROW_CLOSED = "hist_row_closed";

        public static final String[] ALL_COLUMN_NAMES = {
                COLUMN_NAME_HIST_ID,
                COLUMN_NAME_HIST_TYPE,
                COLUMN_NAME_HIST_ACTION,
                COLUMN_NAME_HIST_FROM_DTM,
                COLUMN_NAME_HIST_FROM_MSG,
                COLUMN_NAME_HIST_FROM_MCCMNC,
                COLUMN_NAME_HIST_FROM_LATITUDE,
                COLUMN_NAME_HIST_FROM_LONGITUDE,
                COLUMN_NAME_HIST_TO_DTM,
                COLUMN_NAME_HIST_TO_MSG,
                COLUMN_NAME_HIST_TO_MCCMNC,
                COLUMN_NAME_HIST_TO_LATITUDE,
                COLUMN_NAME_HIST_TO_LONGITUDE,
                COLUMN_NAME_HIST_LAST_UPDATE_DTM,
                COLUMN_NAME_HIST_ROW_CLOSED
        };

    }

    public static abstract class StatsTable implements BaseColumns {

        public static final String TABLE_NAME = "stats";

        public static final String COLUMN_NAME_STAT_ID = "_id";
        public static final String COLUMN_NAME_STAT_MCCMNC = "stat_mccmnc";
        public static final String COLUMN_NAME_STAT_START_TIME = "start_time";
        public static final String COLUMN_NAME_STAT_END_TIME = "end_time";

        public static final String[] ALL_COLUMN_NAMES = {
                 COLUMN_NAME_STAT_ID
                ,COLUMN_NAME_STAT_MCCMNC
                ,COLUMN_NAME_STAT_START_TIME
                ,COLUMN_NAME_STAT_END_TIME
        };

    }
}