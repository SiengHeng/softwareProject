package com.university.courseenrollment.demogradle.config;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.Collections;

@Configuration
@Slf4j
public class GoogleSheetsConfig {

    private static final String APPLICATION_NAME = "University Course Enrollment System";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

    @Value("${google.sheets.enabled:false}")
    private boolean enabled;

    @Value("${google.sheets.credentials.file:}")
    private String credentialsFile;

    @Value("${google.sheets.credentials.json:}")
    private String credentialsJson;

    @Bean
    public Sheets googleSheetsService() {
        if (!enabled) {
            log.info("Google Sheets integration is disabled");
            return null;
        }

        try {
            final NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
            GoogleCredentials credentials = getCredentials();
            
            return new Sheets.Builder(httpTransport, JSON_FACTORY, 
                    new HttpCredentialsAdapter(credentials))
                    .setApplicationName(APPLICATION_NAME)
                    .build();
        } catch (Exception e) {
            log.error("Failed to initialize Google Sheets service", e);
            return null;
        }
    }

    private GoogleCredentials getCredentials() throws IOException {
        // Priority 1: JSON string from properties
        if (credentialsJson != null && !credentialsJson.isEmpty()) {
            log.info("Loading Google credentials from JSON string");
            ByteArrayInputStream stream = new ByteArrayInputStream(
                    credentialsJson.getBytes(StandardCharsets.UTF_8));
            return GoogleCredentials.fromStream(stream)
                    .createScoped(Collections.singletonList(SheetsScopes.SPREADSHEETS));
        }

        // Priority 2: File path from properties
        if (credentialsFile != null && !credentialsFile.isEmpty()) {
            log.info("Loading Google credentials from file: {}", credentialsFile);
            return GoogleCredentials.fromStream(new FileInputStream(credentialsFile))
                    .createScoped(Collections.singletonList(SheetsScopes.SPREADSHEETS));
        }

        // Priority 3: Default application credentials
        log.info("Loading Google credentials from default application credentials");
        return GoogleCredentials.getApplicationDefault()
                .createScoped(Collections.singletonList(SheetsScopes.SPREADSHEETS));
    }

    @Bean
    public boolean isGoogleSheetsEnabled() {
        return enabled;
    }
}
