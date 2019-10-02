package com.santiagogil.takestock.view.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.santiagogil.takestock.view.fragment.FragmentRecyclerItems;

import java.util.ArrayList;
import java.util.List;

public class ItemListsViewPagerAdapter extends FragmentStatePagerAdapter {

    private List<FragmentRecyclerItems> fragmentRecyclerItems;

    public ItemListsViewPagerAdapter(FragmentManager fm) {
        super(fm);
        fragmentRecyclerItems = new ArrayList<>();
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentRecyclerItems.get(position);
    }

    @Override
    public int getCount() {
        return fragmentRecyclerItems.size();
    }

    public List<FragmentRecyclerItems> getFragmentRecyclerItems() {
        return fragmentRecyclerItems;
    }

    public void updateFragmentsWithFilter(String filter){
        for(FragmentRecyclerItems fragmentRecyclerItems : this.fragmentRecyclerItems){
            if(fragmentRecyclerItems != null){
                fragmentRecyclerItems.getArguments().putString(FragmentRecyclerItems.FILTER, filter);
                if (fragmentRecyclerItems.getItemList() != null)
                    fragmentRecyclerItems.updateListWithFilter();
            }
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return this.fragmentRecyclerItems.get(position).getTitle();
    }
}
