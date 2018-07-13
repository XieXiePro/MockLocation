package com.xp.pro.mocklocation;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class LocationDialogDemo extends AppCompatActivity {
    LocationBean mLocationBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initMockLocationData();
        createLocationDialog();
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

    /**
     * 创建模拟定位对话框
     */
    private void createLocationDialog() {
        LocationDialog.Builder builder = new LocationDialog.Builder(this);
        builder.setLatitude(mLocationBean.getLatitude());
        builder.setLongitude(mLocationBean.getLongitude());
        builder.create().show();
    }
}
