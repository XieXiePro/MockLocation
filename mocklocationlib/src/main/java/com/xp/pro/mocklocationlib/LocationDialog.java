package com.xp.pro.mocklocationlib;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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

        LocationWidget idLocationWigdet;

        private String positiveButtonText;

        private OnClickListener positiveButtonClickListener;

        public Builder(Context context) {
            this.context = context;
        }

        /**
         * Set the positive button resource and it's listener
         */
        public Builder setPositiveButton(int positiveButtonText, OnClickListener listener) {
            this.positiveButtonText = (String) context
                    .getText(positiveButtonText);
            this.positiveButtonClickListener = listener;
            return this;
        }

        public Builder setPositiveButton(String positiveButtonText, OnClickListener listener) {
            this.positiveButtonText = positiveButtonText;
            this.positiveButtonClickListener = listener;
            return this;
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
            final LocationDialog mLocationDialog = new LocationDialog(context);
            View view = LayoutInflater.from(context).inflate(R.layout.location_content_view, null);
            idLocationWigdet = (LocationWidget) view.findViewById(R.id.id_location_wigdet);

            // set the confirm button
            if (positiveButtonText != null) {
                ((Button) view.findViewById(R.id.btn_mock))
                        .setText(positiveButtonText);
                if (positiveButtonClickListener != null) {
                    view.findViewById(R.id.btn_mock)
                            .setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    positiveButtonClickListener.onClick(mLocationDialog,
                                            DialogInterface.BUTTON_POSITIVE);
                                    //点击按钮停止定位
                                    idLocationWigdet.stopMockLocation();
                                }
                            });
                }
            } else {
                // if no confirm button just set the visibility to GONE
                view.findViewById(R.id.btn_mock)
                        .setVisibility(View.GONE);
            }
            idLocationWigdet.setMangerLocationData(latitude, longitude);
            idLocationWigdet.startMockLocation();
            idLocationWigdet.refreshData();
            mLocationDialog.setTitle("模拟定位");
            mLocationDialog.setContentView(view);
            return mLocationDialog;
        }
    }
}