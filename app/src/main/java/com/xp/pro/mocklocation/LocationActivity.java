package com.xp.pro.mocklocation;

import android.app.Activity;
import android.os.Bundle;

import com.xp.pro.mocklocationlib.LocationBean;
import com.xp.pro.mocklocationlib.LocationWidget;

public class LocationActivity extends Activity {
    LocationWidget idLocationWidget;
    LocationBean mLocationBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.location_content_view);
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
        idLocationWidget = (LocationWidget) findViewById(R.id.id_location_wigdet);
    }

    @Override
    protected void onResume() {
        super.onResume();
        idLocationWidget.setMangerLocationData(mLocationBean.getLatitude(), mLocationBean.getLongitude());
        idLocationWidget.startMockLocation();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        idLocationWidget.refreshData();
    }

    @Override
    protected void onPause() {
        idLocationWidget.removeUpdates();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        idLocationWidget.stopMockLocation();
        super.onDestroy();
    }
}