package com.mioai.gamehub;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

public class FriendlistFragment extends Fragment
{
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_friendlist, container, false);

        view.findViewById(R.id.textView2).setOnClickListener((v) ->
                Navigation.findNavController(v).navigate(R.id.navigateToPlayFragment));
        return view;
    }
}