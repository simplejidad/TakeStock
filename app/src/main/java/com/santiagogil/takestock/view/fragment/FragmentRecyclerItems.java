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

        hideKeyboard();
        loadRecyclerView(view);
        updateItemList();
        updateRecyclerViewPosition();
        updateListWithFilter();

        return view;
    }

    private void updateItemList() {
        itemList = behaviourGetItemList.getItemList(getContext());
        itemRecyclerAdapter.setItems(itemList);
        itemRecyclerAdapter.notifyDataSetChanged();
    }

    private void hideKeyboard() {
        this.getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    private void updateRecyclerViewPosition() {

        if (isNoBundleAttached()) {
            Integer position = null;
            try {
                position = bundle.getInt(POSITION);
            } catch (Exception e) {
                Toast.makeText(getContext(), "No position in bundle", Toast.LENGTH_SHORT).show();
            }

            if (isValidPositionSelected(position)) {
                recyclerView.scrollToPosition(position);
            } else if (isInvalidPositionSelected(position)) {
                recyclerView.scrollToPosition(position - 1);
            }
        }
    }

    private boolean isInvalidPositionSelected(Integer position) {
        return position > 0;
    }

    private boolean isValidPositionSelected(Integer position) {
        return position <= itemRecyclerAdapter.getItems().size();
    }

    private boolean isNoBundleAttached() {
        return bundle != null;
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

        void onItemTouched(
                Item touchedItem,
                Integer touchedPosition,
                BehaviourGetItemList behaviourGetItemList
        );

        void updateActionBarTitle(String title);

    }

    private class OnItemChangedListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            updateReciclerView(view);
        }

        private void updateReciclerView(View view) {
            ItemsController itemsController = new ItemsController();
            Integer touchedPosition = recyclerView.getChildAdapterPosition(view);
            Item touchedItem = itemRecyclerAdapter.getItemAtPosition(touchedPosition);
            Item updatedItem = itemsController.getItemFromLocalDatabase(getContext(), touchedItem.getID());
            updateRecyclerAdapterItem(touchedPosition, updatedItem);
            itemRecyclerAdapter.notifyDataSetChanged();
        }

        private void updateRecyclerAdapterItem(Integer itemPosition, Item updatedItem) {
            itemRecyclerAdapter.getItemAtPosition(itemPosition).setActive(updatedItem.isActive());
            itemRecyclerAdapter.getItemAtPosition(itemPosition).setConsumptionRate(updatedItem.getConsumptionRate());
            itemRecyclerAdapter.getItemAtPosition(itemPosition).setImage(updatedItem.getImage());
            itemRecyclerAdapter.getItemAtPosition(itemPosition).setMinimumPurchaceQuantity(updatedItem.getMinimumPurchaceQuantity());
            itemRecyclerAdapter.getItemAtPosition(itemPosition).setName(updatedItem.getName());
            itemRecyclerAdapter.getItemAtPosition(itemPosition).setStock(updatedItem.getStock());
            itemRecyclerAdapter.getItemAtPosition(itemPosition).setPrice(updatedItem.getPrice());
            itemRecyclerAdapter.getItemAtPosition(itemPosition).setUnitsInCart(updatedItem.getUnitsInCart());
        }
    }

    private class OnItemTouchedListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            Integer touchedPosition = recyclerView.getChildAdapterPosition(view);
            Item touchedItem = itemRecyclerAdapter.getItemAtPosition(touchedPosition);
            fragmentActivityCommunicator.onItemTouched(touchedItem, touchedPosition,
                    (BehaviourGetItemList) bundle.getSerializable(BEHAVIOURGETITEMLIST));

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

        if(isNoFilterSelected(filter))
            getUnfilteredList(filteredItemList);
        else{
            filteredItemList.clear();
            for(Item item : itemList){
                if(itemNameMatchesFilter(filter, item))
                    filteredItemList.add(item);
            }
            if(filteredListIsEmpty(filteredItemList))
                showNoItemsFoundItem(filteredItemList);
            itemRecyclerAdapter.setItems(filteredItemList);
            itemRecyclerAdapter.notifyDataSetChanged();
        }



    }

    private boolean itemNameMatchesFilter(String filter, Item item) {
        return item.getName().toLowerCase().contains(filter.toLowerCase());
    }

    private void showNoItemsFoundItem(List<Item> filteredItemList) {
        Item anItem = new Item("*** No Items Found ***");
        filteredItemList.add(anItem);
    }

    private boolean filteredListIsEmpty(List<Item> filteredItemList) {
        return filteredItemList.size() == 0;
    }

    private void getUnfilteredList(List<Item> filteredItemList) {
        filteredItemList.clear();
        filteredItemList.addAll(itemList);
    }

    private boolean isNoFilterSelected(String filter) {
        return filter.equals("") || filter.equals(null);
    }

    public String getTitle() {
        return getArguments().getString(TITLE);
    }

}
