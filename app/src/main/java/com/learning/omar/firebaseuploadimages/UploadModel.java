package com.learning.omar.firebaseuploadimages;

import com.google.firebase.database.Exclude;

public class UploadModel {

    private String img_name;
    private String img_url;
    private String mKey;

    public UploadModel() {
    }


    public UploadModel(String img_name, String img_url) {

        if (img_name.trim().equals("")) {
            img_name = "No Name";
        }

        this.img_name = img_name;
        this.img_url = img_url;
    }

    public void setImg_name(String img_name) {
        this.img_name = img_name;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }

    public String getImg_name() {
        return img_name;
    }

    public String getImg_url() {
        return img_url;
    }

    @Exclude
    public String getmKey() {
        return mKey;
    }

    @Exclude
    public void setmKey(String mKey) {
        this.mKey = mKey;
    }
}
