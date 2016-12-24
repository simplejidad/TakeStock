package com.santiagogil.takestock.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.santiagogil.takestock.R;
import com.santiagogil.takestock.controller.ItemsController;
import com.santiagogil.takestock.model.pojos.Item;
import com.santiagogil.takestock.util.ResultListener;

import java.util.List;

public class FragmentMainView extends Fragment implements View.OnClickListener{

    private ItemsController itemsController;
    private RecyclerView recyclerView;
    private EditText editTextAddItem;
    private ItemRecyclerAdapter itemRecyclerAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_main_view, container, false);
        itemsController = new ItemsController();

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerViewItems);
        itemRecyclerAdapter = new ItemRecyclerAdapter(getContext(), this, new ItemListener());
        itemsController.getItems(getContext(), new ResultListener<List<Item>>() {
            @Override
            public void finish(List<Item> result) {
                itemRecyclerAdapter.setItems(result);
            }
        });

        //updateRecyclerAdapter();

        recyclerView.setAdapter(itemRecyclerAdapter);
        //itemRecyclerAdapter.setItems(new ArrayList<Item>());

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);

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

        return view;

    }

    @Override
    public void onClick(View view) {
        itemRecyclerAdapter.setItems(itemsController.getItemsFromLocalDatabase(getContext()));
        itemRecyclerAdapter.notifyDataSetChanged();
    }

    public interface FragmentActivityCommunicator {
        void onItemTouched(Item touchedItem);
    }

    public class ItemListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            Integer touchedPosition = recyclerView.getChildAdapterPosition(view);
            Item touchedItem = itemRecyclerAdapter.getItemAtPosition(touchedPosition);

            FragmentActivityCommunicator fragmentActivityCommunicator = (FragmentActivityCommunicator) getActivity();
            fragmentActivityCommunicator.onItemTouched(touchedItem);

        }
    }

    public void updateRecyclerAdapter (){

        itemsController.getItems(getContext(), new ResultListener<List<Item>>() {
            @Override
            public void finish(List<Item> result) {

                itemRecyclerAdapter.setItems(result);
                itemRecyclerAdapter.notifyDataSetChanged();

                if(itemsController.getItemsFromLocalDatabase(getContext()).size() == 0) {
                    for (Item item : result){
                        itemsController.addItemToLocalDatabase(getContext(), item);
                    }
                };
            }
        });
    }



    public void addNewItem(View view){
        String itemName = editTextAddItem.getText().toString();
        editTextAddItem.setText("");
        Item item = new Item(itemName);
        itemsController.addItemToDatabases(view.getContext(), item);
        Toast.makeText(view.getContext(), item.getName() + " has been added.", Toast.LENGTH_SHORT).show();
        itemRecyclerAdapter.getItems().add(item);
        itemRecyclerAdapter.notifyDataSetChanged();
    }
}
