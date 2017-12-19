package com.santiagogil.takestock.model.daos;


import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.santiagogil.takestock.model.pojos.User;
import com.santiagogil.takestock.util.DatabaseHelper;
import com.santiagogil.takestock.util.FirebaseHelper;
import com.santiagogil.takestock.util.ResultListener;

import java.util.ArrayList;
import java.util.List;

public class UsersFirebaseDAO {

    public void getAllUsers (final ResultListener<List<User>> resultListener){

        final FirebaseHelper firebaseHelper = new FirebaseHelper();

        DatabaseReference dbRef = firebaseHelper.getFirebaseDatabase().getReference().child(DatabaseHelper.TABLEUSERS);
        final List<User> userList = new ArrayList<>();

        dbRef.addListenerForSingleValueEvent(new ValueEventListener()   {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()){
                    User user = data.getValue(User.class);
                    userList.add(user);
                }
                resultListener.finish(userList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });
    }

    public DatabaseReference getUserDB(User user) {
        FirebaseHelper firebaseHelper = new FirebaseHelper();
        return firebaseHelper.getFirebaseDatabase().getReference().child(user.getUserID());

    }
}
