package com.Senior.Faff.Fragment.SearchNearby;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.Senior.Faff.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class Search_Fragment extends Fragment {


    public Search_Fragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root =  inflater.inflate(R.layout.fragment_search_, container, false);


        return root;
    }

}
