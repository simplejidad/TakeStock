package com.santiagogil.takestock.util;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseHelper {

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
}
