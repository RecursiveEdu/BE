/**
 * Copyright (c) 2025 Recursive Education. All rights reserved.
 */
package com.recursive.edu.backend.service.impl;

import com.recursive.edu.backend.constants.UserConstants;
import com.recursive.edu.backend.controller.request.*;
import com.recursive.edu.backend.controller.response.AuthRefreshResponse;
import com.recursive.edu.backend.controller.response.LoginResponse;
import com.recursive.edu.backend.controller.response.OtpValidationResponse;
import com.recursive.edu.backend.controller.response.SignupResponse;
import com.recursive.edu.backend.helper.AuthHelper;
import com.recursive.edu.backend.helper.RedisHelper;
import com.recursive.edu.backend.model.exception.ApplicationException;
import com.recursive.edu.backend.model.postgres.User;
import com.recursive.edu.backend.model.user.UserDetails;
import com.recursive.edu.backend.repository.UserRepository;
import com.recursive.edu.backend.service.AuthService;
import com.recursive.edu.backend.service.JwtService;
import com.recursive.edu.backend.util.GoogleTokenVerifierUtil;
import com.recursive.edu.backend.util.StringHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

/**
 * @author PrantikGuha
 * CreatedAt: {27-10-2025}
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private final JwtService jwtService;
    private final AuthHelper authHelper;
    private final GoogleTokenVerifierUtil googleTokenVerifierUtil;

    @Value("${access.token.expiration.in.milli.secs}")
    private long accessTokenExpInMillis; // 15 * 60 * 1000

    @Value("${refresh.token.expiration.in.milli.secs}")
    private long refreshTokenExpInMillis; // 7 * 24 * 60 * 60 * 1000

    @Override
    public LoginResponse login(LoginRequest loginRequest) throws Exception {
        try {
            User user = userRepository.findByEmail(loginRequest.getEmail()).orElseThrow(() -> new ApplicationException("User not found"));
            if (!encoder.matches(loginRequest.getPassword(), user.getPassword())) {
                throw new ApplicationException("Invalid credentials");
            }
            return getLoginResponse(user);
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
                    Map.of("role", user.getRole(), "uuid", user.getPublicId(), "id", user.getId()),
                    accessTokenExpInMillis);
            return AuthRefreshResponse.builder()
                    .accessToken(newAccessToken)
                    .user(UserDetails.builder()
                            .countryCode(user.getCountryCode())
                            .email(user.getEmail())
                            .firstName(user.getFirstName())
                            .lastName(user.getLastName())
                            .middleName(user.getMiddleName())
                            .name(StringHelper.getName(user.getFirstName(), user.getMiddleName(), user.getLastName()))
                            .mobile(user.getMobile())
                            .emailVerified(user.getEmailVerified())
                            .uuid(user.getPublicId().toString())
                            .role(user.getRole())
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
                    .name(StringHelper.getName(user.getFirstName(), user.getMiddleName(), user.getLastName()))
                    .mobile(user.getMobile())
                    .emailVerified(user.getEmailVerified())
                    .uuid(user.getPublicId().toString())
                    .role(user.getRole())
                    .build();
        } catch (Exception exception) {
            log.error("Exception in AuthServiceImpl.getCurrentUser, ", exception);
            throw exception;
        }
    }

    @Override
    public SignupResponse signup(SignupRequest signupRequest) throws ApplicationException {
        try {
            if (StringHelper.isNotEmpty(signupRequest.getToken())) {
                UserDetails userDetails = googleTokenVerifierUtil.getUserFromToken(signupRequest.getToken());
                Optional<User> user = userRepository.findByEmail(userDetails.getEmail());
                if (user.isPresent()) {
                    return new SignupResponse("210", "User already registered. Logging in..", getLoginResponse(user.get()));
                } else {
                    String[] name = StringHelper.isNotEmpty(userDetails.getName())? userDetails.getName().split(" "): null;
                    String firstName = "", middleName = "", lastName = "";
                    if (name != null) {
                        firstName = name.length > 0? name[0]: "";
                        middleName = name.length > 1? name[1]: "";
                        lastName = name.length > 2? name[2]: "";
                    }
                    User newUser = User.builder()
                            .firstName(firstName)
                            .middleName(middleName)
                            .lastName(lastName)
                            .email(userDetails.getEmail())
                            .emailVerified(userDetails.getEmailVerified())
                            .role(UserConstants.UserRole.PILOT.name())
                            .build();
                    userRepository.saveAndFlush(newUser);
                    return new SignupResponse("210", "User successfully registered. Logging in..", getLoginResponse(newUser));
                }
            } else {
                if (signupRequest.getUserSignupRequest() != null) {
                    UserSignupRequest userSignupRequest = signupRequest.getUserSignupRequest();
                    Optional<User> user = userRepository.findByEmailOrMobile(userSignupRequest.getEmail(), userSignupRequest.getMobile());
                    if (user.isPresent()) {
                        if (user.get().getEmailVerified()) {
                            return new SignupResponse("211", "User already registered. Log in to start");
                        } else {
                            authHelper.sendOtp(user.get().getEmail(), user.get().getFirstName(), UserConstants.OtpType.EMAIL);
                            return new SignupResponse("212", "User already registered. Verify your email.", user.get().getEmail(), getLoginResponse(user.get()));
                        }
                    } else {
                        User newUser = User.builder()
                                .firstName(userSignupRequest.getFirstName())
                                .middleName(userSignupRequest.getMiddleName())
                                .lastName(userSignupRequest.getLastName())
                                .email(userSignupRequest.getEmail())
                                .emailVerified(false)
                                .role(UserConstants.UserRole.PILOT.name())
                                .mobile(userSignupRequest.getMobile())
                                .countryCode(userSignupRequest.getCountryCode())
                                .password(encoder.encode(userSignupRequest.getPassword()))
                                .build();
                        userRepository.saveAndFlush(newUser);
                        authHelper.sendOtp(newUser.getEmail(), newUser.getFirstName(), UserConstants.OtpType.EMAIL);
                        return new SignupResponse("212", "User successfully registered. Verify your email.", newUser.getEmail(), getLoginResponse(newUser));
                    }
                }
            }
        }  catch (Exception exception) {
            log.error("Exception in AuthServiceImpl.signup, ", exception);
            throw exception;
        }
        throw new ApplicationException("Unable to signup, please try after sometime.");
    }

    @Override
    public OtpValidationResponse validateOtp(OtpValidationRequest otpValidationRequest) throws Exception {
        try {
            String otpKey = RedisHelper.getKey(otpValidationRequest.getType() == UserConstants.OtpType.EMAIL ? UserConstants.EMAIL_OTP_FORMAT: UserConstants.MOBILE_OTP_FORMAT
                    , otpValidationRequest.getId());
            String attemptKey = RedisHelper.getKey(otpValidationRequest.getType() == UserConstants.OtpType.EMAIL ?
                            UserConstants.EMAIL_OTP_ATTEMPTS_FORMAT: UserConstants.MOBILE_OTP_ATTEMPTS_FORMAT
                    , otpValidationRequest.getId());
            String timeKey = RedisHelper.getKey(otpValidationRequest.getType() == UserConstants.OtpType.EMAIL ?
                            UserConstants.EMAIL_OTP_TIME_FORMAT: UserConstants.MOBILE_OTP_TIME_FORMAT
                    , otpValidationRequest.getId());
            if (authHelper.validateOtp(otpValidationRequest.getOtp(), otpKey, attemptKey, timeKey)) {
                userRepository.updateEmailVerified(otpValidationRequest.getId());
                return new OtpValidationResponse(true, "0", "Otp successfully validated");
            } else {
                return new OtpValidationResponse(false, "310", "Wrong Otp");
            }
        } catch (ApplicationException applicationException) {
            return new OtpValidationResponse(false, "311", applicationException.getMessage());
        } catch (Exception exception) {
            log.error("Exception in AuthServiceImpl.validateOtp: ", exception);
            throw exception;
        }
    }

    @Override
    public String resendEmail(OtpValidationRequest otpValidationRequest) throws ApplicationException {
        try {
            User user = userRepository.findByEmailOrMobile(otpValidationRequest.getId(), otpValidationRequest.getId())
                    .orElseThrow(() -> new ApplicationException("User not found"));
            authHelper.sendOtp(user.getEmail(), user.getFirstName(), UserConstants.OtpType.EMAIL);
            return "Successfully resend OTP";
        } catch (ApplicationException applicationException) {
            throw applicationException;
        } catch (Exception exception) {
            log.error("Exception in resendEmail, ", exception);
            throw new ApplicationException(exception.getMessage());
        }
    }

    private LoginResponse getLoginResponse(User user) {
        try {
            String accessToken = jwtService.generateToken(user.getEmail(),
                    Map.of("role", user.getRole(), "uuid", user.getPublicId(), "id", user.getId()),
                    accessTokenExpInMillis);
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
                            .name(StringHelper.getName(user.getFirstName(), user.getMiddleName(), user.getLastName()))
                            .mobile(user.getMobile())
                            .emailVerified(user.getEmailVerified())
                            .uuid(user.getPublicId().toString())
                            .role(user.getRole())
                            .build())
                    .build();
        } catch (Exception exception) {
            log.error("Exception occurred in AuthServiceImpl.getLoginResponse: ", exception);
            return null;
        }
    }
}
