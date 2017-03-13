package com.aplace.admin.aplace.map;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresPermission;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aplace.admin.aplace.R;
import com.aplace.admin.aplace.main.MainActivity;
import com.aplace.admin.aplace.model.MakeItemOperator;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.BooleanResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.drive.events.CompletionEvent;
import com.google.android.gms.drive.query.Query;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.RuntimeExecutionException;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RestrictTo;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.util.SimpleArrayMap;
import android.support.v4.util.SparseArrayCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import java.io.FileDescriptor;
import java.io.PrintWriter;

/**
 * Created by admin on 10/03/2017.
 */

public class MapActi extends AppCompatActivity implements OnMapReadyCallback, Mapinter {
    private Boolean ClearMap = false;
    private MakeItemOperator Ope;
    private List<ShopContract> ShopList = new ArrayList<>();
    private SupportMapFragment mapFragment;
    private String RequestString;
    private EditText RequestEditText;
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference mDatabase = firebaseDatabase.getReference("aplacedb");
    private DatabaseReference mItemdb = firebaseDatabase.getReference("itemdb");
    private GoogleMap mMap;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        RunningMap();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        RunningMap();
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void RunningMap() {
        Checkperms();
        ////////////////////////////////
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot shop : dataSnapshot.getChildren()) {
                    ShopList.add(shop.getValue(ShopContract.class));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        ///////////////////////////////
        Ope = new MakeItemOperator();
        Ope.MakeOpe();
        /////////////////////////////////
        if (!gpsEnable()) {
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
            if (gpsEnable()) {
                ///turn on map
                mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_xml);
                mapFragment.getMapAsync(this);
                ///find
                RunningRequest();
            }
        }
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

    @Override
    public void RunningRequest() {
        final EditText editText = new EditText(getApplicationContext());
        final RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.map_backgroud);
        ImageButton button = (ImageButton) findViewById(R.id.search_click);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddRequest(editText);
                relativeLayout.addView(editText);
                AnswerRequest(editText);
                //ShowPlace(editText);
            }
        });
    }

    @Override
    public boolean ConditionSuccess(ShopContract shopContract) {
        if(Ope.isChild(shopContract.getType(), RequestString)) {
            return true;
        }
        return false;
    }


    @Override
    public void AddRequest(EditText editText) {
        editText.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
        editText.setHint("Bạn muốn tìm thứ gì ?");
        editText.setTextSize(30);
        editText.setHintTextColor(0xff000000);
        editText.setTextColor(0xff000000);
        editText.setGravity(Gravity.CENTER);
        editText.setSingleLine();

        //editText.setBackgroundColor(0xffffff00);
    }

    @Override
    public void AnswerRequest(final EditText editText) {

        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (event == null) {
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        RelativeLayout parent = (RelativeLayout) v.getParent();
                        parent.removeView(v);
                        ShowPlace(editText);
                    }
                }
                return false;
            }
        });
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public void Checkperms() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
    }

    @Override
    public void ShowPlace(EditText editText) {
        RequestEditText = editText;
        BuildMark(RequestEditText);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        } else {
            ///Init
            mMap = googleMap;
            mMap.setMyLocationEnabled(true);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(21.033333, 105.849998)));
            mMap.setMinZoomPreference(1);
        }
    }


    @Override
    public void BuildMark(EditText editText) {
        RequestString = editText.getText().toString();

        //firebaseDatabase.goOnline();
        mMap.clear();
        for (ShopContract shop : ShopList) {
            if(ConditionSuccess(shop)) {
                LatLng latLng = new LatLng(shop.getLatitue(), shop.getLongtitue());
                mMap.addMarker(new MarkerOptions().position(latLng));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            }
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 100: {

            }
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Success!", Toast.LENGTH_LONG).show();
                // permission was granted, yay! do the
                // calendar task you need to do.
            } else {
                // permission denied, boo! Disable the
                // functionality that depends on this permission.
                Toast.makeText(this, "You did not allow to access your current location", Toast.LENGTH_LONG).show();
            }
        }
    }


}
