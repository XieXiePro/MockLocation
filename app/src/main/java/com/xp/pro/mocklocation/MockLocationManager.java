package com.xp.pro.mocklocation;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Build;
import android.os.SystemClock;
import android.provider.Settings;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * MockLocationManager:
 * Author: xp
 * Date: 18/7/12 22:01
 * Email: xiexiepro@gmail.com
 * Blog: http://XieXiePro.github.io
 */
public class MockLocationManager {

    MockLocationManager() {

    }

    /**
     * 位置管理器
     */
    public LocationManager locationManager = null;

    public LocationManager getLocationManager() {
        return locationManager;
    }

    /**
     * 模拟位置的提供者
     */
    private List<String> mockProviders = null;

    public List<String> getMockProviders() {
        return mockProviders;
    }

    /**
     * 是否成功addTestProvider，默认为true，软件启动时为防止意外退出导致未重置，重置一次
     * Android 6.0系统以下，可以通过Setting.Secure.ALLOW_MOCK_LOCATION获取是否【允许模拟位置】，
     * 当【允许模拟位置】开启时，可addTestProvider；
     * Android 6.0系统及以上，弃用Setting.Secure.ALLOW_MOCK_LOCATION变量，没有【允许模拟位置】选项，
     * 增加【选择模拟位置信息应用】，此时需要选择当前应用，才可以addTestProvider，
     * 但未找到获取当前选择应用的方法，因此通过addTestProvider是否成功来判断是否可用模拟位置。
     */
    private boolean hasAddTestProvider = true;

    /**
     * 启动和停止模拟位置的标识
     */
    public boolean bRun = false;


    /**
     * 初始化服务
     *
     * @param context
     */
    public void initService(Context context) {
        /**
         * 模拟位置服务
         */
        mockProviders = new ArrayList<>();
        mockProviders.add(LocationManager.GPS_PROVIDER);
//        mockProviders.add(LocationManager.NETWORK_PROVIDER);

        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        // 防止程序意外终止，没有停止模拟GPS
        stopMockLocation();
    }

    /**
     * 模拟位置是否启用
     * 若启用，则addTestProvider
     */
    public boolean getUseMockPosition(Context context) {
        // Android 6.0以下，通过Setting.Secure.ALLOW_MOCK_LOCATION判断
        // Android 6.0及以上，需要【选择模拟位置信息应用】，未找到方法，因此通过addTestProvider是否可用判断
        boolean canMockPosition = (Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.ALLOW_MOCK_LOCATION, 0) != 0)
                || Build.VERSION.SDK_INT > 22;
        if (canMockPosition && hasAddTestProvider == false) {
            try {
                for (String providerStr : mockProviders) {
                    LocationProvider provider = locationManager.getProvider(providerStr);
                    if (provider != null) {
                        locationManager.addTestProvider(
                                provider.getName()
                                , provider.requiresNetwork()
                                , provider.requiresSatellite()
                                , provider.requiresCell()
                                , provider.hasMonetaryCost()
                                , provider.supportsAltitude()
                                , provider.supportsSpeed()
                                , provider.supportsBearing()
                                , provider.getPowerRequirement()
                                , provider.getAccuracy());
                    } else {
                        if (providerStr.equals(LocationManager.GPS_PROVIDER)) {
                            locationManager.addTestProvider(
                                    providerStr
                                    , true, true, false, false, true, true, true
                                    , Criteria.POWER_HIGH, Criteria.ACCURACY_FINE);
                        } else if (providerStr.equals(LocationManager.NETWORK_PROVIDER)) {
                            locationManager.addTestProvider(
                                    providerStr
                                    , true, false, true, false, false, false, false
                                    , Criteria.POWER_LOW, Criteria.ACCURACY_FINE);
                        } else {
                            locationManager.addTestProvider(
                                    providerStr
                                    , false, false, false, false, true, true, true
                                    , Criteria.POWER_LOW, Criteria.ACCURACY_FINE);
                        }
                    }
                    locationManager.setTestProviderEnabled(providerStr, true);
                    locationManager.setTestProviderStatus(providerStr, LocationProvider.AVAILABLE, null, System.currentTimeMillis());
                }
                hasAddTestProvider = true;  // 模拟位置可用
                canMockPosition = true;
            } catch (SecurityException e) {
                canMockPosition = false;
            }
        }
        if (canMockPosition == false) {
            stopMockLocation();
        }
        return canMockPosition;
    }

    /**
     * 取消位置模拟，以免启用模拟数据后无法还原使用系统位置
     * 若模拟位置未开启，则removeTestProvider将会抛出异常；
     * 若已addTestProvider后，关闭模拟位置，未removeTestProvider将导致系统GPS无数据更新；
     */
    public void stopMockLocation() {
        if (hasAddTestProvider) {
            for (String provider : mockProviders) {
                try {
                    locationManager.removeTestProvider(provider);
                } catch (Exception ex) {
                    // 此处不需要输出日志，若未成功addTestProvider，则必然会出错
                    // 这里是对于非正常情况的预防措施
                }
            }
            hasAddTestProvider = false;
        }
    }

    /**
     * 模拟位置线程
     */
    private class RunnableMockLocation implements Runnable {

        @Override
        public void run() {
            while (true) {
                try {
                    Thread.sleep(1000);

                    if (hasAddTestProvider == false) {
                        continue;
                    }

                    if (bRun == false) {
                        stopMockLocation();
                        continue;
                    }

                    try {
                        // 模拟位置（addTestProvider成功的前提下）
                        for (String providerStr : mockProviders) {
                            Location mockLocation = new Location(providerStr);
                            mockLocation.setLatitude(22);   // 维度（度）
                            mockLocation.setLongitude(113);  // 经度（度）
                            mockLocation.setAltitude(30);    // 高程（米）
                            mockLocation.setBearing(180);    // 方向（度）
                            mockLocation.setSpeed(10);    //速度（米/秒）
                            mockLocation.setAccuracy(0.1f);   // 精度（米）
                            mockLocation.setTime(new Date().getTime());   // 本地时间
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                                mockLocation.setElapsedRealtimeNanos(SystemClock.elapsedRealtimeNanos());
                            }
                            locationManager.setTestProviderLocation(providerStr, mockLocation);
                        }
                    } catch (Exception e) {
                        // 防止用户在软件运行过程中关闭模拟位置或选择其他应用
                        stopMockLocation();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void startThread() {
        new Thread(new RunnableMockLocation()).start();
    }
}
