package com.mioai.gamehub;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;

import java.util.StringTokenizer;


public class ProfilePage extends Fragment {




    TextView txt_profileName;
    TextView txt_level;
    TextView txt_timesPlayed;
    ProgressBar lvlBar;
    TextView txt_lvlXp;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profilepage, container, false);
        txt_profileName = view.findViewById(R.id.txt_Profile_Name);
        txt_level = view.findViewById(R.id.txt_level);
        txt_timesPlayed =view.findViewById(R.id.txt_times_played);
        txt_lvlXp = view.findViewById(R.id.xp_display);
        lvlBar = view.findViewById(R.id.bar_level_up);
        //todo fehler?? beim verwenden von firebase für profilename und setze die werte für die anderen textviews ein
        txt_profileName.setText(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());

        int currentXp;
        int maxXp=1000;
        int timesPlayed;
        int level;

        SharedPreferences shared = getActivity().getPreferences(Context.MODE_PRIVATE);
        String input;
        input = shared.getString("Profile_Data","");
        if(input.equals(""))
        {
            level = 1;
            currentXp = 0;
            timesPlayed = 0;
            maxXp = 100;
        }
        else
        {
            StringTokenizer st = new StringTokenizer(input);
            level = Integer.parseInt(st.nextToken("."));
            currentXp = Integer.parseInt(st.nextToken("."));
            maxXp = Integer.parseInt(st.nextToken("."));
            timesPlayed = Integer.parseInt(st.nextToken("."));
        }

        String temp = currentXp+"/"+maxXp;
        txt_lvlXp.setText(temp);
        temp = "Level: "+level;
        txt_level.setText(temp);
        temp = timesPlayed+" games";
        txt_timesPlayed.setText(temp);
        lvlBar.setMax(maxXp);
        lvlBar.setProgress(currentXp);

        return view;
    }
}
