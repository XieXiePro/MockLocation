package com.xp.pro.mocklocation;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class Main2Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        TextView createDialog = (TextView) findViewById(R.id.create_dialog);

        createDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        LocationDialog.Builder builder = new LocationDialog.Builder(this);
        builder.setLatitude(25);
        builder.setLongitude(113.5);
        builder.create().show();
    }
}
