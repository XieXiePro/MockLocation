# Android 实现模拟地图定位功能

###  一、项目git地址：
&emsp;&emsp;https://github.com/XieXiePro/MockLocation

###  二、实现原理：
&emsp;&emsp;手机定位方式目前有4种：基站定位，WIFI定位，GPS定位，AGPS定位。

###### &emsp;&emsp;本工程利用手机自带的"模拟位置"功能实现运行时修改LocationManager结果。

###### &emsp;&emsp;原理：使用android自带的调试api，模拟gps provider的结果。

&emsp;&emsp;Android 6.0系统以下，可以通过Setting.Secure.ALLOW_MOCK_LOCATION获取是否【允许模拟位置】，当【允许模拟位置】开启时，可addTestProvider；

&emsp;&emsp;Android 6.0系统及以上，弃用Setting.Secure.ALLOW_MOCK_LOCATION变量，没有【允许模拟位置】选项，
增加【选择模拟位置信息应用】，此时需要选择当前应用，才可以addTestProvider，
但未找到获取当前选择应用的方法，因此通过addTestProvider是否成功来判断是否可用模拟位置。

###  三、代码分析：
MockLocationManager：模拟地址管理类
&emsp;&emsp;首先通过Android系统模拟位置管理器LocationManager获取系统模拟位置服务，Android 6.0以下，通过Setting.Secure.ALLOW_MOCK_LOCATION判断是否可模拟位置，Android 6.0及以上，需要【选择模拟位置信息应用】，未找到方法，因此通过addTestProvider是否可用判断。
```
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
```
&emsp;&emsp;接下来设置模拟经纬度数据：
```
  // 模拟位置（addTestProvider成功的前提下）
  for (String providerStr : mockProviders) {
  Location mockLocation = new Location(providerStr);
  mockLocation.setLatitude(latitude);   // 维度（度）
  mockLocation.setLongitude(longitude);  // 经度（度）
  mockLocation.setAccuracy(0.1f);   // 精度（米）
  mockLocation.setTime(new Date().getTime());   // 本地时间
  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
    mockLocation.setElapsedRealtimeNanos(SystemClock.elapsedRealtimeNanos());
  }
    locationManager.setTestProviderLocation(providerStr, mockLocation);
  }
```
&emsp;&emsp;取消模拟定位方法：
```
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
```

&emsp;&emsp;注册位置服务，获取系统位置
```
        // 注册位置服务，获取系统位置
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mockLocationManager.locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

```
&emsp;&emsp;最后通过LocationListener.onLocationChanged()回调方法获取GPS定位数据：
```
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
```

### 四、使用模拟定位需先开启系统设置中的模拟位置：

*   Android 6.0 以下：【开发者选项 -> 允许模拟位置】
![Android 6.0 以下：【开发者选项 -> 允许模拟位置】](https://upload-images.jianshu.io/upload_images/2783386-9e69fd0b4936dbf1.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

*   Android 6.0 及以上：【开发者选项 -> 选择模拟位置信息应用】

![ Android 6.0 及以上：【开发者选项 -> 选择模拟位置信息应用】](https://upload-images.jianshu.io/upload_images/2783386-5f501fc5b1f4bfd5.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)


参考链接：
[1、【科普】GPS、Wifi等各种手机定位方式的含义及原理详解](https://bbs.feng.com/read-htm-tid-7709847.html)
[2、Android 使用模拟位置（支持Android 6.0）](https://blog.csdn.net/doris_d/article/details/51384285)