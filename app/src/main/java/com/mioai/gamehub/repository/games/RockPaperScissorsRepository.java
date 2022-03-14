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

import java.util.HashMap;
import java.util.Map;

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

    private final FirebaseDatabase database = FirebaseDatabase.getInstance(FIREBASE_DB_URL);
    private final DatabaseReference usersRef = database.getReference(USERS);
    private final DatabaseReference matchesRef = database.getReference(MATCHES);
    private final DatabaseReference waitingRef = database.getReference(WAITING);

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
                {
                    for (int i = 0; i < openMatch.getChildrenCount(); i++)
                    {
                        String matchID = openMatch.getChildren().iterator().next().getKey();
                        joinMatch(matchID);
                    }

                } else
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

        if (firebaseUser != null)
        {
            // Init game values
            match.id_player1 = firebaseUser.getUid();
            match.isCreated = true;

            // Get player name

            match.name_player1 = firebaseUser.getDisplayName();

            String matchID = UIDGenerator.randomUID();

            // Add match ID in waiting list to indicate players can join
            waitingRef.child(ROCK_PAPER_SCISSORS_ID).child(matchID).setValue(true);

            matchesRef.child(ROCK_PAPER_SCISSORS_ID).child(matchID).setValue(match).addOnCompleteListener(task ->
            {
                if (task.isSuccessful())
                {
                    rpsGameMutableLiveData.setValue(match);
                } else
                    logErrorMessage(this, task);
            });
        } else
            logErrorMessage(this, "createMatch(): firebase user is null");


    }

    private void joinMatch(String matchID)
    {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null)
        {
            String username = user.getDisplayName();
            String uid = user.getUid();
            DatabaseReference matchRef = matchesRef.child(ROCK_PAPER_SCISSORS_ID).child(matchID);

            Map<String, Object> matchUpdates = new HashMap<>();
            matchUpdates.put("id_player2", uid);
            matchUpdates.put("name_player2", username);

            matchRef.updateChildren(matchUpdates).addOnCompleteListener(task ->
            {
                if (task.isSuccessful())
                {
                    matchRef.get().addOnCompleteListener(joinTask ->
                    {
                        if (joinTask.isSuccessful())
                        {
                            DataSnapshot data = joinTask.getResult();
                            RockPaperScissorMatch rpsMatch = data.getValue(RockPaperScissorMatch.class);
                            if (rpsMatch != null)
                            {
                                rpsMatch.isCreated = false;
                                rpsGameMutableLiveData.setValue(rpsMatch);

                                // Delete entry in waiting reference
                                waitingRef.child(ROCK_PAPER_SCISSORS_ID).child(matchID).removeValue();
                            } else
                                logErrorMessage(this, "RPS-Match is null");

                        }

                    });
                } else
                    logErrorMessage(this, task);
            });
        } else
            logErrorMessage(this, "Firebase user is null");

    }
}
