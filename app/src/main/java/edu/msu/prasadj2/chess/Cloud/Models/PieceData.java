/*
 * Author: Jaideep Prasad
 * CSE 476 Spring 2020
 */

package edu.msu.prasadj2.chess.Cloud.Models;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

@Root(name = "piece")
public class PieceData {

    @Attribute
    private String type;

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    @Attribute
    private int color;

    public void setColor(int color) {
        this.color = color;
    }

    public int getColor() {
        return color;
    }

    @Attribute
    private float placedX;

    public void setPlacedX(float placedX) {
        this.placedX = placedX;
    }

    public float getPlacedX() {
        return placedX;
    }

    @Attribute
    private float placedY;

    public void setPlacedY(float placedY) {
        this.placedY = placedY;
    }

    public float getPlacedY() {
        return placedY;
    }

    @Attribute(required = false)
    private float startY;

    public void setStartY(float startY) {
        this.startY = startY;
    }

    public float getStartY() {
        return startY;
    }

    public PieceData() {}

    public PieceData(String type, int color, float placedX, float placedY, float startY) {
        this.type = type;
        this.color = color;
        this.placedX = placedX;
        this.placedY = placedY;
        this.startY = startY;
    }
}
