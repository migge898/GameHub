package com.mioai.gamehub.viewmodel.games;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.mioai.gamehub.games.RockPaperScissorsMatch;
import com.mioai.gamehub.repository.games.RockPaperScissorsRepository;

public class RockPaperScissorsViewModel extends AndroidViewModel
{
    private RockPaperScissorsRepository rpsRepository;
    private LiveData<Boolean> matchWillBeCreatedLiveData;
    private LiveData<RockPaperScissorsMatch> createdMatchLiveData;
    private LiveData<RockPaperScissorsMatch> joinedMatchLiveData;

    public RockPaperScissorsViewModel(@NonNull Application application)
    {
        super(application);
        rpsRepository = new RockPaperScissorsRepository();
    }

    public void createOrJoinMatch()
    {
        matchWillBeCreatedLiveData = rpsRepository.matchWillBeCreated();
    }

    public void createMatch()
    {
        createdMatchLiveData = rpsRepository.createMatch();
    }

    public void joinMatch()
    {
        joinedMatchLiveData = rpsRepository.joinMatch();
    }

    public LiveData<Boolean> getMatchWillBeCreatedLiveData()
    {
        return matchWillBeCreatedLiveData;
    }

    public LiveData<RockPaperScissorsMatch> getCreatedMatchLiveData()
    {
        return createdMatchLiveData;
    }

    public LiveData<RockPaperScissorsMatch> getJoinedMatchLiveData()
    {
        return joinedMatchLiveData;
    }


}
