/*
 * Author: Jaideep Prasad
 * CSE 476 Spring 2020
 */

package edu.msu.prasadj2.chess;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;
import java.util.ArrayList;

import edu.msu.prasadj2.chess.Cloud.Models.PieceData;


/**
 * Custom view class for our Chess game.
 */
public class ChessView extends View {

    // The actual chess game object
    private Chess chess;
    // The text view that displays the current turn
    private TextView currentTurn;
    // The done button
    private Button done;
    // The resign button
    private Button resign;
    // The chess activity this view is a part of
    private ChessActivity chessActivity = null;


    /**
     * Constructor for the chess view
     * @param context Application context
     */
    public ChessView(Context context) {
        super(context);
        init(null, 0);
    }

    /**
     * Constructor for the chess view
     * @param context Application context
     * @param attrs Attribute set
     */
    public ChessView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    /**
     * Constructor for the chess view
     * @param context Application context
     * @param attrs Attribute set
     * @param defStyle The style
     */
    public ChessView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    /**
     * Initializes this custom view
     * @param attrs The attribute set
     * @param defStyle The style
     */
    private void init(AttributeSet attrs, int defStyle) {
        setHapticFeedbackEnabled(true);
        chess = new Chess(getContext());
    }

    /**
     * Initialize all other views on the screen
     * @param view The current turn text view
     * @param done The done button
     * @param resign The resign button
     * @param playerOneName Player one name
     * @param playerTwoName Player two name
     * @param username Current username
     */
    public void initializeViews(TextView view, Button done, Button resign,
                                String playerOneName, String playerTwoName, String username) {
        currentTurn = view;
        this.done = done;
        this.resign = resign;
        chess.setPlayerOneName(playerOneName);
        chess.setPlayerTwoName(playerTwoName);
        chess.setCurrentUser(username);
        chess.updateTurnDisplayMessage(this);
        this.done.setEnabled(false);
    }

    /**
     * Draws the view
     * @param canvas Graphics environment
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        chess.draw(canvas);
    }

    /**
     * Touch event handler
     * @param event The motion event
     * @return true if event was handled
     */
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return chess.onTouchEvent(this, event);
    }

    /**
     * Handler for the "Done" button
     */
    public void onDone() {
        chess.onDone(this);
        resign.setEnabled(false);
    }

    /**
     * Handler for the "Resign" button
     */
    public void onResign() {
        chess.onResign(this);
    }

    /**
     * Gets the checkmate status
     * @return true if a checkmate has occurred
     */
    public boolean isCheckmate() {
        return chess.isCheckmate();
    }

    /**
     * Gets the current turn text view
     * @return The current turn text view
     */
    public TextView getCurrentTurn() {
        return currentTurn;
    }

    /**
     * Gets the done button
     * @return The done button
     */
    public Button getDone() {
        return done;
    }

    /**
     * Gets the resign button
     * @return The resign button
     */
    public Button getResign() {
        return resign;
    }

    /**
     * Gets the name of the winner
     * @return The name of the winner
     */
    public String getWinnerName() {
        return chess.getWinnerName();
    }

    /**
     * Gets the name of the loser
     * @return The name of the loser
     */
    public String getLoserName() {
        return chess.getLoserName();
    }

    /**
     * Notifies this view about the activity it is a part of
     * @param chessActivity The chess activity
     */
    public void setChessActivity(ChessActivity chessActivity) {
        this.chessActivity = chessActivity;
    }

    /**
     * Dismisses any open dialogs
     */
    public void dismissDialogs() {
        chess.dismissPromoteDlg();
    }

    /**
     * Ends the chess game
     */
    public void endGame() {
        chessActivity.endGame();
    }


    /**
     * Determine if it is the current user's turn
     * @return true if it is
     */
    public boolean isCurrentUserTurn() {
        return chess.isCurrentUserTurn();
    }

    /**
     * Updates the game state with the latest server communication data
     * @param context The application context
     * @param piecesData The updated data for the pieces in the game
     */
    public void updateGame(Context context, ArrayList<PieceData> piecesData) {
        chess.updateGame(context, this, piecesData);
    }

    /**
     * Saves the current game state as XML
     * @param xml The XmlSerializer
     * @throws IOException if any issues occur during XML creation
     */
    public void saveGame(@NonNull XmlSerializer xml) throws IOException {
        chess.saveGame(xml);
    }

    /**
     * Saves the current game state
     * @param bundle The bundle to save into
     */
    public void putToBundle(@NonNull String key, @NonNull Bundle bundle) {
        chess.dismissPromoteDlg();
        bundle.putSerializable(key, chess);
    }

    /**
     * Reloads the game state
     * @param context The application context
     * @param currentTurn The current turn text view
     * @param done The done button
     * @param key The bundle key
     * @param bundle The saved instance state
     */
    public void reload(@NonNull Context context,
                       @NonNull TextView currentTurn, @NonNull Button done, @NonNull Button resign,
                       @NonNull String key, @NonNull Bundle bundle) {
        this.currentTurn = currentTurn;
        this.done = done;
        this.resign = resign;
        chess = (Chess)bundle.getSerializable(key);
        if (chess != null) {
            chess.reload(context, this);
        }
    }

}