package com.santiagogil.takestock.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.santiagogil.takestock.R;
import com.santiagogil.takestock.controller.ConsumptionsController;
import com.santiagogil.takestock.controller.ItemsController;
import com.santiagogil.takestock.model.daos.DatabaseHelper;
import com.santiagogil.takestock.model.pojos.Item;

public class FragmentItemDetail extends Fragment {

    public static FragmentItemDetail provideFragment(Item item, Integer position){
        Bundle bundle = new Bundle();
        bundle.putInt(FragmentItemDetail.POSITION, position);
        bundle.putString(DatabaseHelper.ID, item.getID());
        bundle.putString(DatabaseHelper.NAME, item.getName());
        bundle.putInt(DatabaseHelper.STOCK, item.getStock());
        bundle.putInt(DatabaseHelper.MINIMUMPURCHACEQUANTITY, item.getMinimumPurchaceQuantity());
        bundle.putInt(DatabaseHelper.CONSUMPTIONRATE, item.getConsumptionRate());

        FragmentItemDetail fragmentItemDetail = new FragmentItemDetail();

        fragmentItemDetail.setArguments(bundle);

        return fragmentItemDetail;
    }

    private TextView textViewItemName;
    private TextView textViewItemStock;
    private TextView textViewMinimumPurchace;
    private TextView textViewConsumptionRate;
    private View fragmentView;
    private Button deleteButton;
    private Button editButton;
    private Button backButton;
    private RecyclerView recyclerView;
    private ConsumptionRecyclerAdapter consumptionRecyclerAdapter;
    private ConsumptionsController consumptionsController;

    static final String POSITION = "position";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentView = inflater.inflate(R.layout.fragment_item_detail, container, false);

        final Bundle bundle = getArguments();

        textViewItemName = (TextView) fragmentView.findViewById(R.id.textViewItemName);
        textViewItemStock = (TextView) fragmentView.findViewById(R.id.textViewStock);
        textViewConsumptionRate = (TextView) fragmentView.findViewById(R.id.textViewConsumptionRate);
        textViewMinimumPurchace = (TextView) fragmentView.findViewById(R.id.textViewMinimumPurchaceAmmount);
        backButton = (Button) fragmentView.findViewById(R.id.buttonBack);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });
        deleteButton = (Button) fragmentView.findViewById(R.id.buttonDeleteItem);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ItemsController itemsController = new ItemsController();
                itemsController.deleteItemFromDatabases(getContext(), bundle.getLong(DatabaseHelper.ID));
                Toast.makeText(getContext(), "Item Deleted", Toast.LENGTH_SHORT).show();
                FragmentActivityCommunicator fragmentActivityCommunicator = (FragmentActivityCommunicator) getActivity();
                fragmentActivityCommunicator.refreshFragmentMainView(bundle.getInt(POSITION));
            }
        });
        editButton = (Button) fragmentView.findViewById(R.id.buttonEditItem);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FragmentActivityCommunicator fragmentActivityCommunicator = (FragmentActivityCommunicator) getActivity();
                fragmentActivityCommunicator.showFragmentEditItem(getArguments());

            }
        });



        textViewItemName.setText(bundle.getString(DatabaseHelper.NAME));
        textViewItemStock.setText(((Integer) bundle.getInt(DatabaseHelper.STOCK)).toString());
        textViewMinimumPurchace.setText(((Integer) bundle.getInt(DatabaseHelper.MINIMUMPURCHACEQUANTITY)).toString());
        textViewConsumptionRate.setText(((Integer) bundle.getInt(DatabaseHelper.CONSUMPTIONRATE)).toString());


        recyclerView = (RecyclerView) fragmentView.findViewById(R.id.recyclerViewConsumptions);
        consumptionRecyclerAdapter = new ConsumptionRecyclerAdapter(getContext());
        consumptionsController = new ConsumptionsController();
        consumptionRecyclerAdapter.setConsumptionList(consumptionsController.getItemConsumptionList(getContext(), bundle.getString(DatabaseHelper.ID)));
        recyclerView.setAdapter(consumptionRecyclerAdapter);

        return fragmentView;
    }

    public interface FragmentActivityCommunicator{
        void refreshFragmentMainView(Integer position);
        void showFragmentEditItem(Bundle bundle);
    }
}
