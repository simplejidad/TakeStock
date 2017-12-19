package com.santiagogil.takestock.view.Fragments;


import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.santiagogil.takestock.R;
import com.santiagogil.takestock.model.pojos.Behaviours.BehaviourGetItemList;
import com.santiagogil.takestock.util.SharedElementTransition;
import com.santiagogil.takestock.view.Adapters.ItemsViewPagerAdapter;

public class FragmentItemsViewPager extends Fragment {

    private ViewPager itemsViewPager;
    private Bundle bundle;
    private ItemsViewPagerAdapter itemsViewPagerAdapter;
    private Integer currentPosition;

    public static final String POSITION = "Position";
    public static final String BEHAVIOURGETITEMLIST = "behaviourGetList";
    public static final String ITEMID = "ItemID";

    public FragmentItemsViewPager() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){

        View view = inflater.inflate(R.layout.fragment_items_viewpager, container, false);

        bundle = getArguments();
        itemsViewPager = (ViewPager) view.findViewById(R.id.itemsViewPager);
        itemsViewPagerAdapter = new ItemsViewPagerAdapter(getChildFragmentManager(), getContext(), (BehaviourGetItemList) bundle.getSerializable(FragmentRecyclerItems.BEHAVIOURGETITEMLIST));
        itemsViewPager.setAdapter(itemsViewPagerAdapter);
        currentPosition = itemsViewPagerAdapter.getTouchedItemPosition(bundle.getString(ITEMID));
        itemsViewPager.setCurrentItem(currentPosition);

        itemsViewPagerAdapter.notifyDataSetChanged();

        itemsViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                currentPosition = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        return view;
    }

    public void reloadWithUpdatedData(Integer position){

        itemsViewPagerAdapter = new ItemsViewPagerAdapter(getChildFragmentManager(), getContext(), (BehaviourGetItemList) bundle.getSerializable(FragmentRecyclerItems.BEHAVIOURGETITEMLIST));
        itemsViewPager.setAdapter(itemsViewPagerAdapter);
        itemsViewPager.setCurrentItem(position);

    }


    @Override
    public void onResume() {
        super.onResume();
        reloadWithUpdatedData(currentPosition);
    }



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        postponeEnterTransition();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setSharedElementEnterTransition(new SharedElementTransition());
        }
        setSharedElementReturnTransition(null);
    }
}
