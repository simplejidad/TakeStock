package com.santiagogil.takestock.view.item_detail;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.santiagogil.takestock.R;
import com.santiagogil.takestock.model.pojos.Behaviours.BehaviourGetItemList;
import com.santiagogil.takestock.view.item_lists.FragmentItemList;

public class FragmentItemsViewPager extends Fragment {

    private ViewPager itemsViewPager;
    private Bundle bundle;

    public static final String POSITION = "Position";

    public FragmentItemsViewPager() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_items_viewpager, container, false);
        bundle = getArguments();
        itemsViewPager = (ViewPager) view.findViewById(R.id.itemsViewPager);
        ItemsViewPagerAdapter itemsViewPagerAdapter = new ItemsViewPagerAdapter(getChildFragmentManager(), getContext(), (BehaviourGetItemList) bundle.getSerializable(FragmentItemList.BEHAVIOURGETITEMLIST));
        itemsViewPager.setAdapter(itemsViewPagerAdapter);
        itemsViewPager.setCurrentItem(bundle.getInt(POSITION));
        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
    }
}
