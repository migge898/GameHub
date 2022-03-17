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

    /**
     * Constants for internal status flag
     */
    private final static int WAITING_FOR_PLAYER = -1;
    private final static int TIE = 0;
    private final static int PLAYER_1_WINS_ROUND = 1;
    private final static int PLAYER_2_WINS_ROUND = 2;
    private final static int PLAYER_1_WINS_MATCH = 11;
    private final static int PLAYER_2_WINS_MATCH = 22;
    private int previousStatus = -1;

    /**
     * Careful: Positions hardcoded to weapons List in {@link com.mioai.gamehub.games.RockPaperScissorsFragment}
     */
    private static final int NO_WEAPON_SELECTED = -1;
    private static final int ROCK = 0;
    private static final int PAPER = 1;
    private static final int SCISSORS = 2;

    private String openMatchID = "";

    private RockPaperScissorsMatch rpsMatch;

    public RockPaperScissorsRepository()
    {
        this.rpsMatch = new RockPaperScissorsMatch();
    }

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

                            rpsMatch = snapshot.getValue(RockPaperScissorsMatch.class);
                            updateStatus();
                            createdMatchMutableLiveData.setValue(rpsMatch);

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

                            rpsMatch = snapshot.getValue(RockPaperScissorsMatch.class);
                            updateStatus();
                            joinedMatchMutableLiveData.setValue(rpsMatch);

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
        int status;
        boolean player1PlayedCard = rpsMatch.weapon_player1 != NO_WEAPON_SELECTED;
        boolean player2PlayedCard = rpsMatch.weapon_player2 != NO_WEAPON_SELECTED;
        if (player1PlayedCard && player2PlayedCard)
        {
            status = fight();

        } else
            status = WAITING_FOR_PLAYER;

        if (status != previousStatus)
        {
            DatabaseReference matchRef = matchesRef.child(ROCK_PAPER_SCISSORS_ID).child(openMatchID);
            Map<String, Object> matchUpdates = new HashMap<>();
            matchUpdates.put("status", status);

            matchUpdates.put("score_player1", rpsMatch.score_player1);
            matchUpdates.put("score_player2", rpsMatch.score_player2);

            matchRef.updateChildren(matchUpdates);
            previousStatus = status;
        }


    }

    private int fight()
    {
        int status;

        weaponPlayer1 = rpsMatch.weapon_player1;
        weaponPlayer2 = rpsMatch.weapon_player2;
        if (rpsMatch.weapon_player1 == rpsMatch.weapon_player2)
            status = TIE;
        else if (weaponPlayer1 == ROCK)
        {

            if (weaponPlayer2 == SCISSORS)
                status = statusPlayer1Wins();
            else status = statusPlayer2Wins();

        } else if (weaponPlayer1 == PAPER)
        {
            if (weaponPlayer2 == ROCK)
                status = statusPlayer1Wins();
            else status = statusPlayer2Wins();
        } else
        {
            if (weaponPlayer2 == PAPER)
                status = statusPlayer1Wins();
            else status = statusPlayer2Wins();
        }

        return status;

    }

    private int statusPlayer1Wins()
    {
        return ++rpsMatch.score_player1 == 3 ? PLAYER_1_WINS_MATCH : PLAYER_1_WINS_ROUND;
    }

    private int statusPlayer2Wins()
    {
        return ++rpsMatch.score_player2 == 3 ? PLAYER_2_WINS_MATCH : PLAYER_2_WINS_ROUND;
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
