/*
 * Author: Jaideep Prasad
 * CSE 476 Spring 2020
 */

package edu.msu.prasadj2.chess;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;

import androidx.annotation.NonNull;

import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Generic class for chess pieces
 */
public abstract class ChessPiece implements Serializable {

    private static final long serialVersionUID = 8772336535167844804L;

    public static final int PIECE_COLOR_WHITE = 1; // Color id for white pieces
    public static final int PIECE_COLOR_BLACK = 2; // Color id for black pieces

    // The chess game board this piece is a part of
    private Chess chess;

    // The color of the piece
    private int color;

    /**
     * The image resource id
     */
    private int id;

    /**
     * The image for the actual piece.
     */
    private transient Bitmap piece;

    /**
     * x location. Primarily used for drawing/movement purposes.
     * We use relative x locations in the range 0-1 for the center
     * of the chess piece.
     */
    private float x;

    // The actual legal x position of the the chess piece on the chess board
    private float placedX;

    /**
     * y location. Also relative values from 0-1. Primarily used for drawing/movement purposes.
     */
    private float y;

    // The actual legal y position of the chess piece on the chess board
    private float placedY;


    /**
     * Chess piece constructor
     * @param context Application context
     * @param color The color of the piece
     * @param id Image resource id
     * @param startX Starting x location on the chess board
     * @param startY Starting y location on the chess board
     */
    protected ChessPiece(@NonNull Context context,
                         Chess chess, int color, int id, float startX, float startY) {
        this.chess = chess;
        this.color = color;
        placedX = startX;
        x = placedX;
        placedY = startY;
        y = placedY;
        this.id = id;

        piece = BitmapFactory.decodeResource(context.getResources(), id);

    }

    /**
     * Draws the chess piece
     * @param canvas Graphics environment
     * @param marginX Horizontal margin based on device screen size
     * @param marginY Vertical margin based on device screen size
     * @param boardSize Size of the chess board based on device screen size
     * @param scaleFactor Scaling factor of graphics based on device screen size
     */
    public final void draw(@NonNull Canvas canvas, int marginX, int marginY,
                     int boardSize, float scaleFactor) {
        canvas.save();

        // Convert x,y to pixels and add the margin, then draw
        canvas.translate(marginX + x * boardSize, marginY + y * boardSize);

        // Scale it to the right size
        canvas.scale(scaleFactor, scaleFactor);

        // This magic code makes the center of the piece at 0, 0
        canvas.translate(-piece.getWidth() / 2f, -piece.getHeight() / 2f);

        // Draw the bitmap
        canvas.drawBitmap(piece, 0, 0, null);
        canvas.restore();
    }

    /**
     * Test to see if we have touched a chess piece
     * @param testX X location as a normalized coordinate (0 to 1)
     * @param testY Y location as a normalized coordinate (0 to 1)
     * @param boardSize the size of the chess board in pixels
     * @param scaleFactor the amount to scale a piece by
     * @return true if we hit the piece
     */
    public final boolean hit(float testX, float testY,
                       int boardSize, float scaleFactor) {
        if (color != chess.getCurrentColor()) { // Do not grab opponent's pieces
            return false;
        }

        // Make relative to the location and size to the piece size
        int pX = (int)((testX - x) * boardSize / scaleFactor) +
                piece.getWidth() / 2;
        int pY = (int)((testY - y) * boardSize / scaleFactor) +
                piece.getHeight() / 2;

        if(pX < 0 || pX >= piece.getWidth() ||
                pY < 0 || pY >= piece.getHeight()) {
            return false;
        }

        // We are within the rectangle of the piece.
        // Are we touching actual picture?
        return (piece.getPixel(pX, pY) & 0xff000000) != 0;
    }

    /**
     * Move the chess piece by dx, dy
     * @param dx x amount to move
     * @param dy y amount to move
     */
    public final void move(float dx, float dy) {
        x += dx;
        y += dy;
    }

    /**
     * Attempt to place the piece within the nearest board square
     * @return true if piece placed
     */
    public boolean maybePlacePiece() {
        // Convert relative coordinates to numerators of fractions with denominators of 16
        float hexX = x*16f;
        float hexY = y*16f;

        // Round numerators to the nearest whole, odd number
        int roundX = Math.round(hexX);
        if (roundX % 2 == 0) {
            roundX = roundX < hexX ? roundX + 1 : roundX - 1;
        }
        int roundY = Math.round(hexY);
        if (roundY % 2 == 0) {
            roundY = roundY < hexY ? roundY + 1 : roundY - 1;
        }

        // Convert everything back to relative coordinates by dividing by 16
        float finalX = roundX/16f;
        float finalY = roundY/16f;

        // Check validity of attempted placement
        if(isValidPlacement(finalX, finalY)) {
            // New placement succeeded
            placedX = finalX;
            placedY = finalY;
            x = placedX;
            y = placedY;
            return true;
        }

        // New placement failed. Set piece back to its original placed position
        x = placedX;
        y = placedY;
        return false;
    }

    /**
     * Determines if a set of relative coordinates are within the boundaries of the board
     * @param x The x coordinate 0-1
     * @param y The y coordinate 0-1
     * @return true if coordinates are within the chess board
     */
    protected final boolean isWithinBoard(float x, float y) {
        return !(x < 1 / 16f) && !(x > 15 / 16f) && !(y < 1 / 16f) && !(y > 15 / 16f);
    }

    /**
     * Get all possible legal moves for this piece at its current location
     * @return An array of legal placement coordinates
     */
    protected abstract ArrayList<Float> getLegalMoves();

    /**
     * Determines if a set of relative coordinates are legal for piece placement
     * @param x The x coordinate 0-1
     * @param y The y coordinate 0-1
     * @return true if coordinates are valid
     */
    private boolean isValidPlacement(float x, float y) {
        ArrayList<Float> legalMoves = getLegalMoves();
        for (int i = 1; i < legalMoves.size(); i += 2) {
            if (Math.abs(x - legalMoves.get(i-1)) < 0.001f
                    && Math.abs(y - legalMoves.get(i)) < 0.001f) {
                return true;
            }
        }
        return false;
    }

    /**
     * Marks this piece for elimination
     */
    public void die() {
        chess.setDeadPiece(this);
    }

    /**
     * Get the color of this piece
     * @return The piece color
     */
    public final int getColor() {
        return color;
    }

    /**
     * Gets the placed x location
     * @return The placed x location
     */
    public final float getPlacedX() {
        return placedX;
    }

    /**
     * Sets the placed x location
     * @param placedX The x location to set to
     */
    public final void setPlacedX(float placedX) {
        this.placedX = placedX;
    }

    /**
     * Gets the placed y location
     * @return The placed y location
     */
    public final float getPlacedY() {
        return placedY;
    }

    /**
     * Sets the placed y location
     * @param placedY The y location to set to
     */
    public final void setPlacedY(int placedY) {
        this.placedY = placedY;
    }

    /**
     * Get the chess game this piece is a part of
     * @return The chess game
     */
    protected final Chess getChess() {
        return chess;
    }

    /**
     * Reloads transient variables
     * @param context The application context
     */
    public final void reload(@NonNull Context context) {
        piece = BitmapFactory.decodeResource(context.getResources(), id);
    }

    /**
     * Saves the current piece as XML
     * @param xml The XmlSerializer
     * @throws IOException if any issues occur during XML creation
     */
    public final void saveXml(@NonNull XmlSerializer xml) throws IOException {
        xml.startTag(null, "piece");
        saveTypeXml(xml);
        xml.attribute(null, "color", Integer.toString(color));
        xml.attribute(null, "placedX", Float.toString(placedX));
        xml.attribute(null, "placedY",Float.toString(placedY));
        xml.endTag(null, "piece");
    }

    /**
     * Saves to XML any appropriate type-specific values for this piece
     * @param xml The XmlSerializer
     * @throws IOException if any issues occur during XML creation
     */
    protected abstract void saveTypeXml(@NonNull XmlSerializer xml) throws IOException;

}
