package com.aplace.admin.aplace.A.map;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.aplace.admin.aplace.A.Addfriend.Addfiend;
import com.aplace.admin.aplace.A.Image.ImageRequest;
import com.aplace.admin.aplace.A.Image.Image_list;
import com.aplace.admin.aplace.R;
import com.aplace.admin.aplace.A.Search.SearchRequest;
import com.aplace.admin.aplace.View.HorizontalListView;
import com.aplace.admin.aplace.model.Contract;
import com.aplace.admin.aplace.model.FriendList;
import com.facebook.login.widget.ProfilePictureView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.snapshot.Snapshot;
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.IDN;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.facebook.R.id.time;

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
    private ArrayList<Contract> Marker_list = new ArrayList<>();
    private Set<Contract> Marker_choose_set;
    private ArrayList<String> Marker_choose_list;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseAuth.AuthStateListener mAuthListener;
    private String User_id;
    private String User_from;
    private Bitmap mBitmap;
    private ProfilePictureView profilePictureView;
    private HorizontalListView friendlist_view;
    private BitmapDescriptor profile_picture_Descriptor;
    private ArrayList<String> friend_id_list = new ArrayList<>();
    private ArrayList<String> profile_url_list = new ArrayList<>();
    private String picture_profile_download_url;
    private DataSnapshot snapshot;
    private boolean Init_ok = false;
    private Handler handler = new Handler();
    private Bitmap bitmapforfriend;

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
        if (getIntent().getStringExtra("user_from").equals("Facebook")) RunningMap();

    }

    @Override
    public void RunningMap() {
        setContentView(R.layout.map);

        profilePictureView = (ProfilePictureView) findViewById(R.id.profile_image);
        profilePictureView.setVisibility(View.INVISIBLE);

        friendlist_view = (HorizontalListView) findViewById(R.id.friendlist_view_);

        User_id = getIntent().getStringExtra("user_id");
        User_from = getIntent().getStringExtra("user_from");
        TextView textView = (TextView) findViewById(R.id.testshowid);
        textView.setHint("your id :" + User_id);

        Ok_button = (Button) findViewById(R.id.ok_button);
        Ok_button.setVisibility(View.INVISIBLE);


        Marker_list = new ArrayList<>();
        Marker_choose_set = new HashSet<>();
        Marker_choose_list = new ArrayList<>();
        ///turn on map
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_xml);
        mapFragment.getMapAsync(this);

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                GetDB_Snapshot();
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();


        Runnable runnable1 = new Runnable() {
            @Override
            public void run() {
                while (true) {
                    if (Init_ok) {

                        Looper.prepare();

                        ForSearchButton();

                        ForAddfriend();

                        ForUpdateButton();

                        Forfriendmap();

                        break;
                    }
                }
            }
        };


        Thread thread1 = new Thread(runnable1);
        thread1.start();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Retrieve_marker_builded_and_friend(User_from, User_id, -1);
                Retrieve_friendlist();
                Updata_picture_profile_to_firebase();
                handler.postDelayed(this, 10000);
            }
        }, 10000);


    }


    @Override
    public void Forfriendmap() {
        friendlist_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Retrieve_marker_builded_and_friend(User_from, friend_id_list.get(position), position);
            }
        });
    }

    @Override
    public void Retrieve_marker_builded_and_friend(String from, String id, int position) {
        Marker_list.clear();
        for (DataSnapshot place : snapshot.child(from).child(id).child("activity").getChildren()) {
            Contract contract = place.getValue(Contract.class);
            Marker_list.add(contract);
        }

        /////
        if (User_id.equals(id)) {
            friend_id_list.clear();
            profile_url_list.clear();
            for (DataSnapshot friend : snapshot.child(User_from).child(User_id).child("friend").getChildren()) {
                String Id = friend.getValue().toString();
                friend_id_list.add(Id);
                profile_url_list.add(snapshot.child(User_from).child(Id).child("profile")
                        .child("image_url").getValue().toString());
            }
        } else {
            Show_marker_added(User_from, friend_id_list.get(position), position);
        }
        /////
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    public void Retrieve_friendlist() {

        friendlist_view.setAdapter(new FriendList(getApplicationContext(), friend_id_list, profile_url_list));
    }


    @Override
    public void ForAddfriend() {
        ImageView imageView = (ImageView) findViewById(R.id.addfriend_button);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Addfiend.class);
                intent.putExtra("user_from", "Facebook");
                intent.putExtra("user_id", User_id);
                startActivity(intent);
            }
        });
    }

    @Override
    public void GetDB_Snapshot() {
        mDatabase.child("aplace").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                snapshot = dataSnapshot;
                Init_ok = true;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void ForMarkClick(String from, String id, int position) {

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Marker_choose_set.clear();
                Marker_choose_list.clear();
                for (Contract contract : Marker_list) {
                    if (contract.getLatitue() - marker.getPosition().latitude < 1e-20
                            && contract.getLongtitue() - marker.getPosition().longitude < 1e-20) {
                        Marker_choose_set.add(contract);
                    }
                }

                for (Contract contract : Marker_choose_set) {
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
    public void Show_marker_added(String from, String id, int position) {
        mMap.clear();
        ForMarkClick(from, id, position);

        BitmapDescriptor bitmapDescriptor = Profile_picture(User_from, User_id, -1);
        for (Contract contract : Marker_list) {
            LatLng ln = new LatLng(contract.getLatitue(), contract.getLongtitue());
            //LatLng ln1 = new LatLng(contract.getLatitue() + 1e-3, contract.getLongtitue() + 1e-3);

            if (User_id.equals(id)) {
                mMap.addMarker(new MarkerOptions().position(ln).icon(bitmapDescriptor));
                //mMap.addMarker(new MarkerOptions().position(ln1)));
            } else {
                mMap.addMarker(new MarkerOptions().position(ln).icon(Profile_picture(from, id, position)));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(ln));            }
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
            case 123: {
                friendlist_view.setVisibility(View.INVISIBLE);
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
            case 234: {
                mMap.clear();
                Ok_button.setVisibility(View.INVISIBLE);
            }
            break;
            case RESULT_OK: {
                Ok_button.setVisibility(View.INVISIBLE);
                Image_uri = data.getData();
                Add_mark_permanent();
                mMarker.remove();
                Update_image_to_storage();
            }
            break;
            case 445: {
                Show_marker_added(User_from, User_id, -1);
            }
            break;
            default:
                return;
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
    public Bitmap Circle_of(Bitmap bitmap) {
        Bitmap circleBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        BitmapShader shader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        Paint paint = new Paint();
        paint.setShader(shader);
        paint.setAntiAlias(true);
        Canvas c = new Canvas(circleBitmap);
        c.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2, bitmap.getWidth() / 2, paint);
        return circleBitmap;
    }


    @Override
    public BitmapDescriptor Profile_picture(String from, String id, int position) {
        if (User_id.equals(id)) {
            profilePictureView.setEnabled(true);
            profilePictureView.setProfileId(User_id);
            profilePictureView.setDrawingCacheEnabled(true);
            profilePictureView.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
            profilePictureView.layout(0, 0, profilePictureView.getMeasuredWidth(), profilePictureView.getMeasuredHeight());
            profilePictureView.buildDrawingCache();
            mBitmap = profilePictureView.getDrawingCache();
            return BitmapDescriptorFactory.fromBitmap(Circle_of(mBitmap));
        } else {
            Picasso.with(getApplicationContext())
                    .load(profile_url_list.get(position))
                    .into(new Target() {
                        @Override
                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                            bitmapforfriend = bitmap;
                        }

                        @Override
                        public void onBitmapFailed(Drawable errorDrawable) {

                        }

                        @Override
                        public void onPrepareLoad(Drawable placeHolderDrawable) {
                        }
                    });

        }

        return BitmapDescriptorFactory.fromBitmap(Circle_of(bitmapforfriend));
    }
    //return profile.getProfilePictureUri(20, 20).;


    @Override
    public void Update_image_to_storage() {
        UploadTask uploadTask = mStorage.child(User_from).child(User_id).child("images/").child(Image_uri.getLastPathSegment()).putFile(Image_uri);
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
        super.onStart();
        mGoogleApiClient.connect();
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
                    Retrieve_friendlist();
                    Updata_picture_profile_to_firebase();
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
        mDatabase.child("aplace").child(User_from).child(User_id).child("activity").push().setValue(contract);
        ///not fix

    }


    @Override
    public void onConnectionSuspended(int i) {

    }


    @Override
    public void Updata_picture_profile_to_firebase() {
        profile_picture_Descriptor = Profile_picture(User_from, User_id, -1);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        mBitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
        byte[] byteArray = stream.toByteArray();
        UploadTask uploadTask = mStorage.child(User_from).child(User_id).child("profile/").child("picture").putBytes(byteArray);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                @VisibleForTesting String picture_profile_download_url_ = taskSnapshot.getDownloadUrl().toString();
                mDatabase.child("aplace").child(User_from).child(User_id).child("profile")
                        .child("image_url")
                        .setValue(picture_profile_download_url_);
            }
        });
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
