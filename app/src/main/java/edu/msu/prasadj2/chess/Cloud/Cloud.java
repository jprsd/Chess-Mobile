/*
 * Author: Jaideep Prasad
 * CSE 476 Spring 2020
 */

package edu.msu.prasadj2.chess.Cloud;

import android.util.Log;
import android.util.Xml;

import androidx.annotation.NonNull;

import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;
import java.io.StringWriter;

import edu.msu.prasadj2.chess.ChessActivity;
import edu.msu.prasadj2.chess.Cloud.Models.GameLoadResult;
import edu.msu.prasadj2.chess.Cloud.Models.GameSaveResult;
import edu.msu.prasadj2.chess.Cloud.Models.LoginResult;
import edu.msu.prasadj2.chess.Cloud.Models.LogoutResult;
import edu.msu.prasadj2.chess.Cloud.Models.MatchmakingResult;
import edu.msu.prasadj2.chess.Cloud.Models.RegisterResult;
import edu.msu.prasadj2.chess.MatchmakingActivity;
import retrofit2.Retrofit;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

/**
 * Class for dealing with all cloud-related tasks
 */
@SuppressWarnings("deprecation")
public class Cloud {
    public static final int POLLING_DELAY = 1000;

    private static final String MAGIC = "Ax34Y?lz8Pyq3r9!";
    private static final String BASE_URL = "https://webdev.cse.msu.edu/~prasadj2/cse476/project2/";
    public static final String REGISTER_PATH = "register.php";
    public static final String LOGIN_PATH = "login_version2.php";
    public static final String LOGOUT_PATH = "logout.php";
    public static final String MATCHMAKING_PATH = "matchmaking.php";
    public static final String LOAD_PATH = "load.php";
    public static final String SAVE_PATH = "save.php";

    private static String COOKIE;

    private static Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(SimpleXmlConverterFactory.create())
            .build();

    /**
     * Attempts to register the user
     * @param userID UserID
     * @param password Password
     * @return true if the registration was successful
     */
    public boolean register(String userID, String password) {
        userID = userID.trim();
        password = password.trim();
        if (userID.length() == 0 || password.length() == 0) {
            return false;
        }

        XmlSerializer xml = Xml.newSerializer();
        StringWriter writer = new StringWriter();
        try {
            configureUserInfoXml(xml, writer, userID, password);
        } catch (IOException e) {
            return false;
        }

        ChessService service = retrofit.create(ChessService.class);
        final String xmlStr = writer.toString();
        try {
            RegisterResult result = service.registerUser(xmlStr).execute().body();
            if (result != null && result.getStatus() != null && result.getStatus().equals("yes")) {
                return true;
            }
            String message = result != null ? result.getMessage() : "unknown error";
            Log.e("Register", "Failed to register user, message = '" + message + "'");
            return false;
        } catch (IOException e) {
            Log.e("Register", "Exception occurred while trying to register user!", e);
            return false;
        }
    }

    /**
     * Attempts to login the user
     * @param userID UserID
     * @param password Password
     * @return true if login was successful
     */
    public boolean login(String userID, String password) {
        userID = userID.trim();
        password = password.trim();
        if (userID.length() == 0 || password.length() == 0) {
            return false;
        }

        XmlSerializer xml = Xml.newSerializer();
        StringWriter writer = new StringWriter();
        try {
            configureUserInfoXml(xml, writer, userID, password);
        } catch (IOException e) {
            return false;
        }

        ChessService service = retrofit.create(ChessService.class);
        final String xmlStr = writer.toString();
        try {
            LoginResult result = service.loginUser(xmlStr).execute().body();
            if (result != null && result.getStatus() != null && result.getStatus().equals("yes")) {
                COOKIE = "PHPSESSID=" + result.getMessage() + ";";
                return true;
            }
            String message = result != null ? result.getMessage() : "unknown error";
            Log.e("Login", "Failed to login user, message = '" + message + "'");
            return false;
        } catch (IOException e) {
            Log.e("Login", "Exception occurred while trying to login user!", e);
            return false;
        }
    }

    /**
     * Logs out the current user immediately (inactive session will auto log out eventually)
     * @return true if the process was immediately successful
     */
    public boolean logout() {
        XmlSerializer xml = Xml.newSerializer();
        StringWriter writer = new StringWriter();
        try {
            configureSessionXml(xml, writer);
        } catch (IOException e) {
            return false;
        }

        ChessService service = retrofit.create(ChessService.class);
        final String xmlStr = writer.toString();
        try {
            LogoutResult result = service.logoutUser(COOKIE, xmlStr).execute().body();
            if (result != null && result.getStatus() != null && result.getStatus().equals("yes")) {
                return true;
            }
            String message = result != null ? result.getMessage() : "unknown error";
            Log.e("Logout", "Failed to logout, message = '" + message + "'");
            return false;
        } catch (IOException e) {
            Log.e("Logout", "Exception occurred during logout");
            return false;
        }
    }

    /**
     * Attempts to pair the current player with an opponent
     * @param matchmakingActivity The activity that is responsible for the search
     * @return true if an opponent was found
     */
    public boolean findOpponent(MatchmakingActivity matchmakingActivity) {
        XmlSerializer xml = Xml.newSerializer();
        StringWriter writer = new StringWriter();
        try {
            configureSessionXml(xml, writer);
        } catch (IOException e) {
            return false;
        }

        ChessService service = retrofit.create(ChessService.class);
        final String xmlStr = writer.toString();
        try {
            MatchmakingResult result = service.findOpponent(COOKIE, xmlStr).execute().body();
            if (result != null && result.getStatus() != null && result.getStatus().equals("yes")) {
                matchmakingActivity.setPlayerNames(result.getPlayerOneName(),
                        result.getPlayerTwoName());
                Log.i("Matchmaking", "Opponent found");
                return true;
            }
            String message = result != null ? result.getMessage() : "unknown error";
            Log.e("Matchmaking", "Failed to find opponent, message = '" + message + "'");
            return false;
        } catch (IOException e) {
            Log.e("Matchmaking", "Exception occurred during matchmaking");
            return false;
        }
    }

    /**
     * Polls the server for updates to the current game
     * @param chessActivity The activity that is responsible for handling the game
     * @return true if an update occurred
     */
    public boolean loadGame(ChessActivity chessActivity) {
        XmlSerializer xml = Xml.newSerializer();
        StringWriter writer = new StringWriter();
        try {
            configureSessionXml(xml, writer);
        } catch (IOException e) {
            return false;
        }

        ChessService service = retrofit.create(ChessService.class);
        final String xmlStr = writer.toString();
        try {
            GameLoadResult result = service.loadGame(COOKIE, xmlStr).execute().body();
            if (result != null && result.getStatus() != null && result.getStatus().equals("yes")) {
                chessActivity.setServerPiecesData(result.getPiecesData());
                return true;
            }
            else if (result != null && result.getStatus() != null
                    && result.getStatus().equals("resign")) {
                chessActivity.flagOpponentResigned();
                return true;
            }
            String message = result != null ? result.getMessage() : "unknown error";
            Log.e("LoadGame", "Failed to update game state, message = '" + message + "'");
            return false;
        } catch (IOException e) {
            Log.e("LoadGame", "Exception occurred during game state update");
            return false;
        }
    }

    /**
     * Pushes the local game state to the server
     * @param chessActivity The activity that is responsible for handling the game
     * @param resign Determines xml configuration(standard vs resign)
     * @return true if the update was successfully delivered
     */
    public boolean saveGame(ChessActivity chessActivity, boolean resign) {
        XmlSerializer xml = Xml.newSerializer();
        StringWriter writer = new StringWriter();
        try {
            xml.setOutput(writer);

            xml.startDocument("UTF-8", true);

            xml.startTag(null, "chess");
            xml.attribute(null, "magic", MAGIC);

            if (resign) {
                xml.startTag(null, "resign");
                xml.endTag(null, "resign");
            }
            else {
                chessActivity.saveGame(xml);
            }

            xml.endTag(null, "chess");

            xml.endDocument();
        } catch (IOException e) {
            return false;
        }

        ChessService service = retrofit.create(ChessService.class);
        final String xmlStr = writer.toString();
        try {
            GameSaveResult result = service.saveGame(COOKIE, xmlStr).execute().body();
            if (result != null && result.getStatus() != null && result.getStatus().equals("yes")) {
                return true;
            }
            String message = result != null ? result.getMessage() : "unknown error";
            Log.e("SaveGame", "Failed to send game state, message = '" + message + "'");
            return false;
        } catch (IOException e) {
            Log.e("SaveGame", "Exception occurred during game state delivery");
            return false;
        }
    }

    /**
     * Configures the given XmlSerializer with the provided user info
     * @param xml The XmlSerializer
     * @param writer The StringWriter
     * @param userID UserID
     * @param password Password
     * @throws IOException if any issues occur during xml creation
     */
    private void configureUserInfoXml(@NonNull XmlSerializer xml, @NonNull StringWriter writer,
                                      String userID, String password) throws IOException {
        xml.setOutput(writer);

        xml.startDocument("UTF-8", true);

        xml.startTag(null, "chess");

        xml.attribute(null, "user", userID);
        xml.attribute(null, "pw", password);
        xml.attribute(null, "magic", MAGIC);


        xml.endTag(null, "chess");

        xml.endDocument();
    }

    /**
     * Configures the given XmlSerializer for basic session based communications
     * @param xml The XmlSerializer
     * @param writer The StringWriter
     * @throws IOException if any issues occur during xml creation
     */
    private void configureSessionXml(@NonNull XmlSerializer xml,
                                     @NonNull StringWriter writer) throws IOException {
        xml.setOutput(writer);

        xml.startDocument("UTF-8", true);

        xml.startTag(null, "chess");
        xml.attribute(null, "magic", MAGIC);
        xml.endTag(null, "chess");

        xml.endDocument();
    }

}
