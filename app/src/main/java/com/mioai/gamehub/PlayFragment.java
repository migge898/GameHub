package com.mioai.gamehub;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.Arrays;

public class PlayFragment extends Fragment
{
    DemoCollectionAdapter demoCollectionAdapter;
    ViewPager2 viewPager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_play, container, false);

        ArrayList<Fragment> fragments = new ArrayList<>(Arrays.asList(
                new SingleplayerScreen(),
                new MultiplayerScreen(),
                new CoopScreen()
        ));

        demoCollectionAdapter = new DemoCollectionAdapter(
                requireActivity().getSupportFragmentManager(),
                getLifecycle(),
                fragments
        );

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        viewPager = view.findViewById(R.id.pager);
        viewPager.setAdapter(demoCollectionAdapter);
        TabLayout tabLayout = view.findViewById(R.id.tab_layout);
        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) ->
                {
                    switch (position)
                    {
                        case 0:
                            tab.setText("Single");
                            break;
                        case 1:
                            tab.setText("Multi");
                            break;
                        case 2:
                            tab.setText("Coop");
                            break;
                    }

                }
        ).attach();
    }

}