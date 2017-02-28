package com.santiagogil.takestock.view;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.santiagogil.takestock.R;

public class Login_Activity extends AppCompatActivity {

    private EditText editTextEmailField;
    private EditText editTextPasswordField;

    private Button buttonLogin;
    private Button buttonRegister;

    private DatabaseReference firebase;
    private FirebaseAuth fAuth;

    private FirebaseAuth.AuthStateListener authStateListener;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebase = FirebaseDatabase.getInstance().getReference().child("Users");

        fAuth = FirebaseAuth.getInstance();

        editTextEmailField = (EditText) findViewById(R.id.email);
        editTextPasswordField = (EditText) findViewById(R.id.password);
        buttonLogin = (Button) findViewById(R.id.email_sign_in_button);
        buttonRegister = (Button) findViewById(R.id.register_button);

        authStateListener = new FirebaseAuth.AuthStateListener(){
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                if(firebaseAuth.getCurrentUser() != null){

                    startActivity(new Intent(Login_Activity.this, MainActivityCommunicator.class));

                }
            }
        };

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startSingIn();
            }
        });

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                goToRegister();
            }
        });


    }

    private void goToRegister() {

        Intent registerIntent = new Intent(Login_Activity.this, RegisterActivity.class);
        startActivity(registerIntent);
    }

    @Override
    protected void onStart() {
        super.onStart();

        fAuth.addAuthStateListener(authStateListener);
    }

    private void startSingIn(){

        String email = editTextEmailField.getText().toString();
        String password = editTextPasswordField.getText().toString();

        if(TextUtils.isEmpty(email) || TextUtils.isEmpty(password)){

            Toast.makeText(this, "Fields Are Empty", Toast.LENGTH_SHORT).show();

        } else{

            fAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if(!task.isSuccessful()){
                        Toast.makeText(Login_Activity.this, "Sign In Problem", Toast.LENGTH_LONG).show();
                    } else {
                        checkIfUserExists();
                    }
                }
            });
        }


    }

    private void checkIfUserExists(){

        final String userId = fAuth.getCurrentUser().getUid();

        firebase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(userId)){

                    Intent mainIntent = new Intent(Login_Activity.this, MainActivityCommunicator.class);
                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(mainIntent);

                } else{
                    Toast.makeText(Login_Activity.this, "Login Error", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}