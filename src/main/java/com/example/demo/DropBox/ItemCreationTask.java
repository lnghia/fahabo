package com.example.demo.DropBox;

import com.dropbox.core.DbxException;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.sharing.PathLinkMetadata;
import jdk.jshell.Snippet;
import lombok.extern.slf4j.Slf4j;

import javax.mail.FetchProfile;
import javax.swing.text.html.Option;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;
import java.util.concurrent.Callable;

@Slf4j
public class ItemCreationTask implements Callable<ItemCreationTask.ItemCreationResult> {
    private DbxClientV2 dbxClientV2;

    private ByteUploadTask.ByteUploadResult itemToCreate;

//    private final List<PathLinkMetadata> newItems = new ArrayList<>();

    public ItemCreationTask(DbxClientV2 dbxClientV2, ByteUploadTask.ByteUploadResult itemToCreate) {
        this.dbxClientV2 = dbxClientV2;
        this.itemToCreate = itemToCreate;
    }

//    @Override
//    public Map<String, ItemCreationResult> call() {
//        log.info("Calling API to create public uri: " + itemsToCreate.size());
//
//        Map<String, ItemCreationResult> results = new HashMap<>(itemsToCreate.size());
//
//        if(itemsToCreate.isEmpty()){
//            throw new IllegalArgumentException("No items to create.");
//        }
//
//        for(var item : itemsToCreate){
//            PathLinkMetadata pathLinkMetadata = null;
//            URLConnection con = null;
//            try {
//                pathLinkMetadata = dbxClientV2.sharing().createSharedLink("/" + item.getName());
//                con = new URL(pathLinkMetadata.getUrl().replace("dl=0", "raw=1")).openConnection();
//                con.connect();
//                InputStream in = con.getInputStream();
//                in.close();
//            } catch (DbxException e) {
//                log.info("API error while calling createSharedLink. " + e.getMessage());
//                e.printStackTrace();
//                results.put(item.name, ItemCreationResult.createFailureResult(item.name, e));
//            } catch (IOException e) {
//                log.info("Error while getting redirected uri. " + e.getMessage());
//                e.printStackTrace();
//                results.put(item.name, ItemCreationResult.createFailureResult(item.name, e));
//            }
//            results.put(item.name, ItemCreationResult.createSuccessResult(item.name, pathLinkMetadata, con.getURL().toString()));
//        }
//
//        return results;
//    }

    @Override
    public ItemCreationResult call() {
        log.info("Calling API to create public uri: " + itemToCreate.name);

        if(itemToCreate == null){
            throw new IllegalArgumentException("No items to create.");
        }

        PathLinkMetadata pathLinkMetadata = null;
        URLConnection con = null;
        ItemCreationResult result = null;
        try {
            pathLinkMetadata = dbxClientV2.sharing().createSharedLink("/" + itemToCreate.getName());
            con = new URL(pathLinkMetadata.getUrl().replace("dl=0", "raw=1")).openConnection();
            con.connect();
            InputStream in = con.getInputStream();
            in.close();

            result = ItemCreationResult.createSuccessResult(itemToCreate.name, pathLinkMetadata, con.getURL().toString());
        } catch (DbxException e) {
            log.info("API error while calling createSharedLink. " + e.getMessage());
            e.printStackTrace();
            result = ItemCreationResult.createFailureResult(itemToCreate.name, e);
//            results.put(item.name, ItemCreationResult.createFailureResult(item.name, e));
        } catch (IOException e) {
            log.info("Error while getting redirected uri. " + e.getMessage());
            e.printStackTrace();
            result = ItemCreationResult.createFailureResult(itemToCreate.name, e);
//            results.put(item.name, ItemCreationResult.createFailureResult(item.name, e));
        }

        return result;
    }

    public static class ItemCreationResult {
        public String name;

        public Optional<PathLinkMetadata> metadata;

        public Optional<String> uri;

        public Optional<Throwable> error;

        private ItemCreationResult(String name, Throwable error, PathLinkMetadata metadata){
            this.name = name;
            this.metadata = Optional.ofNullable(metadata);
            this.error = Optional.ofNullable(error);
        }

        private ItemCreationResult(String name, Throwable error, PathLinkMetadata metadata, String uri){
            this.name = name;
            this.metadata = Optional.ofNullable(metadata);
            this.error = Optional.ofNullable(error);
            this.uri = Optional.ofNullable(uri);
        }

        public boolean isOk(){
            return !error.isPresent() && uri.isPresent();
        }

//        @Override
//        public String toString(){
//            return String.format("ItemCreationResult{name=%s, uri=%s, error=%}", name, uri.toString(), error.toString());
//        }

        public static ItemCreationResult createSuccessResult(String name, PathLinkMetadata metadata, String uri) {
            return new ItemCreationResult(name, null, metadata, uri);
        }

        public static ItemCreationResult createFailureResult(String name, Throwable error){
            return new ItemCreationResult(name, error, null, null);
        }
    }
}
