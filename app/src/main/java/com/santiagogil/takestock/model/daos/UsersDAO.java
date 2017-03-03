package com.santiagogil.takestock.model.daos;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.santiagogil.takestock.util.ResultListener;
import com.santiagogil.takestock.view.MainActivityCommunicator;

public class UsersDAO {

    private Context context;
    private DatabaseHelper databaseHelper;

    public UsersDAO (Context context){
        this.context = context;
        databaseHelper = new DatabaseHelper(context);
    }

    public void createUserWithEmailAndPassword(ResultListener<Void> listenerFromController, String name, String email, String password, String image) {

        createUserOnFirebaseWithEmailAndPassword(listenerFromController, name, email, password, image);

    }

    public void createUserOnFirebaseWithEmailAndPassword(final ResultListener<Void> resultListener, final String name, String email, String password,
                                                         final String image){

        final FirebaseAuth fAuth = FirebaseAuth.getInstance();



        fAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){

                    //String user_id = fAuth.getCurrentUser().getUid();

                    //DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
                    //DatabaseReference currentUserDB = databaseReference.child(user_id);

                    //currentUserDB.child("name").setValue(name);
                    //currentUserDB.child("image").setValue("default");
                    resultListener.finish(null);

                } else{
                    Log.d("TASK: ", task.toString());
                    Toast.makeText(context, "Register Problem", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
