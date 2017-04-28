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
    private List<Item> itemList;
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
        this.itemList = behaviourGetItemList.getItemList(context);
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

        for(Item item : itemList){
            fragmentList.add(FragmentItemDetail.provideFragment(item, itemList.indexOf(item)));
        }
    }

    public Integer getTouchedItemPosition(String itemID){

        for (Item item : itemList){
            if(item.getID().equals(itemID)){
                return itemList.indexOf(item);
            }
        }

        return 0;
    }
}
