package com.example.demo.DropBox;

import com.example.demo.Helpers.Helper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.Callable;

@Slf4j
@Data
public class GetRedirectedLinkTask implements Callable<GetRedirectedLinkTask.GetRedirectedLinkResult> {
    private String fileName;

    private String previewLink;

    public GetRedirectedLinkTask(String name, String uri){
        fileName = name;
        previewLink = uri;
    }

    @Override
    public GetRedirectedLinkResult call() {
        log.info("Getting redirected link for: " + fileName);

        URLConnection con = null;
        try {
            con = new URL(previewLink.replace("dl=0", "raw=1")).openConnection();
            con.connect();
            InputStream in = con.getInputStream();
            in.close();

            return GetRedirectedLinkResult.createSuccessResult(fileName, con.getURL().toString());
        } catch (IOException e) {
            e.printStackTrace();
            log.info("Error while trying to get redirected uri for: " + fileName + " : " + previewLink);
            return GetRedirectedLinkResult.createFailureResultWithThrowable(fileName, null, e);
        }
    }

    @Data
    public static class GetRedirectedLinkResult {
        public final String name;

        public final String uri;

        public final Throwable error;

        public GetRedirectedLinkResult(String name, String uri, Throwable error){
            this.name = name;
            this.uri = uri;
            this.error = error;
        }

        public boolean isOk() {
            return error == null && uri != null;
        }

        public static GetRedirectedLinkResult createSuccessResult(String name, String uri) {
            return new GetRedirectedLinkResult(name, uri, null);
        }

        public static GetRedirectedLinkResult createFailureResultWithThrowable(String name, String uri, Throwable error) {
            return new GetRedirectedLinkResult(name, null, error);
        }
    }
}
