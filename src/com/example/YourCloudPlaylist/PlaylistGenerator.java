package com.example.YourCloudPlaylist;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;
import com.dropbox.core.DbxException;
import com.dropbox.core.v2.files.FolderMetadata;
import com.dropbox.core.v2.files.Metadata;
import com.dropbox.core.v2.sharing.SharedLinkMetadata;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by pglg on 22-03-2016.
 */
public class PlaylistGenerator extends AsyncTask<String, String, Void> {

    private ProgressDialog dialog;
    private Context context;
    private String exceptionText;

    PlaylistGenerator(Context context) {
        this.context = context;
        dialog = new ProgressDialog(context);
    }

    private String toDirectURL(String shareURL) {
        char[] chars = shareURL.toCharArray();
        chars[chars.length - 1] = '1';
        return String.valueOf(chars);
    }

    private String getShareURL(String strURL) throws Exception {
        URLConnection conn = null;
        String redirectedUrl = null;
        try {
            URL inputURL = new URL(strURL);
            conn = inputURL.openConnection();
            conn.connect();

            InputStream is = conn.getInputStream();
            redirectedUrl = conn.getURL().toString();
            is.close();

        } catch (MalformedURLException e) {
            throw new Exception(context.getResources().getString(R.string.connection_error));
        } catch (IOException e) {
            throw new Exception(context.getResources().getString(R.string.connection_error));
        }

        return toDirectURL(redirectedUrl);
    }

    private void makePlaylist(String path, String name, List<String> urls, List<String> names) throws Exception {

        StringBuilder playlistText = new StringBuilder("#EXTM3U");
        try {
            File playlist = new File(path, File.separator + name + ".m3u");
            playlist.createNewFile();
            FileOutputStream fOut = new FileOutputStream(playlist);
            playlistText.append(System.getProperty("line.separator"));
            for (int i = 0; i < urls.size(); i++) {
                playlistText.append("#EXTINF:-1,");
                playlistText.append(names.get(i));
                playlistText.append(System.getProperty("line.separator"));
                playlistText.append(urls.get(i));
                playlistText.append(System.getProperty("line.separator"));
            }
            fOut.write(playlistText.toString().getBytes());
            fOut.close();
        } catch (IOException e) {
            throw new Exception(context.getResources().getString(R.string.saving_file_error));
        }

    }

    @Override
    protected void onPreExecute() {
        this.dialog.setMessage(context.getResources().getString(R.string.wait));
        this.dialog.setIndeterminate(false);
        this.dialog.setCancelable(false);
        this.dialog.show();
    }

    private List<SharedLinkMetadata> getShareFilesInfo() {
        try {
            return DropboxApi.clientV2.sharing().listSharedLinks().getLinks();
        } catch (DbxException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String obtainSharedUrlFromFilePathLower(String fileLowerPath, List<SharedLinkMetadata> sharedFilesInfo) {
        String obtainedUrl = null;
        for (SharedLinkMetadata metadata : sharedFilesInfo) {
            if (metadata.getPathLower().equals(fileLowerPath)) {
                obtainedUrl = metadata.getUrl();
                break;
            }

        }
        return obtainedUrl;
    }


    @Override
    protected Void doInBackground(String... params) {

        String devicePath = params[0];
        String dropboxPath = params[1];
        String playlistName = params[2];
        List<SharedLinkMetadata> sharedFilesInfo = getShareFilesInfo();
        List<String> urls = new ArrayList<>();
        List<String> names = new ArrayList<>();

        try {
            List<Metadata> directoryContent = DropboxApi.clientV2.files().listFolder(dropboxPath).getEntries();
            for (Metadata metadata : directoryContent) {
                if (!(metadata instanceof FolderMetadata)) {
                    publishProgress(context.getResources().getString(R.string.making_link) + "\n" + metadata.getName());
                    String obtainedUrl = obtainSharedUrlFromFilePathLower(metadata.getPathLower(), sharedFilesInfo);
                    if (obtainedUrl == null)
                        obtainedUrl = DropboxApi.clientV2.sharing().createSharedLinkWithSettings(metadata.getPathDisplay()).getUrl();

                    urls.add(getShareURL(obtainedUrl));
                    names.add(metadata.getName());
                }
            }
            publishProgress(context.getResources().getString(R.string.creating_playlist));
            makePlaylist(devicePath, playlistName, urls, names);
        } catch (DbxException e) {
            exceptionText = e.getMessage();
            return null;
        } catch (Exception e) {
            exceptionText = e.getMessage();
            return null;
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
        this.dialog.setMessage(values[0]);
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        this.dialog.cancel();
        Toast.makeText(context, context.getResources().getString(R.string.incorrect_playlist),
                Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
        if (exceptionText != null) {
            Toast.makeText(context, context.getResources().getString(R.string.error_occured) + "\n" + exceptionText, Toast.LENGTH_LONG).show();
        }

    }
}
