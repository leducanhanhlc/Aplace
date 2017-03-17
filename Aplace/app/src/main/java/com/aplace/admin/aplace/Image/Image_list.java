package com.aplace.admin.aplace.Image;

import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ViewFlipper;

import com.aplace.admin.aplace.R;
import com.aplace.admin.aplace.model.Contract;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Set;

import static com.google.android.gms.fitness.data.WorkoutExercises.SWING;

/**
 * Created by admin on 17/03/2017.
 */

public class Image_list extends AppCompatActivity implements Image_list_inter{

    private ArrayList<String> Marker_choose_list;
    private ViewFlipper viewFlipper;
    private FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
    private StorageReference mStorage = firebaseStorage.getReference().child("images/");

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image);
        getSupportActionBar().hide();
        Marker_choose_list =  getIntent().getStringArrayListExtra("list");
        viewFlipper = (ViewFlipper) findViewById(R.id.image_fliper);
        Set_to_list();
    }
    
    @Override
    public void Set_to_list() {
        for (String url : Marker_choose_list) {
            ImageView imageView = new ImageView(getApplicationContext());
            Picasso.with(getApplicationContext()).load(url).into(imageView);
            viewFlipper.addView(imageView);
        }

        Button back = (Button) findViewById(R.id.back_press);
        Button next = (Button) findViewById(R.id.next_press);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewFlipper.showPrevious();
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewFlipper.showNext();
            }
        });
    }

}
