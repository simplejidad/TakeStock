package com.santiagogil.takestock.view;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;

import com.santiagogil.takestock.controller.ItemsController;
import com.santiagogil.takestock.model.pojos.Item;

import java.util.ArrayList;
import java.util.List;

public class ItemsViewPagerAdapter extends FragmentStatePagerAdapter {

    private List<Fragment> fragmentList;
    private Context context;

    public void setContext(Context context) {
        this.context = context;
    }

    public ItemsViewPagerAdapter(FragmentManager fm, Context context, Integer independence){
        super(fm);
        this.context = context;
        fragmentList = new ArrayList<>();
        retrieveItemFragmentLists(independence);
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }

    public void retrieveItemFragmentLists(Integer independence){

        ItemsController itemsController = new ItemsController();
        List<Item> items = itemsController.getActiveItemsByIndependence(context, independence);
        for(Item item : items){
            fragmentList.add(FragmentItemDetail.provideFragment(item, items.indexOf(item)));
        }
    }
}
