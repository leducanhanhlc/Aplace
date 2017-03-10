package com.aplace.admin.aplace.map;

import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.aplace.admin.aplace.R;
import com.aplace.admin.aplace.main.MainActivity;

/**
 * Created by admin on 10/03/2017.
 */

public class MapActi extends AppCompatActivity implements Mapinter {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(!gpsEnable()){
            setContentView(R.layout.gps);
            Button gps = (Button) findViewById(R.id.gps_xml);
            gps.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(RequireGps());
                }
            });

        } else {
            setContentView(R.layout.map);
        }
    }



    @Override
    public void RunningMap() {


    }

    @Override
    public Intent RequireGps() {
        return new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);

    }


    @Override
    public boolean gpsEnable() {
        LocationManager mn = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        return mn.isProviderEnabled(LocationManager.GPS_PROVIDER);

    }
}
