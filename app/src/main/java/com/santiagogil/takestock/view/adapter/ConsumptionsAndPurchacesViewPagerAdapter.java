package com.santiagogil.takestock.view.adapter;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.santiagogil.takestock.view.fragment.SimpleRecyclerFragment;

import java.util.List;

public class ConsumptionsAndPurchacesViewPagerAdapter extends FragmentStatePagerAdapter {

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
        fragmentList.get(0).onConsumptionsUpdated();
    }

    public void onPurchacesUpdated(){
        fragmentList.get(1).onPurchacesUpdated();
    }
}
