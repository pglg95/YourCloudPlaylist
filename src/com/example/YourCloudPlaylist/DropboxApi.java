package com.example.YourCloudPlaylist;

import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;

/**
 * Created by pglg on 28-02-2016.
 */
public class DropboxApi {

    static DbxClientV2 clientV2;

    static void initializeDropboxClient(String accessToken) {

        DbxRequestConfig requestConfig = DbxRequestConfig.newBuilder("YourCloudPlaylist")
                .build();

        clientV2 = new DbxClientV2(requestConfig, accessToken);
    }
}
