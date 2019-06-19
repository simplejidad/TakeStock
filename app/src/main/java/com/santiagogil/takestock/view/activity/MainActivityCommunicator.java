package com.santiagogil.takestock.view.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.santiagogil.takestock.R;
import com.santiagogil.takestock.controller.ConsumptionsController;
import com.santiagogil.takestock.controller.ItemsController;
import com.santiagogil.takestock.model.pojos.Behaviours.BehaviourGetItemList;
import com.santiagogil.takestock.model.pojos.Consumption;
import com.santiagogil.takestock.model.pojos.Item;
import com.santiagogil.takestock.util.FirebaseHelper;
import com.santiagogil.takestock.util.ResultListener;
import com.santiagogil.takestock.view.DialogAddItem;
import com.santiagogil.takestock.view.fragment.FragmentItemsViewPager;
import com.santiagogil.takestock.view.fragment.FragmentRecyclerItems;
import com.santiagogil.takestock.view.fragment.FragmentItemListsViewPager;

import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivityCommunicator extends AppCompatActivity implements FragmentRecyclerItems.FragmentActivityCommunicator, DialogAddItem.AddItemDialogCommunicator, FragmentItemsViewPager.Listener {

    public static final String NIGHT_MODE = "night mode";

    private FirebaseAuth fAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private NavigationView navigationView;
    private FragmentItemListsViewPager fragmentItemListsViewPager;
    private String filter = "";
    private Toolbar toolbar;
    private EditText toolbarEditText;
    private Context context;
    private FragmentItemsViewPager fragmentItemsViewPager;
    @BindView(R.id.bottom_navigation_view) BottomNavigationView bottomNavigationView;
    @BindView(R.id.buttonEditItem) ImageView toolbarEditItembutton;
    @BindView(R.id.buttonDeleteItem) ImageView toolbarDeleteItemButton;

    @OnClick(R.id.buttonDeleteItem)
    void deleteOrRestoreItem(){
        fragmentItemsViewPager.deleteItem();
    }

    protected void onCreate(Bundle savedInstanceState) {
        if (isNightModeEnabled()) {
            setTheme(R.style.MainActivityCommunicatorThemeDark);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this, this);

        context = this;

        toolbar = findViewById(R.id.toolbar);
        toolbarEditText = toolbar.findViewById(R.id.toolbar_edit_text_search);

        bottomNavigationView.setPadding(0,0,0,0);

        //addGroceriesListToCurrentUsers();

        bottomNavigationView.setMeasureAllChildren(false);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_add_item:
                        DialogAddItem dialogAddItem = new DialogAddItem();
                        dialogAddItem.setCommunicator(MainActivityCommunicator.this);
                        dialogAddItem.show(getFragmentManager(), null);
                        break;
                    case R.id.action_search:
                        toolbarEditText.setBackgroundColor(ContextCompat.getColor( context, R.color.icons_enabled));
                        toolbarEditText.setFocusableInTouchMode(true);
                        toolbarEditText.requestFocus();
                        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                        ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput(toolbarEditText, 0);
                        break;
                    case R.id.action_back:
                        onBackPressed();
                        break;

                }
                return true;
            }
        });


        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);


        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null) {
                    Intent intent = new Intent(MainActivityCommunicator.this, OnboardingActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            }
        };

        fAuth = FirebaseAuth.getInstance();

        if (fAuth.getCurrentUser() == null) {

            Intent intent = new Intent(MainActivityCommunicator.this, OnboardingActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);

        } else {

            navigationView =  findViewById(R.id.navigationView);
            NavigationViewListener navigationViewListener = new NavigationViewListener();
            navigationView.setNavigationItemSelectedListener(navigationViewListener);


            fragmentItemListsViewPager = new FragmentItemListsViewPager();
            Bundle bundle = new Bundle();
            bundle.putString(FragmentRecyclerItems.FILTER, filter);
            fragmentItemListsViewPager.setArguments(bundle);
            fragmentItemListsViewPager.setFragmentActivityCommunicator(MainActivityCommunicator.this);
            FragmentManager fragmentManager = this.getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_holder, fragmentItemListsViewPager);
            fragmentTransaction.commit();

            ConsumptionsController consumptionsController = new ConsumptionsController();
            consumptionsController.updateConsumptionsDatabase(this, new ResultListener<List<Consumption>>() {
                @Override
                public void finish(List<Consumption> result) {

                }
            });

            }
        }

    @Override
    public void onItemTouched (Item touchedItem, Integer touchedPosition, BehaviourGetItemList
    behaviourGetItemList, TextView textViewItemName, TextView textViewItemStock,
                               TextView textViewItemIndependence){

        fragmentItemsViewPager = new FragmentItemsViewPager();
        Bundle bundle = new Bundle();
        bundle.putSerializable(FragmentItemsViewPager.BEHAVIOURGETITEMLIST, behaviourGetItemList);
        bundle.putString(FragmentItemsViewPager.ITEMID, touchedItem.getID());
        bundle.putInt(FragmentItemsViewPager.POSITION, touchedPosition);
        fragmentItemsViewPager.setArguments(bundle);
        fragmentItemsViewPager.setListener(this);

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_holder, fragmentItemsViewPager);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

        togleToolBarItems();

    }

    @Override
    public void updateActionBarTitle(String title) {
        Objects.requireNonNull(getSupportActionBar()).setTitle(title);
    }

    @Override
        public void onBackPressed () {
            hideKeyboard(this);
            int fragments = getSupportFragmentManager().getBackStackEntryCount();
            if (fragments == 0) {
                moveTaskToBack(true);
            }
            toolbarEditText.setVisibility(View.VISIBLE);
            super.onBackPressed();
        }

    @Override
    public void addNewItem(String itemName) {


        ItemsController itemsController = new ItemsController();
        Item item = new Item(itemName);
        itemsController.addItemToDatabases(this, item);
        //TODO: check if item already exists
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_holder);
        getSupportFragmentManager().beginTransaction().detach(fragment).attach(fragment).commit();
    }

    @Override
    public void updateToolbarIcon(Item item) {
        if(item.getActive()){
            toolbarDeleteItemButton.setImageResource(R.drawable.ic_delete_black_24dp);
        } else {
            toolbarDeleteItemButton.setImageResource(R.drawable.ic_restore_black_24dp);
        }
    }

    private class NavigationViewListener implements NavigationView.OnNavigationItemSelectedListener {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.action_logout)
                    logout();
                if (item.getItemId() == R.id.changeTheme) {
                    SharedPreferences mPrefs =  PreferenceManager.getDefaultSharedPreferences(MainActivityCommunicator.this);
                    SharedPreferences.Editor editor = mPrefs.edit();
                    editor.putBoolean(NIGHT_MODE, !isNightModeEnabled());
                    editor.apply();
                    recreate();
                }
                return false;
            }
        }

    @Override
    protected void onStart () {
        super.onStart();
        fAuth.addAuthStateListener(authStateListener);
    }

    private void logout() {

        fAuth.signOut();
        Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        //This method is called when the up button is pressed. Just the pop back stack.
        onBackPressed();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_main_menu, menu);

        addListenerToSearchEditText();

        return true;
    }

    private void addListenerToSearchEditText() {
        EditText editTextSearch = toolbar.findViewById(R.id.toolbar_edit_text_search);
        editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filter = s.toString();
                fragmentItemListsViewPager.getArguments().putString(FragmentRecyclerItems.FILTER, filter);
                fragmentItemListsViewPager.getItemListsViewPagerAdapter().updateFragmentsWithFilter(filter);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void addGroceriesListToCurrentUsers() {

        final FirebaseHelper firebaseHelper = new FirebaseHelper();
        DatabaseReference databaseReference =  firebaseHelper.getUserDB();
        ItemsController itemsController = new ItemsController();
        List<Item> items = itemsController.getAllItems(context);
        for(Item item: items){

            databaseReference.child("Lists").child("Groceries").child("Items").child(item.getID()).setValue(item.getID());

        }

    }


    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        assert imm != null;
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public void togleToolBarItems(){
        toolbarEditText.setVisibility(View.GONE);
        toolbarDeleteItemButton.setVisibility(View.VISIBLE);
        toolbarEditItembutton.setVisibility(View.VISIBLE);
    }

    private boolean isNightModeEnabled() {
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(MainActivityCommunicator.this);
        return mPrefs.getBoolean(NIGHT_MODE, false);
    }
}


