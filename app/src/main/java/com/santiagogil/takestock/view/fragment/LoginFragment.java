package com.santiagogil.takestock.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
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

import java.util.List;

public class LoginFragment extends Fragment {

    private EditText editTextEmailField;
    private EditText editTextPasswordField;

    private TextInputLayout textInputLayoutEmail;
    private TextInputLayout textInputLayoutPassword;

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

    private OnboardingActivityCommunicator onboardingActivityCommunicator;



    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        setUpFirebase();

        setUpUI(view);

        return view;
    }

    private void setUpUI(View view) {
        editTextEmailField = (EditText) view.findViewById(R.id.email);
        editTextPasswordField = (EditText) view.findViewById(R.id.password);
        buttonLogin = (Button) view.findViewById(R.id.email_sign_in_button);
        buttonRegister = (Button) view.findViewById(R.id.register_button);
        textInputLayoutPassword = (TextInputLayout) view.findViewById(R.id.text_input_layout_password);
        textInputLayoutEmail = (TextInputLayout) view.findViewById(R.id.text_input_layout_email);

        editTextEmailField.addTextChangedListener(new TextWatcher() {
              @Override
              public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

              @Override
              public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

              @Override
              public void afterTextChanged(Editable editable) {

                  if(!isValidEmail((CharSequence) editable)){
                      textInputLayoutEmail.setError("Enter a valid email address");
                  } else{
                      textInputLayoutEmail.setError(null);
                  }
              }
          }
        );

        editTextPasswordField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if(!isPasswordValid((CharSequence) editable))
                    textInputLayoutPassword.setError("Password must be at least 6 characters long");
                else
                    textInputLayoutPassword.setError(null);
            }
        });

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
    }

    private void setUpFirebase() {
        firebase = FirebaseDatabase.getInstance().getReference().child(DatabaseHelper.TABLEUSERS);
        mAuth = FirebaseAuth.getInstance();
        mAuth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

            }
        });
    }

    private boolean isPasswordValid(CharSequence target) {

        return target.length()>5;
    }

    public final static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);

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

                    }
                });

    }

    private void startSingIn(){

        String email = editTextEmailField.getText().toString();
        String password = editTextPasswordField.getText().toString();

        if(TextUtils.isEmpty(email) ||
                TextUtils.isEmpty(password) ||
                textInputLayoutEmail.getError()!= null ||
                textInputLayoutPassword.getError() != null ){

            Toast.makeText(getContext(), "Fields Are Empty or invalid", Toast.LENGTH_SHORT).show();

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

                                onboardingActivityCommunicator.startMainActivity();

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

    public interface OnboardingActivityCommunicator {

        void startMainActivity();

    }

    public void setOnboardingActivityCommunicator(OnboardingActivityCommunicator onboardingActivityCommunicator) {
        this.onboardingActivityCommunicator = onboardingActivityCommunicator;
    }

}
