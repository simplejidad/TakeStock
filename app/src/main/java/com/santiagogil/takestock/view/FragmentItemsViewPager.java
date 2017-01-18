package com.santiagogil.takestock.view;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.santiagogil.takestock.R;

public class FragmentItemsViewPager extends Fragment {

    private ViewPager itemsViewPager;

    public FragmentItemsViewPager() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_items_viewpager, container, false);

        itemsViewPager = (ViewPager) view.findViewById(R.id.itemsViewPager);

        return view;
    }

}
