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
 * Class for the king chess piece
 */
public class King extends ChessPiece {

    private static final long serialVersionUID = 8209629202168881643L;

    /**
     * Constructor for this piece
     * @param context Application context
     * @param color Color id (black or white)
     * @param startX Starting x location on the chess board
     * @param startY Starting y location on the chess board
     */
    public King(Context context, Chess chess, int color, float startX, float startY) {
        super(context, chess, color,
                color == ChessPiece.PIECE_COLOR_WHITE ?
                        R.drawable.chess_klt45 : R.drawable.chess_kdt45,
                startX,
                startY);


    }

    /**
     * Marks this piece for elimination and establishes checkmate
     */
    @Override
    public void die() {
        super.die();
        getChess().checkmate();
    }

    /**
     * Get all possible legal moves for this piece at its current location
     * @return An array of legal placement coordinates
     */
    @Override
    protected ArrayList<Float> getLegalMoves() {
        ArrayList<Float> legalMoves = new ArrayList<Float>();
        ArrayList<Float> tempMoveStore= new ArrayList<Float>();


        float currentX = getPlacedX();
        float currentY = getPlacedY();

        tempMoveStore.add(currentX+1/8f);
        tempMoveStore.add(currentY+1/8f);

        tempMoveStore.add(currentX+1/8f);
        tempMoveStore.add(currentY-1/8f);

        tempMoveStore.add(currentX);
        tempMoveStore.add(currentY+1/8f);

        tempMoveStore.add(currentX);
        tempMoveStore.add(currentY-1/8f);

        tempMoveStore.add(currentX+1/8f);
        tempMoveStore.add(currentY);

        tempMoveStore.add(currentX-1/8f);
        tempMoveStore.add(currentY);

        tempMoveStore.add(currentX-1/8f);
        tempMoveStore.add(currentY+1/8f);

        tempMoveStore.add(currentX-1/8f);
        tempMoveStore.add(currentY-1/8f);



        int occupiedByOpponent = getColor() == ChessPiece.PIECE_COLOR_WHITE ?
                ChessPiece.PIECE_COLOR_BLACK : ChessPiece.PIECE_COLOR_WHITE;
        for (int i=0; i<tempMoveStore.size();i+=2){

            float X= tempMoveStore.get(i);
            float Y=tempMoveStore.get(i+1);

            if (isWithinBoard(X,Y)){
                if (getChess().squareStatusAt(X,Y) == occupiedByOpponent ){
                    legalMoves.add(X);
                    legalMoves.add(Y);
                }
                else if (getChess().squareStatusAt(X,Y)==0){
                    legalMoves.add(X);
                    legalMoves.add(Y);
                }


            }
        }


        return legalMoves;
    }

    /**
     * Saves to XML any appropriate type-specific values for this piece
     * @param xml The XmlSerializer
     * @throws IOException if any issues occur during XML creation
     */
    @Override
    protected void saveTypeXml(@NonNull XmlSerializer xml) throws IOException {
        xml.attribute(null, "type", "king");
    }

}
