/*
 * Author: Jaideep Prasad
 * CSE 476 Spring 2020
 */

package edu.msu.prasadj2.chess;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import edu.msu.prasadj2.chess.Cloud.Cloud;

/**
 * Activity where a player waits to be paired with an opponent
 */
public class MatchmakingActivity extends AppCompatActivity {

    public static final String PLAYER_ONE_DATA = "MatchmakingActivity.playerOneName";
    public static final String PLAYER_TWO_DATA = "MatchmakingActivity.playerTwoName";
    private String playerOneName;
    private String playerTwoName;

    // Tracks matched status in case user exits before finding an opponent
    private volatile boolean matched = false;

    /**
     * Thread for finding an opponent
     */
    private class MatchmakingThread extends Thread {
        // Thread running flag
        private volatile boolean search = true;

        /**
         * Searches for a player periodically
         */
        @Override
        public void run() {
            Cloud cloud = new Cloud();
            boolean opponentFound = false;
            while (search) {
                opponentFound = cloud.findOpponent(MatchmakingActivity.this);
                if (opponentFound) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            matched = true;
                            Toast.makeText(MatchmakingActivity.this,
                                    R.string.opponent_found, Toast.LENGTH_LONG).show();
                            Intent startGameIntent =
                                    new Intent(MatchmakingActivity.this,
                                            ChessActivity.class);
                            startGameIntent.putExtra(PLAYER_ONE_DATA, playerOneName);
                            startGameIntent.putExtra(PLAYER_TWO_DATA, playerTwoName);
                            startGameIntent.putExtra(MainActivity.CURRENT_USER_DATA,
                                    getIntent().getStringExtra(MainActivity.CURRENT_USER_DATA));
                            ChessActivity.preInitialize();
                            startActivity(startGameIntent);
                            finish();
                        }
                    });
                    return;
                }
                try {
                    sleep(Cloud.POLLING_DELAY);
                } catch (InterruptedException e) {
                    return;
                }
            }
        }

        /**
         * Stops the thread
         */
        public void terminate() {
            search = false;
            interrupt();
        }
    }

    // The matchmaking thread for this activity
    private MatchmakingThread matchmakingThread = new MatchmakingThread();

    /**
     * Creates the activity
     * @param savedInstanceState The previously saved activity state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matchmaking);
        matchmakingThread.start();
    }

    /**
     * Destroys the activity
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        matchmakingThread.terminate();
        if (!matched) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    (new Cloud()).logout();
                }
            }).start();
        }
    }

    /**
     * Saves the activity state
     * @param outState The bundle to save to
     */
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        matchmakingThread.terminate();
    }

    /**
     * Sets the player names
     * @param playerOneName Name for the first player
     * @param playerTwoName Name for the second player
     */
    public void setPlayerNames(String playerOneName, String playerTwoName) {
        this.playerOneName = playerOneName;
        this.playerTwoName = playerTwoName;
    }

}
