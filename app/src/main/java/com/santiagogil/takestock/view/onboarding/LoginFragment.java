package com.santiagogil.takestock.view.onboarding;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.santiagogil.takestock.R;
import com.santiagogil.takestock.controller.ItemsController;
import com.santiagogil.takestock.model.pojos.Item;
import com.santiagogil.takestock.util.DatabaseHelper;
import com.santiagogil.takestock.util.ResultListener;
import com.santiagogil.takestock.view.MainActivityCommunicator;

import java.util.List;

public class LoginFragment extends Fragment {

    private EditText editTextEmailField;
    private EditText editTextPasswordField;

    private Button buttonLogin;
    private Button buttonRegister;
    private Button buttonAnonymous;
    private SignInButton googleButton;

    private DatabaseReference firebase;
    private FirebaseAuth mAuth;

    private FirebaseAuth.AuthStateListener authStateListener;

    private static final int RC_SIGN_IN = 1;
    private GoogleApiClient googleApiClient;
    private static final String TAG = "LoginActivity";
    private ProgressDialog progressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        firebase = FirebaseDatabase.getInstance().getReference().child(DatabaseHelper.TABLEUSERS);

        mAuth = FirebaseAuth.getInstance();

        mAuth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

            }
        });

        editTextEmailField = (EditText) view.findViewById(R.id.email);
        editTextPasswordField = (EditText) view.findViewById(R.id.password);
        buttonLogin = (Button) view.findViewById(R.id.email_sign_in_button);
        buttonRegister = (Button) view.findViewById(R.id.register_button);

        progressDialog = new ProgressDialog(getContext());

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startSingIn();
            }
        });

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CallRegisterListerner callRegisterListerner = (CallRegisterListerner) getActivity();
                callRegisterListerner.goToRegister(editTextEmailField.getText().toString());
            }
        });

        /*buttonAnonymous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signInAnonymously();
            }
        });

        // Google Sign In Button
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();


        try {
            googleApiClient = new GoogleApiClient.Builder(getContext())
                    .enableAutoManage(getActivity(), new GoogleApiClient.OnConnectionFailedListener() {
                        @Override
                        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

                            Toast.makeText(getContext(), "Connection Failed", Toast.LENGTH_SHORT).show();

                        }
                    })
                    .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                    .build();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }

        googleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                signInWithGoogle();
            }
        });*/

        return view;
    }

    private void signInWithGoogle(){
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);

            progressDialog.setMessage("Starting Sign in...");
            progressDialog.show();

            handleSignInResult(result);
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            firebaseAuthWithGoogle(acct);
            //mStatusTextView.setText(getString(R.string.signed_in_fmt, acct.getDisplayName()));
            //updateUI(true);
        } else {
            progressDialog.dismiss();
            // Signed out, show unauthenticated UI.
            //updateUI(false);
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {

        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                        if(!task.isSuccessful()) {
                            Log.w(TAG, "signiInWithCredential", task.getException());
                            Toast.makeText(getContext(), "Authentication Failed",
                                    Toast.LENGTH_SHORT).show();
                        }else {
                            checkIfUserExists();
                        }

                        progressDialog.show();
                    }
                });

    }

    private void startSingIn(){

        String email = editTextEmailField.getText().toString();
        String password = editTextPasswordField.getText().toString();

        if(TextUtils.isEmpty(email) || TextUtils.isEmpty(password)){

            Toast.makeText(getContext(), "Fields Are Empty", Toast.LENGTH_SHORT).show();

        } else{

            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if(!task.isSuccessful()){
                        Toast.makeText(getContext(), "Sign In Problem", Toast.LENGTH_LONG).show();
                    } else {
                        checkIfUserExists();
                    }
                }
            });
        }


    }

    private void checkIfUserExists(){

        if(mAuth.getCurrentUser() != null){

            final String userId = mAuth.getCurrentUser().getUid();

            firebase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.hasChild(userId)){

                        ItemsController itemsController = new ItemsController();
                        itemsController.updateItemsDatabase(getContext(), new ResultListener<List<Item>>(){
                            @Override
                            public void finish(List<Item> result) {
                                Toast.makeText(getContext(), "Items Updated", Toast.LENGTH_SHORT).show();

                                progressDialog.dismiss();
                                Intent mainIntent = new Intent(getContext(), MainActivityCommunicator.class);
                                mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(mainIntent);
                            }
                        });

                    } else{
                        Toast.makeText(getContext(), "Login Error", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    public interface CallRegisterListerner{
        void goToRegister(String string);
    }

    private void signInAnonymously(){
        mAuth.signInAnonymously()
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInAnonymously:onComplete:" + task.isSuccessful());

                        if(task.isSuccessful()){

                            ItemsController itemsController = new ItemsController();
                            itemsController.updateItemsDatabase(getContext(), new ResultListener<List<Item>>(){
                                @Override
                                public void finish(List<Item> result) {
                                    Toast.makeText(getContext(), "Items Updated", Toast.LENGTH_SHORT).show();

                                    Intent intent = new Intent(getContext(), MainActivityCommunicator.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                }
                            });
                        }

                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInAnonymously", task.getException());
                            Toast.makeText(getContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }
}
