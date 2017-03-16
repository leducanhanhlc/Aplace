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

public interface MapFace_Inter {
    public void Add_mark_allway_center();
    public void Add_mark_permanent();
    public void RunningMap();
    public Intent RequireGps();
    public boolean gpsEnable();
    public void MoveCamtoUserLocation();
    public void ForUpdateButton();
    public void Choose_image();
    public void Update_image_to_storage();
    public void Update_marker_builded();
    public void Update_database_to_firebase();
    public void ForSearchButton();
    public void ForMarkClick();
}
