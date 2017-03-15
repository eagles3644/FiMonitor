package com.tishcn.fimonitor.ui;

import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.tishcn.fimonitor.R;
import com.tishcn.fimonitor.sql.DBTask;
import com.tishcn.fimonitor.sql.FiMonitorContract;
import com.tishcn.fimonitor.sql.FiMonitorDbHelper;
import com.tishcn.fimonitor.sql.FiMonitorDbRunnable;
import com.tishcn.fimonitor.util.Constants;
import com.tishcn.fimonitor.util.DateFormat;
import com.tishcn.fimonitor.util.FiMonitor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by leona on 1/7/2017.
 */

public class HistoryMapFragment extends Fragment implements OnMapReadyCallback {

    FiMonitor mFiMonitor;
    FiMonitorDbHelper mDatabase;
    Cursor mCursor;
    boolean mInternetAvailable;
    Snackbar mSnackBar;
    GoogleMap mMap;

    public HistoryMapFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mFiMonitor = new FiMonitor(getContext());
        mDatabase = new FiMonitorDbHelper(getContext());

        View rootView = inflater.inflate(R.layout.fragment_history_map, container, false);

        if(mFiMonitor.getNetworkOperatorName(mFiMonitor.getNetworkOperator()).equals(Constants.NOT_CONNECTED)
                && !mFiMonitor.wifiConnected()){
            mInternetAvailable = false;
            /*mSnackBar = Snackbar.make(rootView, "Map could not be displayed. No Internet connection" +
                    " available.", Snackbar.LENGTH_INDEFINITE);
            mSnackBar.setAction("Dismiss", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mSnackBar.dismiss();
                }
            });
            mSnackBar.show();*/
        } else {
            mInternetAvailable = false;
        }

        MapView mapView = (MapView) rootView.findViewById(R.id.event_map);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        mapView.getMapAsync(this);

        return rootView;
    }

    @Override
    public void onPause(){
        super.onPause();
        if(mSnackBar != null && mSnackBar.isShownOrQueued()) {
            mSnackBar.dismiss();
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
    }

    @Override
    public void onResume(){
        super.onResume();
    }

    private void loadMapObjects() {
        final int[] histCount = new int[1];
        new DBTask().execute(new FiMonitorDbRunnable() {
            @Override
            public void executeDBTask() {
                histCount[0] = mDatabase.getHistoryRowsWithLocationCount();
            }

            @Override
            public void postExecuteDBTask() {
                if (histCount[0] == 0) {
                    mSnackBar = Snackbar.make(getView(), "No event logs with location data " +
                            "exist. Nothing to display on map.", Snackbar.LENGTH_INDEFINITE);
                    mSnackBar.setAction("Dismiss", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mSnackBar.dismiss();
                        }
                    });
                    mSnackBar.show();
                } else {
                    new DBTask().execute(new FiMonitorDbRunnable() {
                        @Override
                        public void executeDBTask() {
                            mCursor = mDatabase.getHistoryRowsWithLocation();
                        }

                        @Override
                        public void postExecuteDBTask() {
                            Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
                            List<Address> addresses = null;
                            LatLng latLng=  null;
                            mCursor.moveToPosition(-1);
                            ArrayList<Marker> markers = new ArrayList<>();
                            while (mCursor.moveToNext()) {
                                String headText = "";
                                String action = mCursor.getString(mCursor.getColumnIndex(FiMonitorContract.HistoryTable.COLUMN_NAME_HIST_ACTION));
                                double lat = Double.parseDouble(mCursor.getString(mCursor.getColumnIndex(FiMonitorContract.HistoryTable.COLUMN_NAME_HIST_TO_LATITUDE)));
                                double lng = Double.parseDouble(mCursor.getString(mCursor.getColumnIndex(FiMonitorContract.HistoryTable.COLUMN_NAME_HIST_TO_LONGITUDE)));
                                long fromTime = mCursor.getLong(mCursor.getColumnIndex(FiMonitorContract.HistoryTable.COLUMN_NAME_HIST_FROM_DTM));
                                String fromMCCMNC = mCursor.getString(mCursor
                                        .getColumnIndex(FiMonitorContract.HistoryTable.COLUMN_NAME_HIST_FROM_MCCMNC));
                                long toTime = mCursor.getLong(mCursor.getColumnIndex(FiMonitorContract.HistoryTable.COLUMN_NAME_HIST_TO_DTM));
                                String toMCCMNC = mCursor.getString(mCursor
                                        .getColumnIndex(FiMonitorContract.HistoryTable.COLUMN_NAME_HIST_TO_MCCMNC));
                                String fromMsg = mFiMonitor.getNetworkOperatorName(fromMCCMNC);//.concat("/").concat(fromMCCMNC);
                                String toMsg = mFiMonitor.getNetworkOperatorName(toMCCMNC);//.concat("/").concat(toMCCMNC);
                                String type = mCursor.getString(mCursor.getColumnIndex(
                                        FiMonitorContract.HistoryTable.COLUMN_NAME_HIST_TYPE));
                                String time = "";
                                if(action.equals(Constants.HIST_ACTION_DISCONNECT_CONNECT)){
                                    time = DateFormat.formatDateTime(fromTime);
                                    if (fromMsg.equals(toMsg)) {
                                        if (type.equals(Constants.HIST_TYPE_CELL)){
                                            headText = toMsg.concat(" ");
                                        } else {
                                            headText = type.concat(" ");
                                        }
                                        headText = headText.concat("dropped connection for ")
                                                .concat(String.valueOf(((toTime - fromTime) / 1000))
                                                        .concat(" seconds."));
                                    } else {
                                        headText = "Changed from ".concat(fromMsg)
                                                .concat(" to ").concat(toMsg).concat(" in ")
                                                .concat(String.valueOf(((toTime - fromTime) / 1000))
                                                        .concat(" seconds."));
                                    }
                                } else if(action.equals(Constants.HIST_ACTION_CONNECTED)){
                                    time = DateFormat.formatDateTime(toTime);
                                    headText = action.concat(" to ").concat(toMsg).concat(".");
                                } else if(action.equals(Constants.HIST_ACTION_DISCONNECTED)){
                                    time = DateFormat.formatDateTime(fromTime);
                                    headText = action.concat(" from ").concat(fromMsg).concat(".");
                                }
                                latLng = new LatLng(lat, lng);
                                String title = time;
                                try {
                                    if(mInternetAvailable) {
                                        addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                                        if (addresses.size() == 0) {
                                            title = title + "- Unknown Address";
                                        } else {
                                            Address address = addresses.get(0);
                                            title = title + "-" + address.getAddressLine(0);
                                        }
                                    } else {
                                        title = time;
                                    }
                                    Log.d("HistMap", "Lat/Lng" + latLng.latitude + "/" + latLng.longitude);
                                    MarkerOptions markerOptions = new MarkerOptions().position(latLng)
                                            .title(title)
                                            .snippet(headText);
                                    Marker marker = mMap.addMarker(markerOptions);
                                    //marker.showInfoWindow();
                                    markers.add(marker);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                            LatLngBounds.Builder builder = new LatLngBounds.Builder();
                            for(Marker marker: markers) {
                                builder.include(marker.getPosition());
                            }
                            LatLngBounds bounds = builder.build();
                            int padding = 150; // offset from edges of the map in pixels
                            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                            mMap.animateCamera(cu);
                            mCursor.close();
                            Toast.makeText(getContext(), "If multiple events occured at the same location, " +
                                    "only most recent is shown.", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        //if(mInternetAvailable){
            loadMapObjects();
        //}
    }
}
