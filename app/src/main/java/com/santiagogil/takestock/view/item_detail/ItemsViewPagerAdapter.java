package com.santiagogil.takestock.view.item_detail;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.santiagogil.takestock.model.pojos.Behaviours.BehaviourGetItemList;
import com.santiagogil.takestock.model.pojos.Item;

import java.util.ArrayList;
import java.util.List;

public class ItemsViewPagerAdapter extends FragmentStatePagerAdapter {

    private List<Fragment> fragmentList;
    private Context context;
    private BehaviourGetItemList behaviourGetItemList;

    public void setContext(Context context) {
        this.context = context;
    }

    public ItemsViewPagerAdapter(FragmentManager fm, Context context, BehaviourGetItemList behaviourGetItemList){
        super(fm);
        this.context = context;
        fragmentList = new ArrayList<>();
        this.behaviourGetItemList = behaviourGetItemList;
        populateFragmentList();
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }

    public void populateFragmentList(){

        List<Item> items = behaviourGetItemList.getItemList(context);
        for(Item item : items){
            fragmentList.add(FragmentItemDetail.provideFragment(item, items.indexOf(item)));
        }
    }
}
