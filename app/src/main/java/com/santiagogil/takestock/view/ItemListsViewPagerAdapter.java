package com.santiagogil.takestock.view;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class ItemListsViewPagerAdapter extends FragmentStatePagerAdapter {

    private List<FragmentItemList> fragmentItemListList;
    private Context context;

    public ItemListsViewPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
        fragmentItemListList = new ArrayList<>();
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

        fragmentItemListList.add(FragmentItemList.getfragmentItemList("Todos", -1, 0));
        fragmentItemListList.add(FragmentItemList.getfragmentItemList("Stock 0", 0, 0));
        fragmentItemListList.add(FragmentItemList.getfragmentItemList("Un Mes", 30, 0));
        fragmentItemListList.add(FragmentItemList.getfragmentItemList("Mucho", 500, 0));
    }
}
