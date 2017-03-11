package com.aplace.admin.aplace.map;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;

/**
 * Created by admin on 11/03/2017.
 */

public interface Mapinter {
    public void Checkperms();
    public void ShowPlace();
    public void AddRequest(EditText editText);
    public void AnswerRequest(EditText editText);
    public void RunningMap();
    public Intent RequireGps();
    public boolean gpsEnable();
    public void RunningRequest();
}
