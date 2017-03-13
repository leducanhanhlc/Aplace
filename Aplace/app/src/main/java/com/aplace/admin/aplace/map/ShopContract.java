package com.aplace.admin.aplace.map;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by admin on 13/03/2017.
 */

public class ShopContract {
    public String type;
    public String diachi;
    public double latitue;
    public double longtitue;
    public String photoUrl;

    public ShopContract(String type, String diachi, double latitue, double longtitue, String photoUrl) {
        this.type = type;
        this.diachi = diachi;
        this.latitue = latitue;
        this.longtitue = longtitue;
        this.photoUrl = photoUrl;
    }

    public ShopContract() {

    }

    public Double getLatitue() {
        return latitue;
    }

    public Double getLongtitue() {
        return longtitue;
    }

    public String getDiachi() {
        return diachi;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public String getType() {
        return type;
    }
}
