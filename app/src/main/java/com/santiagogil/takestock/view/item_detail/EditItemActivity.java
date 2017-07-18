package com.santiagogil.takestock.view.item_detail;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.santiagogil.takestock.R;
import com.santiagogil.takestock.controller.ItemsController;
import com.santiagogil.takestock.util.DatabaseHelper;
import com.santiagogil.takestock.model.pojos.Item;

public class EditItemActivity extends AppCompatActivity {

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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.fragment_edit_item);

        Intent intent = getIntent();
        final Bundle bundle = intent.getExtras();

        itemsController = new ItemsController();
        item = itemsController.getItemFromLocalDatabase(EditItemActivity.this, bundle.getString(DatabaseHelper.ID));

        editTextItemName = (EditText) findViewById(R.id.edit_text_item_name);
        editTextItemStock = (EditText) findViewById(R.id.editTextStock);
        editTextConsumptionRate = (EditText) findViewById(R.id.editTextConsumptionRate);
        editTextMinimumPurchace = (EditText) findViewById(R.id.editTextMinimumPurchaceAmmount);
        cancelButton = (Button) findViewById(R.id.buttonCancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        saveButton = (Button) findViewById(R.id.buttonSave);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                updateItemDetails();

                onBackPressed();
            }
        });

        editTextItemName.setText(item.getName());
        editTextItemStock.setText(item.getStock().toString());
        editTextConsumptionRate.setText(item.getConsumptionRate().toString());
        editTextMinimumPurchace.setText(item.getMinimumPurchaceQuantity().toString());

    }



    public void updateItemDetails (){

        String updatedItemName = editTextItemName.getText().toString();
        Integer updatedItemStock = tryParse(editTextItemStock.getText().toString());
        Integer updatedItemMinimumPurchaceQuantity = tryParse(editTextMinimumPurchace.getText().toString());
        Integer updatedItemConsumptionRate = tryParse(editTextConsumptionRate.getText().toString());
        Boolean updatedActiveStatus = true;

        ItemsController itemsController = new ItemsController();
        itemsController.updateItemDetails(EditItemActivity.this, item.getID(), updatedItemName, updatedItemStock, updatedItemConsumptionRate, updatedItemMinimumPurchaceQuantity, updatedActiveStatus);

    }

    public static Integer tryParse(String text) {
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }
}
