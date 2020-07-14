/*
 * Author: Jaideep Prasad
 * CSE 476 Spring 2020
 */

package edu.msu.prasadj2.chess.Cloud.Models;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

@Root(name = "chess")
public class MatchmakingResult {
    @Attribute
    private String status;

    @Attribute(name = "msg", required = false)
    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Attribute(name = "playerOne", required = false)
    private String playerOneName;

    public String getPlayerOneName() {
        return playerOneName;
    }

    public void setPlayerOneName(String playerOneName) {
        this.playerOneName = playerOneName;
    }

    @Attribute(name = "playerTwo", required = false)
    private String playerTwoName;

    public String getPlayerTwoName() {
        return playerTwoName;
    }

    public void setPlayerTwoName(String playerTwoName) {
        this.playerTwoName = playerTwoName;
    }

    public MatchmakingResult() {}

    public MatchmakingResult(String status, String msg,
                             String playerOneName, String playerTwoName) {
        this.status = status;
        this.message = msg;
        this.playerOneName = playerOneName;
        this.playerTwoName = playerTwoName;
    }
}
