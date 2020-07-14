/*
 * Author: Jaideep Prasad
 * Author: Ze Liu
 * CSE 476 Spring 2020
 */

package edu.msu.prasadj2.chess;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import edu.msu.prasadj2.chess.Cloud.Cloud;

public class GameOverActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_over);

        // Set winner and loser text views
        TextView winnerView = ((TextView)findViewById(R.id.winnerBox));
        String winnerString = winnerView.getText().toString() + " " +
                getIntent().getStringExtra(ChessActivity.WINNER_DATA);
        winnerView.setText(winnerString);
        TextView loserView = ((TextView)findViewById(R.id.loserBox));
        String loserString = loserView.getText().toString() + " " +
                getIntent().getStringExtra(ChessActivity.LOSER_DATA);
        loserView.setText(loserString);
    }

    /**
     * Handler for New Game button
     * @param view The current view
     */
    public void onNewGame(View view) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                (new Cloud()).logout();
            }
        }).start();
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    /**
     * Overrides back button to New Game
     */
    @Override
    public void onBackPressed() {
        onNewGame(null);
    }
}
