package com.aplace.admin.aplace.model;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.ContactsContract;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.aplace.admin.aplace.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 20/03/2017.
 */

public class FriendList extends ArrayAdapter<String> {
    private Context activity;
    private final ArrayList<String> friendid_list;
    private final  ArrayList<String> url;
    private Bitmap profile_picture_bitmap;
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference mDatabase = firebaseDatabase.getReference();

    public FriendList(Context activity, ArrayList<String> friendid_list, ArrayList<String> url) {
        super(activity, R.layout.friendview, (String[]) url.toArray(new String[url.size()]));
        this.activity = activity;
        this.friendid_list = friendid_list;
        this.url = url;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(activity);
        View friendview = inflater.inflate(R.layout.friendview, null,true);
        ImageView avatar = (ImageView) friendview.findViewById(R.id.avatar);
        if(url.size() != 0 && url.get(position) != null)
        Picasso.with(activity)
                .load(url.get(position))
                .into(avatar);
        return friendview;

    }
}
