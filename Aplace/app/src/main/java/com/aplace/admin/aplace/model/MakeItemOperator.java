package com.aplace.admin.aplace.model;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by admin on 13/03/2017.
 */

public class MakeItemOperator extends AppCompatActivity implements OperaterInter {

    @Override
    public boolean isChild(String Child, String Parent) {
        return true;
    }

    @Override
    public void MakeOpe() {

    }


}
