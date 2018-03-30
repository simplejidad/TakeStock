package com.santiagogil.takestock.view.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.santiagogil.takestock.R;
import com.santiagogil.takestock.view.fragment.LoginFragment;
import com.santiagogil.takestock.view.fragment.RegisterFragment;

public class OnboardingActivity extends AppCompatActivity implements LoginFragment.CallRegisterListerner , LoginFragment.OnboardingActivityCommunicator, RegisterFragment.OnboardingActivityCommunicator {

    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseAuth mAuth;
    private FragmentManager fragmentManager;

    public SharedPreferences appPreferences;
    boolean isAppInstalled = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        appPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        isAppInstalled = appPreferences.getBoolean("isAppInstalled",false);
        if(isAppInstalled==false){

            //  create short code

            Intent shortcutIntent = new Intent(getApplicationContext(),MainActivityCommunicator.class);
            shortcutIntent.setAction(Intent.ACTION_MAIN);
            Intent intent = new Intent();
            intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
            intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "AppShortcut");
            intent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, Intent.ShortcutIconResource
                    .fromContext(getApplicationContext(), R.mipmap.ic_launcher));
            intent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
            getApplicationContext().sendBroadcast(intent);

            //Make preference true

            SharedPreferences.Editor editor = appPreferences.edit();
            editor.putBoolean("isAppInstalled", true);
            editor.commit();
        }



        fragmentManager = getSupportFragmentManager();
        mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser() == null){

            LoginFragment loginFragment = new LoginFragment();
            loginFragment.setOnboardingActivityCommunicator(OnboardingActivity.this);
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

        RegisterFragment registerFragment = new RegisterFragment();
        registerFragment.setOnboardingActivityCommunicator(OnboardingActivity.this);
        Bundle bundle = new Bundle();
        bundle.putString(RegisterFragment.EMAIL, email);
        registerFragment.setArguments(bundle);
        FragmentManager fragmentManager = this.getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_holder, registerFragment);
        fragmentTransaction.commit();

    }

    @Override
    public void startMainActivity() {
        Intent mainIntent = new Intent(OnboardingActivity.this, MainActivityCommunicator.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(mainIntent);
    }
}
