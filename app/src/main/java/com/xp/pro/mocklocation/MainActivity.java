package com.xp.pro.mocklocation;

import android.app.Activity;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends Activity {

    private TextView tvSystemMockPositionStatus = null;
    private Button btnStartMock = null;
    private Button btnStopMock = null;
    private TextView tvProvider = null;
    private TextView tvTime = null;
    private TextView tvLatitude = null;
    private TextView tvLongitude = null;

    MockLocationManager mockLocationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvSystemMockPositionStatus = (TextView) findViewById(R.id.tv_system_mock_position_status);
        btnStartMock = (Button) findViewById(R.id.btn_start_mock);
        btnStopMock = (Button) findViewById(R.id.btn_stop_mock);
        tvProvider = (TextView) findViewById(R.id.tv_provider);
        tvTime = (TextView) findViewById(R.id.tv_time);
        tvLatitude = (TextView) findViewById(R.id.tv_latitude);
        tvLongitude = (TextView) findViewById(R.id.tv_longitude);

        mockLocationManager = new MockLocationManager();

        btnStartMock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mockLocationManager.getUseMockPosition(MainActivity.this)) {
                    mockLocationManager.bRun = true;
                    btnStartMock.setEnabled(false);
                    btnStopMock.setEnabled(true);
                }
            }
        });
        btnStopMock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mockLocationManager.bRun = false;
                mockLocationManager.stopMockLocation();
                btnStartMock.setEnabled(true);
                btnStopMock.setEnabled(false);
            }
        });

        mockLocationManager.initService(this);

        mockLocationManager.startThread();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        // 判断系统是否允许模拟位置，并addTestProvider
        if (mockLocationManager.getUseMockPosition(MainActivity.this) == false) {
            mockLocationManager.bRun = false;
            btnStartMock.setEnabled(false);
            btnStopMock.setEnabled(false);
            tvSystemMockPositionStatus.setText("未开启");
        } else {
            if (mockLocationManager.bRun) {
                btnStartMock.setEnabled(false);
                btnStopMock.setEnabled(true);
            } else {
                btnStartMock.setEnabled(true);
                btnStopMock.setEnabled(false);
            }
            tvSystemMockPositionStatus.setText("已开启");
        }

        // 注册位置服务，获取系统位置
        mockLocationManager.locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
    }

    @Override
    protected void onPause() {
        mockLocationManager.locationManager.removeUpdates(locationListener);

        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mockLocationManager.bRun = false;
        mockLocationManager.stopMockLocation();

        super.onDestroy();
    }

    private LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(final Location location) {
            try {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tvProvider.setText(location.getProvider());
                        tvTime.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(location.getTime())));
                        tvLatitude.setText(location.getLatitude() + " °");
                        tvLongitude.setText(location.getLongitude() + " °");
                    }
                });
            } catch (Exception ex) {
                ex.printStackTrace();
            }
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
}