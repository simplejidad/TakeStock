package com.santiagogil.takestock.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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

    private String itemID;
    private String itemName;
    private Integer itemStock;
    private Integer itemConsumptionRate;
    private Integer itemMinimumPurchaceQuantity;



    static final String POSITION = "position";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentView = inflater.inflate(R.layout.fragment_edit_item, container, false);

        final Bundle bundle = getArguments();

        itemID = bundle.getString(DatabaseHelper.ID);
        itemName = bundle.getString(DatabaseHelper.NAME);
        itemStock = bundle.getInt(DatabaseHelper.STOCK);
        itemConsumptionRate = bundle.getInt(DatabaseHelper.CONSUMPTIONRATE);
        itemMinimumPurchaceQuantity = bundle.getInt(DatabaseHelper.MINIMUMPURCHACEQUANTITY);

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



            }
        });

        editTextItemName.setText(itemName);
        editTextItemStock.setText(itemStock);
        editTextConsumptionRate.setText(itemConsumptionRate);
        editTextMinimumPurchace.setText(itemMinimumPurchaceQuantity);

        return fragmentView;
    }

    public interface FragmentActivityCommunicator{
        void refreshFragmentMainView(Integer position);
    }

    public void updateItemDetails (){

        String updatedItemName = editTextItemName.getText().toString();
        Integer updatedItemStock = tryParse(editTextItemStock.getText().toString());
        Integer updatedItemMinimumPurchaceQuantity = tryParse(editTextMinimumPurchace.getText().toString());
        Integer updatedItemConsumptionRate = tryParse(editTextConsumptionRate.getText().toString());

        ItemsController itemsController = new ItemsController();
        itemsController.updateItemDetails(getContext(), itemID, itemName, itemStock, itemConsumptionRate, itemMinimumPurchaceQuantity);

    }

    public static Integer tryParse(String text) {
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
