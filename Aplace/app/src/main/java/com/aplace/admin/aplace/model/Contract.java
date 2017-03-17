package com.aplace.admin.aplace.model;

import android.net.Uri;

import java.io.Serializable;
import java.net.URL;

/**
 * Created by admin on 16/03/2017.
 */

public class Contract{
    private String image_Uri;
    private String image_Url;
    private double latitue;
    private double longtitue;
    public Contract(double longtitue, double latitue, String image_Uri, String image_url) {
        this.latitue = latitue;
        this.longtitue = longtitue;
        this.image_Uri = image_Uri;
        image_Url = image_url;
    }

    public Contract () {}
    public double getLatitue() {
        return latitue;
    }

    public double getLongtitue() {
        return longtitue;
    }

    public String getImage_Uri() {
        return image_Uri;
    }

    public String getImage_Url() {return image_Url;}

    public void setImage_Uri(String image_Uri) {
        this.image_Uri = image_Uri;
    }

    public void setImage_Url(String image_Url) {
        this.image_Url = image_Url;
    }

    public void setLatitue(double latitue) {
        this.latitue = latitue;
    }

    public void setLongtitue(double longtitue) {
        this.longtitue = longtitue;
    }
}
