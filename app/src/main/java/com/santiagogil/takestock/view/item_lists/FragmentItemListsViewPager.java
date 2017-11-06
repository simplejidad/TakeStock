package com.santiagogil.takestock.view.item_lists;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.santiagogil.takestock.R;
import com.santiagogil.takestock.model.pojos.Behaviours.BehaviourGetItemList;
import com.santiagogil.takestock.model.pojos.Behaviours.GetActiveItemsByIndependenceSortedAlphabetically;
import com.santiagogil.takestock.model.pojos.Behaviours.GetActiveItemsSortedByConsumptionRate;
import com.santiagogil.takestock.model.pojos.Behaviours.GetAllActiveItemsAlphabetically;
import com.santiagogil.takestock.model.pojos.Behaviours.GetAllActiveItemsSortedByIndependence;
import com.santiagogil.takestock.model.pojos.Behaviours.GetAllActiveItemsWithStockZero;
import com.santiagogil.takestock.model.pojos.Behaviours.GetAllInactiveItemsAlphabetically;
import com.santiagogil.takestock.util.FragmentLifecycle;

import java.util.ArrayList;

public class FragmentItemListsViewPager extends Fragment {

    private ViewPager itemListsViewPager;
    private ItemListsViewPagerAdapter itemListsViewPagerAdapter;
    private FragmentItemList.FragmentActivityCommunicator fragmentActivityCommunicator;
    private Integer currentFragmentPosition = 0;


    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_item_lists_view_pager, container, false);

        itemListsViewPager = (ViewPager) view.findViewById(R.id.viewPagerItemLists);
        itemListsViewPagerAdapter = new ItemListsViewPagerAdapter(getChildFragmentManager());

        populateFragmentListOnViewPagerAdapter(getArguments().getString(FragmentItemList.FILTER));

        itemListsViewPager.setAdapter(itemListsViewPagerAdapter);

        itemListsViewPager.addOnPageChangeListener(pageChangeListener);

        updateActionBarTitle(currentFragmentPosition);

        return view;
    }

    private ViewPager.OnPageChangeListener pageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int newPosition) {

            FragmentLifecycle fragmentToShow = (FragmentLifecycle) itemListsViewPagerAdapter.getItem(newPosition);
            fragmentToShow.onResumeFragment();
            FragmentLifecycle fragmentToHide = (FragmentLifecycle) itemListsViewPagerAdapter.getItem(currentFragmentPosition);
            fragmentToHide.onPauseFragment();
            currentFragmentPosition = newPosition;
            updateActionBarTitle(currentFragmentPosition);
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) { }

        public void onPageScrollStateChanged(int arg0) { }
    };

    private void updateActionBarTitle(Integer position){

        fragmentActivityCommunicator.updateActionBarTitle(getCurrentFragment(position).getTitle());

    }

    public FragmentItemList getCurrentFragment(Integer position) {

        FragmentItemList currentFragment = itemListsViewPagerAdapter.getFragmentItemListList().get(position);
        return currentFragment;
    }

    public ItemListsViewPagerAdapter getItemListsViewPagerAdapter() {
        return itemListsViewPagerAdapter;
    }

    private void populateFragmentListOnViewPagerAdapter(String filter) {

        BehaviourGetItemList getAllActiveItemsAlphabetically = new GetAllActiveItemsAlphabetically();
        BehaviourGetItemList getAllActiveItemsSortedByIndependence = new GetAllActiveItemsSortedByIndependence();
        BehaviourGetItemList getActiveItemsByIndependenceOneMonth = new GetActiveItemsByIndependenceSortedAlphabetically(30);
        BehaviourGetItemList getActiveItemsByIndependenceThreeMonths = new GetActiveItemsByIndependenceSortedAlphabetically(90);
        BehaviourGetItemList getDeletedItems = new GetAllInactiveItemsAlphabetically();
        BehaviourGetItemList getAllActiveItemsWithStockZero = new GetAllActiveItemsWithStockZero();
        BehaviourGetItemList getAllActiveItemsSortedByConsumptionRate = new GetActiveItemsSortedByConsumptionRate();

        itemListsViewPagerAdapter.getFragmentItemListList().add(FragmentItemList.getfragmentItemList("By Consumption Rate", getAllActiveItemsSortedByConsumptionRate, 0, fragmentActivityCommunicator, filter));
        itemListsViewPagerAdapter.getFragmentItemListList().add(FragmentItemList.getfragmentItemList("All Items A-Z", getAllActiveItemsAlphabetically, 0, fragmentActivityCommunicator, filter));
        itemListsViewPagerAdapter.getFragmentItemListList().add(FragmentItemList.getfragmentItemList("All Items by Independence", getAllActiveItemsSortedByIndependence, 0, fragmentActivityCommunicator, filter));
        itemListsViewPagerAdapter.getFragmentItemListList().add(FragmentItemList.getfragmentItemList("Stock 0", getAllActiveItemsWithStockZero, 0, fragmentActivityCommunicator, filter));
        itemListsViewPagerAdapter.getFragmentItemListList().add(FragmentItemList.getfragmentItemList("Menos de Un Mes", getActiveItemsByIndependenceOneMonth, 0, fragmentActivityCommunicator, filter));
        itemListsViewPagerAdapter.getFragmentItemListList().add(FragmentItemList.getfragmentItemList("Menos de Tres Meses", getActiveItemsByIndependenceThreeMonths, 0, fragmentActivityCommunicator, filter));
        itemListsViewPagerAdapter.getFragmentItemListList().add(FragmentItemList.getfragmentItemList("Deleted Items", getDeletedItems, 0, fragmentActivityCommunicator, filter));
    }

    public void setFragmentActivityCommunicator(FragmentItemList.FragmentActivityCommunicator fragmentActivityCommunicator) {
        this.fragmentActivityCommunicator = fragmentActivityCommunicator;

    }


    @Override
    public void onResume() {
        super.onResume();
        updateActionBarTitle(currentFragmentPosition);
    }
}
