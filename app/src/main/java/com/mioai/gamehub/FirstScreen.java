package com.mioai.gamehub;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class FirstScreen extends Fragment
{

    MyRecyclerViewAdapter recAdapter;
    RecyclerView recyclerView;

    final private String[] iconNames = {"RockPaperScissor", "Coming Soon"};
    final private int[] iconSrc = {R.drawable.ic_paperhandicon, R.drawable.ic_comingsoon};
    private final int[] iconLinks = {R.id.rockPaperScissorsFragment};


    @Override

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        //changing the Style of the Grid
        int spanCount = 3; // Number of columns
        int spacing = 50; // 50px

        //Init the view and adapter
        View view = inflater.inflate(R.layout.fragment_first_screen, container, false);
        recyclerView = view.findViewById(R.id.rvIcons1);
        recyclerView.setLayoutManager(new GridLayoutManager(view.getContext(), spanCount));
        recAdapter = new MyRecyclerViewAdapter(view.getContext(), iconNames, iconSrc);
        recyclerView.setAdapter(recAdapter);
        //TODO make onCLick Listener for the Recycler
        //view.findViewById(R.id.rvIcons1).setOnClickListener(c);
        recAdapter.setClickListener(this::onItemClick);


        //decorating the RecyclerView
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(spanCount, spacing, true));

        return view;
    }

    private void onItemClick(View v, int pos)
    {
        if (pos == 0)
            Navigation.findNavController(v).navigate(iconLinks[pos]);
    }
}