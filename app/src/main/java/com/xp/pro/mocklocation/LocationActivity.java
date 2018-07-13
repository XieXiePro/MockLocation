package com.xp.pro.mocklocation;

import android.app.Activity;
import android.os.Bundle;

public class LocationActivity extends Activity {
    LocationWigdet idLocationWigdet;
    LocationBean mLocationBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        initMockLocationData();
        initView();
    }

    private void initMockLocationData() {
        double latitude;
        double longitude;
        try {
            latitude = getIntent().getDoubleExtra("latitude", 0.0);
            longitude = getIntent().getDoubleExtra("longitude", 0.0);

        } catch (Exception e) {
            latitude = 0;
            longitude = 0;
        }
        mLocationBean = new LocationBean();
        mLocationBean.setLatitude(latitude);
        mLocationBean.setLongitude(longitude);
    }

    private void initView() {
        idLocationWigdet = (LocationWigdet) findViewById(R.id.id_location_wigdet);
    }

    @Override
    protected void onResume() {
        super.onResume();
        idLocationWigdet.setMangerLocationData(mLocationBean.getLatitude(), mLocationBean.getLongitude());
        idLocationWigdet.startMockLocation();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        idLocationWigdet.refreshData();
    }

    @Override
    protected void onPause() {
        idLocationWigdet.removeUpdates();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        idLocationWigdet.stopMockLocation();
        super.onDestroy();
    }
}