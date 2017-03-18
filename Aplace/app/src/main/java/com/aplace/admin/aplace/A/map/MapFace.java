package com.aplace.admin.aplace.A.map;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.v4.app.ActivityCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.aplace.admin.aplace.A.Image.ImageRequest;
import com.aplace.admin.aplace.A.Image.Image_list;
import com.aplace.admin.aplace.R;
import com.aplace.admin.aplace.A.Search.SearchRequest;
import com.aplace.admin.aplace.model.Contract;
import com.facebook.Profile;
import com.facebook.login.widget.ProfilePictureView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutionException;

/**
 * Created by admin on 10/03/2017.
 */

public class MapFace extends AppCompatActivity implements OnMapReadyCallback,
        MapFace_Inter,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {
    private int InitMoveCam = 0;
    private SupportMapFragment mapFragment;
    private GoogleMap mMap;
    private LocationManager locationManager;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private LocationRequest mLocationRequest;
    private Marker mMarker;
    private int ADD_IMAGE_TO_PLACE_CODE = 123;
    private int ADD_SEARCH_PLACE_CODE = 445;
    private int SELECT_IMAGE = 238;
    private Button Ok_button;
    private Uri Image_uri;
    private Uri Image_url;
    private FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
    private StorageReference mStorage = firebaseStorage.getReference();
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference mDatabase = firebaseDatabase.getReference();
    private double cLatitue;
    private double cLongtitue;
    private ArrayList<Contract> Marker_list;
    private Set<Contract>  Marker_choose_set;
    private ArrayList<String> Marker_choose_list;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseAuth.AuthStateListener mAuthListener;
    private String User_id;
    private String User_from;
    private Bitmap mBitmap;
    private ProfilePictureView profilePictureView;

    @Override
    public void ForSearchButton() {
        ImageButton search = (ImageButton) findViewById(R.id.search_click);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Add_mark()
                startActivityForResult(new Intent(getApplicationContext(), SearchRequest.class), 0);
            }
        });
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        buildGoogleApiClient();
        RunningMap();

    }

    @Override
    public void Retrieve_marker_builded() {
        mDatabase.child("aplace").child(User_from).child(User_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot place : dataSnapshot.getChildren()) {
                        Contract contract = place.getValue(Contract.class);
                        //LatLng latLng = new LatLng(contract.getLatitue(), contract.getLongtitue());
                        Marker_list.add(contract);
                        //mMap.addMarker(new MarkerOptions().position(latLng));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    public void RunningMap() {
        setContentView(R.layout.map);

        profilePictureView = (ProfilePictureView) findViewById(R.id.profile_image);
        profilePictureView.setVisibility(View.INVISIBLE);

        User_id = getIntent().getStringExtra("user_id");
        User_from = getIntent().getStringExtra("user_from");

        Ok_button = (Button) findViewById(R.id.ok_button);
        Ok_button.setVisibility(View.INVISIBLE);

        Marker_list = new ArrayList<>();
        Marker_choose_set = new HashSet<>();
        Marker_choose_list = new ArrayList<>();
        ///turn on map
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_xml);
        mapFragment.getMapAsync(this);

        Retrieve_marker_builded();

        ForSearchButton();
        ForUpdateButton();
    }

    @Override
    public void ForMarkClick() {

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Marker_choose_set.clear();
                for(Contract contract : Marker_list) {
                    if(contract.getLatitue() - marker.getPosition().latitude < 1e-20
                            && contract.getLongtitue() - marker.getPosition().longitude < 1e-20) {
                        Marker_choose_set.add(contract);
                    }
                }

                for(Contract contract : Marker_choose_set) {
                    Marker_choose_list.add(contract.getImage_Url());
                }

                Intent intent = new Intent(getApplicationContext(), Image_list.class);
                intent.putStringArrayListExtra("list", Marker_choose_list);
                startActivity(intent);
                return false;
            }
        });
    }

    @Override
    public void ForUpdateButton() {
        ImageButton add_bt = (ImageButton) findViewById(R.id.add);
        add_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Add_mark()
                startActivityForResult(new Intent(getApplicationContext(), ImageRequest.class), 0);
            }
        });
    }

    @Override
    public void Show_marker_added() {
        ForMarkClick();
        BitmapDescriptor bitmapDescriptor = Profile_picture();
        for(Contract contract : Marker_list) {
            LatLng ln = new LatLng(contract.getLatitue(), contract.getLongtitue());
            //LatLng ln1 = new LatLng(contract.getLatitue() + 1e-3, contract.getLongtitue() + 1e-3);
            mMap.addMarker(new MarkerOptions().position(ln).icon(bitmapDescriptor));
            //mMap.addMarker(new MarkerOptions().position(ln1)));
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
            case 123 : {
                Add_mark_allway_center();
                Ok_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        cLatitue = mMarker.getPosition().latitude;
                        cLongtitue = mMarker.getPosition().longitude;
                        Choose_image();
                    }
                });

            }
            break;
            case 234 : {
                mMap.clear();
                Ok_button.setVisibility(View.INVISIBLE);
            }
            break;
            case RESULT_OK : {
                Ok_button.setVisibility(View.INVISIBLE);
                Image_uri = data.getData();
                Add_mark_permanent();
                mMarker.remove();
                Update_image_to_storage();
            }
            break;
            case 445 : {
                Show_marker_added();
            }


            default:return;
        }

    }

    @Override
    public void Choose_image() {
        Intent intent = new Intent();
// Show only images, no videos or anything else
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
// Always show the chooser (if there are multiple options available)
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_IMAGE);

    }

    @Override
    public void Add_mark_permanent() {
        if (mLastLocation != null) {
            LatLng ln = new LatLng
                    (mLastLocation.getLatitude(), mLastLocation.getLongitude());
            mMarker = mMap.addMarker(new MarkerOptions()
                    .position(ln));
        }
    }


    @Override
    public BitmapDescriptor Profile_picture() {
        profilePictureView.setEnabled(true);
        profilePictureView.setProfileId(User_id);
        profilePictureView.buildDrawingCache();
        mBitmap = profilePictureView.getDrawingCache();
        Bitmap circleBitmap = Bitmap.createBitmap(mBitmap.getWidth(), mBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        BitmapShader shader = new BitmapShader (mBitmap,  Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        Paint paint = new Paint();
        paint.setShader(shader);
        paint.setAntiAlias(true);
        Canvas c = new Canvas(circleBitmap);
        c.drawCircle(mBitmap.getWidth()/2, mBitmap.getHeight()/2, mBitmap.getWidth()/2, paint);
        return BitmapDescriptorFactory.fromBitmap(circleBitmap);
        //return profile.getProfilePictureUri(20, 20).;

    }

    @Override
    public void Update_image_to_storage() {
        UploadTask uploadTask = mStorage.child("images/").child(User_from).child(User_id).child(Image_uri.getLastPathSegment()).putFile(Image_uri);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(getApplicationContext(), "Tải ảnh lên thành công", Toast.LENGTH_SHORT).show();
                @VisibleForTesting Uri uri = taskSnapshot.getDownloadUrl();
                Image_url = uri;
                Update_database_to_firebase();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Tải ảnh lên thất bại", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }



    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public Intent RequireGps() {
        return new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
    }


    @Override
    public boolean gpsEnable() {
        locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

    }


    @Override
    public void MoveCamtoUserLocation() {
        if (mLastLocation != null) {
            LatLng ln = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLng(ln));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(14));

        }
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
            mMap.setOnMapClickListener(new OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {
                    if (!gpsEnable()) startActivity(RequireGps());
                }
            });
            mMap.setMyLocationEnabled(true);
            mMap.setMinZoomPreference(1);

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

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }



    @Override
    public void Update_database_to_firebase() {
        Contract contract = new Contract(cLongtitue, cLatitue, Image_uri.toString(), Image_url.toString());
        mDatabase.child("aplace").child(User_from).child(User_id).push().setValue(contract);
    }



    @Override
    public void onConnectionSuspended(int i) {

    }



    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        if (InitMoveCam < 2) {
            InitMoveCam++;
            MoveCamtoUserLocation();
        }
    }




    @Override
    public void Add_mark_allway_center() {
        if (mLastLocation != null) {
            LatLng ln = new LatLng
                    (mLastLocation.getLatitude(), mLastLocation.getLongitude());
            mMarker = mMap.addMarker(new MarkerOptions().position(ln));
            Ok_button.setVisibility(View.VISIBLE);
            mMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
                @Override
                public void onCameraMove() {
                    mMarker.setPosition(mMap.getCameraPosition().target);//to center in map
                    //marker.setTitle("Nhấn vào để lưu ảnh tại đây");
                }
            });
        }
    }


}
