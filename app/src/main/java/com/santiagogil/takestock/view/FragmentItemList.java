package com.santiagogil.takestock.view;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.santiagogil.takestock.R;
import com.santiagogil.takestock.controller.ConsumptionsController;
import com.santiagogil.takestock.controller.ItemsController;
import com.santiagogil.takestock.model.pojos.Consumption;
import com.santiagogil.takestock.model.pojos.Item;
import com.santiagogil.takestock.util.ResultListener;

import java.util.List;



public class FragmentItemList extends Fragment {


    private ItemsController itemsController;
    private RecyclerView recyclerView;
    private EditText editTextAddItem;
    private ItemRecyclerAdapter itemRecyclerAdapter;
    private TextView title;
    private Button buttonNewItem;


    private Bundle bundle;
    private Integer independence;
    private FragmentActivityCommunicator fragmentActivityCommunicator;

    public static final String TITLE = "title";
    public static final String INDEPENDENCE = "independence";
    public static final String POSITION = "position";


    public static FragmentItemList getfragmentItemList(String title, Integer independence, Integer position, FragmentActivityCommunicator fragmentActivityCommunicator) {

        FragmentItemList fragmentItemList = new FragmentItemList();
        fragmentItemList.setFragmentActivityCommunicator(fragmentActivityCommunicator);
        Bundle bundle = new Bundle();
        bundle.putString(TITLE, title);
        bundle.putInt(INDEPENDENCE, independence);
        bundle.putInt(POSITION, position);

        fragmentItemList.setArguments(bundle);

        return fragmentItemList;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_item_list, container, false);

        loadComponents(view);


        bundle = getArguments();
        independence = bundle.getInt(INDEPENDENCE);
        title.setText(bundle.getString(TITLE));

        this.getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        loadRecyclerView(view);



        itemsController = new ItemsController();
        List<Item> itemList = itemsController.getActiveItemsByIndependence(getContext(), independence);

        if (independence == -1) {
            itemList = itemsController.sortItemsAlphabetically(getContext(), itemList);
        } else {
            itemList = itemsController.sortItemsByIndependence(getContext(), itemList);
        }

        itemRecyclerAdapter.setItems(itemList);
        itemRecyclerAdapter.notifyDataSetChanged();



        buttonNewItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editTextAddItem.getText().toString().equals("")) {
                    Toast.makeText(view.getContext(), "Write an item name", Toast.LENGTH_SHORT).show();
                } else {
                    addNewItem(view);
                }

                editTextAddItem.clearFocus();
            }
        });

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
        itemRecyclerAdapter = new ItemRecyclerAdapter(getContext(), new OnItemStockChangedListener(), new OnItemTouchedListener());
        recyclerView.setAdapter(itemRecyclerAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
    }

    private void loadComponents(View view) {

        title = (TextView) view.findViewById(R.id.textViewTitle);
        buttonNewItem = (Button) view.findViewById(R.id.buttonNewItem);
        editTextAddItem = (EditText) view.findViewById(R.id.editText);
    }

    public void updateItemList() {

        List<Item> itemList = itemsController.getActiveItemsByIndependence(getContext(), independence);

        if (independence == -1) {
            itemList = itemsController.sortItemsAlphabetically(getContext(), itemList);
        } else {
            itemList = itemsController.sortItemsByIndependence(getContext(), itemList);
        }
        itemRecyclerAdapter.setItems(itemList);
        itemRecyclerAdapter.notifyDataSetChanged();


    }

    public interface FragmentActivityCommunicator {

        void onItemTouched(Item touchedItem, Integer touchedPosition, Integer independence);

    }

    private class OnItemStockChangedListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {

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
        }
    }

    private class OnItemTouchedListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            Integer touchedPosition = recyclerView.getChildAdapterPosition(view);
            Item touchedItem = itemRecyclerAdapter.getItemAtPosition(touchedPosition);
            fragmentActivityCommunicator.onItemTouched(touchedItem, touchedPosition, bundle.getInt(INDEPENDENCE));

        }
    }


    public void addNewItem(View view) {
        String itemName = editTextAddItem.getText().toString();
        editTextAddItem.setText("");
        Item item = new Item(itemName);
        String newItemID = itemsController.addItemToDatabases(view.getContext(), item);
        //TODO: check if item already exists
        List<Item> items = itemsController.getActiveItemsByIndependence(getContext(), bundle.getInt(INDEPENDENCE));
        itemRecyclerAdapter.setItems(items);
        itemRecyclerAdapter.notifyDataSetChanged();
        InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
        recyclerView.getLayoutManager().smoothScrollToPosition(recyclerView, null, findItemPosition(newItemID));

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


}
