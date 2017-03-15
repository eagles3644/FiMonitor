package com.tishcn.fimonitor.ui;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.tishcn.fimonitor.R;
import com.tishcn.fimonitor.util.Constants;
import com.tishcn.fimonitor.util.FiMonitor;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LatLng mLatLng;
    private String mTime;
    private String mHead;
    private boolean mInternetAvailable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        mLatLng = new LatLng(intent.getDoubleExtra(Constants.MAP_LAT_CONSTANT,0), intent.getDoubleExtra(Constants.MAP_LNG_CONSTANT,0));
        Log.d("MapActLatLng", "Lat/Lng" + String.valueOf(mLatLng.latitude + "/" + mLatLng.longitude));
        mTime = intent.getStringExtra(Constants.MAP_TIME_CONSTANT);
        mHead = intent.getStringExtra(Constants.MAP_HEAD_TEXT);
        ActionBar actionBar = this.getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle(mTime);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        FiMonitor fiMonitor = new FiMonitor(getApplicationContext());
        mInternetAvailable = fiMonitor.wifiConnected() || !fiMonitor.getNetworkOperatorName(fiMonitor.getNetworkOperator()).equals(Constants.NOT_CONNECTED);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        List<Address> addresses = null;
        try {
            String title;
            if (mInternetAvailable) {
                addresses = geocoder.getFromLocation(mLatLng.latitude, mLatLng.longitude, 1);
                if (addresses.size() == 0) {
                    title = "Unknown Address";
                } else {
                    Address address = addresses.get(0);
                    title = address.getAddressLine(0);
                }
            } else {
                title = mTime;
            }
            MarkerOptions markerOptions = new MarkerOptions().position(mLatLng)
                    .title(title)
                    .snippet(mHead);
            Marker marker = mMap.addMarker(markerOptions);
            marker.showInfoWindow();
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mLatLng, 19));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
