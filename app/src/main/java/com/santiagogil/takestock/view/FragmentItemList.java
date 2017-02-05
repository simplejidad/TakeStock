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
import android.widget.TextView;
import android.widget.Toast;

import com.santiagogil.takestock.R;
import com.santiagogil.takestock.controller.ConsumptionsController;
import com.santiagogil.takestock.controller.ItemsController;
import com.santiagogil.takestock.model.pojos.Consumption;
import com.santiagogil.takestock.model.pojos.Item;
import com.santiagogil.takestock.util.ResultListener;

import java.util.List;



public class FragmentItemList extends Fragment implements View.OnClickListener{


    private ItemsController itemsController;
    private RecyclerView recyclerView;
    private EditText editTextAddItem;
    private ItemRecyclerAdapter itemRecyclerAdapter;
    private ConsumptionsController consumptionsController;
    private TextView title;
    private Bundle bundle;

    public static final String TITLE = "title";
    public static final String INDEPENDENCE = "independence";
    public static final String POSITION = "position";

    public static FragmentItemList getfragmentItemList(String title, Integer independence, Integer position){

        FragmentItemList fragmentItemList = new FragmentItemList();
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
        title = (TextView) view.findViewById(R.id.textViewTitle);
        bundle = getArguments();
        title.setText(bundle.getString(TITLE));
        itemsController = new ItemsController();
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerViewItems);
        itemRecyclerAdapter = new ItemRecyclerAdapter(getContext(), this, new FragmentItemList.ItemListener());
        recyclerView.setAdapter(itemRecyclerAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        this.getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        itemsController.getActiveItemsByIndependence(getContext(), bundle.getInt(INDEPENDENCE), new ResultListener<List<Item>>() {
            @Override
            public void finish(List<Item> result) {
                itemRecyclerAdapter.setItems(result);
                itemRecyclerAdapter.notifyDataSetChanged();

                Button buttonNewItem = (Button) view.findViewById(R.id.buttonNewItem);
                editTextAddItem = (EditText) view.findViewById(R.id.editText);

                buttonNewItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(editTextAddItem.getText().toString().equals("")){
                            Toast.makeText(view.getContext(), "Write an item name", Toast.LENGTH_SHORT).show();
                        } else {
                            addNewItem(view);
                        }

                        editTextAddItem.clearFocus();
                    }
                });

                Bundle bundle = getArguments();
                if(bundle != null){

                    Integer position = null;

                    try {
                        position = bundle.getInt(POSITION);
                    }catch (Exception e){
                        Toast.makeText(getContext(), "No position in bundle", Toast.LENGTH_SHORT).show();
                    }

                    if (position <= itemRecyclerAdapter.getItems().size()){

                        recyclerView.scrollToPosition(position);

                    } else if(position > 0){
                        recyclerView.scrollToPosition(position - 1);
                    }
                }
            }
        });

        consumptionsController = new ConsumptionsController();
        consumptionsController.updateConsumptionsDatabase(getContext(), new ResultListener<List<Consumption>>(){
            @Override
            public void finish(List<Consumption> result) {

            }
        });

        return view;

    }

    @Override
    public void onClick(View view) {

        itemRecyclerAdapter.setItems(itemsController.getActiveItemsByIndependence(getContext(), bundle.getInt(INDEPENDENCE)));
        itemRecyclerAdapter.notifyDataSetChanged();
    }

    public interface FragmentActivityCommunicator {
        void onItemTouched(Item touchedItem, Integer touchedPosition, Integer independence);
    }

    public class ItemListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            Integer touchedPosition = recyclerView.getChildAdapterPosition(view);
            Item touchedItem = itemRecyclerAdapter.getItemAtPosition(touchedPosition);

            FragmentItemList.FragmentActivityCommunicator fragmentActivityCommunicator = (FragmentItemList.FragmentActivityCommunicator) getActivity();
            fragmentActivityCommunicator.onItemTouched(touchedItem, touchedPosition, bundle.getInt(INDEPENDENCE));

        }
    }

    public void addNewItem(View view){
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

    public Integer findItemPosition(String itemID){
        List<Item> items = itemRecyclerAdapter.getItems();
        for(Item item : items){
            if(item.getID().equals(itemID)){
                return(items.indexOf(item));
            }
        }
        return null;
    }
}
