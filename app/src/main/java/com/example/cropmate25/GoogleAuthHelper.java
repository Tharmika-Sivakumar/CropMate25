package com.example.cropmate25;

import com.google.auth.oauth2.GoogleCredentials;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collections;

public class GoogleAuthHelper {

    private static final String SCOPES = "https://www.googleapis.com/auth/firebase.messaging";

    public static String getAccessToken(String credentialsPath) throws IOException {
        GoogleCredentials googleCredentials = GoogleCredentials
                .fromStream(new FileInputStream(credentialsPath))
                .createScoped(Collections.singleton(SCOPES));
        googleCredentials.refreshIfExpired();
        return googleCredentials.getAccessToken().getTokenValue();
    }
}
