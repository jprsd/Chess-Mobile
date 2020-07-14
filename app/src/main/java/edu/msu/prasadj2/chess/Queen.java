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
 * Class for the queen chess piece
 */
public class Queen extends ChessPiece {

    private static final long serialVersionUID = -4207766558337987310L;

    /**
     * Constructor for this piece
     * @param context Application context
     * @param color Color id (black or white)
     * @param startX Starting x location on the chess board
     * @param startY Starting y location on the chess board
     */
    public Queen(Context context, Chess chess, int color, float startX, float startY) {
        super(context, chess, color,
                color == ChessPiece.PIECE_COLOR_WHITE ?
                        R.drawable.chess_qlt45 : R.drawable.chess_qdt45,
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

        ArrayList<Float> Top = new ArrayList<Float>();

        float currentX = getPlacedX();
        float currentY = getPlacedY();


        // top
        Top.add(currentX);
        Top.add(currentY-1/8f);
        Top.add(currentX);
        Top.add(currentY-2/8f);
        Top.add(currentX);
        Top.add(currentY-3/8f);
        Top.add(currentX);
        Top.add(currentY-4/8f);
        Top.add(currentX);
        Top.add(currentY-5/8f);
        Top.add(currentX);
        Top.add(currentY-6/8f);
        Top.add(currentX);
        Top.add(currentY-7/8f);


        int occupiedByOpponent = getColor() == ChessPiece.PIECE_COLOR_WHITE ?
                ChessPiece.PIECE_COLOR_BLACK : ChessPiece.PIECE_COLOR_WHITE;

        for (int i=0; i<Top.size();i+=2){
            float X= Top.get(i);
            float Y=Top.get(i+1);

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

        // Bottom
        ArrayList<Float> Bottom = new ArrayList<Float>();
        Bottom.add(currentX);
        Bottom.add(currentY+1/8f);
        Bottom.add(currentX);
        Bottom.add(currentY+2/8f);
        Bottom.add(currentX);
        Bottom.add(currentY+3/8f);
        Bottom.add(currentX);
        Bottom.add(currentY+4/8f);
        Bottom.add(currentX);
        Bottom.add(currentY+5/8f);
        Bottom.add(currentX);
        Bottom.add(currentY+6/8f);
        Bottom.add(currentX);
        Bottom.add(currentY+7/8f);


        for (int i=0; i<Bottom.size();i+=2){
            float X= Bottom.get(i);
            float Y=Bottom.get(i+1);

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




        //Right

        ArrayList<Float> Right = new ArrayList<Float>();
        Right.add(currentX+1/8f);
        Right.add(currentY);
        Right.add(currentX+2/8f);
        Right.add(currentY);
        Right.add(currentX+3/8f);
        Right.add(currentY);
        Right.add(currentX+4/8f);
        Right.add(currentY);
        Right.add(currentX+5/8f);
        Right.add(currentY);
        Right.add(currentX+6/8f);
        Right.add(currentY);
        Right.add(currentX+7/8f);
        Right.add(currentY);


        for (int i=0; i<Right.size();i+=2){
            float X= Right.get(i);
            float Y=Right.get(i+1);

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

        //Left

        ArrayList<Float> Left = new ArrayList<Float>();
        Left.add(currentX-1/8f);
        Left.add(currentY);
        Left.add(currentX-2/8f);
        Left.add(currentY);
        Left.add(currentX-3/8f);
        Left.add(currentY);
        Left.add(currentX-4/8f);
        Left.add(currentY);
        Left.add(currentX-5/8f);
        Left.add(currentY);
        Left.add(currentX-6/8f);
        Left.add(currentY);
        Left.add(currentX-7/8f);
        Left.add(currentY);


        for (int i=0; i<Left.size();i+=2){
            float X= Left.get(i);
            float Y=Left.get(i+1);

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

        ArrayList<Float> CrossTopRight = new ArrayList<Float>();




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
        xml.attribute(null, "type", "queen");
    }

}
