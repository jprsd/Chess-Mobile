/*
 * Author: Jaideep Prasad
 * CSE 476 Spring 2020
 */

package edu.msu.prasadj2.chess;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.net.CookieHandler;
import java.net.CookieManager;

import edu.msu.prasadj2.chess.Cloud.Cloud;

/**
 * Class for the main activity
 */
public class MainActivity extends AppCompatActivity {

    public static final String CURRENT_USER_DATA = "MainActivity.CurrentUserName";

    private static final String PREFERENCES = "Chess.MainActivity.LoginPreferences";
    private static final String REMEMBER = "Chess.MainActivity.RememberLogin";
    private static final String USER_ID = "Chess.MainActivity.UserID";
    private static final String USER_PASSWORD = "Chess.MainActivity.UserPassword";

    private static final String STATE = "MainActivity.state";

    // The instructions dialog box
    private AlertDialog instructionsDlg = null;

    /**
     * Creates the activity
     * @param savedInstanceState The previously saved activity state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        readPreferences();
        if (savedInstanceState != null && savedInstanceState.getBoolean(STATE)) {
            onShowInstructions(null);
        }

        CookieManager cookieManager = new CookieManager();
        CookieHandler.setDefault(cookieManager);
    }

    /**
     * Destroys the activity
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (instructionsDlg != null && instructionsDlg.isShowing()) {
            instructionsDlg.dismiss();
        }
    }

    /**
     * Saves the state of this activity
     * @param bundle The bundle to save into
     */
    @Override
    protected void onSaveInstanceState(@NonNull Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putBoolean(STATE, instructionsDlg != null && instructionsDlg.isShowing());
    }

    /**
     * Button handler for logging in
     * @param view The current application view
     */
    public void onLogin(@NonNull final View view) {
        writePreferences();
        final String userID = ((EditText)findViewById(R.id.userName)).getText().toString().trim();
        final String password = ((EditText)findViewById(R.id.password)).getText().toString().trim();

        if (userID.length() <= 0 || password.length() < 8) {
            Toast.makeText(this,
                    R.string.invalid_login_credentials, Toast.LENGTH_SHORT).show();
            return;
        }

        view.setEnabled(false);
        final Button registerButton = (Button)findViewById(R.id.register_button);
        registerButton.setEnabled(false);

        new Thread(new Runnable() {
            @Override
            public void run() {
                Cloud cloud = new Cloud();
                final boolean loggedIn = cloud.login(userID, password);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        view.setEnabled(true);
                        registerButton.setEnabled(true);
                        if (loggedIn) {
                            Intent intent = new Intent(MainActivity.this,
                                    MatchmakingActivity.class);
                            intent.putExtra(CURRENT_USER_DATA, userID);
                            startActivity(intent);
                        }
                        else {
                            Toast.makeText(MainActivity.this,
                                    R.string.login_server_error, Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        }).start();
    }

    /**
     * Button handler for explaining how to play the game
     * @param view The current application view
     */
    @SuppressLint("InflateParams")
    public void onShowInstructions(View view) {
        if (instructionsDlg != null && instructionsDlg.isShowing()) {
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.instructions_title);

        LayoutInflater layoutInflater = getLayoutInflater();
        builder.setView(layoutInflater.inflate(R.layout.dialog_instructions, null));
        builder.setPositiveButton(android.R.string.ok, null);

        instructionsDlg = builder.create();
        instructionsDlg.show();
    }

    /**
     * Button handler for registering a new user
     * @param view The current view
     */
    public void onStartRegister(View view) {
        Intent newUserIntent = new Intent(this, NewUserActivity.class);
        startActivity(newUserIntent);
    }

    /**
     * Remember checkbox handler
     * @param view The checkbox view
     */
    public void onRemember(View view) {
        writePreferences();
    }

    /**
     * Loads user login preferences for the app
     */
    private void readPreferences() {
        SharedPreferences settings = getSharedPreferences(PREFERENCES, MODE_PRIVATE);
        ((CheckBox)findViewById(R.id.rememberCheckBox))
                .setChecked(settings.getBoolean(REMEMBER, false));
        ((EditText)findViewById(R.id.userName)).setText(settings.getString(USER_ID, ""));
        ((EditText)findViewById(R.id.password)).setText(settings.getString(USER_PASSWORD, ""));
    }

    /**
     * Saves user login preferences for the app
     */
    private void writePreferences() {
        SharedPreferences settings = getSharedPreferences(PREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();

        boolean remember = ((CheckBox)findViewById(R.id.rememberCheckBox)).isChecked();
        editor.putBoolean(REMEMBER, remember);
        editor.putString(USER_ID, remember ?
                ((EditText)findViewById(R.id.userName)).getText().toString().trim() : "");
        editor.putString(USER_PASSWORD, remember ?
                ((EditText)findViewById(R.id.password)).getText().toString().trim() : "");

        editor.apply();
    }

}

