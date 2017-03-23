package com.santiagogil.takestock.view;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.santiagogil.takestock.R;
import com.santiagogil.takestock.model.pojos.Behaviours.BehaviourGetItemList;
import com.santiagogil.takestock.model.pojos.Behaviours.GetActiveItemsByIndependence;
import com.santiagogil.takestock.model.pojos.Behaviours.GetAllActiveItemsAlphabetically;
import com.santiagogil.takestock.model.pojos.Behaviours.GetAllActiveItemsSortedByIndependence;
import com.santiagogil.takestock.model.pojos.Behaviours.GetAllActiveItemsWithStockZero;
import com.santiagogil.takestock.model.pojos.Behaviours.GetAllInactiveItemsAlphabetically;

public class FragmentItemListsViewPager extends Fragment{

    private ViewPager itemListsViewPager;
    private ViewPager.OnPageChangeListener onPageChangeListener;
    private ItemListsViewPagerAdapter itemListsViewPagerAdapter;
    private FragmentItemList.FragmentActivityCommunicator fragmentActivityCommunicator;

    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_item_lists_view_pager, container, false);
        itemListsViewPager = (ViewPager) view.findViewById(R.id.viewPagerItemLists);
        itemListsViewPagerAdapter = new ItemListsViewPagerAdapter(getChildFragmentManager(), fragmentActivityCommunicator);

        populateFragmentListOnViewPagerAdapter();

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

    private void populateFragmentListOnViewPagerAdapter() {

        BehaviourGetItemList getAllActiveItemsAlphabetically = new GetAllActiveItemsAlphabetically(getContext());
        BehaviourGetItemList getActiveItemsByIndependenceZero = new GetActiveItemsByIndependence(getContext(), 0);
        BehaviourGetItemList getAllActiveItemsSortedByIndependence = new GetAllActiveItemsSortedByIndependence(getContext());
        BehaviourGetItemList getActiveItemsByIndependenceOneMonth = new GetActiveItemsByIndependence(getContext(), 30);
        BehaviourGetItemList getActiveItemsByIndependenceThreeMonths = new GetActiveItemsByIndependence(getContext(), 90);
        BehaviourGetItemList getDeletedItems = new GetAllInactiveItemsAlphabetically(getContext());
        BehaviourGetItemList getAllActiveItemsWithStockZero = new GetAllActiveItemsWithStockZero(getContext());

        itemListsViewPagerAdapter.getFragmentItemListList().add(FragmentItemList.getfragmentItemList("All Items A-Z", getAllActiveItemsAlphabetically, 0, fragmentActivityCommunicator));
        itemListsViewPagerAdapter.getFragmentItemListList().add(FragmentItemList.getfragmentItemList("All Items by Independence", getAllActiveItemsSortedByIndependence, 0, fragmentActivityCommunicator));
        itemListsViewPagerAdapter.getFragmentItemListList().add(FragmentItemList.getfragmentItemList("Stock 0", getAllActiveItemsWithStockZero, 0, fragmentActivityCommunicator));
        itemListsViewPagerAdapter.getFragmentItemListList().add(FragmentItemList.getfragmentItemList("Independence 0", getActiveItemsByIndependenceZero, 0, fragmentActivityCommunicator));
        itemListsViewPagerAdapter.getFragmentItemListList().add(FragmentItemList.getfragmentItemList("Menos de Un Mes", getActiveItemsByIndependenceOneMonth, 0, fragmentActivityCommunicator));
        itemListsViewPagerAdapter.getFragmentItemListList().add(FragmentItemList.getfragmentItemList("Menos de Tres Meses", getActiveItemsByIndependenceThreeMonths, 0, fragmentActivityCommunicator));
        itemListsViewPagerAdapter.getFragmentItemListList().add(FragmentItemList.getfragmentItemList("Deleted Items", getDeletedItems, 0, fragmentActivityCommunicator));
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
