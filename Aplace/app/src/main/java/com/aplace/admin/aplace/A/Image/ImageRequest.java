package com.aplace.admin.aplace.A.Image;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.aplace.admin.aplace.R;

/**
 * Created by admin on 15/03/2017.
 */

public class ImageRequest extends AppCompatActivity implements ImageRequest_Inter {

    //private Firebase mFirebase;


    String Image_Request_List[] = {"Chọn địa điểm", "Null"};
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_list);
        getSupportActionBar().hide();
        //mFirebase = new Firebase(getApplicationContext());
        //nMap = new MapFace();
        ListView Update_list = (ListView) findViewById(R.id.update_list_listview);
        Update_list.setAdapter(new ArrayAdapter<String>(getApplicationContext(), R.layout.item, Image_Request_List));
        Update_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0) setResult(123);
                if(position == 1) setResult(234);
                finish();
            }
        });
    }


}
