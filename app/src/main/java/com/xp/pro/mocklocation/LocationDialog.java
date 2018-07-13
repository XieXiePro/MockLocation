package com.xp.pro.mocklocation;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.view.LayoutInflater;
import android.view.View;

/**
 * LocationDialog:
 * Author: xp
 * Date: 18/7/13 00:18
 * Email: xiexiepro@gmail.com
 * Blog: http://XieXiePro.github.io
 */
public class LocationDialog extends Dialog {


    public LocationDialog(Context context) {
        super(context);
    }

    public LocationDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);
    }

    public LocationDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    public static class Builder {

        private Context context;

        private double latitude;

        private double longitude;

        LocationWigdet idLocationWigdet;

        public Builder(Context context) {
            this.context = context;
        }

        public double getLatitude() {
            return latitude;
        }

        public void setLatitude(double latitude) {
            this.latitude = latitude;
        }

        public double getLongitude() {
            return longitude;
        }

        public void setLongitude(double longitude) {
            this.longitude = longitude;
        }

        public LocationDialog create() {
            LocationDialog mLocationDialog = new LocationDialog(context);
            View view = LayoutInflater.from(context).inflate(R.layout.activity_location, null);
            idLocationWigdet = (LocationWigdet) view.findViewById(R.id.id_location_wigdet);
            idLocationWigdet.setMangerLocationData(latitude, longitude);
            idLocationWigdet.startMockLocation();
            idLocationWigdet.refreshData();
            mLocationDialog.setTitle("模拟定位");
            mLocationDialog.setContentView(view);
            return mLocationDialog;
        }
    }
}
