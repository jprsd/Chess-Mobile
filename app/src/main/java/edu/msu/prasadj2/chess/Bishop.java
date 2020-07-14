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
 * Class for the bishop chess piece
 */
public class Bishop extends ChessPiece {

    private static final long serialVersionUID = -1413659713039906837L;

    /**
     * Constructor for this piece
     * @param context Application context
     * @param color Color id (black or white)
     * @param startX Starting x location on the chess board
     * @param startY Starting y location on the chess board
     */
    public Bishop(Context context, Chess chess, int color, float startX, float startY) {
        super(context, chess, color,
                color == ChessPiece.PIECE_COLOR_WHITE ?
                        R.drawable.chess_blt45 : R.drawable.chess_bdt45,
                startX,
                startY);
    }

    /**
     * Get all possible legal moves for this piece at its current location
     * @return An array of legal placement coordinates
     */
    @Override
    protected ArrayList<Float> getLegalMoves() {
        ArrayList<Float> legalMoves = new ArrayList<Float>();

        ArrayList<Float> CrossTopRight = new ArrayList<Float>();

        float currentX = getPlacedX();
        float currentY = getPlacedY();


        // top right
        CrossTopRight.add(currentX+1/8f);
        CrossTopRight.add(currentY-1/8f);
        CrossTopRight.add(currentX+1/4f);
        CrossTopRight.add(currentY-1/4f);
        CrossTopRight.add(currentX+3/8f);
        CrossTopRight.add(currentY-3/8f);
        CrossTopRight.add(currentX+1/2f);
        CrossTopRight.add(currentY-1/2f);
        CrossTopRight.add(currentX+5/8f);
        CrossTopRight.add(currentY-5/8f);
        CrossTopRight.add(currentX+6/8f);
        CrossTopRight.add(currentY-6/8f);
        CrossTopRight.add(currentX+7/8f);
        CrossTopRight.add(currentY-7/8f);

        int occupiedByOpponent = getColor() == ChessPiece.PIECE_COLOR_WHITE ?
                ChessPiece.PIECE_COLOR_BLACK : ChessPiece.PIECE_COLOR_WHITE;

        for (int i=0; i<CrossTopRight.size();i+=2){
            float X= CrossTopRight.get(i);
            float Y=CrossTopRight.get(i+1);

            if(isWithinBoard(X,Y)){
                if (getChess().squareStatusAt(X,Y) == occupiedByOpponent ){
                    legalMoves.add(X);
                    legalMoves.add(Y);
                    break;
                }
                else if (getChess().squareStatusAt(X,Y)==0){
                    legalMoves.add(X);
                    legalMoves.add(Y);
                }
                else if (getChess().squareStatusAt(X,Y) != occupiedByOpponent ){
                    break;
                }
            }
        }

        //bottom right

        ArrayList<Float> CrossBottomRight = new ArrayList<Float>();
        CrossBottomRight.add(currentX+1/8f);
        CrossBottomRight.add(currentY+1/8f);
        CrossBottomRight.add(currentX+1/4f);
        CrossBottomRight.add(currentY+1/4f);
        CrossBottomRight.add(currentX+3/8f);
        CrossBottomRight.add(currentY+3/8f);
        CrossBottomRight.add(currentX+1/2f);
        CrossBottomRight.add(currentY+1/2f);
        CrossBottomRight.add(currentX+5/8f);
        CrossBottomRight.add(currentY+5/8f);
        CrossBottomRight.add(currentX+6/8f);
        CrossBottomRight.add(currentY+6/8f);
        CrossBottomRight.add(currentX+7/8f);
        CrossBottomRight.add(currentY+7/8f);




        for (int i=0; i<CrossBottomRight.size();i+=2){
            float X= CrossBottomRight.get(i);
            float Y=CrossBottomRight.get(i+1);

            if(isWithinBoard(X,Y)){
                if (getChess().squareStatusAt(X,Y) == occupiedByOpponent ){
                    legalMoves.add(X);
                    legalMoves.add(Y);
                    break;
                }
                else if (getChess().squareStatusAt(X,Y)==0){
                    legalMoves.add(X);
                    legalMoves.add(Y);
                }
                else if (getChess().squareStatusAt(X,Y) != occupiedByOpponent ){
                    break;
                }
            }
        }





        /// topleft
        ArrayList<Float> CrossTopLeft = new ArrayList<Float>();
        CrossTopLeft.add(currentX-1/8f);
        CrossTopLeft.add(currentY-1/8f);
        CrossTopLeft.add(currentX-1/4f);
        CrossTopLeft.add(currentY-1/4f);
        CrossTopLeft.add(currentX-3/8f);
        CrossTopLeft.add(currentY-3/8f);
        CrossTopLeft.add(currentX-1/2f);
        CrossTopLeft.add(currentY-1/2f);
        CrossTopLeft.add(currentX-5/8f);
        CrossTopLeft.add(currentY-5/8f);
        CrossTopLeft.add(currentX-6/8f);
        CrossTopLeft.add(currentY-6/8f);
        CrossTopLeft.add(currentX-7/8f);
        CrossTopLeft.add(currentY-7/8f);




        for (int i=0; i<CrossTopLeft.size();i+=2){
            float X= CrossTopLeft.get(i);
            float Y=CrossTopLeft.get(i+1);

            if(isWithinBoard(X,Y)){
                if (getChess().squareStatusAt(X,Y) == occupiedByOpponent ){
                    legalMoves.add(X);
                    legalMoves.add(Y);
                    break;
                }
                else if (getChess().squareStatusAt(X,Y)==0){
                    legalMoves.add(X);
                    legalMoves.add(Y);
                }
                else if (getChess().squareStatusAt(X,Y) != occupiedByOpponent ){
                    break;
                }
            }
        }


        //bottomleft
        ArrayList<Float> CrossBottomLeft = new ArrayList<Float>();
        CrossBottomLeft.add(currentX-1/8f);
        CrossBottomLeft.add(currentY+1/8f);
        CrossBottomLeft.add(currentX-1/4f);
        CrossBottomLeft.add(currentY+1/4f);
        CrossBottomLeft.add(currentX-3/8f);
        CrossBottomLeft.add(currentY+3/8f);
        CrossBottomLeft.add(currentX-1/2f);
        CrossBottomLeft.add(currentY+1/2f);
        CrossBottomLeft.add(currentX-5/8f);
        CrossBottomLeft.add(currentY+5/8f);
        CrossBottomLeft.add(currentX-6/8f);
        CrossBottomLeft.add(currentY+6/8f);
        CrossBottomLeft.add(currentX-7/8f);
        CrossBottomLeft.add(currentY+7/8f);




        for (int i=0; i<CrossBottomLeft.size();i+=2){
            float X= CrossBottomLeft.get(i);
            float Y=CrossBottomLeft.get(i+1);

            if(isWithinBoard(X,Y)){
                if (getChess().squareStatusAt(X,Y) == occupiedByOpponent ){
                    legalMoves.add(X);
                    legalMoves.add(Y);
                    break;
                }
                else if (getChess().squareStatusAt(X,Y)==0){
                    legalMoves.add(X);
                    legalMoves.add(Y);
                }
                else if (getChess().squareStatusAt(X,Y) != occupiedByOpponent ){
                    break;
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
        xml.attribute(null, "type", "bishop");
    }

}
