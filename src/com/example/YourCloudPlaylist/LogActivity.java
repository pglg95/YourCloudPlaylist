package com.example.YourCloudPlaylist;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;
import com.dropbox.core.android.Auth;

/**
 * Created by pglg on 20-03-2016.
 */
public class LogActivity extends Activity {

    private Boolean exit = false;
    private boolean immediatelyAfterAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.logging);
    }

    /*The Handler here handles accidental back presses,
     it simply shows a Toast, and if there is another back press within 3 seconds,
     it closes the application.
     */
    @Override
    public void onBackPressed() {
        if (exit) {
            finish(); // finish activity
        } else {
            Toast.makeText(this, "Press Back again to Exit.",
                    Toast.LENGTH_SHORT).show();
            exit = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    exit = false;
                }
            }, 3 * 1000);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Auth.getOAuth2Token() != null) {
            try {
                String accessToken = Auth.getOAuth2Token();
                storeAuth(accessToken);
                DropboxApi.initializeDropboxClient(accessToken);

                Intent openMyActivity = new Intent(this, MainActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        .addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(openMyActivity);


            } catch (IllegalStateException e) {
                Toast.makeText(this, "Error authenticating2", Toast.LENGTH_LONG).show();
            }
        } else if (immediatelyAfterAuth) {
            Toast.makeText(this, "Error authenticating", Toast.LENGTH_LONG).show();
            immediatelyAfterAuth = false;
        }
    }

    private void storeAuth(String accessToken) {
        if (accessToken != null) {
            SharedPreferences prefs = getSharedPreferences("prefs", 0);
            SharedPreferences.Editor edit = prefs.edit();
            edit.putString("ACCESS_KEY", "oauth2:");
            edit.putString("ACCESS_SECRET", accessToken);
            edit.commit();
        }
    }

    public void onExitButtonClick(View view) {
        finish();
    }

    public void onLogInButtonClick(View view) {
        new Authentication().execute();
    }

    private class Authentication extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            Auth.startOAuth2Authentication(LogActivity.this, getString(R.string.app_key));
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            immediatelyAfterAuth = true;
        }
    }
}
