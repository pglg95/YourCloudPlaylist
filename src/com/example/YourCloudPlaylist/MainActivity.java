package com.example.YourCloudPlaylist;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends Activity {

    private final int DEVICE_EXPLORER = 1;
    private final int DROPBOX_EXPLORER = 2;
    private Boolean exit = false;
    private TextView playlistNameTextView;
    private TextView devicePathTextView;
    private TextView dropboxPathTextView;
    private String currentDir;

    private int lastEditedTextView = 0; // to avoid retouched text view

    /******To disable keyboard showing************/
    private View.OnTouchListener devicePathTextViewTouchBehaviour = new View.OnTouchListener() {
        public boolean onTouch(View v, MotionEvent event) {

            /*To disable opened keyboard*/
            View view = getCurrentFocus();
            if (view != null) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }

            if (lastEditedTextView != 1) {
                lastEditedTextView = 1;
                onDeviceExplorerButtonClick(v);
            }
            return true;
        }
    };

    private View.OnTouchListener dropboxPathTextViewTouchBehaviour = new View.OnTouchListener() {
        public boolean onTouch(View v, MotionEvent event) {

              /*To disable opened keyboard*/
            View view = getCurrentFocus();
            if (view != null) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }

            if (lastEditedTextView != 2) {
                lastEditedTextView = 2;
                onDropboxExplorerButtonClick(v);
            }
            return true;
        }
    };

    /********************************************************/
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        playlistNameTextView = (TextView) findViewById(R.id.playlist_name);
        devicePathTextView = (TextView) findViewById(R.id.device_path);
        devicePathTextView.setOnTouchListener(devicePathTextViewTouchBehaviour);
        dropboxPathTextView = (TextView) findViewById(R.id.dropbox_path);
        dropboxPathTextView.setOnTouchListener(dropboxPathTextViewTouchBehaviour);

    }

    @Override
    public void onBackPressed() {
        if (exit) {
            finish();
        } else {
            Toast.makeText(this, "Press Back again to Exit. ",
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
    public void onResume() {
        super.onResume();
    }

    public void onDeviceExplorerButtonClick(View view) {

        Intent openExplorer = new Intent(this, FileExplorer.class);
        openExplorer.putExtra("mode", FileType.DEVICE_FILE);
        startActivityForResult(openExplorer, DEVICE_EXPLORER);
    }

    public void onDropboxExplorerButtonClick(View view) {
        Intent openExplorer = new Intent(this, FileExplorer.class);
        openExplorer.putExtra("mode", FileType.DROPBOX_FILE);
        startActivityForResult(openExplorer, DROPBOX_EXPLORER);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            currentDir = data.getExtras().getString("path");
            switch (requestCode) {
                case DEVICE_EXPLORER: {
                    devicePathTextView.setText(currentDir);
                    break;
                }
                case DROPBOX_EXPLORER: {
                    dropboxPathTextView.setText(currentDir);
                }
            }
        }
    }

    public void onGenerateButtonClick(View view) {
        if (isFieldsContentValid()) {
            String playlistName = String.valueOf(playlistNameTextView.getText());
            new PlaylistGenerator(this).execute(devicePathTextView.getText().toString(),
                    dropboxPathTextView.getText().toString(),
                    playlistName);
        } else
            Toast.makeText(this, this.getResources().getString(R.string.field_validation_error), Toast.LENGTH_LONG).show();

    }

    private boolean isFieldsContentValid() {
        return playlistNameTextView.getText().length() > 0 && devicePathTextView.getText().length() > 0
                && dropboxPathTextView.getText().length() > 0;
    }
}
