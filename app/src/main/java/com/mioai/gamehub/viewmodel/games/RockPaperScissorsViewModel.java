package com.mioai.gamehub.viewmodel.games;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.mioai.gamehub.games.RockPaperScissorMatch;
import com.mioai.gamehub.repository.games.RockPaperScissorsRepository;

public class RockPaperScissorsViewModel extends AndroidViewModel
{
    private RockPaperScissorsRepository rpsRepository;
    private LiveData<RockPaperScissorMatch> rpsMatchLiveData;

    public RockPaperScissorsViewModel(@NonNull Application application)
    {
        super(application);
        rpsRepository = new RockPaperScissorsRepository();
    }

    public void createOrJoinMatch()
    {
        rpsMatchLiveData = rpsRepository.createOrJoinMatch();
    }

    public LiveData<RockPaperScissorMatch> getRockPaperScissorsMatch()
    {
        return rpsMatchLiveData;
    }
}
