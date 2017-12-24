package com.santiagogil.takestock.view.Fragments;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.santiagogil.takestock.R;
import com.santiagogil.takestock.controller.ConsumptionsController;
import com.santiagogil.takestock.controller.ItemsController;
import com.santiagogil.takestock.util.DatabaseHelper;
import com.santiagogil.takestock.model.pojos.Item;
import com.santiagogil.takestock.view.Adapters.ConsumptionRecyclerAdapter;
import com.santiagogil.takestock.view.Adapters.ConsumptionsAndPurchacesViewPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class FragmentItemDetail extends Fragment implements SimpleRecyclerFragment.FragmentRecyclerToFragmentCommunicator{

    public static FragmentItemDetail provideFragment(Item item, Integer position){
        Bundle bundle = new Bundle();
        bundle.putInt(FragmentItemDetail.POSITION, position);
        bundle.putString(DatabaseHelper.ID, item.getID());

        FragmentItemDetail fragmentItemDetail = new FragmentItemDetail();

        fragmentItemDetail.setArguments(bundle);

        return fragmentItemDetail;
    }

    private TextView textViewItemName;
    private TextView textViewItemStock;
    private TextView textViewMinimumPurchace;
    private TextView textViewConsumptionRate;
    private TextView textViewItemIndependence;
    private TextView textViewItemPrice;
    private View fragmentView;
    private ImageButton deleteButton;
    private ImageButton editButton;
    private ImageButton backButton;
    private ViewPager viewPager;
    private RecyclerView recyclerView;
    private ConsumptionRecyclerAdapter consumptionRecyclerAdapter;
    private ConsumptionsController consumptionsController;
    private ItemsController itemController;
    private String itemID;
    private Item item;
    private Context context;
    private List<SimpleRecyclerFragment> fragmentList;
    private ConsumptionsAndPurchacesViewPagerAdapter consumptionsAndPurchacesViewPagerAdapter;

    private ImageButton buttonStockSubtract;
    private Button buttonStockAdd;
    private Button buttonCartToStock;
    private ImageButton buttonCartSubtract;
    private ImageButton buttonCartAdd;

    public static final String POSITION = "position";
    public static final String TRANSITION_ITEM_NAME = "TransitionItemName";
    public static final String TRANSITION_ITEM_STOCK = "TransitionItemStock";
    public static final String TRANSITION_ITEM_INDEPENDENCE = "";

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            textViewItemName.setTransitionName(FragmentItemDetail.TRANSITION_ITEM_NAME+item.getID().trim().toLowerCase());
            textViewItemStock.setTransitionName(FragmentItemDetail.TRANSITION_ITEM_STOCK+item.getID().trim().toLowerCase());
            textViewItemIndependence.setTransitionName(FragmentItemDetail.TRANSITION_ITEM_INDEPENDENCE+item.getID().trim().toLowerCase());


        }
        super.onViewCreated(view, savedInstanceState);

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        ((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE))
                .hideSoftInputFromWindow(getView().getWindowToken(), 0);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentView = inflater.inflate(R.layout.fragment_item_detail, container, false);

        itemID = getArguments().getString(DatabaseHelper.ID);

        loadAssets(fragmentView);

        loadViewPager();

        updateFieldsWithItemDetails();

        setOnClickListeners();

        setBackgroundColor(fragmentView, item.getIndependence());

        return fragmentView;
    }

    private void setBackgroundColor(View fragmentView, Integer independence) {

        if(independence == 0){
            fragmentView.setBackgroundColor(ContextCompat.getColor(context, R.color.out_of_stock));
        } else if(independence < 30){
            fragmentView.setBackgroundColor(ContextCompat.getColor(context, R.color.some_independence));
        } else if(independence >=30){
            fragmentView.setBackgroundColor(ContextCompat.getColor(context, R.color.full_independence));
        }
    }

    private void updateFieldsWithItemDetails() {

        updateItem();

        textViewItemName.setText(item.getName());
        textViewItemStock.setText(item.getStock().toString());
        textViewMinimumPurchace.setText(item.getMinimumPurchaceQuantity().toString());
        textViewConsumptionRate.setText(item.getConsumptionRate().toString());
        textViewItemIndependence.setText(item.getRoundedIndependence());
        textViewItemIndependence.setCompoundDrawablesRelativeWithIntrinsicBounds(item.getIndependenceEmoticon(), 0 , 0 ,0);
        textViewItemPrice.setText(item.getPrice().toString());
        if(item.getActive()){
            deleteButton.setImageResource(R.drawable.ic_delete_black_24dp);
        } else {
            deleteButton.setImageResource(R.drawable.ic_restore_black_24dp);
        }

        setDrawablesForButtons(item);
        setTextsForButtons(item);;

    }

    private void loadViewPager() {

        viewPager = (ViewPager) fragmentView.findViewById(R.id.view_pager_consumptions_purchaces);
        fragmentList = new ArrayList();
        consumptionsAndPurchacesViewPagerAdapter = new ConsumptionsAndPurchacesViewPagerAdapter(getChildFragmentManager(), fragmentList);


        ConsumptionRecyclerFragment consumptionRecyclerFragment = new ConsumptionRecyclerFragment();
        Bundle bundleConsumptions = new Bundle();
        bundleConsumptions.putString(SimpleRecyclerFragment.ITEMID, itemID);
        bundleConsumptions.putString(SimpleRecyclerFragment.TITLE, "Consumptions");
        consumptionRecyclerFragment.setArguments(bundleConsumptions);
        consumptionRecyclerFragment.setFragmentRecyclerToFragmentCommunicator(this);

        PurchacesRecyclerFragment purchacesRecyclerFragment = new PurchacesRecyclerFragment();
        Bundle bundlePurchaces = new Bundle();
        bundlePurchaces.putString(SimpleRecyclerFragment.ITEMID, itemID);
        bundlePurchaces.putString(SimpleRecyclerFragment.TITLE, "Purchaces");
        purchacesRecyclerFragment.setArguments(bundlePurchaces);
        purchacesRecyclerFragment.setFragmentRecyclerToFragmentCommunicator(this);


        fragmentList.add(consumptionRecyclerFragment);
        fragmentList.add(purchacesRecyclerFragment);

        viewPager.setAdapter(consumptionsAndPurchacesViewPagerAdapter);

        TabLayout tabLayout = (TabLayout) fragmentView.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

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

                ViewSwitcher viewSwitcherConsumptionName = (ViewSwitcher) fragmentView.findViewById(R.id.fragment_item_detail_view_switcher_name);
                ViewSwitcher viewSwitcherConsumptionRate = (ViewSwitcher) fragmentView.findViewById(R.id.fragment_item_detail_view_switcher_consumption_rate);
                ViewSwitcher viewSwitcherMinimumPurchace = (ViewSwitcher) fragmentView.findViewById(R.id.fragment_item_detail_view_switcher_minimum_purchace);
                ViewSwitcher viewSwitcherPrice = (ViewSwitcher) fragmentView.findViewById(R.id.fragment_item_detail_view_switcher_price);
                ViewSwitcher viewSwitcherStock = (ViewSwitcher) fragmentView.findViewById(R.id.fragment_item_detail_view_switcher_stock);
                ViewSwitcher viewSwitcherEdit = (ViewSwitcher) fragmentView.findViewById(R.id.fragment_item_detail_view_switcher_edit);

                viewSwitcherConsumptionName.showNext();
                viewSwitcherConsumptionRate.showNext();
                viewSwitcherMinimumPurchace.showNext();
                viewSwitcherPrice.showNext();
                viewSwitcherStock.showNext();
                viewSwitcherEdit.showNext();

                EditText editTextName = (EditText) fragmentView.findViewById(R.id.fragment_item_detail_hidden_edit_text_name);
                EditText editTextCosumptionRate = (EditText) fragmentView.findViewById(R.id.fragment_item_detail_hidden_edit_text_consumption_rate);
                EditText editTextMinimumPurchace = (EditText) fragmentView.findViewById(R.id.fragment_item_detail_hidden_edit_text_minimum_purchace);
                EditText editTextPrice = (EditText) fragmentView.findViewById(R.id.fragment_item_detail_hidden_edit_text_price);
                EditText editTextStock = (EditText) fragmentView.findViewById(R.id.fragment_item_detail_hidden_edit_text_stock);

                editTextName.setHint(textViewItemName.getText());
                editTextCosumptionRate.setHint(textViewConsumptionRate.getText());
                editTextMinimumPurchace.setHint(textViewMinimumPurchace.getText());
                editTextPrice.setHint(textViewItemPrice.getText());
                editTextStock.setHint(textViewItemStock.getText());

                editTextName.setText(textViewItemName.getText());
                editTextCosumptionRate.setText(textViewConsumptionRate.getText());
                editTextMinimumPurchace.setText(textViewMinimumPurchace.getText());
                editTextPrice.setText(textViewItemPrice.getText());
                editTextStock.setText(textViewItemStock.getText());

                ImageButton saveButton = (ImageButton) fragmentView.findViewById(R.id.button_save);
                ImageButton cancelButton = (ImageButton) fragmentView.findViewById(R.id.button_cancel);

                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ViewSwitcher viewSwitcherConsumptionName = (ViewSwitcher) fragmentView.findViewById(R.id.fragment_item_detail_view_switcher_name);
                        ViewSwitcher viewSwitcherConsumptionRate = (ViewSwitcher) fragmentView.findViewById(R.id.fragment_item_detail_view_switcher_consumption_rate);
                        ViewSwitcher viewSwitcherMinimumPurchace = (ViewSwitcher) fragmentView.findViewById(R.id.fragment_item_detail_view_switcher_minimum_purchace);
                        ViewSwitcher viewSwitcherPrice = (ViewSwitcher) fragmentView.findViewById(R.id.fragment_item_detail_view_switcher_price);
                        ViewSwitcher viewSwitcherStock = (ViewSwitcher) fragmentView.findViewById(R.id.fragment_item_detail_view_switcher_stock);
                        ViewSwitcher viewSwitcherEdit = (ViewSwitcher) fragmentView.findViewById(R.id.fragment_item_detail_view_switcher_edit);

                        viewSwitcherConsumptionName.showNext();
                        viewSwitcherConsumptionRate.showNext();
                        viewSwitcherMinimumPurchace.showNext();
                        viewSwitcherPrice.showNext();
                        viewSwitcherStock.showNext();
                        viewSwitcherEdit.showNext();
                    }
                });

                saveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        updateItemDetails();
                        ViewSwitcher viewSwitcherConsumptionName = (ViewSwitcher) fragmentView.findViewById(R.id.fragment_item_detail_view_switcher_name);
                        ViewSwitcher viewSwitcherConsumptionRate = (ViewSwitcher) fragmentView.findViewById(R.id.fragment_item_detail_view_switcher_consumption_rate);
                        ViewSwitcher viewSwitcherMinimumPurchace = (ViewSwitcher) fragmentView.findViewById(R.id.fragment_item_detail_view_switcher_minimum_purchace);
                        ViewSwitcher viewSwitcherPrice = (ViewSwitcher) fragmentView.findViewById(R.id.fragment_item_detail_view_switcher_price);
                        ViewSwitcher viewSwitcherStock = (ViewSwitcher) fragmentView.findViewById(R.id.fragment_item_detail_view_switcher_stock);
                        ViewSwitcher viewSwitcherEdit = (ViewSwitcher) fragmentView.findViewById(R.id.fragment_item_detail_view_switcher_edit);

                        viewSwitcherConsumptionName.showNext();
                        viewSwitcherConsumptionRate.showNext();
                        viewSwitcherMinimumPurchace.showNext();
                        viewSwitcherPrice.showNext();
                        viewSwitcherStock.showNext();
                        viewSwitcherEdit.showNext();

                        EditText editTextName = (EditText) fragmentView.findViewById(R.id.fragment_item_detail_hidden_edit_text_name);
                        EditText editTextCosumptionRate = (EditText) fragmentView.findViewById(R.id.fragment_item_detail_hidden_edit_text_consumption_rate);
                        EditText editTextMinimumPurchace = (EditText) fragmentView.findViewById(R.id.fragment_item_detail_hidden_edit_text_minimum_purchace);
                        EditText editTextPrice = (EditText) fragmentView.findViewById(R.id.fragment_item_detail_hidden_edit_text_price);
                        EditText editTextStock = (EditText) fragmentView.findViewById(R.id.fragment_item_detail_hidden_edit_text_stock);


                        editTextName.setText(textViewItemName.getText());
                        editTextCosumptionRate.setText(textViewConsumptionRate.getText());
                        editTextMinimumPurchace.setText(textViewMinimumPurchace.getText());
                        editTextPrice.setText(textViewItemPrice.getText());
                        editTextStock.setText(textViewItemStock.getText());

                        textViewItemName.setText(editTextName.getText().toString());
                        textViewConsumptionRate.setText(editTextCosumptionRate.getText().toString());
                        textViewMinimumPurchace.setText(editTextMinimumPurchace.getText().toString());


                        textViewItemPrice.setText(editTextPrice.getText().toString());


                        textViewItemStock.setText(editTextStock.getText().toString());

                    }
                });

            }
        });

        buttonStockAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                increaseItemStock(item);
                consumptionsAndPurchacesViewPagerAdapter.onPurchacesUpdated();
                updateFieldsWithItemDetails();
            }
        });

        buttonStockSubtract.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                decreaseItemStock(item);
                consumptionsAndPurchacesViewPagerAdapter.onConsumptionsUpdated();
                updateFieldsWithItemDetails();
            }
        });

        buttonCartAdd.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                increaseCart(item);
                updateFieldsWithItemDetails();
            }
        });


        buttonCartSubtract.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                decreaseItemCart(item);
                updateFieldsWithItemDetails();
            }
        });

        buttonCartToStock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cartToStock(item);
                consumptionsAndPurchacesViewPagerAdapter.onPurchacesUpdated();
                updateFieldsWithItemDetails();;
            }
        });
    }

    private void setDrawablesForButtons(Item item){
        if(item.getCart() == 0) {

            buttonCartToStock.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_home_black_24dp, 0, R.drawable.ic_shopping_cart_empty , 0);
        } else {

            buttonCartToStock.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_home_black_24dp, 0, R.drawable.ic_shopping_cart_black_24dp, 0);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void loadAssets(View fragmentView){

        textViewItemPrice = (TextView) fragmentView.findViewById(R.id.text_view_item_price);
        textViewItemName = (TextView) fragmentView.findViewById(R.id.text_view_item_name);
        textViewItemStock = (TextView) fragmentView.findViewById(R.id.text_view_item_stock);
        textViewConsumptionRate = (TextView) fragmentView.findViewById(R.id.textViewConsumptionRate);
        textViewMinimumPurchace = (TextView) fragmentView.findViewById(R.id.textViewMinimumPurchaceAmmount);
        textViewItemIndependence = (TextView) fragmentView.findViewById(R.id.text_view_independence);
        backButton = (ImageButton) fragmentView.findViewById(R.id.buttonBack);
        deleteButton = (ImageButton) fragmentView.findViewById(R.id.buttonDeleteItem);
        editButton = (ImageButton) fragmentView.findViewById(R.id.buttonEditItem);
        buttonStockAdd = (Button) fragmentView.findViewById(R.id.buttonAdd);
        buttonStockSubtract = (ImageButton) fragmentView.findViewById(R.id.buttonSubtract);
        buttonCartSubtract = (ImageButton) fragmentView.findViewById(R.id.button_cart_subtract);
        buttonCartAdd = (ImageButton) fragmentView.findViewById(R.id.button_cart_add);
        buttonCartToStock = (Button) fragmentView.findViewById(R.id.button_cart_to_stock);

    }

    private void cartToStock(Item item) {

        if(item.getCart() > 0){

            ItemsController itemsController = new ItemsController();
            itemsController.cartToStock(context, item);
        } else {
            Toast.makeText(context, "Cart is Empty", Toast.LENGTH_SHORT).show();
        }

    }

    private void decreaseItemCart(Item item) {

        if(item.getCart() == 0){
            Toast.makeText(context, "Nothing left to remove", Toast.LENGTH_SHORT).show();
        } else {

            ItemsController itemsController = new ItemsController();
            itemsController.decreaseItemCart(context, item);
        }
    }

    private void increaseCart(Item item) {

        ItemsController itemsController = new ItemsController();
        itemsController.increaseItemCart(context, item);

    }

    private  void increaseItemStock(Item item){

        ItemsController itemsController = new ItemsController();
        itemsController.increaseItemStock(context, item);

    }

    private void decreaseItemStock(Item item){

        if(item.getStock() == 0){
            Toast.makeText(context, "Nothing left to consume", Toast.LENGTH_SHORT).show();
        } else {

            ItemsController itemsController = new ItemsController();
            itemsController.decreaseItemStock(context, item);
            ConsumptionsController consumptionsController = new ConsumptionsController();
            consumptionsController.addConsumptionToDatabases(context, item.getID());
            itemsController.updateItemConsumptionRate(context, item.getID());

        }
    }

    private void setTextsForButtons(Item item){

        buttonStockAdd.setText(item.getMinimumPurchaceQuantity().toString());
        buttonCartToStock.setText("<" + item.getCart());

    }

    private void updateItem(){

        itemController = new ItemsController();
        item = itemController.getItemFromLocalDatabase(getContext(), itemID);

    }

    public void updateItemDetails (){

        EditText editTextName = (EditText) fragmentView.findViewById(R.id.fragment_item_detail_hidden_edit_text_name);
        EditText editTextCosumptionRate = (EditText) fragmentView.findViewById(R.id.fragment_item_detail_hidden_edit_text_consumption_rate);
        EditText editTextMinimumPurchace = (EditText) fragmentView.findViewById(R.id.fragment_item_detail_hidden_edit_text_minimum_purchace);
        EditText editTextPrice = (EditText) fragmentView.findViewById(R.id.fragment_item_detail_hidden_edit_text_price);
        EditText editTextStock = (EditText) fragmentView.findViewById(R.id.fragment_item_detail_hidden_edit_text_stock);


        String updatedItemName = editTextName.getText().toString();
        Integer updatedItemStock = tryParse(editTextStock.getText().toString());
        Integer updatedItemMinimumPurchaceQuantity = tryParse(editTextMinimumPurchace.getText().toString());
        Integer updatedItemConsumptionRate = tryParse(editTextCosumptionRate.getText().toString());

        Double updatedPrice = Double.parseDouble(editTextPrice.getText().toString());

        Boolean updatedActiveStatus = item.getActive();

        ItemsController itemsController = new ItemsController();
        itemsController.updateItemDetails(
                getContext(), item.getID(), updatedItemName, updatedItemStock,
                updatedItemConsumptionRate, updatedItemMinimumPurchaceQuantity, updatedActiveStatus,
                updatedPrice, item.getCart());

    }

    public static Integer tryParse(String text) {
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Override
    public void onItemStockChanged() {
        updateFieldsWithItemDetails();
    }
}
