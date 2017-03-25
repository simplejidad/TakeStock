package com.santiagogil.takestock.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.Toast;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.santiagogil.takestock.R;
import com.santiagogil.takestock.controller.ConsumptionsController;
import com.santiagogil.takestock.controller.ItemsController;
import com.santiagogil.takestock.model.pojos.Behaviours.BehaviourGetItemList;
import com.santiagogil.takestock.model.pojos.Consumption;
import com.santiagogil.takestock.model.pojos.Item;
import com.santiagogil.takestock.util.DatabaseHelper;
import com.santiagogil.takestock.util.FirebaseHelper;
import com.santiagogil.takestock.util.ResultListener;
import com.santiagogil.takestock.view.onboarding.OnboardingActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivityCommunicator extends AppCompatActivity implements FragmentItemList.FragmentActivityCommunicator, FragmentItemDetail.FragmentActivityCommunicator {


    private FirebaseAuth fAuth;
    private FirebaseAuth.AuthStateListener authStateListener;

    private NavigationView navigationView;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //updateFirebaseDBNames();

        fAuth = FirebaseAuth.getInstance();

        authStateListener = new FirebaseAuth.AuthStateListener(){
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser() == null){
                    Intent intent = new Intent(MainActivityCommunicator.this, OnboardingActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            }
        };

        navigationView = (NavigationView) findViewById(R.id.navigationView);
        NavigationViewListener navigationViewListener = new NavigationViewListener();
        navigationView.setNavigationItemSelectedListener(navigationViewListener);


        FragmentItemListsViewPager fragmentMainView = new FragmentItemListsViewPager();
        fragmentMainView.setFragmentActivityCommunicator(MainActivityCommunicator.this);
        FragmentManager fragmentManager = this.getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_holder, fragmentMainView);
        fragmentTransaction.commit();

        ItemsController itemsController = new ItemsController();
        itemsController.updateItemsDatabase(this, new ResultListener<List<Item>>(){
            @Override
            public void finish(List<Item> result) {

            }
        });

        ConsumptionsController consumptionsController = new ConsumptionsController();
        consumptionsController.updateConsumptionsDatabase(this, new ResultListener<List<Consumption>>(){
            @Override
            public void finish(List<Consumption> result) {

            }
        });

    }

    @Override
    public void refreshFragmentMainView(Integer position) {

        FragmentItemListsViewPager fragmentMainView = new FragmentItemListsViewPager();
        fragmentMainView.setFragmentActivityCommunicator(MainActivityCommunicator.this);
        FragmentManager fragmentManager = getSupportFragmentManager();
        Bundle bundle = new Bundle();
        bundle.putInt(FragmentItemList.POSITION, position);
        fragmentMainView.setArguments(bundle);
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_holder, fragmentMainView);
        fragmentTransaction.commit();
    }

    @Override
    public void showFragmentEditItem(Bundle bundle) {

        FragmentEditItem fragmentEditItem = new FragmentEditItem();
        fragmentEditItem.setArguments(bundle);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_holder, fragmentEditItem);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

    }

    @Override
    public void onItemTouched(Item touchedItem, Integer touchedPosition, BehaviourGetItemList behaviourGetItemList) {

        FragmentItemsViewPager fragmentItemsViewPager = new FragmentItemsViewPager();

        Bundle bundle = new Bundle();
        bundle.putInt(FragmentItemsViewPager.POSITION, touchedPosition);
        bundle.putSerializable(FragmentItemList.BEHAVIOURGETITEMLIST, behaviourGetItemList);
        fragmentItemsViewPager.setArguments(bundle);

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.fragment_holder, fragmentItemsViewPager);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

    }

    @Override
    public void onBackPressed() {

        int fragments = getSupportFragmentManager().getBackStackEntryCount();
        if (fragments == 0) {
            moveTaskToBack(true);
        }
        super.onBackPressed();
    }

    private class NavigationViewListener implements NavigationView.OnNavigationItemSelectedListener {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            if(item.getItemId() == R.id.action_logout){

                logout();
            }


            return false;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        fAuth.addAuthStateListener(authStateListener);


    }

    private void logout() {

        fAuth.signOut();
        Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show();
    }

    private void updateFirebaseDBNames(){


        FirebaseHelper firebaseHelper = new FirebaseHelper();

        DatabaseReference updateUser1 = firebaseHelper.getFirebaseDatabase().getReference().child(DatabaseHelper.TABLEUSERS).child("4yXCcx84YobNncJquPpzby4VxG63");

        updateUser1.child(DatabaseHelper.IMAGE).setValue("default");
        updateUser1.child(DatabaseHelper.NAME).setValue("Santiago");

        DatabaseReference updateUser2 = firebaseHelper.getFirebaseDatabase().getReference().child(DatabaseHelper.TABLEUSERS).child("nyfcAr49fXg7kCOk3ixGE69FyWf1");

        updateUser2.child(DatabaseHelper.IMAGE).setValue("default");
        updateUser2.child(DatabaseHelper.NAME).setValue("Testing");

        firebaseHelper.getFirebaseDatabase().getReference().child("User").removeValue();

    }


}

