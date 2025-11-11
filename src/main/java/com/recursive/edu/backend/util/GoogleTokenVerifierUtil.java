/**
 * Copyright (c) 2025 Recursive Education. All rights reserved.
 */
package com.recursive.edu.backend.util;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.recursive.edu.backend.model.exception.ApplicationException;
import com.recursive.edu.backend.model.user.UserDetails;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.Optional;

/**
 * @author PrantikGuha
 * CreatedAt: {07-11-2025}
 */
@Component
@Slf4j
public class GoogleTokenVerifierUtil {

    @Value("${gcp.web.client.id}")
    private String webClientId;

    private GoogleIdTokenVerifier verifier;

    @PostConstruct
    public void init() {
        this.verifier = new GoogleIdTokenVerifier.Builder(
                new NetHttpTransport(), new JacksonFactory())
                .setAudience(List.of(webClientId))
                .build();
    }

    /**
     * Verifies a Google ID token.
     *
     * @param idTokenString ID token received from client
     * @return Optional payload if valid, empty otherwise
     */
    private Optional<GoogleIdToken.Payload> verify(String idTokenString) {
        if (idTokenString == null || idTokenString.isBlank()) {
            log.error("GoogleTokenVerifierUtil: Missing ID token");
            return Optional.empty();
        }

        try {
            GoogleIdToken idToken = verifier.verify(idTokenString);
            if (idToken == null) {
                log.error("Invalid Google token");
                return Optional.empty();
            }

            GoogleIdToken.Payload payload = idToken.getPayload();
            return Optional.of(payload);

        } catch (GeneralSecurityException | IOException e) {
            log.error("Token verification failed: {}", e.getMessage());
            return Optional.empty();
        }
    }

    public UserDetails getUserFromToken(String token) throws ApplicationException {
        try {
            Optional<GoogleIdToken.Payload> payloadOptional = verify(token);
            if (payloadOptional.isPresent()) {
                return UserDetails.builder()
                        .email(payloadOptional.get().getEmail())
                        .emailVerified(payloadOptional.get().getEmailVerified())
                        .build();
            }
        } catch (Exception exception) {
            throw new ApplicationException(String.format("Invalid token: %s", exception.getMessage()));
        }
        throw new ApplicationException("Invalid token");
    }
}
