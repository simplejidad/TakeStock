package com.santiagogil.takestock.view;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.santiagogil.takestock.R;

public class FragmentItemListsViewPager extends Fragment{

    private ViewPager itemListsViewPager;
    private ViewPager.OnPageChangeListener onPageChangeListener;
    private ItemListsViewPagerAdapter itemListsViewPagerAdapter;
    private FragmentItemList.FragmentActivityCommunicator fragmentActivityCommunicator;

    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_item_lists_view_pager, container, false);
        itemListsViewPager = (ViewPager) view.findViewById(R.id.viewPagerItemLists);
        itemListsViewPagerAdapter = new ItemListsViewPagerAdapter(getChildFragmentManager(), fragmentActivityCommunicator);
        itemListsViewPager.setAdapter(itemListsViewPagerAdapter);

        onPageChangeListener = new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                itemListsViewPagerAdapter.getFragmentItemListList().get(position).updateItemList();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        };

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        itemListsViewPager.addOnPageChangeListener(onPageChangeListener);
    }

    public void setFragmentActivityCommunicator(FragmentItemList.FragmentActivityCommunicator fragmentActivityCommunicator) {
        this.fragmentActivityCommunicator = fragmentActivityCommunicator;
    }

}
