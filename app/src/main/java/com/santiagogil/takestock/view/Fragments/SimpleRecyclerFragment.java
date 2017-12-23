package com.santiagogil.takestock.view.Fragments;


import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.santiagogil.takestock.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class SimpleRecyclerFragment extends Fragment {

    public static final String ITEMID = "ItemID";
    public static final String TITLE = "Title";

    public SimpleRecyclerFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_simple_recycler, container, false);
    }

    public String getTitle(){
        return getArguments().getString(TITLE);
    }

}
