package com.aplace.admin.aplace.A.map;

import android.content.Intent;
import android.graphics.Bitmap;

import com.google.android.gms.maps.model.BitmapDescriptor;

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
    public void Retrieve_marker_builded_and_friend(String from, String id, int position);
    public void Update_database_to_firebase();
    public void ForSearchButton();
    public void ForMarkClick(String from, String id, int position);
    public void Show_marker_added(String from, String id, int position);
    public BitmapDescriptor Profile_picture(String from, String id, int position);
    public Bitmap Circle_of(Bitmap bitmap);
    public void Retrieve_friendlist();
    public void Updata_picture_profile_to_firebase();
    public void ForAddfriend();
    public void GetDB_Snapshot();
    public void Forfriendmap();
}
