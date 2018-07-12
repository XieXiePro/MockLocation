package com.xp.pro.mocklocation;

import android.app.Activity;
import android.os.Bundle;

public class MainActivity extends Activity {
    LocationWigdet idLocationWigdet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        idLocationWigdet = (LocationWigdet) findViewById(R.id.id_location_wigdet);
    }

    @Override
    protected void onResume() {
        super.onResume();
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