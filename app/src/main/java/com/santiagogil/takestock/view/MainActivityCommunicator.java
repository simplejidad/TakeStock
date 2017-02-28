package com.santiagogil.takestock.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.santiagogil.takestock.R;
import com.santiagogil.takestock.controller.ConsumptionsController;
import com.santiagogil.takestock.controller.ItemsController;
import com.santiagogil.takestock.model.pojos.Consumption;
import com.santiagogil.takestock.model.pojos.Item;
import com.santiagogil.takestock.util.ResultListener;

import java.util.List;

public class MainActivityCommunicator extends AppCompatActivity implements FragmentItemList.FragmentActivityCommunicator, FragmentItemDetail.FragmentActivityCommunicator {


    private FirebaseAuth fAuth;
    private FirebaseAuth.AuthStateListener authStateListener;

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fAuth = FirebaseAuth.getInstance();

        authStateListener = new FirebaseAuth.AuthStateListener(){
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser() == null){
                    Intent loginIntent = new Intent(MainActivityCommunicator.this, Login_Activity.class);
                    loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(loginIntent);
                }
            }
        };

        navigationView = (NavigationView) findViewById(R.id.navigationView);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationViewListener navigationViewListener = new NavigationViewListener();
        navigationView.setNavigationItemSelectedListener(navigationViewListener);


        Fragment fragmentMainView = new FragmentItemListsViewPager();
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
    public void onItemTouched(Item touchedItem, Integer touchedPosition, Integer independence) {

        FragmentItemsViewPager fragmentItemsViewPager = new FragmentItemsViewPager();

        Bundle bundle = new Bundle();
        bundle.putInt(FragmentItemsViewPager.POSITION, touchedPosition);
        bundle.putInt(FragmentItemList.INDEPENDENCE, independence);
        fragmentItemsViewPager.setArguments(bundle);

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_holder, fragmentItemsViewPager);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

    }

    @Override
    public void onBackPressed() {
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


}

