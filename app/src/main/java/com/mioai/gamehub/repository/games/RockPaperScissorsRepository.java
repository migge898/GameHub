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
import com.mioai.gamehub.games.RockPaperScissorsMatch;
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
     * ID to categorize matches from type {@link RockPaperScissorsMatch} in Firebase Realtime Database
     */
    private final static String ROCK_PAPER_SCISSORS_ID = "rps";

    private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    private final FirebaseDatabase database = FirebaseDatabase.getInstance(FIREBASE_DB_URL);
    private final DatabaseReference usersRef = database.getReference(USERS);
    private final DatabaseReference matchesRef = database.getReference(MATCHES);
    private final DatabaseReference waitingRef = database.getReference(WAITING);

    private String openMatchID = "";

    public MutableLiveData<Boolean> matchWillBeCreated()
    {
        MutableLiveData<Boolean> matchWillBeCreatedMutableLiveData = new MutableLiveData<>();
        waitingRef.child(ROCK_PAPER_SCISSORS_ID).addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot openMatch)
            {
                matchWillBeCreatedMutableLiveData.setValue(!openMatch.exists());

                if (!openMatch.exists() || openMatchID.equals(""))
                    openMatchID = UIDGenerator.randomUID();
                else
                    // Get the matchID of the first available match in waiting list
                    openMatchID = openMatch.getChildren().iterator().next().getKey();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {
                logErrorMessage(this, error.getMessage());
            }
        });

        return matchWillBeCreatedMutableLiveData;
    }

    public MutableLiveData<RockPaperScissorsMatch> createMatch()
    {
        MutableLiveData<RockPaperScissorsMatch> createdMatchMutableLiveData = new MutableLiveData<>();

        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        if (firebaseUser != null)
        {
            RockPaperScissorsMatch createdMatch = new RockPaperScissorsMatch();

            // Init game values
            createdMatch.id_player1 = firebaseUser.getUid();

            // Get player name
            createdMatch.name_player1 = firebaseUser.getDisplayName();

            // Add match ID in waiting list to indicate players can join
            waitingRef.child(ROCK_PAPER_SCISSORS_ID).child(openMatchID).setValue(true);

            DatabaseReference matchRef = matchesRef.child(ROCK_PAPER_SCISSORS_ID).child(openMatchID);

            matchRef.setValue(createdMatch).addOnCompleteListener(task ->
            {
                if (task.isSuccessful())
                {
                    createdMatchMutableLiveData.setValue(createdMatch);
                    matchRef.addValueEventListener(new ValueEventListener()
                    {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot)
                        {
                            createdMatchMutableLiveData.setValue(snapshot.getValue(RockPaperScissorsMatch.class));
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error)
                        {
                            logErrorMessage(this, error.getMessage());
                        }
                    });

                } else
                    logErrorMessage(this, task);
            });

        } else
            logErrorMessage(this, "createMatch(): firebase user is null");

        return createdMatchMutableLiveData;
    }

    public MutableLiveData<RockPaperScissorsMatch> joinMatch()
    {
        MutableLiveData<RockPaperScissorsMatch> joinedMatchMutableLiveData = new MutableLiveData<>();

        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null)
        {
            String username = user.getDisplayName();
            String uid = user.getUid();
            DatabaseReference matchRef = matchesRef.child(ROCK_PAPER_SCISSORS_ID).child(openMatchID);

            Map<String, Object> matchUpdates = new HashMap<>();
            matchUpdates.put("id_player2", uid);
            matchUpdates.put("name_player2", username);

            matchRef.updateChildren(matchUpdates).addOnCompleteListener(task ->
            {
                if (task.isSuccessful())
                {
                    matchRef.addValueEventListener(new ValueEventListener()
                    {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot)
                        {
                            joinedMatchMutableLiveData.setValue(snapshot.getValue(RockPaperScissorsMatch.class));

                            // Delete entry in waiting reference
                            waitingRef.child(ROCK_PAPER_SCISSORS_ID).child(openMatchID).removeValue();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error)
                        {
                            logErrorMessage(this, error.getMessage());
                        }
                    });
                } else
                    logErrorMessage(this, task);
            });
        } else
            logErrorMessage(this, "Firebase user is null");

        return joinedMatchMutableLiveData;
    }
}
