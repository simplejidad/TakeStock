package com.santiagogil.takestock.util;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseHelper {

    public static final String DEFAULT_ITEM_LIST = "defaultItemList";
    public static final String DATABASES = "Databases";

    public FirebaseHelper(){

    }

    public FirebaseDatabase getFirebaseDatabase() {
        return FirebaseDatabase.getInstance();
    }

    public FirebaseAuth getmAuth() {
        return FirebaseAuth.getInstance();
    }

    public DatabaseReference getUserDB() {
        return getFirebaseDatabase().getReference().child(getmAuth().getCurrentUser().getUid());
    }

    public DatabaseReference getUserDBWithUserID(String userID){
        return getFirebaseDatabase().getReference().child(userID);
    }

    public DatabaseReference newGetUserDBByID(String userID) {
        return getFirebaseDatabase().getReference().child(FirebaseHelper.DATABASES).child(userID);
    }

    public String getCurrentUserID() {
        return getmAuth().getCurrentUser().getUid();
    }

    public DatabaseReference getDefaultItemDatabase() {
        return getFirebaseDatabase().getReference().child(DEFAULT_ITEM_LIST);
    }

}
