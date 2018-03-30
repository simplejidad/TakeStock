package com.santiagogil.takestock.view.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.santiagogil.takestock.R;
import com.santiagogil.takestock.controller.ItemsController;
import com.santiagogil.takestock.model.pojos.Behaviours.BehaviourGetItemList;
import com.santiagogil.takestock.model.pojos.Item;
import com.santiagogil.takestock.util.FragmentLifecycle;
import com.santiagogil.takestock.view.adapter.ItemRecyclerAdapter;

import java.util.ArrayList;
import java.util.List;



public class FragmentRecyclerItems extends Fragment implements FragmentLifecycle {

    private RecyclerView recyclerView;
    private ItemRecyclerAdapter itemRecyclerAdapter;
    private BehaviourGetItemList behaviourGetItemList;
    private Bundle bundle;
    private FragmentActivityCommunicator fragmentActivityCommunicator;
    private List<Item> itemList;

    public static final String TITLE = "textViewTitle";
    public static final String POSITION = "position";
    public static final String BEHAVIOURGETITEMLIST = "behaviourGetList";
    public static final String FILTER = "filter";


    public static FragmentRecyclerItems getfragmentItemList(String title, BehaviourGetItemList behaviourGetItemList, Integer position, FragmentActivityCommunicator fragmentActivityCommunicator, String filter) {

        FragmentRecyclerItems fragmentRecyclerItems = new FragmentRecyclerItems();
        fragmentRecyclerItems.setFragmentActivityCommunicator(fragmentActivityCommunicator);
        Bundle bundle = new Bundle();
        bundle.putString(TITLE, title);
        bundle.putInt(POSITION, position);
        bundle.putString(FILTER, filter);
        bundle.putSerializable(BEHAVIOURGETITEMLIST, behaviourGetItemList);

        fragmentRecyclerItems.setArguments(bundle);

        return fragmentRecyclerItems;
    }



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_item_list, container, false);

        bundle = getArguments();

        behaviourGetItemList = (BehaviourGetItemList) bundle.getSerializable(BEHAVIOURGETITEMLIST);

        this.getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        loadRecyclerView(view);

        itemList = behaviourGetItemList.getItemList(getContext());
        itemRecyclerAdapter.setItems(itemList);
        itemRecyclerAdapter.notifyDataSetChanged();

        updateRecyclerViewPosition();

        updateListWithFilter();

        return view;
    }

    private void updateRecyclerViewPosition() {

        if (bundle != null) {

            Integer position = null;

            try {
                position = bundle.getInt(POSITION);
            } catch (Exception e) {
                Toast.makeText(getContext(), "No position in bundle", Toast.LENGTH_SHORT).show();
            }

            if (position <= itemRecyclerAdapter.getItems().size()) {

                recyclerView.scrollToPosition(position);

            } else if (position > 0) {
                recyclerView.scrollToPosition(position - 1);
            }
        }
    }

    private void loadRecyclerView(View view) {

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerViewItems);
        itemRecyclerAdapter = new ItemRecyclerAdapter(getContext(), new OnItemChangedListener(), new OnItemTouchedListener());
        recyclerView.setAdapter(itemRecyclerAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);

    }

    public interface FragmentActivityCommunicator {

        void onItemTouched(Item touchedItem, Integer touchedPosition,
                           BehaviourGetItemList behaviourGetItemList, TextView textViewItemName,
                           TextView textViewItemStock, TextView textViewItemIndependence);

        void updateActionBarTitle(String title);

    }

    private class OnItemChangedListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {

            ItemsController itemsController = new ItemsController();
            Integer touchedPosition = recyclerView.getChildAdapterPosition(view);
            Item touchedItem = itemRecyclerAdapter.getItemAtPosition(touchedPosition);
            Item updatedItem = itemsController.getItemFromLocalDatabase(getContext(), touchedItem.getID());
            updateRecyclerAdapterItem(touchedPosition, updatedItem);
            itemRecyclerAdapter.notifyDataSetChanged();

        }

        private void updateRecyclerAdapterItem(Integer itemPosition, Item updatedItem) {
            itemRecyclerAdapter.getItemAtPosition(itemPosition).setActive(updatedItem.getActive());
            itemRecyclerAdapter.getItemAtPosition(itemPosition).setConsumptionRate(updatedItem.getConsumptionRate());
            itemRecyclerAdapter.getItemAtPosition(itemPosition).setImage(updatedItem.getImage());
            itemRecyclerAdapter.getItemAtPosition(itemPosition).setMinimumPurchaceQuantity(updatedItem.getMinimumPurchaceQuantity());
            itemRecyclerAdapter.getItemAtPosition(itemPosition).setName(updatedItem.getName());
            itemRecyclerAdapter.getItemAtPosition(itemPosition).setStock(updatedItem.getStock());
            itemRecyclerAdapter.getItemAtPosition(itemPosition).setPrice(updatedItem.getPrice());
            itemRecyclerAdapter.getItemAtPosition(itemPosition).setCart(updatedItem.getCart());
        }
    }

    private class OnItemTouchedListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            Integer touchedPosition = recyclerView.getChildAdapterPosition(view);
            Item touchedItem = itemRecyclerAdapter.getItemAtPosition(touchedPosition);
            TextView textViewItemName = (TextView) view.findViewById(R.id.text_view_item_name);
            TextView textViewItemStock = (TextView) view.findViewById(R.id.text_view_item_stock);
            TextView textViewItemIndependence = (TextView) view.findViewById(R.id.text_view_item_independence);
            fragmentActivityCommunicator.onItemTouched(touchedItem, touchedPosition,
                    (BehaviourGetItemList) bundle.getSerializable(BEHAVIOURGETITEMLIST),
                    textViewItemName, textViewItemStock, textViewItemIndependence);

        }
    }


    public void setFragmentActivityCommunicator(FragmentActivityCommunicator fragmentActivityCommunicator) {
        this.fragmentActivityCommunicator = fragmentActivityCommunicator;
    }

    @Override
    public void onPauseFragment() {

    }

    @Override
    public void onResumeFragment() {

    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        postponeEnterTransition();
    }

    public List<Item> getItemList() {
        return itemList;
    }

    public void updateListWithFilter(){

        String filter = getArguments().getString(FILTER);
        List<Item> filteredItemList = new ArrayList<>();

        if(filter.equals("") || filter.equals(null)){
            filteredItemList.clear();
            filteredItemList.addAll(itemList);
        } else{
            filteredItemList.clear();
            for(Item item : itemList){
                if(item.getName().toLowerCase().contains(filter.toLowerCase())){
                    filteredItemList.add(item);

                }
            }
            if(filteredItemList.size() == 0){
                Item anItem = new Item("*** No Items Found ***");
                filteredItemList.add(anItem);
            }
            itemRecyclerAdapter.setItems(filteredItemList);
            itemRecyclerAdapter.notifyDataSetChanged();
        }



    }

    public String getTitle() {
        return getArguments().getString(TITLE);
    }

}
