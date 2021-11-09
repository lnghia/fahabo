package com.example.demo.Firebase;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Configuration
@Slf4j
public class FirebaseInitiator {
    @Value("${FIREBASE_AUTH_FILE}")
    private String FIREBASE_AUTH_FILE;

    @Bean
    public void initFirebaseApp() {
        try {
            log.info("Initiating firebase application.");

            InputStream inputStream = null;

            if (FIREBASE_AUTH_FILE != null && !FIREBASE_AUTH_FILE.isBlank() && !FIREBASE_AUTH_FILE.isEmpty()) {
                inputStream = new BufferedInputStream(new FileInputStream(FIREBASE_AUTH_FILE));
            }

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials((inputStream != null) ? GoogleCredentials.fromStream(inputStream) : GoogleCredentials.getApplicationDefault())
                    .build();

            FirebaseApp.initializeApp(options);

            log.info("Initiated firebase application successfully.");
        } catch (IOException e) {
            log.error("Couldn't find firebase credentials file.", e.getMessage());
            e.printStackTrace();
        }
    }
}
