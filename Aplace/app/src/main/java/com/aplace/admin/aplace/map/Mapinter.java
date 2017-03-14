package com.aplace.admin.aplace.map;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * Created by admin on 11/03/2017.
 */

public interface Mapinter {
    public void RunningMap();
    public Intent RequireGps();
    public boolean gpsEnable();
    public void MoveCamtoUserLocation();
    public void BuildGooglePlacesAPI();
}
