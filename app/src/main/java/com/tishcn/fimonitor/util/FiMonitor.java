package com.tishcn.fimonitor.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.preference.PreferenceManager;
import android.telephony.CellInfo;
import android.telephony.CellInfoCdma;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellInfoWcdma;
import android.telephony.CellSignalStrengthCdma;
import android.telephony.CellSignalStrengthGsm;
import android.telephony.CellSignalStrengthLte;
import android.telephony.CellSignalStrengthWcdma;
import android.telephony.TelephonyManager;

import java.util.List;

/**
 * Created by leona on 7/1/2016.
 */
public class FiMonitor {

    private Context mContext;

    public FiMonitor(Context context){

        mContext = context;

    }

    private WifiInfo getCurrentWifiInfo(){

        WifiInfo wifiInfo = null;
        NetworkInfo networkInfo = null;

        ConnectivityManager connManager = (ConnectivityManager)
                mContext.getSystemService(Context.CONNECTIVITY_SERVICE);

        Network networks[] = connManager.getAllNetworks();

        for (Network network : networks) {

            networkInfo = connManager.getNetworkInfo(network);

            if (networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {

                break;

            }

        }

        if (networkInfo != null && networkInfo.isConnected()) {

            WifiManager wifiManager = (WifiManager)
                    mContext.getSystemService(Context.WIFI_SERVICE);

            wifiInfo = wifiManager.getConnectionInfo();
        }

        return wifiInfo;
    }

    public boolean wifiConnected() {

        WifiInfo wifiInfo = getCurrentWifiInfo();
        boolean bolWifiConnected = false;
        if (wifiInfo != null) {
            if (wifiInfo.getSupplicantState().toString().equals(Constants.COMPLETED)) {
                bolWifiConnected = true;
            }
        }
        return bolWifiConnected;
    }

    public String getWifiSsid() {

        String ssid;
        WifiInfo wifiInfo = getCurrentWifiInfo();

        if(wifiInfo == null){
            ssid = Constants.WIFI_UNKNOWN_SSID;
        } else {
            ssid = wifiInfo.getSSID();
            ssid = ssid.substring(1, ssid.length()-1);
        }

        return ssid;
    }

    public String getWifiSignalStrength(){

        String rssi;
        WifiInfo wifiInfo = getCurrentWifiInfo();

        if(wifiInfo == null){
            rssi = "";
        } else {
            rssi = String.valueOf(wifiInfo.getRssi()).concat(" ")
                    .concat(Constants.SIGNAL_STRENGTH_UNITS);
        }

        return rssi;

    }

    public String getWifiLinkSpeed(){

        String linkSpeed;
        WifiInfo wifiInfo = getCurrentWifiInfo();

        if(wifiInfo == null){
            linkSpeed = "";
        } else {
            linkSpeed = String.valueOf(wifiInfo.getLinkSpeed()).concat(" ")
                    .concat(WifiInfo.LINK_SPEED_UNITS);
        }

        return linkSpeed;

    }

    public String getWifiFrequency(){

        String frequency;
        WifiInfo wifiInfo = getCurrentWifiInfo();

        if(wifiInfo == null){
            frequency = "";
        } else {
            frequency = String.valueOf(wifiInfo.getFrequency())
                    .concat(" ").concat(WifiInfo.FREQUENCY_UNITS);
        }

        return frequency;

    }

    public String getNetworkOperatorName(String mccmnc){

        boolean matchFound = false;
        List<String> hniList = Constants.NETWORK_OPERATOR_IDS_SPRINT;
        String strNetworkOperatorName = Constants.NETWORK_OPERATOR_NAME_UNKNOWN;

        if(mccmnc ==  null
                || mccmnc.isEmpty()
                || mccmnc.equals(Constants.NETWORK_OPERATOR_ID_NONE)){

            strNetworkOperatorName = Constants.NOT_CONNECTED;

        } else {

            if(mccmnc.equals(Constants.HIST_ACTION_DISCONNECTED)){
                strNetworkOperatorName = mccmnc;
                matchFound = true;
            }

            if(!matchFound){
                for (int i = 0; i < hniList.size(); i++) {
                    if (mccmnc.equals(hniList.get(i))) {
                        strNetworkOperatorName = Constants.NETWORK_OPERATOR_NAME_SPRINT;
                        matchFound = true;
                        break;
                    }
                }
            }

            if(!matchFound){
                hniList = Constants.NETWORK_OPERATOR_IDS_TMOBILE;
                for(int i=0; i<hniList.size(); i++){
                    if(mccmnc.equals(hniList.get(i))){
                        strNetworkOperatorName = Constants.NETWORK_OPERATOR_NAME_TMOBILE;
                        matchFound = true;
                        break;
                    }
                }
            }

            if(!matchFound){
                hniList = Constants.NETWORK_OPERATOR_IDS_US_CELLULAR;
                for(int i=0; i<hniList.size(); i++){
                    if(mccmnc.equals(hniList.get(i))){
                        strNetworkOperatorName = Constants.NETWORK_OPERATOR_NAME_US_CELLULAR;
                        matchFound = true;
                        break;
                    }
                }
            }

        }

        return strNetworkOperatorName;
    }

    public String getNetworkOperator() {

        TelephonyManager telephonyManager = (TelephonyManager)
                mContext.getSystemService(Context.TELEPHONY_SERVICE);

        String networkOperator = telephonyManager.getNetworkOperator();
        if(networkOperator.equals(Constants.NETWORK_OPERATOR_ID_NONE)
                || networkOperator.isEmpty()
                || networkOperator.equals("")){
            networkOperator = Constants.NETWORK_OPERATOR_ID_NONE;
        } else {
            networkOperator = telephonyManager.getSubscriberId().substring(0,6);
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(Constants.PREF_CELL_LAST_OPERATOR, networkOperator);
            editor.apply();
        }

        return networkOperator;
    }

    public String getNetworkTypeName(){

        TelephonyManager telephonyManager = (TelephonyManager)
                mContext.getSystemService(Context.TELEPHONY_SERVICE);

        int intNetworkType = telephonyManager.getNetworkType();
        String strNetworkTypeName;

        switch (intNetworkType){
            case TelephonyManager.NETWORK_TYPE_1xRTT:
                strNetworkTypeName = Constants.NETWORK_TYPE_NAME_1xRTT;
                break;
            case TelephonyManager.NETWORK_TYPE_CDMA:
                strNetworkTypeName = Constants.NETWORK_TYPE_NAME_CDMA;
                break;
            case TelephonyManager.NETWORK_TYPE_EDGE:
                strNetworkTypeName = Constants.NETWORK_TYPE_NAME_EDGE;
                break;
            case TelephonyManager.NETWORK_TYPE_EHRPD:
                strNetworkTypeName = Constants.NETWORK_TYPE_NAME_EHRPD;
                break;
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
                strNetworkTypeName = Constants.NETWORK_TYPE_NAME_EVDO_0;
                break;
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
                strNetworkTypeName = Constants.NETWORK_TYPE_NAME_EVDO_A;
                break;
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
                strNetworkTypeName = Constants.NETWORK_TYPE_NAME_EVDO_B;
                break;
            case TelephonyManager.NETWORK_TYPE_GPRS:
                strNetworkTypeName = Constants.NETWORK_TYPE_NAME_GPRS;
                break;
            case TelephonyManager.NETWORK_TYPE_HSDPA:
                strNetworkTypeName = Constants.NETWORK_TYPE_NAME_HSDPA;
                break;
            case TelephonyManager.NETWORK_TYPE_HSPA:
                strNetworkTypeName = Constants.NETWORK_TYPE_NAME_HSPA;
                break;
            case TelephonyManager.NETWORK_TYPE_HSPAP:
                strNetworkTypeName = Constants.NETWORK_TYPE_NAME_HSPAP;
                break;
            case TelephonyManager.NETWORK_TYPE_HSUPA:
                strNetworkTypeName = Constants.NETWORK_TYPE_NAME_HSUPA;
                break;
            case TelephonyManager.NETWORK_TYPE_IDEN:
                strNetworkTypeName = Constants.NETWORK_TYPE_NAME_IDEN;
                break;
            case TelephonyManager.NETWORK_TYPE_LTE:
                strNetworkTypeName = Constants.NETWORK_TYPE_NAME_LTE;
                break;
            case TelephonyManager.NETWORK_TYPE_UMTS:
                strNetworkTypeName = Constants.NETWORK_TYPE_NAME_UMTS;
                break;
            case TelephonyManager.NETWORK_TYPE_UNKNOWN:
                strNetworkTypeName = Constants.NETWORK_TYPE_NAME_UNKNOWN;
                break;
            default:
                strNetworkTypeName = "";
                break;
        }

        if(!strNetworkTypeName.equals("") && !strNetworkTypeName.equals(Constants.NETWORK_TYPE_NAME_UNKNOWN)){
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(Constants.PREF_LAST_NETWORK_TYPE, strNetworkTypeName);
            editor.apply();
        }

        return strNetworkTypeName;
    }

    public String getNetworkSignalStrength(){

        TelephonyManager telephonyManager = getTelephonyService();
        List<CellInfo> cellInfos = telephonyManager.getAllCellInfo();
        String networkSignalStrength = Constants.NETWORK_SIGNAL_STRENGTH_UNKNOWN;

        if(cellInfos!=null){
            for (int i = 0 ; i<cellInfos.size(); i++){
                if (cellInfos.get(i).isRegistered()){
                    if(cellInfos.get(i) instanceof CellInfoWcdma){
                        CellInfoWcdma cellInfoWcdma = (CellInfoWcdma) telephonyManager.getAllCellInfo().get(0);
                        CellSignalStrengthWcdma cellSignalStrengthWcdma = cellInfoWcdma.getCellSignalStrength();
                        networkSignalStrength = String.valueOf(cellSignalStrengthWcdma.getDbm());
                    }else if(cellInfos.get(i) instanceof CellInfoGsm){
                        CellInfoGsm cellInfogsm = (CellInfoGsm) telephonyManager.getAllCellInfo().get(0);
                        CellSignalStrengthGsm cellSignalStrengthGsm = cellInfogsm.getCellSignalStrength();
                        networkSignalStrength = String.valueOf(cellSignalStrengthGsm.getDbm());
                    }else if(cellInfos.get(i) instanceof CellInfoLte){
                        CellInfoLte cellInfoLte = (CellInfoLte) telephonyManager.getAllCellInfo().get(0);
                        CellSignalStrengthLte cellSignalStrengthLte = cellInfoLte.getCellSignalStrength();
                        networkSignalStrength = String.valueOf(cellSignalStrengthLte.getDbm());
                    } else if(cellInfos.get(i) instanceof CellInfoCdma){
                        CellInfoCdma cellInfoCdma = (CellInfoCdma) telephonyManager.getAllCellInfo().get(0);
                        CellSignalStrengthCdma cellSignalStrengthCdma = cellInfoCdma.getCellSignalStrength();
                        networkSignalStrength = String.valueOf(cellSignalStrengthCdma.getDbm());
                    }
                }
            }
            if(!networkSignalStrength.equals(Constants.NETWORK_SIGNAL_STRENGTH_UNKNOWN)) {
                networkSignalStrength = networkSignalStrength.concat(" ").concat(Constants.SIGNAL_STRENGTH_UNITS);
            }
        }
        return networkSignalStrength;
    }

    private TelephonyManager getTelephonyService() {

         return (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);

    }

}
