package com.example.demo.DropBox;

import com.dropbox.core.v2.DbxClientV2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.concurrent.CompletionService;
import java.util.concurrent.Executor;

public class DropBoxHelper {
    @Value("${THREAD_NUM}")
    private int THREAD_NUM;

    public void temp(){

    }

    @Autowired
    private DbxClientV2 clientV2;
}
