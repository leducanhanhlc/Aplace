package com.aplace.admin.aplace.main;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.accessibility.AccessibilityManager;
import android.widget.Button;
import android.widget.Toast;

import com.aplace.admin.aplace.map.MapActi;
import com.aplace.admin.aplace.R;

import java.util.Map;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startActivity(new Intent(getApplicationContext(), MapActi.class));
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
