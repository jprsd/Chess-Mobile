/*
 * Author: Jaideep Prasad
 * CSE 476 Spring 2020
 */

package edu.msu.prasadj2.chess.Cloud.Models;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

@Root(name = "chess")
public class GameSaveResult {
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

    public GameSaveResult() {}

    public GameSaveResult(String status, String msg) {
        this.status = status;
        this.message = msg;
    }
}
