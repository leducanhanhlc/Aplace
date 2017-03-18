package com.aplace.admin.aplace.main;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.aplace.admin.aplace.A.Sign_in.Sign_in_pre;
import com.aplace.admin.aplace.A.map.MapFace;
import com.aplace.admin.aplace.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference mDatabase = firebaseDatabase.getReference();
    private TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
       // startActivity(new Intent(getApplicationContext(), MakeItemOperator.class));
        startActivity(new Intent(getApplicationContext(), Sign_in_pre.class));
    }
}
