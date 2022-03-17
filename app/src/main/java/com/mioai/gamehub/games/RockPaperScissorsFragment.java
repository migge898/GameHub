package com.mioai.gamehub.games;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.auth.FirebaseAuth;
import com.mioai.gamehub.R;
import com.mioai.gamehub.viewmodel.games.RockPaperScissorsViewModel;

import java.util.Arrays;
import java.util.List;

public class RockPaperScissorsFragment extends Fragment
{

    private LinearLayout linearLayoutWeapons;
    private CardView cardViewPaper, cardViewRock, cardViewScissors;
    private CardView cardViewEnemyWeapon;

    private TextView textViewPlayerName, textViewEnemyName;
    private TextView textViewPlayerScore, textViewEnemyScore;
    private TextView textViewStatus;

    private List<CardView> weapons;

    private boolean weaponLocked;
    private boolean weaponSelected;

    private int selectedWeaponPosition = -1;

    // Careful: Positions hardcoded to wepons-List!
    private static final int ROCK = 0;
    private static final int PAPER = 1;
    private static final int SCISSORS = 2;

    private RockPaperScissorsViewModel matchViewModel;

    String playerUid = FirebaseAuth.getInstance().getCurrentUser().getUid();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_rock_paper_scissors, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        initLayout(view);
        initGameViewModel();
        initMatch();
    }

    private void initMatch()
    {
        matchViewModel.createOrJoinMatch();
        matchViewModel.getMatchWillBeCreatedLiveData().observe(this, matchWillBeCreated ->
        {
            if (matchWillBeCreated)
                createMatch();
            else
                joinMatch();
        });
    }

    private void joinMatch()
    {
        matchViewModel.joinMatch();
        matchViewModel.getJoinedMatchLiveData().observe(this, joinedMatch ->
        {
            textViewPlayerName.setText(joinedMatch.name_player2);
            textViewPlayerScore.setText(String.valueOf(joinedMatch.score_player2));
            textViewEnemyName.setText(joinedMatch.name_player1);
            textViewEnemyScore.setText(String.valueOf(joinedMatch.score_player1));

            if (joinedMatch.weapon_player1 != -1)
            {
                Toast.makeText(getActivity(), "Enemy played " + joinedMatch.weapon_player1, Toast.LENGTH_SHORT).show();
            }
            if (joinedMatch.weapon_player2 != -1)
            {
                Toast.makeText(getActivity(), "You played " + joinedMatch.weapon_player2, Toast.LENGTH_SHORT).show();
            }

            updateStatus(joinedMatch);
        });
    }

    private void createMatch()
    {
        matchViewModel.createMatch();
        matchViewModel.getCreatedMatchLiveData().observe(this, createdMatch ->
        {
            textViewPlayerName.setText(createdMatch.name_player1);
            textViewPlayerScore.setText(String.valueOf(createdMatch.score_player1));
            textViewEnemyName.setText(createdMatch.name_player2);
            textViewEnemyScore.setText(String.valueOf(createdMatch.score_player2));

            if (createdMatch.weapon_player1 != -1)
            {
                Toast.makeText(getActivity(), "You played " + createdMatch.weapon_player1, Toast.LENGTH_SHORT).show();
            }
            if (createdMatch.weapon_player2 != -1)
            {
                Toast.makeText(getActivity(), "Enemy played " + createdMatch.weapon_player2, Toast.LENGTH_SHORT).show();
            }


            updateStatus(createdMatch);
        });
    }

    private void updateStatus(RockPaperScissorsMatch rpsGame)
    {
        boolean isPlayer1 = playerUid.equals(rpsGame.id_player1);
        int status = rpsGame.status;
        String statusText = "";

        if (isPlayer1)
        {
            switch (status)
            {
                case 0:
                    statusText = getString(R.string.tie);
                    break;
                case 1:
                    statusText = getString(R.string.won);
                    break;
                case 2:
                    statusText = getString(R.string.lost);
                    break;
                case 11:
                    statusText = getString(R.string.game_won);
                    break;
                case 22:
                    statusText = getString(R.string.game_lost);
                    break;
                default:
                    statusText = getString(R.string.waiting);
                    break;
            }

        } else
        {
            switch (status)
            {
                case 0:
                    statusText = getString(R.string.tie);
                    break;
                case 1:
                    statusText = getString(R.string.lost);
                    break;
                case 2:
                    statusText = getString(R.string.won);
                    break;
                case 11:
                    statusText = getString(R.string.game_lost);
                    break;
                case 22:
                    statusText = getString(R.string.game_won);
                    break;
                default:
                    statusText = getString(R.string.waiting);
                    break;
            }

        }

        textViewStatus.setText(statusText);
    }

    private void initLayout(View view)
    {
        // Scoreboard
        textViewPlayerName = view.findViewById(R.id.textviewPlayerName);
        textViewEnemyName = view.findViewById(R.id.textviewEnemyName);
        textViewPlayerScore = view.findViewById(R.id.textviewPlayerScore);
        textViewEnemyScore = view.findViewById(R.id.textviewEnemyScore);

        linearLayoutWeapons = view.findViewById(R.id.linear_weapon);
        cardViewPaper = view.findViewById(R.id.cardPaper);
        cardViewRock = view.findViewById(R.id.cardRock);
        cardViewScissors = view.findViewById(R.id.cardScissors);
        cardViewEnemyWeapon = view.findViewById(R.id.cardEnemyWeapon);

        textViewStatus = view.findViewById(R.id.textViewStatus);

        weapons = Arrays.asList(cardViewRock, cardViewPaper, cardViewScissors);

        weapons.forEach(w ->
        {
            w.setOnClickListener(v ->
            {
                w.setCardBackgroundColor(ContextCompat.getColor(getActivity(), R.color.secondaryColor));

                selectedWeaponPosition = weapons.indexOf(w);
                weaponLocked = true;
                matchViewModel.playCard(selectedWeaponPosition);

                weapons.forEach(ww -> ww.setEnabled(false));
            });
        });

    }

    private void initGameViewModel()
    {
        matchViewModel = new ViewModelProvider(this).get(RockPaperScissorsViewModel.class);
    }

}