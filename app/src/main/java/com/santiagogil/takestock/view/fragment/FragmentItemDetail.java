package com.santiagogil.takestock.view.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.santiagogil.takestock.R;
import com.santiagogil.takestock.controller.ConsumptionsController;
import com.santiagogil.takestock.controller.ItemsController;
import com.santiagogil.takestock.model.pojos.Item;
import com.santiagogil.takestock.util.DatabaseHelper;
import com.santiagogil.takestock.view.adapter.ConsumptionsAndPurchacesViewPagerAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.view.View.VISIBLE;

public class FragmentItemDetail extends Fragment implements SimpleRecyclerFragment.FragmentRecyclerToFragmentCommunicator{

    public static FragmentItemDetail provideFragment(Item item, Integer position){
        Bundle bundle = new Bundle();
        bundle.putInt(FragmentItemDetail.POSITION, position);
        bundle.putString(DatabaseHelper.ID, item.getID());

        FragmentItemDetail fragmentItemDetail = new FragmentItemDetail();

        fragmentItemDetail.setArguments(bundle);

        return fragmentItemDetail;
    }

    private String itemID;
    private Item item;
    private Context context;
    private ConsumptionsAndPurchacesViewPagerAdapter consumptionsAndPurchacesViewPagerAdapter;

    @BindView(R.id.tabs) TabLayout tabLayout;
    @BindView(R.id.text_view_item_price)  TextView textViewItemPrice ;
    @BindView(R.id.text_view_item_name) TextView textViewItemName;
    @BindView(R.id.text_view_item_stock) TextView textViewItemStock;
    @BindView(R.id.textViewConsumptionRate) TextView textViewConsumptionRate;
    @BindView(R.id.textViewMinimumPurchaceAmmount)TextView textViewMinimumPurchace;
    @BindView(R.id.text_view_independence) TextView textViewItemIndependence;
    @BindView(R.id.buttonAdd) TextView buttonStockAdd;
    @BindView(R.id.buttonSubtract) ImageView buttonStockSubtract;
    @BindView(R.id.button_cart_subtract) ImageView buttonCartSubtract;
    @BindView(R.id.button_cart_add) ImageView buttonCartAdd;
    @BindView(R.id.button_cart_to_stock) TextView buttonCartToStock;
    @BindView(R.id.linear_layout_action_buttons) LinearLayout linearLayoutActionButtons;
    @BindView(R.id.view_pager_consumptions_purchaces) ViewPager viewPager;
    @BindView(R.id.fragment_item_detail_hidden_edit_text_name) EditText editTextName;
    @BindView(R.id.fragment_item_detail_hidden_edit_text_consumption_rate) EditText editTextCosumptionRate;
    @BindView(R.id.fragment_item_detail_hidden_edit_text_minimum_purchace) EditText editTextMinimumPurchace ;
    @BindView(R.id.fragment_item_detail_hidden_edit_text_price) EditText editTextPrice ;
    @BindView(R.id.fragment_item_detail_hidden_edit_text_stock) EditText editTextStock ;
    @BindView(R.id.fragment_item_detail_view_switcher_name) ViewSwitcher viewSwitcherConsumptionName;
    @BindView(R.id.fragment_item_detail_view_switcher_consumption_rate) ViewSwitcher viewSwitcherConsumptionRate;
    @BindView(R.id.fragment_item_detail_view_switcher_minimum_purchace) ViewSwitcher viewSwitcherMinimumPurchace;
    @BindView(R.id.fragment_item_detail_view_switcher_price) ViewSwitcher viewSwitcherPrice;
    @BindView(R.id.fragment_item_detail_view_switcher_stock) ViewSwitcher viewSwitcherStock;
    @BindView(R.id.button_cancel) ImageView cancelButton;
    @BindView(R.id.button_save) ImageView saveButton;

    public static final String POSITION = "position";
    public static final String TRANSITION_ITEM_NAME = "TransitionItemName";
    public static final String TRANSITION_ITEM_STOCK = "TransitionItemStock";
    public static final String TRANSITION_ITEM_INDEPENDENCE = "";

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        ButterKnife.bind(this,view);
        itemID = getArguments().getString(DatabaseHelper.ID);

        loadViewPager();

        updateFieldsWithItemDetails();

        setOnClickListeners();

        // TODO: barra indicadora de stock

        textViewItemName.setTransitionName(FragmentItemDetail.TRANSITION_ITEM_NAME+item.getID().trim().toLowerCase());
        textViewItemStock.setTransitionName(FragmentItemDetail.TRANSITION_ITEM_STOCK+item.getID().trim().toLowerCase());
        textViewItemIndependence.setTransitionName(FragmentItemDetail.TRANSITION_ITEM_INDEPENDENCE+item.getID().trim().toLowerCase());

        super.onViewCreated(view, savedInstanceState);

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        ((InputMethodManager) Objects.requireNonNull(getActivity().getSystemService(Context.INPUT_METHOD_SERVICE)))
                .hideSoftInputFromWindow(Objects.requireNonNull(getView()).getWindowToken(), 0);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_item_detail, container, false);
    }

    @SuppressLint("SetTextI18n")
    private void updateFieldsWithItemDetails() {

        updateItem();

        textViewItemName.setText(item.getName());
        textViewItemStock.setText(item.getStock().toString());
        textViewMinimumPurchace.setText(item.getMinimumPurchaceQuantity().toString());
        textViewConsumptionRate.setText(item.getConsumptionRate().toString());
        textViewItemIndependence.setText(item.getRoundedIndependence());
        textViewItemIndependence.setCompoundDrawablesRelativeWithIntrinsicBounds(item.getIndependenceEmoticon(), 0 , 0 ,0);
        textViewItemPrice.setText(item.getPrice().toString());

        setDrawablesForButtons(item);
        setTextsForButtons(item);

    }

    private void loadViewPager() {

        List<SimpleRecyclerFragment> fragmentList = new ArrayList();
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
        tabLayout.setupWithViewPager(viewPager);
    }

    private void setOnClickListeners() {

        /*editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                linearLayoutActionButtons.setVisibility(View.INVISIBLE);

                viewSwitcherConsumptionName.showNext();
                viewSwitcherConsumptionRate.showNext();
                viewSwitcherMinimumPurchace.showNext();
                viewSwitcherPrice.showNext();
                viewSwitcherStock.showNext();
                cancelButton.setVisibility(VISIBLE);
                saveButton.setVisibility(VISIBLE);

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
            }
        });*/

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                viewSwitcherConsumptionName.showNext();
                viewSwitcherConsumptionRate.showNext();
                viewSwitcherMinimumPurchace.showNext();
                viewSwitcherPrice.showNext();
                viewSwitcherStock.showNext();
                cancelButton.setVisibility(View.GONE);
                saveButton.setVisibility(View.GONE);

                linearLayoutActionButtons.setVisibility(View.VISIBLE);

                hideKeyboard((Activity) context);
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                updateItemDetails();
                updateItem();

                textViewItemName.setText(editTextName.getText().toString());
                textViewConsumptionRate.setText(editTextCosumptionRate.getText().toString());
                textViewMinimumPurchace.setText(editTextMinimumPurchace.getText().toString());
                textViewItemPrice.setText(editTextPrice.getText().toString());
                textViewItemStock.setText(editTextStock.getText().toString());
                textViewItemIndependence.setText(item.getIndependence().toString());

                linearLayoutActionButtons.setVisibility(VISIBLE);
                buttonStockAdd.setText(editTextMinimumPurchace.getText().toString());

                viewSwitcherConsumptionName.showNext();
                viewSwitcherConsumptionRate.showNext();
                viewSwitcherMinimumPurchace.showNext();
                viewSwitcherPrice.showNext();
                viewSwitcherStock.showNext();
                cancelButton.setVisibility(View.GONE);
                saveButton.setVisibility(View.GONE);

                hideKeyboard((Activity) context);
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
                updateFieldsWithItemDetails();
            }
        });
    }

    private void setDrawablesForButtons(Item item){
        if(item.getStock() == 0) {
            buttonStockSubtract.setImageResource(R.drawable.ic_home_minus_disabled);
        } else{
            buttonStockSubtract.setImageResource(R.drawable.ic_home_minus_enabled);
        }
        if(item.getCart() == 0) {
            buttonCartToStock.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_up_to, 0, 0 , 0);
            buttonCartToStock.setVisibility(View.GONE);
            buttonCartSubtract.setImageResource(R.drawable.ic_cart_substract_disabled);
        } else {
            buttonCartToStock.setVisibility(VISIBLE);
            buttonCartToStock.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_up_to, 0, R.drawable.ic_shopping_cart_black_24dp, 0);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
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

    @SuppressLint("SetTextI18n")
    private void setTextsForButtons(Item item){
        buttonStockAdd.setText(item.getMinimumPurchaceQuantity().toString());
        buttonCartToStock.setText(item.getCart().toString());
    }

    private void updateItem(){
        ItemsController itemController = new ItemsController();
        item = itemController.getItemFromLocalDatabase(getContext(), itemID);
    }

    public void updateItemDetails (){
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

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        Objects.requireNonNull(imm).hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public void onItemDeleted(){
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

    public Item getItem() {
        return item;
    }
}
