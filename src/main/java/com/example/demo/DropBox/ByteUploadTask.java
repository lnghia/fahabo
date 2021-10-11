package com.example.demo.DropBox;

import com.dropbox.core.DbxApiException;
import com.dropbox.core.DbxException;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.sharing.PathLinkMetadata;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.Callable;

@Slf4j
public class ByteUploadTask implements Callable<ByteUploadTask.ByteUploadResult> {

    private DbxClientV2 dbxClientV2;

    private final InputStream fileStreamToUpload;

    private final String fileName;

    public ByteUploadTask(InputStream fileStream, String fileName, DbxClientV2 clientV2) {
        this.fileStreamToUpload = fileStream;
        this.fileName = fileName;
        dbxClientV2 = clientV2;
    }

    public InputStream getFileStreamToUpload() {
        return fileStreamToUpload;
    }

    @Override
    public ByteUploadResult call() {
        try{
            FileMetadata metadata = dbxClientV2.files().uploadBuilder("/" + fileName).uploadAndFinish(fileStreamToUpload);

//            PathLinkMetadata pathLinkMetadata = dbxClientV2.sharing().createSharedLink("/" + fileName);

//            URLConnection con = new URL(pathLinkMetadata.getUrl().replace("dl=0", "raw=1")).openConnection();
//            con.connect();
//            InputStream in = con.getInputStream();
//            log.info(con.getURL().toString());
//            in.close();

//            if(metadata.get.getUrl() != null && !pathLinkMetadata.getUrl().isEmpty()){
//                return ByteUploadResult.createSuccessResult(fileStreamToUpload, con.getURL().toString(), fileName);
//            }
            return ByteUploadResult.createSuccessResult(fileStreamToUpload, null, fileName);
//            return ByteUploadResult.createFailureResultWithThrowable(fileStreamToUpload, new Exception("Unknown error."), fileName);
        }
        catch (Exception ex){
            return ByteUploadResult.createFailureResultWithThrowable(fileStreamToUpload, ex, fileName);
        }
    }

    @Data
    public static class ByteUploadResult {
        public final InputStream fileStreamToUpload;

        public final String name;

        public final String uri;

        public final Throwable error;

        public ByteUploadResult(InputStream fileStreamToUpload, Throwable error, String uri, String name) {
            this.fileStreamToUpload = fileStreamToUpload;
            this.uri = uri;
            this.error = error;
            this.name = name;
        }

        public ByteUploadResult(InputStream fileStreamToUpload, Throwable error, String name){
            this.fileStreamToUpload = fileStreamToUpload;
            this.error = error;
            this.name = name;
            this.uri = "";
        }

        public boolean isOk() {
            return error == null;
        }

        public static ByteUploadResult createSuccessResult(InputStream file, String uri, String name) {
            return new ByteUploadResult(file, null, name);
        }

        public static ByteUploadResult createFailureResultWithThrowable(InputStream file, Throwable error, String name) {
            return new ByteUploadResult(file, error, name);
        }
    }
}
