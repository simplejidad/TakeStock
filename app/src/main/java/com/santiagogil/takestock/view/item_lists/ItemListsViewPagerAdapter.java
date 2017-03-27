package com.santiagogil.takestock.view.item_lists;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class ItemListsViewPagerAdapter extends FragmentStatePagerAdapter {

    private List<FragmentItemList> fragmentItemListList;
    //private FragmentItemList.FragmentActivityCommunicator fragmentActivityCommunicator;


    public ItemListsViewPagerAdapter(FragmentManager fm) {
        super(fm);
        fragmentItemListList = new ArrayList<>();
        //this.fragmentActivityCommunicator = fragmentActivityCommunicator;
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentItemListList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentItemListList.size();
    }

    public List<FragmentItemList> getFragmentItemListList() {
        return fragmentItemListList;
    }

}
