package com.santiagogil.takestock.view.activity;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.santiagogil.takestock.R;
import com.santiagogil.takestock.view.DialogAddItem;
import com.santiagogil.takestock.view.fragment.FragmentItemListsViewPager;

public class ItemsListsActivity extends AppCompatActivity {

    private FirebaseAuth fAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private NavigationView navigationView;
    private FragmentItemListsViewPager fragmentItemListsViewPager;
    private String filter = "";
    private Toolbar toolbar;
    private EditText toolbarEditText;
    private BottomNavigationView bottomNavigationView;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lists);

        toolbar =  findViewById(R.id.toolbar);
        toolbarEditText =  toolbar.findViewById(R.id.toolbar_edit_text_search);

        bottomNavigationView =  findViewById(R.id.bottom_navigation_view);
        bottomNavigationView.setPadding(0,0,0,0);

        bottomNavigationView.setMeasureAllChildren(false);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_add_item:
                        DialogAddItem dialogAddItem = new DialogAddItem();
                        //dialogAddItem.setCommunicator(MainActivityCommunicator.this);
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
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}
