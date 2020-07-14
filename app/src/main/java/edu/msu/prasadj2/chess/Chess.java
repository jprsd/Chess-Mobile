/*
 * Author: Jaideep Prasad
 * CSE 476 Spring 2020
 */

package edu.msu.prasadj2.chess;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.HapticFeedbackConstants;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;

import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

import edu.msu.prasadj2.chess.Cloud.Models.PieceData;

/**
 * Class for the chess game
 */
public class Chess implements Serializable {

    private static final long serialVersionUID = 5161037918495829113L;

    public static final int SQUARE_EMPTY = 0;

    // The current user's (device user) name
    private String currentUserName;
    // The current user's (device user) color
    private int currentUserColor;

    // The current player
    private int currentColor;

    // Name for player 1
    private String playerOneName;
    // Name for player 2
    private String playerTwoName;

    // The winner of the game
    private String winnerName;
    // The loser of the game;
    private String loserName;

    // Tracks when the current turn is complete
    private boolean turnComplete = false;

    /**
     * Percentage of the display width or height that
     * is occupied by the chess board.
     */
    private final static float SCALE_IN_VIEW = 0.95f;

    /**
     * Paint for filling the area the chess board is in
     */
    private transient Paint fillPaintGreen;

    /**
     * Paint for outlining the area the chess board is in
     */
    private transient Paint outlinePaint;

    // Size of a generic chess board square. Used for calculating scaling.
    private float genericPieceWidth;
    // The size of the board
    private int boardSize;
    // Scale factor used for various screen sizes
    private float scaleFactor;
    // Horizontal margin for various screen sizes
    private int marginX;
    // Vertical margin for various screen sizes
    private int marginY;

    // List of all of the chess pieces on the board
    private ArrayList<ChessPiece> pieces = new ArrayList<ChessPiece>();

    /**
     * This variable is set to a piece we are dragging. If
     * we are not dragging, the variable is null.
     */
    private ChessPiece dragging = null;

    // Chess piece that will be eliminated
    private ChessPiece deadPiece = null;

    // Pawn to promote
    private Pawn promotePawn = null;

    // Promote dialog box
    private transient AlertDialog promoteDlg = null;

    // True if a king has been eliminated
    private boolean checkmate = false;

    /**
     * Most recent relative X touch when dragging
     */
    private float lastRelX;

    /**
     * Most recent relative Y touch when dragging
     */
    private float lastRelY;

    /**
     * Chess game constructor
     * @param context Application context
     */
    public Chess(@NonNull Context context){
        // Create paint for filling the area the chessboard will
        // be solved in.
        fillPaintGreen = new Paint(Paint.ANTI_ALIAS_FLAG);
        fillPaintGreen.setColor(0xFF008000);

        outlinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        outlinePaint.setColor(0xff8fbc8f);
        outlinePaint.setStrokeWidth(5f);

        genericPieceWidth = (float) BitmapFactory.decodeResource(context.getResources(),
                        R.drawable.chess_plt45).getWidth();

        playerOneName = context.getResources().getString(R.string.player1_name);
        playerTwoName = context.getResources().getString(R.string.player2_name);

        // Player 1 starts first
        currentColor = ChessPiece.PIECE_COLOR_WHITE;

        initializeBoard(context);
    }

    /**
     * Draws the game
     * @param canvas Graphics environment
     */
    public void draw(@NonNull Canvas canvas) {
        int wid = canvas.getWidth();
        int hit = canvas.getHeight();

        // Determine the minimum of the two dimensions
        int minDim = wid < hit ? wid : hit;

        boardSize = (int)(minDim * SCALE_IN_VIEW);
        // Compute the margins so we center the chess board
        marginX = (wid - boardSize) / 2;
        marginY = (hit - boardSize) / 2;

        //
        // Draw the outline of the chess board
        //
        canvas.drawLine(marginX, marginY, marginX + boardSize, marginY, outlinePaint);
        canvas.drawLine(marginX, marginY, marginX, marginY+boardSize, outlinePaint);
        canvas.drawLine(marginX+boardSize, marginY, marginX+boardSize,
                marginY+boardSize, outlinePaint);
        canvas.drawLine(marginX, marginY+boardSize, marginX+boardSize,
                marginY+boardSize, outlinePaint);

        for (int j=0;j<4;j++) {
            for (int i = 0; i < 4; i++) {
                canvas.drawRect(marginX + (2 * i + 1) * boardSize / 8,
                        marginY+2*j*boardSize/8,
                        (float) (marginX + (2 * i + 2) * boardSize / 8),
                        (float) (marginY + (2*j+1) * boardSize/8), fillPaintGreen);
            }
        }
        for (int j=0;j<4;j++) {
            for (int i = 0; i < 4; i++) {
                canvas.drawRect(marginX + (2 * i) * boardSize / 8,
                        marginY+(2*j+1)*boardSize/8,
                        (float) (marginX + (2 * i + 1) * boardSize / 8),
                        (float) (marginY + (2*j+2) * boardSize/8), fillPaintGreen);
            }
        }

        scaleFactor = (float) boardSize / 8 / genericPieceWidth;

        for (ChessPiece piece : pieces) {
            piece.draw(canvas, marginX, marginY, boardSize, scaleFactor);
        }

    }

    /**
     * Handle a touch event from the view.
     * @param view The view that is the source of the touch
     * @param event The motion event describing the touch
     * @return true if the touch is handled.
     */
    public boolean onTouchEvent(View view, MotionEvent event) {
        if (!isCurrentUserTurn() || turnComplete) {
            return false;
        }

        //
        // Convert an x,y location to a relative location in the
        // puzzle.
        //
        float relX = (event.getX() - marginX) / boardSize;
        float relY = (event.getY() - marginY) / boardSize;

        switch (event.getActionMasked()) {

            case MotionEvent.ACTION_DOWN:
                return onTouched(relX, relY);

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                return onReleased(view, relX, relY);

            case MotionEvent.ACTION_MOVE:
                // If we are dragging, move the piece and force a redraw
                if(dragging != null) {
                    dragging.move(relX - lastRelX, relY - lastRelY);
                    lastRelX = relX;
                    lastRelY = relY;
                    view.invalidate();
                    return true;
                }
                break;
        }
        return false;
    }

    /**
     * Initializes or resets the chess board
     * @param context Graphics environment
     */
    private void initializeBoard(Context context) {
        // Start with clear board
        pieces.clear();
        // Pawns
        for (int i=0;i<8;i++){
            pieces.add(new Pawn(context, this, ChessPiece.PIECE_COLOR_WHITE,
                    (i/8f + 1/16f), 13/16f));
            pieces.add(new Pawn(context, this, ChessPiece.PIECE_COLOR_BLACK,
                    (i/8f + 1/16f), 3/16f));
        }
        // Rooks
        for (int i=0;i<2;i++){
            pieces.add(new Rook(context, this, ChessPiece.PIECE_COLOR_WHITE,
                    i == 0 ? 1/16f : 15/16f, 15/16f));
            pieces.add(new Rook(context, this, ChessPiece.PIECE_COLOR_BLACK,
                    i == 0 ? 1/16f : 15/16f, 1/16f));
        }
        // Knights
        for (int i=0;i<2;i++){
            pieces.add(new Knight(context, this, ChessPiece.PIECE_COLOR_WHITE,
                    i == 0 ? 3/16f : 13/16f, 15/16f));
            pieces.add(new Knight(context, this, ChessPiece.PIECE_COLOR_BLACK,
                    i == 0 ? 3/16f : 13/16f, 1/16f));
        }
        // Bishops
        for (int i=0;i<2;i++){
            pieces.add(new Bishop(context, this, ChessPiece.PIECE_COLOR_WHITE,
                    i == 0 ? 5/16f : 11/16f, 15/16f));
            pieces.add(new Bishop(context, this, ChessPiece.PIECE_COLOR_BLACK,
                    i == 0 ? 5/16f : 11/16f, 1/16f));
        }
        // Queens
        pieces.add(new Queen(context, this,
                ChessPiece.PIECE_COLOR_WHITE, 7/16f, 15/16f));
        pieces.add(new Queen(context, this,
                ChessPiece.PIECE_COLOR_BLACK, 7/16f, 1/16f));
        // Kings
        pieces.add(new King(context, this,
                ChessPiece.PIECE_COLOR_WHITE, 9/16f, 15/16f));
        pieces.add(new King(context, this,
                ChessPiece.PIECE_COLOR_BLACK, 9/16f, 1/16f));
    }

    /**
     * Handle a touch message. This is when we get an initial touch
     * @param x x location for the touch, relative to the chess board - 0 to 1 over the board
     * @param y y location for the touch, relative to the chess board - 0 to 1 over the board
     * @return true if the touch is handled
     */
    private boolean onTouched(float x, float y) {

        // Check each piece to see if it has been hit
        // We do this in reverse order so we find the pieces in front
        for(int p=pieces.size()-1; p>=0;  p--) {
            if(pieces.get(p).hit(x, y, boardSize, scaleFactor)) {
                // We hit a piece!
                dragging = pieces.get(p);
                pieces.remove(p);
                pieces.add(dragging);
                lastRelX = x;
                lastRelY = y;
                return true;
            }
        }

        return false;
    }

    /**
     * Handle a release of a touch message.
     * @param x x location for the touch release, relative to the board - 0 to 1 over the board
     * @param y y location for the touch release, relative to the board - 0 to 1 over the board
     * @return true if the touch is handled
     */
    private boolean onReleased(View view, float x, float y) {

        if(dragging != null) {
            if (dragging.maybePlacePiece()) {
                maybeKillPieceAt(dragging.getPlacedX(), dragging.getPlacedY(), dragging);
                ((ChessView)view).getDone().setEnabled(true);
                turnComplete = true;
            }
            else {
                // Invalid move
                Toast.makeText(view.getContext(),
                        R.string.toast_invalid, Toast.LENGTH_SHORT).show();
                ((ChessView)view).getDone().setEnabled(false);
            }
            dragging = null;

            if (deadPiece != null) {
                pieces.remove(deadPiece);
                deadPiece = null;
            }

            if (promotePawn != null) {
                showPromoteDlg(view);
            }

            if (checkmate) {
                ((ChessView)view).getResign().setEnabled(false);
                winnerName = currentColor == ChessPiece.PIECE_COLOR_WHITE ?
                        playerOneName : playerTwoName;
                loserName = currentColor == ChessPiece.PIECE_COLOR_WHITE ?
                        playerTwoName : playerOneName;
                updateTurnDisplayMessage(view);
            }

            view.invalidate(); // Always invalidate since pieces snap on release no matter what
            view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
            return true;
        }

        return false;
    }

    /**
     * Gets the square status at the given relative coordinates
     * @param x The x location 0-1.
     * @param y The y location 0-1.
     * @return A status of empty or occupied (by a white or black piece).
     */
    public int squareStatusAt(float x, float y) {
        for (ChessPiece piece : pieces) {
            if (Math.abs(x - piece.getPlacedX()) < 0.001f &&
                    Math.abs(y - piece.getPlacedY()) < 0.001f) {
                return piece.getColor();
            }
        }
        return SQUARE_EMPTY;
    }

    /**
     * Potentially mark an enemy piece for elimination if it exists at this location
     * @param x The relative x coordinate 0-1
     * @param y The relative y coordinate 0-1
     * @param currentPiece The chess piece that is currently active
     * @return true if an enemy piece was killed
     */
    private boolean maybeKillPieceAt(float x, float y, ChessPiece currentPiece) {
        for (ChessPiece piece : pieces) {
            if (piece == currentPiece) {
                continue;
            }
            if (Math.abs(x - piece.getPlacedX()) < 0.001f &&
                    Math.abs(y - piece.getPlacedY()) < 0.001f) {
                if (piece.getColor() != currentPiece.getColor()) {
                    // Overtake enemy piece at this location
                    piece.die();
                    return true;
                }
                else {
                    break;
                }
            }
        }
        return false;
    }

    /**
     * Marks the given chess piece for elimination
     * @param piece The chess piece to eliminate
     */
    public void setDeadPiece(ChessPiece piece) {
        deadPiece = piece;
    }

    /**
     * Establishes checkmate
     */
    public void checkmate() {
        checkmate = true;
    }

    /**
     * Gets the checkmate status
     * @return true if a checkmate has occurred
     */
    public boolean isCheckmate() {
        return checkmate;
    }

    /**
     * Marks a pawn for promotion
     * @param pawn The pawn to mark for promotion
     */
    public void promotePawn(Pawn pawn) {
        promotePawn = pawn;
    }

    /**
     * Updates the current turn display message
     */
    public void updateTurnDisplayMessage(@NonNull View view) {
        String message;
        if (checkmate) {
            message = winnerName + " " + view.getResources().getString(R.string.wins);
        }
        else {
            String currentPlayer = currentColor == ChessPiece.PIECE_COLOR_WHITE ?
                    playerOneName : playerTwoName;
            String playerColor = currentColor == ChessPiece.PIECE_COLOR_WHITE ?
                    view.getResources().getString(R.string.white) :
                    view.getResources().getString(R.string.black);
            message = view.getResources().getString(R.string.current_turn) + " " + playerColor +
                    currentPlayer;
        }
        ((ChessView)view).getCurrentTurn().setText(message);
    }

    /**
     * Ends the current player's turn or declares the current player as winner
     * @param view The current view
     */
    public void onDone(View view) {
        if (checkmate) {
            ((ChessView)view).endGame();
            return;
        }
        turnComplete = false;
        currentColor = currentColor == ChessPiece.PIECE_COLOR_WHITE ?
                ChessPiece.PIECE_COLOR_BLACK : ChessPiece.PIECE_COLOR_WHITE;
        ((ChessView)view).getDone().setEnabled(false);
        updateTurnDisplayMessage(view);
    }

    /**
     * Declares opponent player as winner and ends the game
     * @param view The current view
     */
    public void onResign(View view) {
        turnComplete = true;
        ((ChessView)view).getDone().setEnabled(false);
        winnerName = currentColor == ChessPiece.PIECE_COLOR_WHITE ? playerTwoName : playerOneName;
        loserName = currentColor == ChessPiece.PIECE_COLOR_WHITE ? playerOneName : playerTwoName;
    }

    /**
     * Updates the game state with the latest server communication data
     * @param context The application context
     * @param view The current view
     * @param piecesData The updated data for the pieces in the game
     */
    public void updateGame(Context context, ChessView view, ArrayList<PieceData> piecesData) {
        pieces.clear();
        int kingCounter = 0;
        for (PieceData pieceData : piecesData) {
            String type = pieceData.getType();
            int color = pieceData.getColor();
            float placedX = pieceData.getPlacedX();
            float placedY = pieceData.getPlacedY();
            if (type.equals("bishop")) {
                pieces.add(new Bishop(context, this, color, placedX, placedY));
            }
            else if (type.equals("king")) {
                pieces.add(new King(context, this, color, placedX, placedY));
                kingCounter++;
            }
            else if (type.equals("knight")) {
                pieces.add(new Knight(context, this, color, placedX, placedY));
            }
            else if (type.equals("pawn")) {
                float startY = pieceData.getStartY();
                Pawn pawn = new Pawn(context, this, color, placedX, placedY);
                pawn.setStartY(startY);
                pieces.add(pawn);

            }
            else if (type.equals("queen")) {
                pieces.add(new Queen(context, this, color, placedX, placedY));
            }
            else if (type.equals("rook")) {
                pieces.add(new Rook(context, this, color, placedX, placedY));
            }
        }
        if (kingCounter < 2) {
            checkmate();
            view.getResign().setEnabled(false);
            view.getDone().setEnabled(true);
            winnerName = currentColor == ChessPiece.PIECE_COLOR_WHITE ?
                    playerOneName : playerTwoName;
            loserName = currentColor == ChessPiece.PIECE_COLOR_WHITE ?
                    playerTwoName : playerOneName;
        }
        else {
            view.getResign().setEnabled(true);
            turnComplete = false;
            currentColor = currentColor == ChessPiece.PIECE_COLOR_WHITE ?
                    ChessPiece.PIECE_COLOR_BLACK : ChessPiece.PIECE_COLOR_WHITE;
        }
        updateTurnDisplayMessage(view);
        view.invalidate();
    }

    /**
     * Saves the current game state as XML
     * @param xml The XmlSerializer
     * @throws IOException if any issues occur during XML creation
     */
    public void saveGame(@NonNull XmlSerializer xml) throws IOException {
        for (ChessPiece piece : pieces) {
            piece.saveXml(xml);
        }
    }

    /**
     * Determine if it is the current user's turn
     * @return true if it is
     */
    public boolean isCurrentUserTurn() {
        return currentColor == currentUserColor;
    }

    /**
     * Get the current player's color
     * @return The current player's color
     */
    public int getCurrentColor() {
        return currentColor;
    }

    /**
     * Sets the player one name
     * @param name The name
     */
    public void setPlayerOneName(String name) {
        if (name == null || name.length() == 0) {
            return;
        }
        playerOneName = name;
    }

    /**
     * Set the player two name
     * @param name The name
     */
    public void setPlayerTwoName(String name) {
        if (name == null || name.length() == 0) {
            return;
        }
        playerTwoName = name;

        if (playerTwoName.equals(playerOneName)) {
            playerTwoName = playerOneName + " 2";
        }
    }

    /**
     * Set the current user's name and determine their color
     * @param username The name
     */
    public void setCurrentUser(String username) {
        currentUserName = username;
        currentUserColor = currentUserName.equals(playerOneName) ?
                ChessPiece.PIECE_COLOR_WHITE : ChessPiece.PIECE_COLOR_BLACK;
    }

    /**
     * Gets the name of the game winner
     * @return The name of the winner
     */
    public String getWinnerName() {
        return winnerName;
    }

    /**
     * Gets the name of the loser of the game
     * @return The name of the loser
     */
    public String getLoserName() {
        return loserName;
    }


    /**
     * Shows the pawn promotion dialog box
     * @param view The current view
     */
    private void showPromoteDlg(@NonNull final View view) {
        final float pawnX = promotePawn.getPlacedX();
        final float pawnY = promotePawn.getPlacedY();
        final int pawnColor = promotePawn.getColor();

        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
        builder.setTitle(R.string.promote_dlg_title);
        builder.setItems(R.array.promotions, new DialogInterface.OnClickListener() {
            private static final int QUEEN = 0, ROOK = 1, KNIGHT = 2, BISHOP = 3;
            @Override
            public void onClick(DialogInterface dialog, int which) {
                pieces.remove(promotePawn);
                switch (which) {
                    case QUEEN:
                        pieces.add(new Queen(view.getContext(),
                                Chess.this, pawnColor, pawnX, pawnY));
                        break;
                    case ROOK:
                        pieces.add(new Rook(view.getContext(),
                                Chess.this, pawnColor, pawnX, pawnY));
                        break;
                    case KNIGHT:
                        pieces.add(new Knight(view.getContext(),
                                Chess.this, pawnColor, pawnX, pawnY));
                        break;
                    case BISHOP:
                        pieces.add(new Bishop(view.getContext(),
                                Chess.this, pawnColor, pawnX, pawnY));
                        break;
                }
                promotePawn = null;
                view.post(new Runnable() {
                    @Override
                    public void run() {
                        view.invalidate();
                    }
                });
            }
        });
        builder.setCancelable(false);
        promoteDlg = builder.create();
        promoteDlg.show();
    }

    /**
     * Dismisses the any open dialog boxes. Use during instance saving.
     */
    public void dismissPromoteDlg() {
        if (promoteDlg != null && promoteDlg.isShowing()) {
            promoteDlg.dismiss();
        }
    }

    /**
     * Reloads transient variables
     * @param context The application context
     */
    public void reload(Context context, View view) {
        fillPaintGreen = new Paint(Paint.ANTI_ALIAS_FLAG);
        fillPaintGreen.setColor(0xFF008000);

        outlinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        outlinePaint.setColor(0xff8fbc8f);
        outlinePaint.setStrokeWidth(5f);

        for (ChessPiece piece: pieces) {
            piece.reload(context);
        }

        updateTurnDisplayMessage(view);

        if (turnComplete) {
            ((ChessView)view).getDone().setEnabled(true);
        }
        else {
            ((ChessView)view).getDone().setEnabled(false);
        }

        if (checkmate) {
            ((ChessView)view).getResign().setEnabled(false);
        }

        if (promotePawn != null) {
            showPromoteDlg(view);
        }
    }

}
