package com.santiagogil.takestock.view.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.santiagogil.takestock.R;
import com.santiagogil.takestock.model.pojos.Behaviours.BehaviourGetItemList;
import com.santiagogil.takestock.model.pojos.Item;
import com.santiagogil.takestock.view.adapter.ItemsViewPagerAdapter;

import java.util.Objects;

public class FragmentItemsViewPager extends Fragment {

    private ViewPager itemsViewPager;
    private Bundle bundle;
    private ItemsViewPagerAdapter itemsViewPagerAdapter;
    private Integer currentPosition;
    private Listener listener;

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public static final String POSITION = "Position";
    public static final String BEHAVIOURGETITEMLIST = "behaviourGetList";
    public static final String ITEMID = "ItemID";

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        bundle = getArguments();
        itemsViewPager = view.findViewById(R.id.itemsViewPager);
        itemsViewPagerAdapter = new ItemsViewPagerAdapter(
                getChildFragmentManager(),
                getContext(),
                (BehaviourGetItemList) Objects.requireNonNull(bundle.getSerializable(FragmentRecyclerItems.BEHAVIOURGETITEMLIST))
            );
        itemsViewPager.setAdapter(itemsViewPagerAdapter);
        currentPosition = itemsViewPagerAdapter.getTouchedItemPosition(bundle.getString(ITEMID));
        itemsViewPager.setCurrentItem(currentPosition);
        itemsViewPagerAdapter.notifyDataSetChanged();

        itemsViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) { }

            @Override
            public void onPageSelected(int position) {
                currentPosition = position;
                if(getCurrentItem() != null) {
                    listener.updateToolbarIcon(getCurrentItem());
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        return inflater.inflate(R.layout.fragment_items_viewpager, container, false);
    }

    public void reloadWithUpdatedData(Integer position){
        itemsViewPagerAdapter = new ItemsViewPagerAdapter(getChildFragmentManager(), getContext(),
                (BehaviourGetItemList) Objects.requireNonNull(bundle.getSerializable(FragmentRecyclerItems.BEHAVIOURGETITEMLIST)));
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
    }

    public void deleteItem(){
        FragmentItemDetail fragmentItemDetail = (FragmentItemDetail) this.itemsViewPagerAdapter.getItem(currentPosition);
        fragmentItemDetail.onItemDeleted();
        listener.updateToolbarIcon(getCurrentItem());
    }

    public void editItem() {
        FragmentItemDetail fragmentItemDetail = (FragmentItemDetail) this.itemsViewPagerAdapter.getItem(currentPosition);
        fragmentItemDetail.editItem();
    }

    public Item getCurrentItem(){
        FragmentItemDetail fragmentItemDetail = (FragmentItemDetail) this.itemsViewPagerAdapter.getItem(currentPosition);
        return  fragmentItemDetail.getItem();
    }

    public interface Listener{
        void updateToolbarIcon(Item item);
    }
}
