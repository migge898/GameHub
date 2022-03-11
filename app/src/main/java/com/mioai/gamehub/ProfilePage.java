package com.mioai.gamehub;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;


public class ProfilePage extends Fragment {


    TextView profileName;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profilepage, container, false);
        profileName = view.findViewById(R.id.txt_profileName);
        profileName.setText("testzwecke");
        view.findViewById(R.id.txt_backToSecondFragment).setOnClickListener((v) ->
                Navigation.findNavController(v).navigate(R.id.navigateToSecondFragment));






        return view;
    }
}
