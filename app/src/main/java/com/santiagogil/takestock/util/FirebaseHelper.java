package com.santiagogil.takestock.util;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.santiagogil.takestock.model.pojos.Item;

import java.util.List;

public class FirebaseHelper {

    public static final String DEFAULT_ITEM_LIST = "defaultItemList";

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

    public String getCurrentUserID() {
        return getmAuth().getCurrentUser().getUid();
    }

    public DatabaseReference getDefaultItemDatabase() {
        return getFirebaseDatabase().getReference().child(DEFAULT_ITEM_LIST);
    }

}
