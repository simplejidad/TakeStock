package com.santiagogil.takestock.view.adapter;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.santiagogil.takestock.view.fragment.SimpleRecyclerFragment;

import java.util.List;

public class ConsumptionsAndPurchacesViewPagerAdapter extends FragmentStatePagerAdapter {

    private static final int PURCHASES_FRAGMENT_INDEX = 1;
    private static final int CONSUMPTIONS_FRAGMENT_INDEX = 0;

    private List<SimpleRecyclerFragment> fragmentList;


    public ConsumptionsAndPurchacesViewPagerAdapter(FragmentManager fm, List<SimpleRecyclerFragment> fragmentList) {
        super(fm);
        this.fragmentList = fragmentList;
    }

    @Override
    public CharSequence getPageTitle(int position){
        return this.fragmentList.get(position).getTitle();
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }

    public void onConsumptionsUpdated(){
        fragmentList.get(CONSUMPTIONS_FRAGMENT_INDEX).onConsumptionsUpdated();
    }

    public void onPurchacesUpdated(){
        fragmentList.get(PURCHASES_FRAGMENT_INDEX).onPurchacesUpdated();
    }
}
