package com.santiagogil.takestock.view.fragment;


import android.os.Bundle;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.santiagogil.takestock.R;
import com.santiagogil.takestock.controller.PurchacesController;
import com.santiagogil.takestock.view.adapter.PurchaceRecyclerAdapter;

/**
 * A simple {@link Fragment} subclass.
 */
public class PurchacesRecyclerFragment extends SimpleRecyclerFragment {

    private PurchaceRecyclerAdapter purchaceRecyclerAdapter;
    private FragmentRecyclerToFragmentCommunicator fragmentRecyclerToFragmentCommunicator;

   public PurchacesRecyclerFragment() {

    }

    private static final String ITEMID = "ItemID";
    private static final String TITLE = "Title";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.simple_recycler_view, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setUpRecyclerView(view);
    }

    private void setUpRecyclerView(View view) {
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        purchaceRecyclerAdapter = new PurchaceRecyclerAdapter(getContext(), this);
        PurchacesController purchacesController = new PurchacesController();
        purchaceRecyclerAdapter.setPurchacesList(purchacesController.getItemPurchaceList(getContext(), getArguments().getString(ITEMID)));
        recyclerView.setAdapter(purchaceRecyclerAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setPadding(6,1,6,1);
    }

    public String getTitle(){
        return getArguments().getString(TITLE);
    }

    @Override
    public void onPurchacesUpdated() {
        PurchacesController purchacesController = new PurchacesController();
        purchaceRecyclerAdapter.setPurchacesList(purchacesController.getItemPurchaceList(getContext(),getArguments().getString(ITEMID)));
        purchaceRecyclerAdapter.notifyDataSetChanged();
        fragmentRecyclerToFragmentCommunicator.onItemStockChanged();
    }

    public void setFragmentRecyclerToFragmentCommunicator(FragmentRecyclerToFragmentCommunicator fragmentRecyclerToFragmentCommunicator) {
        this.fragmentRecyclerToFragmentCommunicator = fragmentRecyclerToFragmentCommunicator;
    }
}
