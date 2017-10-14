package com.example.YourCloudPlaylist;

/**
 * Created by pglg on 12-03-2016.
 */
public interface YourCloudPlaylistFile {
    Object[] childFilesArray();

    boolean isHidden();

    boolean canRead();

    String getPath();

    boolean isDirectory();

    String getName();

    String getParent();
}
