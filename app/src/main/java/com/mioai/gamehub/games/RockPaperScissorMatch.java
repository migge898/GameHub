package com.mioai.gamehub.games;

import com.google.firebase.database.Exclude;

public class RockPaperScissorMatch extends BaseMatch
{
    public String id_player1;
    public String id_player2;

    public String name_player1;
    public String name_player2;

    public int score_player1;
    public int score_player2;

    public int weapon_player1;
    public int weapon_player2;

    /**
     * If <code>true</code>, this user created the match.
     */
    @Exclude
    public boolean isCreated;

    /**
     * -1: No match
     * 0: Tie
     * 1: Player 1 wins round
     * 2: Player 2 wins round
     * 11: Player 1 wins game
     * 22: Player 2 wins game
     */
    public int status = -1;

    public RockPaperScissorMatch()
    {

    }
}
