package com.aplace.admin.aplace.Search;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.aplace.admin.aplace.R;

/**
 * Created by admin on 17/03/2017.
 */

public class SearchRequest extends AppCompatActivity implements SearchInter {
    String SEARCH_LIST[] = {"Hiện những địa điểm bạn đã từng đến", "Null"};
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_list);
        getSupportActionBar().hide();
        //mFirebase = new Firebase(getApplicationContext());
        //nMap = new MapFace();
        ListView Search_list = (ListView) findViewById(R.id.search_list_listview);
        Search_list.setAdapter(new ArrayAdapter<String>(getApplicationContext(), R.layout.item, SEARCH_LIST));
        Search_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0) setResult(445);
                if(position == 1) setResult(234);
                finish();
            }
        });
    }
}
