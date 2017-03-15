package com.tishcn.fimonitor.util;

import com.tishcn.fimonitor.sql.FiMonitorContract;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by leona on 7/3/2016.
 */
public class Constants {

    public static final String PREF_APP_RATED = "pref_app_rated";
    public static final String PREF_APP_OPEN_COUNT = "pref_app_open_count";
    public static final int PREF_APP_OPEN_COUNT_TRIG1 = 8;
    public static final int PREF_APP_OPEN_COUNT_TRIG2 = 18;
    public static final int PREF_APP_OPEN_COUNT_TRIG3 = 30;
    public static final String PREF_WIFI_CONN_STATE = "WIFI_CONN_STATE";
    public static final String PREF_WIFI_CONN_UNKNOWN = "unknown";
    public static final String PREF_WIFI_CONN_TRUE = "true";
    public static final String PREF_WIFI_CONN_FALSE = "false";
    public static final String PREF_WIFI_LAST_SSID = "WIFI_LAST_SSID";
    public static final String PREF_WIFI_LAST_SSID_DISCONNECTED = "Disconnected";
    public static final String PREF_CELL_SERVICE_STATE = "CELL_SERVICE_STATE";
    public static final String PREF_CELL_SERVICE_STATE_UNKNOWN = "unknown";
    public static final String PREF_CELL_SERVICE_STATE_TRUE = "true";
    public static final String PREF_CELL_SERVICE_STATE_FALSE = "false";
    public static final String PREF_CELL_LAST_OPERATOR = "CELL_LAST_OPERATOR";
    public static final String PREF_CELL_LAST_OPERATOR_UNKNOWN = "Unknown";
    public static final String PREF_HIST_ROW_LIMIT = "pref_max_history_rows";
    public static final String PREF_HIST_ROW_LIMIT_DEFAULT_VALUE = "100";
    public static final String PREF_CLEAR_ALL_HISTORY = "pref_clear_history";
    public static final String PREF_LOCATION_PERMISSION = "LOCATION_PERMISSION";
    public static final String PREF_PHONE_PERMISSION = "PHONE_PERMISSION";
    public static final String PREF_VARS_INITIALIZED = "VARS_INITIALIZED";
    public static final String PREF_HOME_FRAGMENT = "pref_home_fragment2";
    public static final String PREF_WIFI_HIST_LOGGING = "pref_wifi_history";
    public static final String PREF_CELL_HIST_LOGGING = "pref_cell_history";
    public static final String PREF_DB_VERSION_NUM = "pref_db_version_num";
    public static final String PREF_LAST_NETWORK_TYPE = "pref_last_network_type";
    public static final String PREF_NOTIF_ENABLED = "pref_notif_enabled";
    public static final String PREF_NOTIF_TYPE = "pref_notif_type";
    public static final String PREF_NOTIF_PRIORITY = "pref_notif_priority";
    public static final String PREF_NOTIF_TRIGGER = "pref_notif_trigger";
    public static final String PREF_NOTIF_SHOW_CELLULAR = "pref_notif_show_cellular";
    public static final String PREF_NOTIF_SHOW_WIFI = "pref_notif_show_wifi";
    public static final String PREF_NOTIF_SHOW_APP_ICON = "pref_notif_show_app_icon";
    public static final String PREF_NOTIF_SHOW_BUTTONS = "pref_notif_show_buttons";
    public static final String PREF_NOTIF_BUTTON1 = "pref_notif_button1";
    public static final String PREF_NOTIF_BUTTON2 = "pref_notif_button2";
    public static final String PREF_NOTIF_BUTTON3 = "pref_notif_button3";
    public static final String PREF_NOTIF_PLAY_SOUND = "pref_notif_play_sound";
    public static final String PREF_NOTIF_VIBRATE = "pref_notif_vibrate";
    public static final String PREF_NOTIF_LED = "pref_notif_led";
    public static final String PREF_NOTIF_SHOW_ON_LOCK_SCREEN = "pref_notif_show_on_lock_screen";
    public static final String PREF_STAT_CHART_SIZE = "pref_stat_chart_size";
    public static final String PREF_STAT_CHART_SIZE_DEFAULT = "300";
    public static final String PREF_STATS_EXIST = "pref_stats_exist"+FiMonitorContract.DATABASE_VERSION;
    public static final String PREF_STATS_SPIN_VALUE = "pref_stats_spin_value";

    public static final String HISTORY_CHANGE_INTENT_ACTION = "com.tishcn.fimonitor.HIST_CHANGE";
    public static final String HISTORY_FRAG_TITLE = "Event Log";

    public static final String WIFI_UNKNOWN_SSID = "unknown ssid";

    public static final String CONNECTING = "Connecting";

    public static final String SIGNAL_STRENGTH_UNITS = "dBm";

    public static final String COMPLETED = "COMPLETED";

    public static final String NOT_CONNECTED = "Not Connected";

    public static final String HIST_TYPE_WIFI = "Wifi";
    public static final String HIST_TYPE_CELL = "Cell";
    public static final String HIST_ACTION_CONNECTED = "Connected";
    public static final String HIST_ACTION_DISCONNECTED = "Disconnected";
    public static final long HIST_NEW_ROW_TIME_DIFFERENCE_MILLIS = 59000;
    public static final String HIST_CONFIRM_DELETE_ALL = "Are you sure you want to clear the event log?";
    public static final String HIST_ACTION_DISCONNECT_CONNECT = "Disconnected and Connected";

    public static final List<String> NETWORK_OPERATOR_IDS_TMOBILE =
            Arrays.asList("310160","310200","310210","310220","310230","310240","310250",
                    "310260","310270","310310","310490","310660","310800");
    public static final List<String> NETWORK_OPERATOR_IDS_SPRINT =
            Arrays.asList("310120","310830","311260","311490","311870","311880","311940",
                    "312190","312240","312250","312530","316010");
    public static final List<String> NETWORK_OPERATOR_IDS_US_CELLULAR =
            Arrays.asList("310730","311220","311580","31070","31000");
    public static final String NETWORK_OPERATOR_NAME_TMOBILE = "T-Mobile";
    public static final String NETWORK_OPERATOR_NAME_SPRINT = "Sprint";
    public static final String NETWORK_OPERATOR_NAME_US_CELLULAR = "U.S. Cellular";
    public static final String NETWORK_OPERATOR_NAME_UNKNOWN = "Unknown";
    public static final String NETWORK_OPERATOR_ID_NONE = "00000";

    public static final String NETWORK_TYPE_NAME_1xRTT = "1xRTT";
    public static final String NETWORK_TYPE_NAME_CDMA = "CDMA";
    public static final String NETWORK_TYPE_NAME_EDGE = "EDGE";
    public static final String NETWORK_TYPE_NAME_EHRPD = "EHRPT";
    public static final String NETWORK_TYPE_NAME_EVDO_0 = "EVDO 0";
    public static final String NETWORK_TYPE_NAME_EVDO_A = "EVDO A";
    public static final String NETWORK_TYPE_NAME_EVDO_B = "EVDO B";
    public static final String NETWORK_TYPE_NAME_GPRS = "GPRS";
    public static final String NETWORK_TYPE_NAME_HSDPA = "HSDPA";
    public static final String NETWORK_TYPE_NAME_HSPA = "HSPA";
    public static final String NETWORK_TYPE_NAME_HSPAP = "HSPAP";
    public static final String NETWORK_TYPE_NAME_HSUPA = "HSUPA";
    public static final String NETWORK_TYPE_NAME_IDEN = "IDEN";
    public static final String NETWORK_TYPE_NAME_LTE = "LTE";
    public static final String NETWORK_TYPE_NAME_UMTS = "UMTS";
    public static final String NETWORK_TYPE_NAME_UNKNOWN = "UNKNOWN";

    public static final String NETWORK_SIGNAL_STRENGTH_UNKNOWN = "UNKNOWN";
    public static final String NETWORK_SIGNAL_STRENGTH_CHANGE_INTENT_ACTION =
            "com.tishcn.fimonitor.NET_STRENGTH_CHANGE";

    public static final String NETWORK_STATE_CHANGE_INTENT_ACTION =
            "com.tishcn.fimonitor.NET_STATE_CHANGE";

    public static final String PLEASE_WAIT = "Please Wait";

    public static final String GOOGLE_FI_APP_PACKAGE = "com.google.android.apps.tycho";

    public static final long SPLASH_DELAY_MILIS = 1000;

    public static final String LOCATION_PERMISSION = "Please grant permission to location data.";
    public static final String PHONE_PERMISSION = "Please grant permission to phone data.";
    public static final String PHONE_AND_LOCATION_PERMISSION = "Please grant permission to location and phone data.";

    public static final String INSTALL_FI_APP = "Please install the Project Fi by Google application.";

    public static final String FI_MONITOR = "Fi Monitor";

    public static final String DIALER_CODES_FRAG_TITLE = "Dialer Codes";
    public static final String DIALER_CODE_TITLE_NEXT_CARRIER = "Next Carrier";
    public static final String DIALER_CODE_TITLE_AUTO_SWITCH = "Auto Switch";
    public static final String DIALER_CODE_TITLE_REPAIR = "Repair";
    public static final String DIALER_CODE_TITLE_INFO = "Info";
    public static final String DIALER_CODE_TITLE_TESTING_ACTIVITY = "Fi Testing Info Activity";
    public static final String DIALER_CODE_CODE_US_CELLULAR = "*#*#34872#*#*";
    public static final String DIALER_CODE_CODE_SPRINT = "*#*#34777#*#*";
    public static final String DIALER_CODE_CODE_TMOBILE = "*#*#34866#*#*";
    public static final String DIALER_CODE_CODE_NEXT_CARRIER = "*#*#346398#*#*";
    public static final String DIALER_CODE_CODE_AUTO_SWITCH = "*#*#342886#*#*";
    public static final String DIALER_CODE_CODE_REPAIR = "*#*#34963#*#*";
    public static final String DIALER_CODE_CODE_INFO = "*#*#344636#*#*";
    public static final String DIALER_CODE_CODE_TESTING_ACTIVITY = "*#*#4636#*#*";
    public static final String UPGRADING = "Upgrading, please wait...";

    public static final String EXTRA_ATTACHED_FRAGMENT = "extra_attached_fragment";

    public static final String NOTIFICATIONS_FRAG_TITLE = "Notifications";
    public static final String NOTIFICATIONS_TRIGGER_CELLULAR = "Cell Connect/Disconnect";
    public static final String NOTIFICATIONS_TRIGGER_WIFI = "WiFi Connect/Disconnect";
    public static final String NOTIFICATION_ON_TRIGGER = "On Trigger";
    public static final String NOTIFICATION_PERSISTENT = "Persistent";
    public static final Set<String> NOTIFICATION_TRIGGER_DEFAULT_SET = new HashSet<>(
            Arrays.asList(NOTIFICATIONS_TRIGGER_CELLULAR, NOTIFICATIONS_TRIGGER_WIFI));
    public static final String NOTIFICATION_GROUP_SUMMARY_MULTIPLE = "Multiple Triggers Detected";
    public static final String NOTIFICATION_GROUP_SUMMARY_SINGLE = "Trigger Detected";
    public static final String NOTIFICATION_PRIORTY_MAX = "Max";
    public static final String NOTIFICATION_PRIORTY_HIGH = "High";
    public static final String NOTIFICATION_PRIORTY_NORMAL = "Normal";
    public static final String NOTIFICATION_PRIORTY_LOW = "Low";
    public static final String NOTIFICATION_PRIORTY_MIN = "Min";
    public static final String NOTIFICATION_DEFAULT_PRIORITY = NOTIFICATION_PRIORTY_NORMAL;
    public static final String NOTIFICATION_BUTTON_EXTRA_DIALER_CODE = "FiMonitor_Button_Extra_Dialer_Code";
    public static final String NOTIFICATION_BUTTON_CLICK_INTENT_ACTION = "com.tishcn.fimonitor.receiver.NotificationButtonClickReceiver";
    public static final String RATE_APP_SNACK_TEXT = "Enjoying the app? Have suggestions?";
    public static final String YES = "YES";
    public static final String SURE = "Sure";
    public static final String RATE_APP_DIALOG_MSG = "Rate the app? Give feedback?";
    public static final String RATE_APP_DIALOG_TITLE = "Let me know?";
    public static final String STATISTICS_FRAG_TITLE = "Statistics";
    public static final String STATS_PIE_MIDDLE_TEXT = "";
    public static final String STATS_ACTION_CONNECT = "Connect";
    public static final String STATS_ACTION_DISCONNECT = "Disconnect";
    public static final String STATS_TYPE_WIFI = "WiFi";
    public static final String STAT_TYPE_CELL = "Cell";
    public static final String STAT_DUR_ALL = "All Available";
    public static final String STAT_DUR_6_HRS = "6 Hours";
    public static final String STAT_DUR_12_HRS = "12 Hours";
    public static final String STAT_DUR_1_DAY = "1 Day";
    public static final String STAT_DUR_2_DAYS = "2 Day";
    public static final String STAT_DUR_3_DAYS = "3 Day";
    public static final String STAT_DUR_1_WEEK = "1 Week";
    public static final String STAT_DUR_2_WEEKS = "2 Weeks";
    public static final String STAT_DUR_3_WEEKS = "3 Weeks";
    public static final String STAT_DUR_4_WEEKS = "4 Weeks";
    public static final String HIST_MSG_INIT_CELL = "Application initialized.";
    public static final String FIX = "Fix";
    public static final String NONE = "None";
    public static final String OTHER = "Other";
    public static final String PREF_STAT_CHART_HOLE = "pref_chart_hole";
    public static final String MAP_LAT_CONSTANT = "map_lat_constant";
    public static final String MAP_LNG_CONSTANT = "map_lng_constant";
    public static final String MAP_TIME_CONSTANT = "map_time_constant";
    public static final String MAP_HEAD_TEXT = "map_head_text";
    public static final String FAKE_NOTIF_TITLE = "Test Notification Trigger";
    public static final CharSequence DISMISS = "Dismiss";
    public static final String HISTORY_MAP_FRAG_TITLE = "Event Log Map";
}
