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
 * Class for the rook chess piece
 */
public class Rook extends ChessPiece {

    private static final long serialVersionUID = 3363431037932345309L;

    /**
     * Constructor for this piece
     * @param context Application context
     * @param color Color id (black or white)
     * @param startX Starting x location on the chess board
     * @param startY Starting y location on the chess board
     */
    public Rook(Context context, Chess chess, int color, float startX, float startY) {
        super(context, chess, color,
                color == ChessPiece.PIECE_COLOR_WHITE ?
                        R.drawable.chess_rlt45 : R.drawable.chess_rdt45,
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



        return legalMoves;
    }

    /**
     * Saves to XML any appropriate type-specific values for this piece
     * @param xml The XmlSerializer
     * @throws IOException if any issues occur during XML creation
     */
    @Override
    protected void saveTypeXml(@NonNull XmlSerializer xml) throws IOException {
        xml.attribute(null, "type", "rook");
    }

}
