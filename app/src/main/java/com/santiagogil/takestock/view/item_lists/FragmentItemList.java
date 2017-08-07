package com.santiagogil.takestock.view.item_lists;


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

import java.util.List;



public class FragmentItemList extends Fragment implements FragmentLifecycle {

    private RecyclerView recyclerView;
    private ItemRecyclerAdapter itemRecyclerAdapter;
    private BehaviourGetItemList behaviourGetItemList;
    private String title;
    private Integer currentRecyclerPosition;
    private Bundle bundle;
    private FragmentActivityCommunicator fragmentActivityCommunicator;

    public static final String TITLE = "textViewTitle";
    public static final String POSITION = "position";
    public static final String BEHAVIOURGETITEMLIST = "behaviourGetList";


    public static FragmentItemList getfragmentItemList(String title, BehaviourGetItemList behaviourGetItemList, Integer position, FragmentActivityCommunicator fragmentActivityCommunicator) {

        FragmentItemList fragmentItemList = new FragmentItemList();
        fragmentItemList.setFragmentActivityCommunicator(fragmentActivityCommunicator);
        Bundle bundle = new Bundle();
        bundle.putString(TITLE, title);
        bundle.putInt(POSITION, position);
        bundle.putSerializable(BEHAVIOURGETITEMLIST, behaviourGetItemList);

        fragmentItemList.setArguments(bundle);



        return fragmentItemList;
    }

    public String getTitle() {
        return getArguments().getString(TITLE);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_item_list, container, false);

        bundle = getArguments();

        title = bundle.getString(TITLE);

        behaviourGetItemList = (BehaviourGetItemList) bundle.getSerializable(BEHAVIOURGETITEMLIST);

        this.getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        loadRecyclerView(view);

        itemRecyclerAdapter.setItems(behaviourGetItemList.getItemList(getContext()));
        itemRecyclerAdapter.notifyDataSetChanged();


        updateRecyclerViewPosition();


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



    public Integer findItemPosition(String itemID) {
        List<Item> items = itemRecyclerAdapter.getItems();
        for (Item item : items) {
            if (item.getID().equals(itemID)) {
                return (items.indexOf(item));
            }
        }
        return null;
    }

    public void setFragmentActivityCommunicator(FragmentActivityCommunicator fragmentActivityCommunicator) {
        this.fragmentActivityCommunicator = fragmentActivityCommunicator;
    }

    @Override
    public void onPauseFragment() {

    }

    @Override
    public void onResumeFragment() {
        Bundle bundle = getArguments();
        fragmentActivityCommunicator.updateActionBarTitle(bundle.getString(TITLE));
    }



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        postponeEnterTransition();
    }

}
