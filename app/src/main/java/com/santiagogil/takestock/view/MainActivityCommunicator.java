package com.santiagogil.takestock.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.santiagogil.takestock.R;
import com.santiagogil.takestock.controller.ConsumptionsController;
import com.santiagogil.takestock.model.pojos.Behaviours.BehaviourGetItemList;
import com.santiagogil.takestock.model.pojos.Consumption;
import com.santiagogil.takestock.model.pojos.Item;
import com.santiagogil.takestock.util.DatabaseHelper;
import com.santiagogil.takestock.util.FirebaseHelper;
import com.santiagogil.takestock.util.ResultListener;
import com.santiagogil.takestock.view.item_detail.EditItemActivity;
import com.santiagogil.takestock.view.item_detail.FragmentItemDetail;
import com.santiagogil.takestock.view.item_detail.FragmentItemsViewPager;
import com.santiagogil.takestock.view.item_lists.FragmentItemList;
import com.santiagogil.takestock.view.item_lists.FragmentItemListsViewPager;
import com.santiagogil.takestock.view.onboarding.OnboardingActivity;

import java.util.List;

public class MainActivityCommunicator extends AppCompatActivity implements FragmentItemList.FragmentActivityCommunicator, FragmentItemDetail.FragmentActivityCommunicator {


    private FirebaseAuth fAuth;
    private FirebaseAuth.AuthStateListener authStateListener;

    private NavigationView navigationView;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //updateFirebaseDBNames();
        createFirebaseDefaultItemListEnglish();


        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null) {
                    Intent intent = new Intent(MainActivityCommunicator.this, OnboardingActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            }
        };

        fAuth = FirebaseAuth.getInstance();

        if (fAuth.getCurrentUser() == null) {

            Intent intent = new Intent(MainActivityCommunicator.this, OnboardingActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);

        } else {

            navigationView = (NavigationView) findViewById(R.id.navigationView);
            NavigationViewListener navigationViewListener = new NavigationViewListener();
            navigationView.setNavigationItemSelectedListener(navigationViewListener);


            FragmentItemListsViewPager fragmentMainView = new FragmentItemListsViewPager();
            fragmentMainView.setFragmentActivityCommunicator(MainActivityCommunicator.this);
            FragmentManager fragmentManager = this.getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_holder, fragmentMainView);
            fragmentTransaction.commit();

            ConsumptionsController consumptionsController = new ConsumptionsController();
            consumptionsController.updateConsumptionsDatabase(this, new ResultListener<List<Consumption>>() {
                @Override
                public void finish(List<Consumption> result) {

                }
            });

            }
        }

        @Override
        public void refreshFragmentMainView (Integer position){

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
        public void showFragmentEditItem (Bundle bundle){

            Intent intent = new Intent(MainActivityCommunicator.this, EditItemActivity.class);
            intent.putExtras(bundle);
            startActivity(intent);
        }

        @Override
        public void onItemTouched (Item touchedItem, Integer touchedPosition, BehaviourGetItemList
        behaviourGetItemList){

            FragmentItemsViewPager fragmentItemsViewPager = new FragmentItemsViewPager();

            Bundle bundle = new Bundle();
            bundle.putSerializable(FragmentItemsViewPager.BEHAVIOURGETITEMLIST, behaviourGetItemList);
            bundle.putString(FragmentItemsViewPager.ITEMID, touchedItem.getID());
            fragmentItemsViewPager.setArguments(bundle);

            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.add(R.id.fragment_holder, fragmentItemsViewPager);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();

        }

        @Override
        public void onBackPressed () {

            int fragments = getSupportFragmentManager().getBackStackEntryCount();
            if (fragments == 0) {
                moveTaskToBack(true);
            }
            super.onBackPressed();
        }

        private class NavigationViewListener implements NavigationView.OnNavigationItemSelectedListener {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                if (item.getItemId() == R.id.action_logout) {

                    logout();
                }


                return false;
            }
        }

        @Override
        protected void onStart () {
            super.onStart();
            fAuth.addAuthStateListener(authStateListener);
        }

    private void logout() {

        fAuth.signOut();
        Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show();
    }

    private void createFirebaseDefaultItemListEnglish() {


        FirebaseHelper firebaseHelper = new FirebaseHelper();

        DatabaseReference defaultItemListEnglish = firebaseHelper.getDefaultItemDatabase().child(DatabaseHelper.TABLEITEMS);


        for(Integer i = 0; i < 25; i++){
            String itemID = "Default Item " + i;
            DatabaseReference defaultItem = defaultItemListEnglish.child(itemID);
            Item item = new Item(itemID);

            defaultItem.child(DatabaseHelper.ACTIVE).setValue(item.getActive());
            defaultItem.child(DatabaseHelper.CONSUMPTIONRATE).setValue(item.getConsumptionRate());
            defaultItem.child(DatabaseHelper.ID).setValue(itemID);
            defaultItem.child(DatabaseHelper.MINIMUMPURCHACEQUANTITY).setValue(item.getMinimumPurchaceQuantity());
            defaultItem.child(DatabaseHelper.NAME).setValue(item.getName());
            defaultItem.child(DatabaseHelper.STOCK).setValue(item.getStock());
            defaultItem.child(DatabaseHelper.USERID).setValue(FirebaseHelper.DEFAULT_ITEM_LIST);

        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        //This method is called when the up button is pressed. Just the pop back stack.
        onBackPressed();
        return true;
    }
}


