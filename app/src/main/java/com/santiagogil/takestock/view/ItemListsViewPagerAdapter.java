package com.santiagogil.takestock.view;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class ItemListsViewPagerAdapter extends FragmentStatePagerAdapter {

    private List<FragmentItemList> fragmentItemListList;
    private FragmentItemList.FragmentActivityCommunicator fragmentActivityCommunicator;



    public ItemListsViewPagerAdapter(FragmentManager fm, FragmentItemList.FragmentActivityCommunicator fragmentActivityCommunicator) {
        super(fm);
        fragmentItemListList = new ArrayList<>();
        this.fragmentActivityCommunicator = fragmentActivityCommunicator;
        retrieveFragments();

    }

    @Override
    public Fragment getItem(int position) {
        return fragmentItemListList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentItemListList.size();
    }

    public void retrieveFragments(){

        fragmentItemListList.add(FragmentItemList.getfragmentItemList("All Items A-Z", -1, 0, fragmentActivityCommunicator));
        fragmentItemListList.add(FragmentItemList.getfragmentItemList("All Items by Independence", -2, 0, fragmentActivityCommunicator));
        fragmentItemListList.add(FragmentItemList.getfragmentItemList("Stock 0", 0, 0, fragmentActivityCommunicator));
        fragmentItemListList.add(FragmentItemList.getfragmentItemList("Un Mes", 30, 0, fragmentActivityCommunicator));
        fragmentItemListList.add(FragmentItemList.getfragmentItemList("Mucho", 500, 0, fragmentActivityCommunicator));
    }

    public List<FragmentItemList> getFragmentItemListList() {
        return fragmentItemListList;
    }

}
