package com.example.demo.DropBox;

import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
public class DropBoxConfig {
    @Value("${DROPBOX_ACCESS_TOKEN}")
    private String DROPBOX_ACCESS_TOKEN;

    @Value("${THREAD_NUM}")
    private int THREAD_NUM;
}
