package com.santiagogil.takestock.view.Fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.santiagogil.takestock.R;
import com.santiagogil.takestock.controller.ConsumptionsController;
import com.santiagogil.takestock.model.pojos.Consumption;
import com.santiagogil.takestock.view.Adapters.ConsumptionRecyclerAdapter;

/**
 * A simple {@link Fragment} subclass.
 */
public class ConsumptionRecyclerFragment extends SimpleRecyclerFragment {

    private Context context = getContext();

    public static ConsumptionRecyclerFragment createFragment(String itemID){

        ConsumptionRecyclerFragment consumptionRecyclerFragment = new ConsumptionRecyclerFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ITEMID, itemID);
        bundle.putString(TITLE, "Consumptions");
        consumptionRecyclerFragment.setArguments(bundle);
        return consumptionRecyclerFragment;

    }

    public ConsumptionRecyclerFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.simple_recycler_view, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        ConsumptionRecyclerAdapter consumptionRecyclerAdapter = new ConsumptionRecyclerAdapter(getContext(), new FragmentItemDetail.OnConsumptionDeletedListener());
        ConsumptionsController consumptionsController = new ConsumptionsController();
        consumptionRecyclerAdapter.setConsumptionList(consumptionsController.sortedItemConsumptionList(getContext(), getArguments().getString(ITEMID)));
        recyclerView.setAdapter(consumptionRecyclerAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setPadding(6,1,6,1);
    }



    public String getTitle(){
        return getArguments().getString(TITLE);
    }
}
