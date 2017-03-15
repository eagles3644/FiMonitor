package com.tishcn.fimonitor.ui;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.tishcn.fimonitor.R;
import com.tishcn.fimonitor.sql.FiMonitorContract;
import com.tishcn.fimonitor.util.Constants;
import com.tishcn.fimonitor.util.DateFormat;
import com.tishcn.fimonitor.util.FiMonitor;

/**
 * Created by leona on 7/4/2016.
 */
public class HistoryAdapter extends CursorAdapter {

    private SparseBooleanArray selectedItemIds = new SparseBooleanArray();

    public HistoryAdapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        View view = LayoutInflater.from(context).inflate(R.layout.hist_wifi_row, null);
        ViewHolder holder = new ViewHolder();
        holder.histId = (TextView) view.findViewById(R.id.hist_wifi_id);
        holder.histType = (TextView) view.findViewById(R.id.hist_wifi_type);
        holder.histTime = (TextView) view.findViewById(R.id.hist_wifi_time);
        holder.histMsg1 = (TextView) view.findViewById(R.id.hist_wifi_message1);
        holder.histMsg2 = (TextView) view.findViewById(R.id.hist_wifi_message2);
        holder.histIcon = (ImageView) view.findViewById(R.id.hist_wifi_icon);
        holder.histLocationIcon = (ImageView) view.findViewById(R.id.hist_location_icon);
        view.setTag(holder);
        return view;
    }

    @Override
    public void bindView(final View view, final Context context, final Cursor cursor) {
        final ViewHolder holder = (ViewHolder) view.getTag();
        final FiMonitor fiMonitor = new FiMonitor(context);
        final int id = cursor.getInt(cursor.getColumnIndex(
                FiMonitorContract.HistoryTable.COLUMN_NAME_HIST_ID));
        String closed = cursor.getString(cursor.getColumnIndex(
                FiMonitorContract.HistoryTable.COLUMN_NAME_HIST_ROW_CLOSED));
        final String type = cursor.getString(cursor.getColumnIndex(
                FiMonitorContract.HistoryTable.COLUMN_NAME_HIST_TYPE));
        String action = cursor.getString(cursor.getColumnIndex(
                FiMonitorContract.HistoryTable.COLUMN_NAME_HIST_ACTION));
        final long fromMillis = cursor.getLong(cursor
                .getColumnIndex(FiMonitorContract.HistoryTable.COLUMN_NAME_HIST_FROM_DTM));
        String fromMsg = cursor.getString(cursor
                .getColumnIndex(FiMonitorContract.HistoryTable.COLUMN_NAME_HIST_FROM_MSG));
        String fromMCCMNC = cursor.getString(cursor
                .getColumnIndex(FiMonitorContract.HistoryTable.COLUMN_NAME_HIST_FROM_MCCMNC));
        final long toMillis = cursor.getLong(cursor
                .getColumnIndex(FiMonitorContract.HistoryTable.COLUMN_NAME_HIST_TO_DTM));
        String toMsg = cursor.getString(cursor
                .getColumnIndex(FiMonitorContract.HistoryTable.COLUMN_NAME_HIST_TO_MSG));
        String toMCCMNC = cursor.getString(cursor
                .getColumnIndex(FiMonitorContract.HistoryTable.COLUMN_NAME_HIST_TO_MCCMNC));
        boolean toLatitudeNull = cursor.isNull(cursor
                .getColumnIndex(FiMonitorContract.HistoryTable.COLUMN_NAME_HIST_TO_LATITUDE));
        boolean toLongitudeNull = cursor.isNull(cursor
                .getColumnIndex(FiMonitorContract.HistoryTable.COLUMN_NAME_HIST_TO_LONGITUDE));

        if(fromMCCMNC != null && type.equals(Constants.HIST_TYPE_CELL)) {
            fromMsg = fiMonitor.getNetworkOperatorName(fromMCCMNC);//.concat("/").concat(fromMCCMNC);
        }

        if(toMCCMNC != null && type.equals(Constants.HIST_TYPE_CELL)){
            toMsg = fiMonitor.getNetworkOperatorName(toMCCMNC);//.concat("/").concat(toMCCMNC);
        }

        holder.histId.setText(String.valueOf(id));
        holder.histType.setText(type);

        if(toLatitudeNull || toLongitudeNull){
            holder.histLocationIcon.setVisibility(View.INVISIBLE);
        } else {
            holder.histLocationIcon.setVisibility(View.VISIBLE);
            final String finalAction = action;
            final String finalFromMsg = fromMsg;
            final String finalToMsg = toMsg;
            holder.histLocationIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String headText = "";
                    String time = "";
                    switch (finalAction) {
                        case Constants.HIST_ACTION_DISCONNECT_CONNECT:
                            time = DateFormat.formatDateTime(fromMillis);
                            if (finalFromMsg.equals(finalToMsg)) {
                                if (type.equals(Constants.HIST_TYPE_CELL)){
                                    headText = finalToMsg.concat(" ");
                                } else {
                                    headText = type.concat(" ");
                                }
                                headText = headText.concat("dropped connection for ")
                                        .concat(String.valueOf(((toMillis - fromMillis) / 1000))
                                                .concat(" seconds."));
                            } else {
                                headText = "Changed from ".concat(finalFromMsg)
                                        .concat(" to ").concat(finalToMsg).concat(" in ")
                                        .concat(String.valueOf(((toMillis - fromMillis) / 1000))
                                                .concat(" seconds."));
                            }
                            break;
                        case Constants.HIST_ACTION_CONNECTED:
                            time = DateFormat.formatDateTime(toMillis);
                            headText = finalAction.concat(" to ").concat(finalToMsg).concat(".");
                            break;
                        case Constants.HIST_ACTION_DISCONNECTED:
                            time = DateFormat.formatDateTime(fromMillis);
                            headText = finalAction.concat(" from ").concat(finalFromMsg).concat(".");
                            break;
                        default:
                            headText = Constants.FAKE_NOTIF_TITLE;
                            break;
                    }
                    String networkOperatorName = fiMonitor.getNetworkOperatorName(fiMonitor.getNetworkOperator());
                    /*boolean connectionAvail = false;
                    if(!networkOperatorName.equals(Constants.NOT_CONNECTED) || fiMonitor.wifiConnected()){
                        connectionAvail = true;
                    }
                    if(connectionAvail) {*/
                        Intent intent = new Intent(context, MapsActivity.class);
                        double lat = Double.parseDouble(cursor.getString(cursor.getColumnIndex(FiMonitorContract.HistoryTable.COLUMN_NAME_HIST_TO_LATITUDE)));
                        double lng = Double.parseDouble(cursor.getString(cursor.getColumnIndex(FiMonitorContract.HistoryTable.COLUMN_NAME_HIST_TO_LONGITUDE)));
                        Log.d("HistAdapt", "Lat/Lng" + String.valueOf(lat + "/" + lng));
                        intent.putExtra(Constants.MAP_LAT_CONSTANT, lat);
                        intent.putExtra(Constants.MAP_LNG_CONSTANT, lng);
                        intent.putExtra(Constants.MAP_TIME_CONSTANT, time);
                        intent.putExtra(Constants.MAP_HEAD_TEXT, headText);
                        context.startActivity(intent);
                    /*} else {
                        //Toast.makeText(context, "Map unavailable, no Internet connection.", Toast.LENGTH_LONG).show();
                        final Snackbar snackbar = Snackbar.make(view,
                                "Map unavailable, no Internet connection.", Snackbar.LENGTH_INDEFINITE);
                        snackbar.setAction(Constants.DISMISS, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                snackbar.dismiss();
                            }
                        });
                        snackbar.show();
                    }*/
                }
            });
        }

        if(action.equals(Constants.HIST_ACTION_DISCONNECT_CONNECT)) {
            holder.histTime.setText(DateFormat.formatDateTime(fromMillis));
            if(fromMsg.equals(toMsg)){
                holder.histMsg1.setText("Dropped connection for "
                        .concat(String.valueOf(((toMillis-fromMillis)/1000))
                                .concat(" seconds.")));
                holder.histMsg2.setText(toMsg);
            } else {
                holder.histMsg1.setText("Changed from ".concat(fromMsg)
                        .concat(" to ").concat(toMsg).concat("."));
                holder.histMsg2.setText(
                        String.valueOf(((toMillis-fromMillis)/1000))
                                .concat(" seconds"));
            }
            if(type.equals(Constants.HIST_TYPE_WIFI)){
                holder.histIcon.setImageResource(R.drawable.ic_signal_wifi_2_bar);
            } else if(type.equals(Constants.HIST_TYPE_CELL)) {
                holder.histIcon.setImageResource(R.drawable.ic_signal_cellular_2_bar);
            }
        } else {
            if(action.equals(Constants.HIST_ACTION_CONNECTED)){
                holder.histTime.setText(DateFormat.formatDateTime(toMillis));
                holder.histMsg2.setText(toMsg);
                if(type.equals(Constants.HIST_TYPE_WIFI)){
                    holder.histIcon.setImageResource(R.drawable.ic_signal_wifi_4_bar);
                } else if(type.equals(Constants.HIST_TYPE_CELL)) {
                    holder.histIcon.setImageResource(R.drawable.ic_signal_cellular_4_bar);
                    if(id == 1){
                        action = Constants.HIST_MSG_INIT_CELL;
                    }
                }
            } else {
                holder.histTime.setText(DateFormat.formatDateTime(fromMillis));
                holder.histMsg2.setText(fromMsg);
                if(type.equals(Constants.HIST_TYPE_WIFI)){
                    holder.histIcon.setImageResource(R.drawable.ic_signal_wifi_0_bar);
                } else if(type.equals(Constants.HIST_TYPE_CELL)) {
                    holder.histIcon.setImageResource(R.drawable.ic_signal_cellular_null);
                }
            }
            holder.histMsg1.setText(action);
        }
    }

    static class ViewHolder {
        TextView histId;
        TextView histType;
        TextView histTime;
        TextView histMsg1;
        TextView histMsg2;
        ImageView histIcon;
        ImageView histLocationIcon;
    }

    public void selectItem(int id, boolean value){
        if (value){
            selectedItemIds.put(id, true);
        } else {
            selectedItemIds.delete(id);
        }
    }

    public void removeSelection(){
        selectedItemIds = new SparseBooleanArray();
    }

    public int getSelectedCount(){
        return selectedItemIds.size();
    }

    public SparseBooleanArray getSelectedIds(){
        return selectedItemIds;
    }
}
