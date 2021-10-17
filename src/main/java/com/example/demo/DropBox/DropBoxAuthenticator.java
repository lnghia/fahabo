package com.example.demo.DropBox;

import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

@Component
public class DropBoxAuthenticator {
//    private static DropBoxAuthenticator instance;

    @Autowired
    private DropBoxConfig dropBoxConfig;

//    public static DropBoxAuthenticator getInstance(){
//        if(instance == null) instance = new DropBoxAuthenticator();
//        return instance;
//    }

    public DbxClientV2 authenticateDropBoxClient(){
        DbxRequestConfig config = new DbxRequestConfig("fahaboUploader/v1.0", "en_US");
        return new DbxClientV2(config, dropBoxConfig.getDROPBOX_ACCESS_TOKEN());
    }
}
