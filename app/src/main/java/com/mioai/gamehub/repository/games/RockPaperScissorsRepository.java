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

    private boolean userHasCreatedMatch = false;

    private int weaponPlayer1;
    private int weaponPlayer2;

    private int previousStatus = -1;

    // Careful: Positions hardcoded to wepons-List!
    private static final int PAPER = 0;
    private static final int ROCK = 1;
    private static final int SCISSORS = 2;

    private String openMatchID = "";

    public MutableLiveData<Boolean> matchWillBeCreated()
    {
        MutableLiveData<Boolean> matchWillBeCreatedMutableLiveData = new MutableLiveData<>();
        waitingRef.child(ROCK_PAPER_SCISSORS_ID).addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot openMatch)
            {


                if (!openMatch.exists())
                {
                    openMatchID = UIDGenerator.randomUID();
                    userHasCreatedMatch = true;
                } else
                    // Get the matchID of the first available match in waiting list
                    openMatchID = openMatch.getChildren().iterator().next().getKey();

                matchWillBeCreatedMutableLiveData.setValue(!openMatch.exists());


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
                            updateStatus();
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
                            updateStatus();
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

    private void updateStatus()
    {
        DatabaseReference matchRef = matchesRef.child(ROCK_PAPER_SCISSORS_ID).child(openMatchID);
        int status = -1;
        boolean player1PlayedCard = weaponPlayer1 != -1;
        boolean player2PlayedCard = weaponPlayer2 != -1;
        if (player1PlayedCard && player2PlayedCard)
            status = fight();

        if (status != previousStatus)
        {
            Map<String, Object> matchUpdates = new HashMap<>();
            matchUpdates.put("status", status);
            matchRef.updateChildren(matchUpdates);
            previousStatus = status;
        }


    }

    private int fight()
    {
        int status = -1;

        if (weaponPlayer1 == weaponPlayer2)
            status = 0;
        else if (weaponPlayer1 == ROCK)
            status = (weaponPlayer2 == SCISSORS) ? 1 : 2;
        else if (weaponPlayer1 == PAPER)
            status = (weaponPlayer2 == ROCK) ? 1 : 2;
        else
            status = (weaponPlayer2 == PAPER) ? 1 : 2;

        return status;

    }

    public void playCard(int selectedWeapon)
    {
        DatabaseReference matchRef = matchesRef.child(ROCK_PAPER_SCISSORS_ID).child(openMatchID);

        Map<String, Object> matchUpdates = new HashMap<>();

        if (userHasCreatedMatch)
        {
            matchUpdates.put("weapon_player1", selectedWeapon);
            weaponPlayer1 = selectedWeapon;
        } else
        {
            matchUpdates.put("weapon_player2", selectedWeapon);
            weaponPlayer2 = selectedWeapon;
        }

        matchRef.updateChildren(matchUpdates);
    }
}
