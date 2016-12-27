package com.santiagogil.takestock.view;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.santiagogil.takestock.R;
import com.santiagogil.takestock.controller.ItemsController;
import com.santiagogil.takestock.model.pojos.Item;

import java.util.List;

public class MainActivityCommunicator extends AppCompatActivity implements FragmentMainView.FragmentActivityCommunicator, FragmentItemDetail.FragmentActivityCommunicator {

    private ItemsController itemsController;
    private List<Item> items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Fragment fragmentMainView = new FragmentMainView();
        FragmentManager fragmentManager = this.getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_holder, fragmentMainView);
        fragmentTransaction.commit();

    }

    @Override
    public void refreshFragmentMainView(Integer position) {

        Fragment fragmentMainView = new FragmentMainView();
        FragmentManager fragmentManager = getSupportFragmentManager();
        Bundle bundle = new Bundle();
        bundle.putInt(FragmentMainView.POSITION, position);
        fragmentMainView.setArguments(bundle);
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_holder, fragmentMainView);
        fragmentTransaction.commit();
    }

    @Override
    public void onItemTouched(Item touchedItem, Integer touchedPosition) {

        FragmentItemDetail fragmentItemDetail = FragmentItemDetail.provideFragment(touchedItem, touchedPosition);

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_holder, fragmentItemDetail);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}

