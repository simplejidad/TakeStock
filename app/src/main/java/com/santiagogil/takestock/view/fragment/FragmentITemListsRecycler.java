package com.santiagogil.takestock.view.fragment;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.santiagogil.takestock.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentITemListsRecycler extends Fragment {


    public FragmentITemListsRecycler() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_lists, container, false);
    }

}
