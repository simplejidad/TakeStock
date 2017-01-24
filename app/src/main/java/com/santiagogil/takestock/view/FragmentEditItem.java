package com.santiagogil.takestock.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.santiagogil.takestock.R;
import com.santiagogil.takestock.controller.ItemsController;
import com.santiagogil.takestock.model.daos.DatabaseHelper;
import com.santiagogil.takestock.model.pojos.Item;

public class FragmentEditItem extends Fragment {

    private EditText editTextItemName;
    private EditText editTextItemStock;
    private EditText editTextMinimumPurchace;
    private EditText editTextConsumptionRate;
    private View fragmentView;
    private Button cancelButton;
    private Button saveButton;

    private Item item;
    private ItemsController itemsController;



    static final String POSITION = "position";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentView = inflater.inflate(R.layout.fragment_edit_item, container, false);

        final Bundle bundle = getArguments();

        itemsController = new ItemsController();
        item = itemsController.getItemFromLocalDatabase(getContext(), bundle.getString(DatabaseHelper.ID));

        editTextItemName = (EditText) fragmentView.findViewById(R.id.editTextItemName);
        editTextItemStock = (EditText) fragmentView.findViewById(R.id.editTextStock);
        editTextConsumptionRate = (EditText) fragmentView.findViewById(R.id.editTextConsumptionRate);
        editTextMinimumPurchace = (EditText) fragmentView.findViewById(R.id.editTextMinimumPurchaceAmmount);
        cancelButton = (Button) fragmentView.findViewById(R.id.buttonCancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });

        saveButton = (Button) fragmentView.findViewById(R.id.buttonSave);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                updateItemDetails();

                getActivity().onBackPressed();
            }
        });

        editTextItemName.setText(item.getName());
        editTextItemStock.setText(item.getStock().toString());
        editTextConsumptionRate.setText(item.getConsumptionRate().toString());
        editTextMinimumPurchace.setText(item.getMinimumPurchaceQuantity().toString());

        return fragmentView;
    }

    public void updateItemDetails (){

        String updatedItemName = editTextItemName.getText().toString();
        Integer updatedItemStock = tryParse(editTextItemStock.getText().toString());
        Integer updatedItemMinimumPurchaceQuantity = tryParse(editTextMinimumPurchace.getText().toString());
        Integer updatedItemConsumptionRate = tryParse(editTextConsumptionRate.getText().toString());

        ItemsController itemsController = new ItemsController();
        itemsController.updateItemDetails(getContext(), item.getID(), updatedItemName, updatedItemStock, updatedItemConsumptionRate, updatedItemMinimumPurchaceQuantity);

    }

    public static Integer tryParse(String text) {
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
