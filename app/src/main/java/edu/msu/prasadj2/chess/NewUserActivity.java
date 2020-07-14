/*
 * Author: Jaideep Prasad
 * CSE 476 Spring 2020
 */

package edu.msu.prasadj2.chess;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import edu.msu.prasadj2.chess.Cloud.Cloud;

/**
 * Activity for creating a new user
 */
public class NewUserActivity extends AppCompatActivity {

    /**
     * Creates the activity
     * @param savedInstanceState The previously saved activity state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
    }

    /**
     * Attempts to create a new user when the register button is pressed
     * @param view The current view
     */
    public void onRegister(@NonNull final View view) {
        final String userID =
                ((EditText)findViewById(R.id.playerName)).getText().toString().trim();
        final String passwordOne =
                ((EditText)findViewById(R.id.passwordOne)).getText().toString().trim();
        String passwordTwo =
                ((EditText)findViewById(R.id.passwordTwo)).getText().toString().trim();

        if (userID.length() <= 0) {
            Toast.makeText(this,
                    R.string.user_id_empty_error, Toast.LENGTH_SHORT).show();
            return;
        }
        for (int i = 0; i < userID.length(); i++) {
            if (!Character.isLetterOrDigit(userID.charAt(i))) {
                Toast.makeText(this,
                        R.string.username_invalid_characters, Toast.LENGTH_SHORT).show();
                return;
            }
        }
        if (!passwordOne.equals(passwordTwo)) {
            Toast.makeText(this,
                    R.string.passwords_mismatch_error, Toast.LENGTH_SHORT).show();
            return;
        }
        if (passwordOne.length() < 8) {
            Toast.makeText(this,
                    R.string.password_short_error, Toast.LENGTH_SHORT).show();
            return;
        }
        for (int i = 0; i < passwordOne.length(); i++) {
            if (!Character.isLetterOrDigit(passwordOne.charAt(i))) {
                Toast.makeText(this,
                        R.string.password_invalid_characters, Toast.LENGTH_SHORT).show();
                return;
            }
        }

        view.setEnabled(false);
        final Button cancelButton = (Button)findViewById(R.id.cancel_button);
        cancelButton.setEnabled(false);

        new Thread(new Runnable() {
            @Override
            public void run() {
                Cloud cloud = new Cloud();
                final boolean registered = cloud.register(userID, passwordOne);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        view.setEnabled(true);
                        cancelButton.setEnabled(true);
                        if (registered) {
                            Toast.makeText(NewUserActivity.this,
                                    R.string.registration_successful, Toast.LENGTH_LONG).show();
                            onCancel(null);
                        }
                        else {
                            Toast.makeText(NewUserActivity.this,
                                    R.string.register_server_error, Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        }).start();
    }

    /**
     * Handler for when the cancel button is pressed
     * @param view The current view
     */
    public void onCancel(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    /**
     * Back button handler override
     */
    @Override
    public void onBackPressed() {
        onCancel(null);
    }

}
