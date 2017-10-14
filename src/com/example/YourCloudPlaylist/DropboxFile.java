package com.example.YourCloudPlaylist;


import com.dropbox.core.v2.files.FolderMetadata;
import com.dropbox.core.v2.files.Metadata;

/**
 * Created by pglg on 12-03-2016.
 */
public class DropboxFile implements YourCloudPlaylistFile {

    private String path;
    private boolean isDirectory;
    private String name;
    private String parentPath;
    private DropboxFile[] filesArray;

    DropboxFile(String path, String name, String parentPath, DropboxFile[] filesArray, boolean isDirectory) {
        this.path = path;
        this.name = name;
        this.parentPath = parentPath;
        this.filesArray = filesArray;
        this.isDirectory = isDirectory;
    }

    @Override
    public DropboxFile[] childFilesArray() {
        return filesArray;
    }

    @Override
    public boolean isHidden() {
        return false;
    }

    @Override
    public boolean canRead() {
        return true;
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public boolean isDirectory() {
        return isDirectory;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getParent() {
        return parentPath;
    }

    public static String getRoot() {
        return "";
    }

    public static String getHome() {
        return getRoot();
    }

    public static DropboxFile fromMetadata(Metadata metadata) {
        return new DropboxFile(metadata.getPathDisplay(),
                metadata.getName(),
                obtainParentPathForFilePath(metadata.getPathDisplay()),
                null,
                metadata instanceof FolderMetadata);
    }

    public static String obtainParentPathForFilePath(String filePath) {
        return filePath.substring(0, filePath.lastIndexOf("/"));
    }

    public DropboxFile withChildFilesArray(DropboxFile[] filesArray) {
        this.filesArray = filesArray;
        return this;
    }

}
