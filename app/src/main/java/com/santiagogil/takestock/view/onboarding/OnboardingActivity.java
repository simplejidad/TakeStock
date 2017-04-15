package com.santiagogil.takestock.view.onboarding;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.santiagogil.takestock.R;
import com.santiagogil.takestock.view.MainActivityCommunicator;

public class OnboardingActivity extends AppCompatActivity implements LoginFragment.CallRegisterListerner {

    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseAuth mAuth;
    private FragmentManager fragmentManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        fragmentManager = getSupportFragmentManager();
        mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser() == null){

            Fragment loginFragment = new LoginFragment();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_holder, loginFragment);
            fragmentTransaction.commit();

        } else{
            Intent intent = new Intent(OnboardingActivity.this, MainActivityCommunicator.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }
    }


    @Override
    public void goToRegister(String email) {

        Fragment registerFragment = new RegisterFragment();
        Bundle bundle = new Bundle();
        bundle.putString(RegisterFragment.EMAIL, email);
        registerFragment.setArguments(bundle);
        FragmentManager fragmentManager = this.getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_holder, registerFragment);
        fragmentTransaction.commit();

    }
}
