package com.xp.pro.mocklocationlib;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * LocationWigdet:模拟位置信息提示控件
 * Author: xp
 * Date: 18/7/12 22:22
 * Email: xiexiepro@gmail.com
 * Blog: http://XieXiePro.github.io
 */
public class LocationWidget extends LinearLayout {
    MockLocationManager mockLocationManager;
    private Context context;
    private TextView tvProvider = null;
    private TextView tvTime = null;
    private TextView tvLatitude = null;
    private TextView tvLongitude = null;
    private TextView tvSystemMockPositionStatus = null;
    private Button btnStartMock = null;
    private Button btnStopMock = null;
    private ImageView locationWigdetTipIv;
    private LinearLayout locationWigdetDataLl;


    public LocationWidget(Context context) {
        super(context);
        this.context = context;
        init(context);
    }

    public LocationWidget(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init(context);
    }

    public LocationWidget(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init(context);
    }

    private void init(final Context context) {
        View layout = LayoutInflater.from(context).inflate(R.layout.location_wiget_layout, this, true);
        tvProvider = (TextView) layout.findViewById(R.id.tv_provider);
        tvTime = (TextView) layout.findViewById(R.id.tv_time);
        tvLatitude = (TextView) layout.findViewById(R.id.tv_latitude);
        tvLongitude = (TextView) layout.findViewById(R.id.tv_longitude);
        tvSystemMockPositionStatus = (TextView) findViewById(R.id.tv_system_mock_position_status);
        locationWigdetTipIv = (ImageView) findViewById(R.id.location_wigdet_tip_iv);
        locationWigdetDataLl = (LinearLayout) findViewById(R.id.location_wigdet_data_ll);

        btnStartMock = (Button) findViewById(R.id.btn_start_mock);
        btnStopMock = (Button) findViewById(R.id.btn_stop_mock);

        mockLocationManager = new MockLocationManager();
        btnStartMock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startMock();
            }
        });
        btnStopMock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopMock();
            }
        });
        mockLocationManager.initService(context);

        mockLocationManager.startThread();

    }

    /**
     * 开始模拟定位
     */
    public void stopMock() {
        stopMockLocation();
        btnStartMock.setEnabled(true);
        btnStopMock.setEnabled(false);
    }

    /**
     * 停止模拟定位
     */
    public void startMock() {
        if (mockLocationManager.getUseMockPosition(context)) {
            startMockLocation();
            btnStartMock.setEnabled(false);
            btnStopMock.setEnabled(true);
        }
    }

    public void refreshData() {
        // 判断系统是否允许模拟位置，并addTestProvider
        if (mockLocationManager.getUseMockPosition(context)) {
            if (mockLocationManager.bRun) {
                btnStartMock.setEnabled(false);
                btnStopMock.setEnabled(true);
            } else {
                btnStartMock.setEnabled(true);
                btnStopMock.setEnabled(false);
            }
            tvSystemMockPositionStatus.setText("已开启");
            locationWigdetTipIv.setVisibility(View.GONE);
            locationWigdetDataLl.setVisibility(View.VISIBLE);
        } else {
            mockLocationManager.bRun = false;
            btnStartMock.setEnabled(false);
            btnStopMock.setEnabled(false);
            tvSystemMockPositionStatus.setText("未开启");
            locationWigdetTipIv.setVisibility(View.VISIBLE);
            locationWigdetDataLl.setVisibility(View.GONE);
        }
        mockLocationManager.locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
    }

    public void removeUpdates() {
        mockLocationManager.locationManager.removeUpdates(locationListener);
    }

    public void stopMockLocation() {
        mockLocationManager.bRun = false;
        mockLocationManager.stopMockLocation();
    }

    public void startMockLocation() {
        mockLocationManager.bRun = true;
    }

    private LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(final Location location) {
            setLocationData(location);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    /**
     * 获取到模拟定位信息，并显示
     *
     * @param location 定位信息
     */
    private void setLocationData(Location location) {
        tvProvider.setText(location.getProvider());
        tvTime.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(location.getTime())));
        tvLatitude.setText(location.getLatitude() + " °");
        tvLongitude.setText(location.getLongitude() + " °");
    }

    public void setMangerLocationData(double lat, double lon) {
        mockLocationManager.setLocationData(lat, lon);
    }
}