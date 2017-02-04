package com.santiagogil.takestock.view;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.santiagogil.takestock.R;
import com.santiagogil.takestock.controller.ConsumptionsController;
import com.santiagogil.takestock.controller.ItemsController;
import com.santiagogil.takestock.model.pojos.Consumption;
import com.santiagogil.takestock.model.pojos.Item;
import com.santiagogil.takestock.util.ResultListener;

import java.util.List;

public class FragmentItemListsViewPager extends Fragment{

    private ItemsController itemsController;
    private RecyclerView recyclerView;
    private EditText editTextAddItem;
    private ItemRecyclerAdapter itemRecyclerAdapter;
    private ConsumptionsController consumptionsController;

    private ViewPager itemListsViewPager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_item_lists_view_pager, container, false);
        itemListsViewPager = (ViewPager) view.findViewById(R.id.viewPagerItemLists);
        ItemListsViewPagerAdapter itemListsViewPagerAdapter = new ItemListsViewPagerAdapter(getChildFragmentManager(), getContext());
        itemListsViewPager.setAdapter(itemListsViewPagerAdapter);

        return view;
    }
}
