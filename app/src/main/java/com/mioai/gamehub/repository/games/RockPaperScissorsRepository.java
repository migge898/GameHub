package com.mioai.gamehub.repository.games;

import static com.mioai.gamehub.utils.HelperClass.logErrorMessage;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mioai.gamehub.games.RockPaperScissorMatch;
import com.mioai.gamehub.utils.UIDGenerator;

public class RockPaperScissorsRepository
{
    private final static String FIREBASE_DB_URL = "https://gamehub-4786a-default-rtdb.europe-west1.firebasedatabase.app/";

    private final static String USERS = "Users";
    private final static String MATCHES = "Matches";
    private final static String WAITING = "Waiting";

    /**
     * ID to categorize matches from type {@link RockPaperScissorMatch} in Firebase Realtime Database
     */
    private final static String ROCK_PAPER_SCISSORS_ID = "rps";

    private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    private final FirebaseDatabase rootRef = FirebaseDatabase.getInstance(FIREBASE_DB_URL);
    private final DatabaseReference usersRef = rootRef.getReference(USERS);
    private final DatabaseReference matchesRef = rootRef.getReference(MATCHES);
    private final DatabaseReference waitingRef = rootRef.getReference(WAITING);

    private MutableLiveData<RockPaperScissorMatch> rpsGameMutableLiveData;

    public RockPaperScissorsRepository()
    {
        this.rpsGameMutableLiveData = new MutableLiveData<>();
    }

    public MutableLiveData<RockPaperScissorMatch> createOrJoinMatch()
    {

        waitingRef.child(ROCK_PAPER_SCISSORS_ID).addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot openMatch)
            {
                if (openMatch.exists())
                    joinMatch();
                else
                    createMatch();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {
                logErrorMessage(this, error.getMessage());
            }
        });

        return rpsGameMutableLiveData;
    }

    private void createMatch()
    {

        RockPaperScissorMatch match = new RockPaperScissorMatch();

        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        String playerUID = firebaseUser.getUid();

        // Init game values
        match.id_player1 = playerUID;
        match.isCreated = true;

        // Get player name
        usersRef.child(playerUID).child("username").addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                match.name_player1 = snapshot.getValue(String.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {
                logErrorMessage(this, error.getMessage());
            }
        });

        String matchUID = UIDGenerator.randomUID();

        // Add match ID in waiting list to indicate players can join
        waitingRef.child(ROCK_PAPER_SCISSORS_ID).child(matchUID).setValue(true);

        matchesRef.child(ROCK_PAPER_SCISSORS_ID).child(matchUID).setValue(match).addOnCompleteListener(task ->
        {
            if (task.isSuccessful())
            {
                rpsGameMutableLiveData.setValue(match);
            } else
                logErrorMessage(this, task);
        });

    }

    private void joinMatch()
    {
        RockPaperScissorMatch m = new RockPaperScissorMatch();
        m.isCreated = false;
        m.id_player1 = firebaseAuth.getCurrentUser().getUid();
        m.name_player1 = "TEST";
        rpsGameMutableLiveData.setValue(m);
    }
}
