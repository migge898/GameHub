package com.mioai.gamehub;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mioai.gamehub.R;


public class ThirdScreen extends Fragment
{


    MyRecyclerViewAdapter recAdapter;
    RecyclerView recyclerView;

    private String[] iconNames = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31", "32", "33", "34", "35", "36", "37", "38", "39", "40", "41", "42", "43", "44", "45", "46", "47", "48"};


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        //changing the Style of the Grid
        int spanCount = 3; // Number of columns
        int spacing = 50; // 50px
        boolean includeEdge = true;

        //Init the view and adapter
        View view = inflater.inflate(R.layout.fragment_second_screen, container, false);
        recyclerView = view.findViewById(R.id.rvIcons2);
        recyclerView.setLayoutManager(new GridLayoutManager(view.getContext(), spanCount));
        recAdapter = new MyRecyclerViewAdapter(view.getContext(), iconNames);
        recyclerView.setAdapter(recAdapter);
        //TODO make onCLick Listener for the Recycler
        //view.findViewById(R.id.rvIcons1).setOnClickListener(c);
        //recAdapter.setClickListener(this);


        //decorating the RecyclerView
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(spanCount, spacing, includeEdge));

        return view;
    }
}