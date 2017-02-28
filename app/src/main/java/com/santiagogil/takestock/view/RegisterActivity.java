package com.santiagogil.takestock.view;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.santiagogil.takestock.R;

public class RegisterActivity extends AppCompatActivity {

    private EditText editTextNameField;
    private EditText editTextEmailField;
    private EditText editTextPasswordField;
    private EditText editTextConfirmPasswordField;

    private Button buttonRegister;

    private FirebaseAuth fAuth;
    private DatabaseReference firebase;

    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        editTextNameField = (EditText) findViewById(R.id.name);
        editTextEmailField = (EditText) findViewById(R.id.email);
        editTextPasswordField = (EditText) findViewById(R.id.password);
        editTextConfirmPasswordField = (EditText) findViewById(R.id.confirmPassword);
        buttonRegister = (Button) findViewById(R.id.email_register_button);

        fAuth = FirebaseAuth.getInstance();

        fAuth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

            }
        });

        progressDialog = new ProgressDialog(this);

        buttonRegister.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                startRegister();

            }
        });

    }
    private void startRegister() {

        final String name = editTextNameField.getText().toString().trim();
        String email = editTextEmailField.getText().toString().trim();
        String password = editTextPasswordField.getText().toString().trim();
        String confirmedPassword = editTextConfirmPasswordField.getText().toString().trim();

        if(!TextUtils.isEmpty(name) && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(password) && !TextUtils.isEmpty(confirmedPassword)){

            if (password.equals(confirmedPassword)){

                progressDialog.setMessage("Signing Up...");
                progressDialog.show();

                fAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){

                            String user_id = fAuth.getCurrentUser().getUid();

                            firebase = FirebaseDatabase.getInstance().getReference().child("Users");
                            DatabaseReference currentUserDB = firebase.child(user_id);

                            currentUserDB.child("name").setValue(name);
                            currentUserDB.child("image").setValue("default");

                            progressDialog.dismiss();

                            Intent mainIntent = new Intent(RegisterActivity.this, MainActivityCommunicator.class);
                            mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(mainIntent);

                        }
                    }
                });

            }
            else{
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            }
        }else {
            Toast.makeText(this, "Fields are Empty", Toast.LENGTH_SHORT).show();
        }

    }

}
