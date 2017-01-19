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
import com.santiagogil.takestock.model.pojos.Item;

public class FragmentEditItem extends Fragment {

    private EditText editTextItemName;
    private EditText editTextItemStock;
    private EditText editTextMinimumPurchace;
    private EditText editTextConsumptionRate;
    private View fragmentView;
    private Button cancelButton;
    private Button saveButton;

    static final String POSITION = "position";
    static final String ID = "id";
    static final String STOCK = "stock";
    static final String NAME = "name";
    static final String CONSUMPTION_RATE = "consumptionRate";
    static final String MINIMUM_PURCHASE_QUANTITY = "minimumPurchaceQuantity";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentView = inflater.inflate(R.layout.fragment_edit_item, container, false);

        final Bundle bundle = getArguments();

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

        editTextItemName.setText(bundle.getString(NAME));
        editTextItemStock.setText(((Integer) bundle.getInt(STOCK)).toString());
        editTextConsumptionRate.setText(((Integer) bundle.getInt(CONSUMPTION_RATE)).toString());
        editTextMinimumPurchace.setText(((Integer) bundle.getInt(MINIMUM_PURCHASE_QUANTITY)).toString());

        return fragmentView;
    }

    public interface FragmentActivityCommunicator{
        void refreshFragmentMainView(Integer position);
    }
}
