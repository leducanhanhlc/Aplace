package com.aplace.admin.aplace.Image;

import android.app.ListActivity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import com.aplace.admin.aplace.R;
import com.aplace.admin.aplace.model.firebase;
import com.google.firebase.database.DatabaseReference;

import java.io.IOException;
import java.net.URI;

/**
 * Created by admin on 15/03/2017.
 */

public class add_image extends AppCompatActivity implements add_image_inter {

    private firebase mFirebase;
    private int PICK_IMAGE_REQUEST = 1;
    private Uri Image_uri;

    String Image_Request_List[] = {"Tải ảnh lên tại vị trí hiện tại", "Tải ảnh lên tại vị trí khác"};
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_update);
        mFirebase = new firebase(getApplicationContext());
        ListView Image_update_list = (ListView) findViewById(R.id.up_image_list);
        Image_update_list.setAdapter(new ArrayAdapter<String>(getApplicationContext(), R.layout.item, Image_Request_List));
        Image_update_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0) add_this();
                if(position == 1) add_other();
            }
        });
    }

    @Override
    public void add_this() {
        Intent intent = new Intent();
// Show only images, no videos or anything else
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
// Always show the chooser (if there are multiple options available)
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    public void add_other() {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Image_uri = data.getData();
            mFirebase.Add_image(Image_uri);
        }
    }
}
