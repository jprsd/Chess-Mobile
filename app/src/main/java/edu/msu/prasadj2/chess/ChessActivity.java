/*
 * Author: Jaideep Prasad
 * CSE 476 Spring 2020
 */

package edu.msu.prasadj2.chess;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;
import java.util.ArrayList;

import edu.msu.prasadj2.chess.Cloud.Cloud;
import edu.msu.prasadj2.chess.Cloud.Models.PieceData;

/**
 * Class for the chess game activity
 */
public class ChessActivity extends AppCompatActivity {

    public static final String WINNER_DATA = "ChessActivity.winnerName";
    public static final String LOSER_DATA = "ChessActivity.loserName";

    private static final String GAME_STATE = "ChessActivity.GameState";

    // Tracks player inactivity time
    private static volatile long inactiveTime = 0;

    // Flag for tracking inactivity time
    private static volatile boolean trackTime = true;

    // Flag for if the game is over
    private volatile boolean gameOver = false;

    // Flag for if the opponent has resigned
    private volatile boolean opponentResigned = false;

    /**
     * Thread for listening to game state updates from the server
     */
    private class GameThread extends Thread {
        // Thread running flag
        private volatile boolean poll = true;

        /**
         * Periodically checks if the game state has been updated
         */
        @Override
        public void run() {
            Cloud cloud = new Cloud();
            boolean gameUpdated = false;
            while (poll) {
                gameUpdated = cloud.loadGame(ChessActivity.this);
                if (gameUpdated) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (opponentResigned) {
                                Toast.makeText(ChessActivity.this,
                                        R.string.opponent_resigned, Toast.LENGTH_LONG).show();
                                onResign(null);
                            }
                            else {
                                updateGame();
                            }
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
            poll = false;
            interrupt();
        }
    }

    /**
     * Thread for delivering game state updates to the server.
     * Construct as needed.
     */
    private class SaveThread extends Thread {
        // Save type (normal or resign)
        private boolean resign = false;

        /**
         * SaveThread constructor
         * @param resign save type (false = normal, true = resign)
         */
        public SaveThread(boolean resign) {
            super();
            this.resign = resign;
        }

        /**
         * Sends game state to the server
         */
        @Override
        public void run() {
            Cloud cloud = new Cloud();
            boolean saved = false;
            long startTime = System.currentTimeMillis();
            long elapsedTime = 0;

            do {
                saved = cloud.saveGame(ChessActivity.this, resign);
                elapsedTime = System.currentTimeMillis() - startTime;
                if (!saved) {
                    boolean interrupted = false;
                    try {
                        sleep(Cloud.POLLING_DELAY);
                    } catch (InterruptedException e) {
                        interrupted = true;
                    }
                    if (interrupted || elapsedTime > 15 * Cloud.POLLING_DELAY) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(ChessActivity.this,
                                        R.string.server_save_fail, Toast.LENGTH_LONG).show();
                            }
                        });
                        return;
                    }
                }
            } while (!saved);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    gameThread.terminate();
                    if (!gameOver) {
                        gameThread = new GameThread();
                        gameThread.start();
                    }
                }
            });
        }
    }

    /**
     * Thread for tracking inactivity time.
     * Construct as needed.
     */
    private class InactivityThread extends Thread {

        /**
         * Tracks player inactivity time and forfeits the game
         * if the player has been inactive for too long
         */
        @Override
        public void run() {
            long startTime = System.currentTimeMillis();
            // Player has 60 seconds to make a move
            while (inactiveTime < 60 * Cloud.POLLING_DELAY) {
                if (trackTime) {
                    inactiveTime = System.currentTimeMillis() - startTime;
                }
                else {
                    inactiveTime = 0;
                    return;
                }
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(ChessActivity.this,
                            R.string.inactive_too_long, Toast.LENGTH_LONG).show();
                    onResign(null);
                }
            });
        }

    }

    // The game polling thread for this activity
    private GameThread gameThread = new GameThread();

    // List that stores the latest incoming server piece data
    private ArrayList<PieceData> serverPiecesData = new ArrayList<PieceData>();

    /**
     * Creates the activity
     * @param savedInstanceState The previously saved activity state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chess);

        getChessView().setChessActivity(this);
        if (savedInstanceState != null) {
            getChessView().reload(this,
                    (TextView)findViewById(R.id.currentTurn), (Button)findViewById(R.id.done),
                    (Button)findViewById(R.id.resign), GAME_STATE, savedInstanceState);
        }
        else {
            initializeViews();
        }

        if (!getChessView().isCurrentUserTurn()) {
            gameThread.start();
            getChessView().getResign().setEnabled(false);
        }
        else {
            if (trackTime && inactiveTime == 0) {
                (new InactivityThread()).start();
            }
        }

    }

    /**
     * Destroys the activity
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        getChessView().dismissDialogs();
        gameThread.terminate();
    }

    /**
     * Handler for the "Done" button
     * @param view The current view
     */
    public void onDone(View view) {
        trackTime = false;
        getChessView().onDone();
        (new SaveThread(false)).start();
    }

    /**
     * Handler for the "Resign" button
     * @param view The current view
     */
    public void onResign(View view) {
        trackTime = false;
        getChessView().onResign();
        endGame();
        (new SaveThread(true)).start();
    }

    /**
     * Notifies this activity of the latest server data
     * @param serverPiecesData The updated server piece data
     */
    public void setServerPiecesData(ArrayList<PieceData> serverPiecesData) {
        this.serverPiecesData = serverPiecesData;
    }

    /**
     * Enables the opponent resign flag
     */
    public void flagOpponentResigned() {
        this.opponentResigned = true;
    }

    /**
     * Updates the game state with the latest server communication data
     */
    public void updateGame() {
        getChessView().updateGame(this, serverPiecesData);
        if (!getChessView().isCheckmate()) {
            inactiveTime = 0;
            trackTime = true;
            (new InactivityThread()).start();
        }
    }

    /**
     * Saves the current game state as XML
     * @param xml The XmlSerializer
     * @throws IOException if any issues occur during XML creation
     */
    public void saveGame(@NonNull XmlSerializer xml) throws IOException {
        getChessView().saveGame(xml);
    }

    /**
     * Ends the game
     */
    public void endGame() {
        gameOver = true;
        Intent intent = new Intent(this, GameOverActivity.class);
        intent.putExtra(WINNER_DATA, getChessView().getWinnerName());
        intent.putExtra(LOSER_DATA, getChessView().getLoserName());
        startActivity(intent);
    }

    /**
     * Notifies the chess view of the other views on screen
     */
    private void initializeViews() {
        TextView textView = (TextView)findViewById(R.id.currentTurn);
        Button done = (Button)findViewById(R.id.done);
        Button resign = (Button)findViewById(R.id.resign);
        getChessView().initializeViews(textView, done, resign,
                getIntent().getStringExtra(MatchmakingActivity.PLAYER_ONE_DATA),
                getIntent().getStringExtra(MatchmakingActivity.PLAYER_TWO_DATA),
                getIntent().getStringExtra(MainActivity.CURRENT_USER_DATA));
    }

    /**
     * Saves the current game state
     * @param bundle The bundle to save to
     */
    @Override
    protected void onSaveInstanceState(@NonNull Bundle bundle) {
        super.onSaveInstanceState(bundle);
        getChessView().putToBundle(GAME_STATE, bundle);
        gameThread.terminate();
    }

    /**
     * Gets the chess view
     * @return The chess view
     */
    private ChessView getChessView() {
        return (ChessView)this.findViewById(R.id.chessView);
    }

    /**
     * Force logout the user on premature exit
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        new Thread(new Runnable() {
            @Override
            public void run() {
                (new Cloud()).logout();
            }
        }).start();
    }

    /**
     * Initialize static volatiles. Should be called prior to activity entry.
     */
    public static void preInitialize() {
        trackTime = true;
        inactiveTime = 0;
    }
}
