package com.santiagogil.takestock.model.pojos;

import android.provider.ContactsContract;

import com.google.firebase.database.PropertyName;
import com.santiagogil.takestock.util.DatabaseHelper;

public class User {
    @PropertyName(DatabaseHelper.NAME)
    private String name;

    private String userID;
    private String image;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
