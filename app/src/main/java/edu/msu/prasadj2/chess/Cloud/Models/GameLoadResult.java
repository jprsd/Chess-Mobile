/*
 * Author: Jaideep Prasad
 * CSE 476 Spring 2020
 */

package edu.msu.prasadj2.chess.Cloud.Models;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.ArrayList;

@Root(name = "chess")
public class GameLoadResult {
    @Attribute
    private String status;

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    @Attribute(name = "msg", required = false)
    private String message;

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    @ElementList(entry = "piece", type = PieceData.class, inline = true, required = false)
    private ArrayList<PieceData> piecesData;

    public void setPiecesData(ArrayList<PieceData> piecesData) {
        this.piecesData = piecesData;
    }

    public ArrayList<PieceData> getPiecesData() {
        return piecesData;
    }

    public GameLoadResult() {}

    public GameLoadResult(String status, String msg, ArrayList<PieceData> piecesData) {
        this.status = status;
        this.message = msg;
        this.piecesData = piecesData;
    }
}
