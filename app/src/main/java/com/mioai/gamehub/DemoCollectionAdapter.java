package com.mioai.gamehub;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;

public class DemoCollectionAdapter extends FragmentStateAdapter
{
    private ArrayList<Fragment> fragmentList;

    public DemoCollectionAdapter(FragmentManager fm, Lifecycle lifecycle, ArrayList<Fragment> fragmentList)
    {
        super(fm, lifecycle);
        this.fragmentList = fragmentList;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position)
    {

        return fragmentList.get(position);
    }

    @Override
    public int getItemCount()
    {
        return fragmentList.size();
    }
}
