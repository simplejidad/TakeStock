package com.santiagogil.takestock.view.item_detail;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
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
import com.santiagogil.takestock.model.pojos.Consumption;
import com.santiagogil.takestock.util.DatabaseHelper;
import com.santiagogil.takestock.model.pojos.Item;
import com.santiagogil.takestock.util.SharedElementTransition;

public class FragmentItemDetail extends Fragment {

    public static FragmentItemDetail provideFragment(Item item, Integer position){
        Bundle bundle = new Bundle();
        bundle.putInt(FragmentItemDetail.POSITION, position);
        bundle.putString(DatabaseHelper.ID, item.getID());

        FragmentItemDetail fragmentItemDetail = new FragmentItemDetail();

        fragmentItemDetail.setArguments(bundle);

        return fragmentItemDetail;
    }

    private FragmentActivityCommunicator fragmentActivityCommunicator;
    private TextView textViewItemName;
    private TextView textViewItemStock;
    private TextView textViewMinimumPurchace;
    private TextView textViewConsumptionRate;
    private TextView textViewItemIndependence;
    private View fragmentView;
    private Button deleteButton;
    private Button editButton;
    private Button backButton;
    private RecyclerView recyclerView;
    private ConsumptionRecyclerAdapter consumptionRecyclerAdapter;
    private ConsumptionsController consumptionsController;
    private ItemsController itemController;
    private Item item;
    private Bundle bundle;

    public static final String POSITION = "position";
    public static final String TRANSITION_ITEM_NAME = "tin";
    public static final String TRANSITION_ITEM_STOCK = "tis";
    public static final String TRANSITION_ITEM_INDEPENDENCE = "tii";

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            textViewItemName.setTransitionName(FragmentItemDetail.TRANSITION_ITEM_NAME+item.getID().trim().toLowerCase());
            textViewItemStock.setTransitionName(FragmentItemDetail.TRANSITION_ITEM_STOCK+item.getID().trim().toLowerCase());
            textViewItemIndependence.setTransitionName(FragmentItemDetail.TRANSITION_ITEM_INDEPENDENCE+item.getID().trim().toLowerCase());
        }
        super.onViewCreated(view, savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentView = inflater.inflate(R.layout.fragment_item_detail, container, false);

        bundle = getArguments();
        itemController = new ItemsController();
        item = itemController.getItemFromLocalDatabase(getContext(), bundle.getString(DatabaseHelper.ID));

        textViewItemName = (TextView) fragmentView.findViewById(R.id.text_view_item_name);
        textViewItemStock = (TextView) fragmentView.findViewById(R.id.text_view_item_stock);
        textViewConsumptionRate = (TextView) fragmentView.findViewById(R.id.textViewConsumptionRate);
        textViewMinimumPurchace = (TextView) fragmentView.findViewById(R.id.textViewMinimumPurchaceAmmount);
        textViewItemIndependence = (TextView) fragmentView.findViewById(R.id.text_view_independence);
        backButton = (Button) fragmentView.findViewById(R.id.buttonBack);
        deleteButton = (Button) fragmentView.findViewById(R.id.buttonDeleteItem);
        editButton = (Button) fragmentView.findViewById(R.id.buttonEditItem);

        updateFieldsWithItemDetails();

        loadLayoutComponents();

        setOnClickListeners();


        loadRecyclerView();


        return fragmentView;
    }

    private void updateFieldsWithItemDetails() {

        textViewItemName.setText(item.getName());
        textViewItemStock.setText(item.getStock().toString());
        textViewMinimumPurchace.setText(item.getMinimumPurchaceQuantity().toString());
        textViewConsumptionRate.setText(item.getConsumptionRate().toString());
        textViewItemIndependence.setText(item.getIndependence().toString());
        if(item.getActive()){
            deleteButton.setText("DELETE");
        } else {
            deleteButton.setText("RESTORE");
        }

    }

    private void loadRecyclerView() {

        recyclerView = (RecyclerView) fragmentView.findViewById(R.id.recyclerViewConsumptions);
        consumptionRecyclerAdapter = new ConsumptionRecyclerAdapter(getContext(), new OnConsumptionDeletedListener());
        consumptionsController = new ConsumptionsController();
        consumptionRecyclerAdapter.setConsumptionList(consumptionsController.sortedItemConsumptionList(getContext(), bundle.getString(DatabaseHelper.ID)));
        recyclerView.setAdapter(consumptionRecyclerAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
    }

    private void setOnClickListeners() {

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ItemsController itemsController = new ItemsController();
                    itemsController.toggleItemIsActiveInDatabases(getContext(), item.getID());
                    item.setActive(!item.getActive());
                    if (item.getActive()){
                    Toast.makeText(getContext(), "Item Restored", Toast.LENGTH_SHORT).show();
                    } else{
                        Toast.makeText(getContext(), "Item Deleted", Toast.LENGTH_SHORT).show();
                    }
                    updateFieldsWithItemDetails();
                }
            });


        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                fragmentActivityCommunicator.goToEditItemActivity(getArguments());

            }
        });
    }

    private void loadLayoutComponents() {




    }

    public interface FragmentActivityCommunicator{
        void goToEditItemActivity(Bundle bundle);
    }

    public class OnConsumptionDeletedListener{

        public void onConsumptionDeleted(Consumption consumption) {
            ConsumptionsController consumptionsController = new ConsumptionsController();
            consumptionsController.deleteConsumption(getContext(), consumption);
            consumptionRecyclerAdapter.setConsumptionList(consumptionsController.getItemConsumptionList(getContext(), bundle.getString(DatabaseHelper.ID)));
            consumptionRecyclerAdapter.notifyDataSetChanged();

            item = itemController.getItemFromLocalDatabase(getContext(), bundle.getString(DatabaseHelper.ID));

            updateFieldsWithItemDetails();


        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.fragmentActivityCommunicator = (FragmentActivityCommunicator) context;

    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        postponeEnterTransition();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setSharedElementEnterTransition(new SharedElementTransition());
        }
        setSharedElementReturnTransition(null);
    }

}
