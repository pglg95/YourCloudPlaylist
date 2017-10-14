package com.example.YourCloudPlaylist;

import android.os.Environment;

import java.io.File;

/**
 * Created by pglg on 13-03-2016.
 */
public class DeviceFile extends File implements YourCloudPlaylistFile {

    public DeviceFile(String path) {
        super(path);
    }

    @Override
    public DeviceFile[] childFilesArray() {
        DeviceFile[] output = new DeviceFile[super.listFiles().length];
        for (int i = 0; i < output.length; i++) {
            output[i] = new DeviceFile(super.listFiles()[i].getPath());
        }
        return output;
    }

    public static String getRoot() {
        return "/";
    }

    public static String getHome() {
        return Environment.getExternalStorageDirectory().getPath();
    }
}
