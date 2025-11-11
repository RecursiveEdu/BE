/**
 * Copyright (c) 2025 Recursive Education. All rights reserved.
 */
package com.recursive.edu.backend.helper;

import com.recursive.edu.backend.constants.UserConstants;
import com.recursive.edu.backend.model.exception.ApplicationException;
import com.recursive.edu.backend.service.EmailService;
import com.recursive.edu.backend.util.StringHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * @author PrantikGuha
 * CreatedAt: {06-11-2025}
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class AuthHelper {

    private static final int OTP_LENGTH = 6;
    private static final int OTP_EXPIRY_MINS = 5;
    private static final int MAX_ATTEMPTS = 5;
    private static final String DIGITS = "0123456789";
    private static final long OTP_REGENERATION_TIME_IN_MILLIS = 60 * 1000;
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final String OTP_ERROR_MSG = "Too many tries, OTP generation not allowed, please try after one minute.";
    private static final String OTP_ERROR_MSG_GEN = "OTP generation not allowed, please try after sometime.";

    private final RedisTemplate<String, String> redisTemplate;
    private final EmailService emailService;

    public void sendOtp(String userId, String name, UserConstants.OtpType otpType) throws ApplicationException {
        switch (otpType) {
            case BOTH:
                break;
            case EMAIL:
                sendEmailOtp(userId, name);
                break;
            case MOBILE:
                sendMobileOtp(userId, name);
                break;
            default:
                log.trace("Invalid otp type");
        }
    }

    private void sendMobileOtp(String mobile, String name) throws ApplicationException {
        try {
            String timeValue = RedisHelper.get(redisTemplate, RedisHelper.getKey(UserConstants.MOBILE_OTP_TIME_FORMAT, mobile));
            if (StringHelper.isNotEmpty(timeValue)) {
                long time = Long.parseLong(timeValue);
                if (System.currentTimeMillis() - time < OTP_REGENERATION_TIME_IN_MILLIS) {
                    throw new ApplicationException(OTP_ERROR_MSG);
                }
            }
            String otp = randomOtp();
            cacheMobileOtp(mobile, otp);
//            emailService.sendOtpEmail(name, mobile, otp, String.valueOf(OTP_EXPIRY_MINS));
        } catch (ApplicationException applicationException) {
            throw applicationException;
        } catch (Exception exception) {
            log.error("Exception in AuthHelper.sendEmailOtp ", exception);
            throw new ApplicationException(OTP_ERROR_MSG_GEN);
        }
    }

    private void sendEmailOtp(String email, String name) throws ApplicationException {
        try {
            String timeValue = RedisHelper.get(redisTemplate, RedisHelper.getKey(UserConstants.EMAIL_OTP_TIME_FORMAT, email));
            if (StringHelper.isNotEmpty(timeValue)) {
                long time = Long.parseLong(timeValue);
                if (System.currentTimeMillis() - time < OTP_REGENERATION_TIME_IN_MILLIS) {
                    throw new ApplicationException(OTP_ERROR_MSG);
                }
            }
            String otp = randomOtp();
            cacheEmailOtp(email, otp);
            emailService.sendOtpEmail(name, email, otp, String.valueOf(OTP_EXPIRY_MINS));
        } catch (ApplicationException applicationException) {
            throw applicationException;
        } catch (Exception exception) {
            log.error("Exception in AuthHelper.sendEmailOtp ", exception);
            throw new ApplicationException(OTP_ERROR_MSG_GEN);
        }
    }

    private String hashOtp(String otp) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(otp.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void cacheMobileOtp(String email, String otp) {
        redisTemplate.execute(new SessionCallback<Void>() {
            @Override
            public <K, V> Void execute(RedisOperations<K, V> operations) throws DataAccessException {
                operations.multi();
                ValueOperations<String, String> valueOperations = (ValueOperations<String, String>) operations.opsForValue();
                valueOperations.set(RedisHelper.getKey(UserConstants.MOBILE_OTP_FORMAT, email), hashOtp(otp), OTP_EXPIRY_MINS, TimeUnit.MINUTES);
                valueOperations.set(RedisHelper.getKey(UserConstants.MOBILE_OTP_ATTEMPTS_FORMAT, email), "0", OTP_EXPIRY_MINS, TimeUnit.MINUTES);
                valueOperations.set(RedisHelper.getKey(UserConstants.MOBILE_OTP_TIME_FORMAT, email), String.valueOf(System.currentTimeMillis()), OTP_EXPIRY_MINS, TimeUnit.MINUTES);
                operations.exec();
                return null;
            }
        });
    }

    private void cacheEmailOtp(String email, String otp) {
        redisTemplate.execute(new SessionCallback<Void>() {
            @Override
            public <K, V> Void execute(RedisOperations<K, V> operations) throws DataAccessException {
                operations.multi();
                ValueOperations<String, String> valueOperations = (ValueOperations<String, String>) operations.opsForValue();
                valueOperations.set(RedisHelper.getKey(UserConstants.EMAIL_OTP_FORMAT, email), hashOtp(otp), OTP_EXPIRY_MINS, TimeUnit.MINUTES);
                valueOperations.set(RedisHelper.getKey(UserConstants.EMAIL_OTP_ATTEMPTS_FORMAT, email), "0", OTP_EXPIRY_MINS, TimeUnit.MINUTES);
                valueOperations.set(RedisHelper.getKey(UserConstants.EMAIL_OTP_TIME_FORMAT, email), String.valueOf(System.currentTimeMillis()), OTP_EXPIRY_MINS, TimeUnit.MINUTES);
                operations.exec();
                return null;
            }
        });
    }

    public boolean validateOtp(String otp, String otpKey, String attemptsKey, String timeKey) throws ApplicationException {
        try {
            String hashedOtp = RedisHelper.get(redisTemplate, otpKey);
            if (hashedOtp == null) return false;

            String attemptStringValue = RedisHelper.get(redisTemplate, attemptsKey);
            int attempts = Integer.parseInt(attemptStringValue == null ? "0" : attemptStringValue);

            if (attempts >= MAX_ATTEMPTS) {
                RedisHelper.delete(redisTemplate, otpKey, attemptsKey, timeKey);
                throw new ApplicationException("Too many tries, please try after sometime");
            }

            if (hashedOtp.equals(hashOtp(otp))) {
                RedisHelper.delete(redisTemplate, otpKey, attemptsKey, timeKey);
                return true;
            } else {
                RedisHelper.increment(redisTemplate, attemptsKey);
                return false;
            }
        } catch (ApplicationException applicationException) {
            throw applicationException;
        } catch (Exception exception) {
            throw new ApplicationException(exception.getMessage());
        }
    }

    private String randomOtp() {
        StringBuilder sb = new StringBuilder(OTP_LENGTH);
        for (int i = 0; i < OTP_LENGTH; i++)
            sb.append(DIGITS.charAt(RANDOM.nextInt(DIGITS.length())));
        return sb.toString();
    }

}
