package com.example.YourCloudPlaylist;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import com.dropbox.core.DbxException;
import com.dropbox.core.v2.files.FolderMetadata;
import com.dropbox.core.v2.files.Metadata;

import java.util.List;

/**
 * Created by pglg on 12-03-2016.
 */
public class FileManager extends AsyncTask<String, Void, YourCloudPlaylistFile> {

    private ProgressDialog dialog;
    private FileType typeOfFile;
    private YourCloudPlaylistFileAsyncResponse delegate;

    public FileManager(FileType typeOfFile, Context context, YourCloudPlaylistFileAsyncResponse delegate) {
        this.delegate = delegate;
        dialog = new ProgressDialog(context);
        this.typeOfFile = typeOfFile;
    }

    @Override
    protected void onPreExecute() {
        if (typeOfFile != FileType.DEVICE_FILE) {
            this.dialog.setMessage("Wait..");
            this.dialog.setIndeterminate(false);
            this.dialog.setCancelable(false);
            this.dialog.show();
        }
    }


    private DropboxFile[] getChildFilesForFilePath(String path) throws DbxException {
        List<Metadata> innerElems = DropboxApi.clientV2.files().listFolder(path).getEntries();
        DropboxFile[] fileArray = new DropboxFile[innerElems.size()];
        for (int i = 0; i < innerElems.size(); i++) {
            fileArray[i] = DropboxFile.fromMetadata(innerElems.get(i));
        }

        return fileArray;
    }

    private DropboxFile[] getChildFilesForFileMetadata(Metadata metadata) throws DbxException {
        if (metadata instanceof FolderMetadata) {
            return getChildFilesForFilePath(metadata.getPathDisplay());
        } else return null;
    }

    private YourCloudPlaylistFile obtainDropboxFileForPath(String path) {
        DropboxFile dropboxFile;
        try {

            if (path.equals(DropboxFile.getRoot())) {
                dropboxFile = new DropboxFile("", "/", null, getChildFilesForFilePath(path), true);

            } else {
                Metadata elem = DropboxApi.clientV2.files().getMetadata(path);
                dropboxFile = DropboxFile.fromMetadata(elem).withChildFilesArray(getChildFilesForFileMetadata(elem));
            }

            return dropboxFile;
        } catch (DbxException e) {
            return null;
        }
    }

    private YourCloudPlaylistFile obtainDeviceFileForPath(String path) {
        return new DeviceFile(path);
    }

    @Override
    protected YourCloudPlaylistFile doInBackground(String... params) {

        if (params.length != 1 || params[0] == null) return null;
        String path = params[0];
        switch (typeOfFile) {
            case DROPBOX_FILE:
                return obtainDropboxFileForPath(path);
            case DEVICE_FILE:
                return obtainDeviceFileForPath(path);
            default:
                return null;
        }
    }

    @Override
    protected void onPostExecute(YourCloudPlaylistFile file) {
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
        delegate.processFinish(file);
    }
}
