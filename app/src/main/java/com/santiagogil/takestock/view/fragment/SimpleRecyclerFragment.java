package com.santiagogil.takestock.view.fragment;


import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.santiagogil.takestock.R;
import com.santiagogil.takestock.view.adapter.ConsumptionRecyclerAdapter;
import com.santiagogil.takestock.view.adapter.PurchaceRecyclerAdapter;

import java.io.Serializable;

/**
 * A simple {@link Fragment} subclass.
 */
public class SimpleRecyclerFragment extends Fragment implements ConsumptionRecyclerAdapter.RecyclerConsumptionsFragmentCommunicator,
        PurchaceRecyclerAdapter.RecyclerPurchacesFragmentCommunicator, Serializable {

    public static final String ITEMID = "ItemID";
    public static final String TITLE = "Title";

    public SimpleRecyclerFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_simple_recycler, container, false);
    }

    public String getTitle(){
        return getArguments().getString(TITLE);
    }

    @Override
    public void onConsumptionsUpdated() {

    }

    @Override
    public void onPurchacesUpdated() {

    }

    public interface FragmentRecyclerToFragmentCommunicator{
        void onItemStockChanged();
    }
}
