/**
 * Copyright (c) 2025 Recursive Education. All rights reserved.
 */
package com.recursive.edu.backend.service.impl;

import com.recursive.edu.backend.constants.UserConstants;
import com.recursive.edu.backend.controller.request.AuthRefreshRequest;
import com.recursive.edu.backend.controller.request.LoginRequest;
import com.recursive.edu.backend.controller.response.AuthRefreshResponse;
import com.recursive.edu.backend.controller.response.LoginResponse;
import com.recursive.edu.backend.model.exception.ApplicationException;
import com.recursive.edu.backend.model.postgres.User;
import com.recursive.edu.backend.model.user.UserDetails;
import com.recursive.edu.backend.repository.UserRepository;
import com.recursive.edu.backend.service.AuthService;
import com.recursive.edu.backend.service.JwtService;
import com.recursive.edu.backend.util.StringHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author PrantikGuha
 * CreatedAt: {27-10-2025}
 */
@Service
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private final JwtService jwtService;

    @Value("${access.token.expiration.in.milli.secs}")
    private long accessTokenExpInMillis; // 15 * 60 * 1000

    @Value("${refresh.token.expiration.in.milli.secs}")
    private long refreshTokenExpInMillis; // 7 * 24 * 60 * 60 * 1000

    public AuthServiceImpl(UserRepository userRepository, PasswordEncoder encoder,
                           JwtService jwtService) {
        this.userRepository = userRepository;
        this.encoder = encoder;
        this.jwtService = jwtService;
    }

    @Override
    public LoginResponse login(LoginRequest loginRequest) throws Exception {
        try {
            User user = userRepository.findByEmail(loginRequest.getEmail()).orElseThrow(() -> new ApplicationException("User not found"));
            if (!encoder.matches(loginRequest.getPassword(), user.getPassword())) {
                throw new ApplicationException("Invalid credentials");
            }
            String accessToken = jwtService.generateToken(user.getEmail(),
                    Map.of("role", user.getRole()), accessTokenExpInMillis);
            String refreshToken = jwtService.generateToken(user.getEmail(),
                    Map.of("type", "refresh"), refreshTokenExpInMillis);
            return LoginResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .user(UserDetails.builder()
                            .countryCode(user.getCountryCode())
                            .email(user.getEmail())
                            .firstName(user.getFirstName())
                            .lastName(user.getLastName())
                            .middleName(user.getMiddleName())
                            .name(String.format(UserConstants.NAME_FORMAT,
                                    user.getFirstName(), user.getMiddleName(), user.getLastName()))
                            .mobile(user.getMobile())
                            .build())
                    .build();
        } catch (Exception exception) {
            log.error("Exception in AuthServiceImpl.login, ", exception);
            throw exception;
        }
    }

    @Override
    public AuthRefreshResponse refresh(AuthRefreshRequest authRefreshRequest) throws Exception{
        try {
            if (!jwtService.isTokenValid(authRefreshRequest.getRefreshToken())) {
                throw new ApplicationException("Invalid refresh token");
            }
            String username = jwtService.extractUsername(authRefreshRequest.getRefreshToken());
            User user = userRepository.findByEmail(username).orElseThrow(() -> new ApplicationException("User not found"));
            String newAccessToken = jwtService.generateToken(user.getEmail(),
                    Map.of("role", user.getRole()), accessTokenExpInMillis);
            return AuthRefreshResponse.builder()
                    .accessToken(newAccessToken)
                    .user(UserDetails.builder()
                            .countryCode(user.getCountryCode())
                            .email(user.getEmail())
                            .firstName(user.getFirstName())
                            .lastName(user.getLastName())
                            .middleName(user.getMiddleName())
                            .name(String.format(UserConstants.NAME_FORMAT,
                                    user.getFirstName(), user.getMiddleName(), user.getLastName()))
                            .mobile(user.getMobile())
                            .build())
                    .build();
        } catch (Exception exception) {
            log.error("Exception in AuthServiceImpl.refresh, ", exception);
            throw exception;
        }
    }

    @Override
    public UserDetails getCurrentUser(String accessToken) throws Exception {
        try {
            if (StringHelper.isEmpty(accessToken)) {
                throw new ApplicationException("Authentication token is required.");
            }
            String token = accessToken.substring(7);
            if (!jwtService.isTokenValid(token))
                throw new ApplicationException("Invalid token");

            String email = jwtService.extractUsername(token);
            User user = userRepository.findByEmail(email).orElseThrow(() -> new ApplicationException("User not found"));

            return UserDetails.builder()
                    .countryCode(user.getCountryCode())
                    .email(user.getEmail())
                    .firstName(user.getFirstName())
                    .lastName(user.getLastName())
                    .middleName(user.getMiddleName())
                    .name(String.format(UserConstants.NAME_FORMAT,
                            user.getFirstName(), user.getMiddleName(), user.getLastName()))
                    .mobile(user.getMobile())
                    .build();
        } catch (Exception exception) {
            log.error("Exception in AuthServiceImpl.getCurrentUser, ", exception);
            throw exception;
        }
    }
}
