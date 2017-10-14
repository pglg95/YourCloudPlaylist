package com.example.YourCloudPlaylist;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by pglg on 20-03-2016.
 */
public class InitialActivity extends Activity {
    private Button button;
    private TextView textView;
    private String secret;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.connectionerr);
        textView = (TextView) findViewById(R.id.connectionerrTextView);
        button = (Button) findViewById(R.id.refreshButton);
        textView.setVisibility(View.INVISIBLE);
        button.setVisibility(View.INVISIBLE);

        if (isAccessTokenSaved()) {
            new DropboxClientInitializer().execute(secret);
        } else {
            Intent openLogActivity = new Intent(this, LogActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    .addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(openLogActivity);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private boolean isAccessTokenSaved() {
        SharedPreferences prefs = getSharedPreferences("prefs", 0);
        String key = prefs.getString("ACCESS_KEY", null);
        String secret = prefs.getString("ACCESS_SECRET", null);
        if (secret != null && key != null) {
            this.secret = secret;
            return true;
        } else return false;
    }

    public void onRefreshButtonClick(View view) {
        textView.setVisibility(View.INVISIBLE);
        button.setVisibility(View.INVISIBLE);
        new DropboxClientInitializer().execute(secret);
    }

    class DropboxClientInitializer extends AsyncTask<String, Void, Boolean> {

        private ProgressDialog dialog = new ProgressDialog(InitialActivity.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            this.dialog.setMessage("Wait..");
            this.dialog.show();
        }

        @Override
        protected Boolean doInBackground(String... params) {
            try {
                DropboxApi.initializeDropboxClient(params[0]);
                return true;
            } catch (Exception e) {
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if (result) {
                Intent openLogActivity = new Intent(InitialActivity.this, MainActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        .addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(openLogActivity);
            } else {
                textView.setVisibility(View.VISIBLE);
                button.setVisibility(View.VISIBLE);
            }
        }
    }
}
