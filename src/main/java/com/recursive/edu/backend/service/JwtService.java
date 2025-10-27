/**
 * Copyright (c) 2025 Recursive Education. All rights reserved.
 */
package com.recursive.edu.backend.service;

import io.jsonwebtoken.Claims;

import java.util.Map;
import java.util.function.Function;

/**
 * @author PrantikGuha
 * CreatedAt: {27-10-2025}
 */
public interface JwtService {
    String generateToken(String subject, Map<String, Object> claims, long expiryMillis);
    <T> T extractClaim(String token, Function<Claims, T> claimsResolver);
    String extractUsername(String token);
    boolean isTokenValid(String token);
}
