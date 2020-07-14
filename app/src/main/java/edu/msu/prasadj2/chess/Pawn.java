/*
 * Author: Jaideep Prasad
 * Author: Enliang Zhao
 * CSE 476 Spring 2020
 */

package edu.msu.prasadj2.chess;

import android.content.Context;

import androidx.annotation.NonNull;

import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Class for the pawn chess piece
 */
public class Pawn extends ChessPiece {

    private static final long serialVersionUID = -9205850914091751535L;

    // Starting y location for pawn
    private float startY;
    // Promotion y location for pawn
    private float promotionY;

    /**
     * Determines the direction (up or down the board) the pawn must advance towards
     * Down the board: 1
     * Up the board: -1
     */
    private int directionality;

    /**
     * Constructor for this piece
     * @param context Application context
     * @param color Color id (black or white)
     * @param startX Starting x location on the chess board
     * @param startY Starting y location on the chess board
     */
    public Pawn(Context context, Chess chess, int color, float startX, float startY) {
        super(context, chess, color,
                color == ChessPiece.PIECE_COLOR_WHITE ?
                        R.drawable.chess_plt45 : R.drawable.chess_pdt45,
                startX,
                startY);

        this.startY = startY;
        if (startY < 0.5f) {
            promotionY = 15/16f;
            directionality = 1;
        }
        else {
            promotionY = 1/16f;
            directionality = -1;
        }
    }

    /**
     * Attempt to place the piece within the nearest board square
     * @return true if piece placed
     */
    @Override
    public boolean maybePlacePiece() {
        boolean isPlaced = super.maybePlacePiece();
        // Check if this pawn can now be promoted
        if (isPlaced && Math.abs(getPlacedY() - promotionY) < 0.001f) {
            getChess().promotePawn(this);
        }
        return isPlaced;
    }

    /**
     * Get all possible legal moves for this piece at its current location
     * @return An array of legal placement coordinates
     */
    @Override
    protected ArrayList<Float> getLegalMoves() {
        ArrayList<Float> legalMoves = new ArrayList<Float>();

        float currentX = getPlacedX();
        float currentY = getPlacedY();
        int rangeMultiplier = Math.abs(startY - currentY) < 0.001f ? 2 : 1;
        float limitY = directionality * rangeMultiplier/8f + currentY;

        // Pawn can move up to two square forward from starting position,
        // or just one square forward otherwise
        while (rangeMultiplier >= 1) {
            if (isWithinBoard(currentX, limitY) &&
                    getChess().squareStatusAt(currentX, limitY) == Chess.SQUARE_EMPTY) {
                legalMoves.add(currentX);
                legalMoves.add(limitY);
            }
            rangeMultiplier--;
            if (rangeMultiplier != 0) {
                limitY = directionality * rangeMultiplier / 8f + currentY;
            }
        }

        // Pawn can capture opponent pieces that are one square diagonal to it on either side
        float x;
        int occupiedByOpponent = getColor() == ChessPiece.PIECE_COLOR_WHITE ?
                ChessPiece.PIECE_COLOR_BLACK : ChessPiece.PIECE_COLOR_WHITE;
        for (int i = 1; i >= -1; i--) {
            switch (i) {
                case 1:
                case -1:
                    x = currentX + i/8f;
                    break;
                default:
                    continue;
            }
            if (isWithinBoard(x, limitY) &&
                    getChess().squareStatusAt(x, limitY) == occupiedByOpponent) {
                legalMoves.add(x);
                legalMoves.add(limitY);
            }
        }

        return legalMoves;
    }

    /**
     * Sets the start y location for this pawn and determines directionality and the promotion y
     * @param startY The starting y location
     */
    public void setStartY(float startY) {
        this.startY = startY;
        if (startY < 0.5f) {
            promotionY = 15/16f;
            directionality = 1;
        }
        else {
            promotionY = 1/16f;
            directionality = -1;
        }
    }

    /**
     * Saves to XML any appropriate type-specific values for this piece
     * @param xml The XmlSerializer
     * @throws IOException if any issues occur during XML creation
     */
    @Override
    protected void saveTypeXml(@NonNull XmlSerializer xml) throws IOException {
        xml.attribute(null, "type", "pawn");
        xml.attribute(null, "startY", Float.toString(startY));
    }

}
